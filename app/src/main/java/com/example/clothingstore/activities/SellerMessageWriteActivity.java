package com.example.clothingstore.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.clothingstore.R;

public class SellerMessageWriteActivity extends AppCompatActivity {

    private CircularImageView profileIv;
    private TextView nameTv;
    private EditText msgEt;
    private Button cancleBtn,sendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_message_write);


        profileIv = findViewById(R.id.profileIv);
        nameTv = findViewById(R.id.nameTv);
        msgEt = findViewById(R.id.msgEt);
        cancleBtn = findViewById(R.id.cancelBtn);
        sendBtn = findViewById(R.id.sendBtn);



        cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}