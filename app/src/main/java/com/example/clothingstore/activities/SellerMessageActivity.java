package com.example.clothingstore.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.example.clothingstore.R;
import com.example.clothingstore.models.ModelMessage;
import com.example.clothingstore.models.ModelReview;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SellerMessageActivity extends AppCompatActivity {

    private TextView messageCountTv;
    private RecyclerView messageRv;
    private FirebaseAuth auth;

    private ArrayList<ModelMessage> messageArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_message);

        messageCountTv = findViewById(R.id.messageCountTv);
        messageRv = findViewById(R.id.messageRv);

        auth = FirebaseAuth.getInstance();
        loadMessage();


    }

    private void loadMessage() {

        final FirebaseUser user = auth.getCurrentUser();

        messageArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child("Message").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String receiver = ""+snapshot.child("receiver").getValue();
                if(receiver.equals(user.getUid())){
                    
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}