package com.example.clothingstore.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.example.clothingstore.R;
import com.example.clothingstore.adapters.AdapterMessage;
import com.example.clothingstore.models.ModelMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SellerMessageActivity extends AppCompatActivity {

    private TextView messageCountTv;
    private RecyclerView messageRv;
    private FirebaseAuth auth;

    private ArrayList<ModelMessage> messageArrayList;
    private AdapterMessage adapterMessage;

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
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Message");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                messageArrayList.clear();

                for(DataSnapshot ds:snapshot.getChildren()){

                    ModelMessage modelMessage = snapshot.getValue(ModelMessage.class);
                  //  String receiver = ""+snapshot.child("receiver").getValue();
                    messageCountTv.setText(modelMessage.getReceiver());

                    assert modelMessage != null;
                    if(modelMessage.getReceiver().equals(user.getUid()))
                    {

                        messageArrayList.add(modelMessage);



                    }

                    adapterMessage = new AdapterMessage(SellerMessageActivity.this,messageArrayList);
                    messageRv.setAdapter(adapterMessage);

                }


              /*  String receiver = ""+snapshot.child("receiver").getValue();
                String sender = ""+snapshot.child("sender").getValue();
                String msg = ""+snapshot.child("msg").getValue();
                String time = ""+snapshot.child("time").getValue();*/


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}