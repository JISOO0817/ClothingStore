package com.example.clothingstore.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.clothingstore.R;
import com.example.clothingstore.adapters.AdapterMessage;
import com.example.clothingstore.models.ModelMarket;
import com.example.clothingstore.models.ModelMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserMessageActivity extends AppCompatActivity {

    private TextView messageCountTv;
    private RecyclerView messageRv;
    private FirebaseAuth auth;

    private ArrayList<ModelMessage> modelMessageArrayList;
    private AdapterMessage adapterMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_message);

        messageCountTv = findViewById(R.id.messageCountTv);
        messageRv = findViewById(R.id.messageRv);

        auth = FirebaseAuth.getInstance();

        loadMessage();

    }

    private void loadMessage() {

        modelMessageArrayList = new ArrayList<>();

        FirebaseUser user = auth.getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Message");
        ref.orderByChild("receiver").equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                modelMessageArrayList.clear();

                for(DataSnapshot ds:snapshot.getChildren()){
                    ModelMessage modelMessage = ds.getValue(ModelMessage.class);

                    modelMessageArrayList.add(modelMessage);
                    messageCountTv.setText(""+modelMessageArrayList.size());
                }

                adapterMessage = new AdapterMessage(UserMessageActivity.this,modelMessageArrayList);
                messageRv.setAdapter(adapterMessage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}