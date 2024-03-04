package com.example.android_assignmenttwo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    private List<Item> itemList;

    public RecyclerViewAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView amount;
        TextView price;
        Button removeButton;


        public MyViewHolder(View itemView){
            super(itemView);

            name = itemView.findViewById(R.id.textViewItemName);
            amount = itemView.findViewById(R.id.textViewItemAmount);
            price = itemView.findViewById(R.id.textViewTotalPrice);
            removeButton = itemView.findViewById(R.id.buttonRemoveItem);
        }
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.MyViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.name.setText(item.getItemName());
        holder.amount.setText(String.valueOf(item.getAmount()));
        holder.price.setText(String.valueOf(item.getTotalPrice()));

        holder.removeButton.setOnClickListener(v -> {
            // Remove item from itemList
            itemList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, itemList.size());

            // Update the entire user object in Firebase
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null && currentUser.getEmail() != null) {
                String encodedEmail = currentUser.getEmail().replace(".", ",");
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("usersList").child(encodedEmail);
                userRef.child("items").setValue(itemList);
            }
        });
    }

    public void updateItemList(List<Item> newItems) {
        this.itemList.clear();
        this.itemList.addAll(newItems);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
