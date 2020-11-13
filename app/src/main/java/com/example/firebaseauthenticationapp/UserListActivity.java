package com.example.firebaseauthenticationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.core.FirestoreClient;

import java.util.ArrayList;

public class UserListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        recyclerView = findViewById(R.id.userListRecyclerView);
        firebaseFirestore = FirebaseFirestore.getInstance();

        //Query
        Query query = firebaseFirestore.collection(Constants.COLLECTION_USER);

        //RecyclerOptions
        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<UserModel, UserViewHolder>(options) {
            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user_list, parent,false);
                return new UserViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull UserModel model) {

                holder.listName.setText(model.getfName());
                holder.listPhone.setText(model.getPhone());
                holder.listEmail.setText(model.getEmail());
            }
        };

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }

    //View Holder
    private class UserViewHolder extends RecyclerView.ViewHolder{

        private TextView listName;
        private TextView listPhone;
        private TextView listEmail;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            listName = itemView.findViewById(R.id.textViewUserName);
            listPhone = itemView.findViewById(R.id.textViewUserPhone);
            listEmail = itemView.findViewById(R.id.textViewUserEmail);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}