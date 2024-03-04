package com.example.android_assignmenttwo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_register#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_register extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_register() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_register.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_register newInstance(String param1, String param2) {
        fragment_register fragment = new fragment_register();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        EditText email = view.findViewById(R.id.editTextRegisterEmail);
        EditText name = view.findViewById(R.id.editTextRegisterName);
        EditText password = view.findViewById(R.id.editTextRegisterPassword);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Button registerButton = view.findViewById(R.id.buttonRegisterToDB);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = email.getText().toString().trim();
                String userPassword = password.getText().toString().trim();

                // Create an instance of FirebaseAuth
                FirebaseAuth mAuth = FirebaseAuth.getInstance();

                // Register the user in Firebase Authentication
                mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // User is successfully registered and logged in
                                    // Now, store additional details in Firebase Realtime Database
                                    String userName = name.getText().toString().trim();
                                    User newUser = new User(userEmail, userName, userPassword);
                                    String encodedEmail = userEmail.replace(".", ",");
                                    DatabaseReference myRef = database.getReference("usersList").child(encodedEmail);
                                    myRef.setValue(newUser)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(getActivity(), "Registration Success", Toast.LENGTH_SHORT).show();
                                                Navigation.findNavController(view).navigate(R.id.action_fragment_register_to_fragment_login);
                                            }).addOnFailureListener(e -> Toast.makeText(getActivity(), "Database write failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                                } else {
                                    // If registration fails, display a message to the user
                                    Toast.makeText(getActivity(), "Authentication failed", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });


        return view;
    }
}