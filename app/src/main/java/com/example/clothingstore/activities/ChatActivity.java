package com.example.clothingstore.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.clothingstore.R;
import com.example.clothingstore.adapters.AdapterMarket;
import com.example.clothingstore.adapters.AdapterMessage;
import com.example.clothingstore.models.ModelChat;
import com.example.clothingstore.models.ModelUsers;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private ImageView backBtn;
    private ImageButton sendBtn;
    private CircularImageView profileIv;
    private TextView userNameTv;
    private EditText sendEt;

    private String marketUid;

    private FirebaseUser user;

    private AdapterMessage adapterMessage;
    List<ModelChat> modelChatList;

    RecyclerView recyclerView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        backBtn = findViewById(R.id.backBtn);
        profileIv = findViewById(R.id.profileIv);
        userNameTv = findViewById(R.id.userNameTv);
        sendEt = findViewById(R.id.sendEt);
        sendBtn = findViewById(R.id.sendBtn);


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);






        user = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = getIntent();
        marketUid = intent.getStringExtra("marketUid");

        loadMarketInfo();



        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msg = sendEt.getText().toString().trim();
                long time = System.currentTimeMillis();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Long.parseLong(String.valueOf(time)));
                String dateFormated = DateFormat.format("hh:mm", calendar).toString();

                if(!msg.equals("")){
                    sendMessage(user.getUid(),marketUid,msg,dateFormated);
                }else{
                    Toast.makeText(ChatActivity.this, "메시지를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }

                sendEt.setText("");
            }
        });
    }

    private void sendMessage(String sender, String receiver, String msg, String time) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("msg",msg);
        hashMap.put("time",time);

        ref.child("Chats").push().setValue(hashMap);   //firebase에 생성(hashmap 담기)

    }


    private void loadMarketInfo() {


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(marketUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ModelUsers users = snapshot.getValue(ModelUsers.class);
                String marketName = ""+snapshot.child("marketName").getValue();
                String marketImage = ""+snapshot.child("profileImage").getValue();


                userNameTv.setText(marketName);

                try{

                    Picasso.get().load(marketImage).placeholder(R.drawable.ic_baseline_add_shopping_white).into(profileIv);;

                }catch(Exception e){
                    profileIv.setImageResource(R.drawable.ic_baseline_add_shopping_white);
                }


                readMessage(user.getUid(),marketUid,marketImage);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void readMessage(final String uid, final String marketUid, final String imageUrl) {

        modelChatList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chats");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelChatList.clear();

                for(DataSnapshot ds:snapshot.getChildren()){

                    ModelChat modelChat = ds.getValue(ModelChat.class);
                    if(modelChat.getReceiver().equals(uid) && modelChat.getSender().equals(marketUid) ||
                        modelChat.getReceiver().equals(marketUid) && modelChat.getSender().equals(uid)){

                        modelChatList.add(modelChat);
                        Log.d("mo", String.valueOf(modelChatList.size()));
                    }

                    adapterMessage = new AdapterMessage(ChatActivity.this,modelChatList,imageUrl);
                    recyclerView.setAdapter(adapterMessage);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}