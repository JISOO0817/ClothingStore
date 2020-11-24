package com.example.clothingstore.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.clothingstore.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class MessageWriteActivity extends AppCompatActivity {

    private CircularImageView profileIv;
    private TextView nameTv;
    private EditText msgEt;
    private Button cancleBtn,sendBtn;
    private String receiver;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_message_write);


        receiver = getIntent().getStringExtra("sender");


        profileIv = findViewById(R.id.profileIv);
        nameTv = findViewById(R.id.nameTv);
        msgEt = findViewById(R.id.msgEt);
        cancleBtn = findViewById(R.id.cancelBtn);
        sendBtn = findViewById(R.id.sendBtn);

        auth = FirebaseAuth.getInstance();


        loadReceiverInfo();



        cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageToUser();
            }
        });
    }

    private void sendMessageToUser() {

        String msg = msgEt.getText().toString().trim();
        String timestamp = ""+System.currentTimeMillis();
        String messageId = ""+System.currentTimeMillis();

        FirebaseUser user = auth.getCurrentUser();
        if(!msg.equals("")){

            HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put("sender",user.getUid());
            hashMap.put("receiver",receiver);
            hashMap.put("msg",msg);
            hashMap.put("time",timestamp);
            hashMap.put("messageId",messageId);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Message");
            ref.child(messageId).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(MessageWriteActivity.this, "쪽지를 전송했습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }

    }

    private void loadReceiverInfo() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(receiver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String accountType = ""+snapshot.child("accountType").getValue();

                if(accountType.equals("Seller")){
                    String name = ""+snapshot.child("marketName").getValue();
                    String image = ""+snapshot.child("profileImage").getValue();

                    nameTv.setText(name);
                    try{
                        Picasso.get().load(image).placeholder(R.drawable.ic_person_gray).into(profileIv);

                    }catch (Exception e){}
                }else if(accountType.equals("User")){

                    String name = ""+snapshot.child("name").getValue();
                    String image = ""+snapshot.child("profileImage").getValue();

                    nameTv.setText(name);
                    try{
                        Picasso.get().load(image).placeholder(R.drawable.ic_person_gray).into(profileIv);

                    }catch (Exception e){}
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}