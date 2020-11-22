package com.example.clothingstore.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.example.clothingstore.R;

public class UserMessageActivity extends AppCompatActivity {

    private TextView messageCountTv;
    private RecyclerView messageRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_message);

        messageCountTv = findViewById(R.id.messageCountTv);
        messageRv = findViewById(R.id.messageRv);

    }
}