package com.example.clothingstore.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
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

import java.util.Calendar;
import java.util.HashMap;

public class UserMessageWriteActivity extends AppCompatActivity {

    private CircularImageView profileIv;
    private TextView nameTv;
    private EditText msgEt;
    private Button cancleBtn,sendBtn;
    private String marketUid;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_message_write);

        marketUid = getIntent().getStringExtra("marketUid");

        auth = FirebaseAuth.getInstance();

        profileIv = findViewById(R.id.profileIv);
        nameTv  = findViewById(R.id.nameTv);
        msgEt = findViewById(R.id.msgEt);
        cancleBtn  = findViewById(R.id.cancelBtn);
        sendBtn = findViewById(R.id.sendBtn);


        loadMarketInfo();


        cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = msgEt.getText().toString().trim();
                long time = System.currentTimeMillis();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Long.parseLong(String.valueOf(time)));
                String dateFormated = DateFormat.format("yyyy/MM/dd",calendar).toString();

                FirebaseUser user = auth.getCurrentUser();
                if(!msg.equals("")){
                    sendMessage(user.getUid(),marketUid,msg,dateFormated);
                }else{
                    Toast.makeText(UserMessageWriteActivity.this, "빈 메세지 입니다.", Toast.LENGTH_SHORT).show();
                }



            }
        });



    }

    private void sendMessage(String uid, String marketUid, String msg, String dateFormated) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender",uid);
        hashMap.put("receiver",marketUid);
        hashMap.put("msg",msg);
        hashMap.put("time",dateFormated);

        ref.child("Message").push().setValue(hashMap);
        Toast.makeText(this, "전송하였습니다.", Toast.LENGTH_SHORT).show();
        finish();
    }



    private void loadMarketInfo() {



        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(marketUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String marketName = ""+snapshot.child("marketName").getValue();
                String marketImage = ""+snapshot.child("profileImage").getValue();


                nameTv.setText(marketName);

                try{
                    Picasso.get().load(marketImage).placeholder(R.drawable.ic_person_gray).into(profileIv);

                }catch (Exception e){}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}