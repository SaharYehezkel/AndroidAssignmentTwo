package com.example.android_assignmenttwo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_list#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_list extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters

    private String mParam1;
    private String mParam2;
    private TextView totalPriceTextView;

    public fragment_list() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_list.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_list newInstance(String param1, String param2) {
        fragment_list fragment = new fragment_list();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        totalPriceTextView = view.findViewById(R.id.totalPriceTextView);

        // Initialize helloUserTextView
        TextView helloUserTextView = view.findViewById(R.id.textViewListHelloUser);

        // Initialize RecyclerView and its adapter
        RecyclerView recyclerView = view.findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<Item> itemList = new ArrayList<>();
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(itemList);
        recyclerView.setAdapter(adapter);

        // Fetch items from Firebase and update the RecyclerView and helloUserTextView
        fetchItemsFromFirebase(helloUserTextView, itemList, adapter);

        Button buttonAddItem = view.findViewById(R.id.buttonAddItem);
        buttonAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Add New Item");

                final EditText inputProductName = new EditText(getActivity());
                inputProductName.setHint("Product Name");
                final EditText inputPrice = new EditText(getActivity());
                inputPrice.setHint("Price per one unit");
                final EditText inputAmount = new EditText(getActivity());
                inputAmount.setHint("Amount");

                LinearLayout layout = new LinearLayout(getActivity());
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.addView(inputProductName);
                layout.addView(inputPrice);
                layout.addView(inputAmount);
                builder.setView(layout);

                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String productName = inputProductName.getText().toString();
                        double price = Double.parseDouble(inputPrice.getText().toString());
                        int amount = Integer.parseInt(inputAmount.getText().toString());

                        Item newItem = new Item(productName, amount, (float) price);
                        itemList.add(newItem);
                        adapter.notifyDataSetChanged();

                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (currentUser != null && currentUser.getEmail() != null) {
                            String encodedEmail = currentUser.getEmail().replace(".", ",");
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("usersList").child(encodedEmail);
                            userRef.child("items").setValue(itemList);
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        return view;
    }

    private void fetchItemsFromFirebase(TextView helloUserTextView, List<Item> itemList, RecyclerViewAdapter adapter) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getEmail() != null) {
            String encodedEmail = currentUser.getEmail().replace(".", ",");
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("usersList").child(encodedEmail);

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    itemList.clear();
                    if (dataSnapshot.exists() && dataSnapshot.hasChild("items")) {
                        for (DataSnapshot itemSnapshot : dataSnapshot.child("items").getChildren()) {
                            Item item = itemSnapshot.getValue(Item.class);
                            itemList.add(item);
                        }
                        adapter.notifyDataSetChanged();

                        User user = dataSnapshot.getValue(User.class);
                        if (user != null && user.getName() != null) {
                            helloUserTextView.setText("Hello " + user.getName());
                        }

                        double totalPrice = calculateTotalPrice(itemList);
                        String formattedTotalPrice = String.format("%.2f", totalPrice);
                        totalPriceTextView.setText("Total Price: " + formattedTotalPrice);
                    }
                }

                private double calculateTotalPrice(List<Item> items) {
                    double total = 0;
                    for (Item item : items) {
                        total += item.getTotalPrice();
                    }
                    return total;
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle possible errors
                }
            });
        }
    }
}