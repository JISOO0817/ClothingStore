package com.example.clothingstore.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.clothingstore.R;

public class ChatListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);


        recyclerView = findViewById(R.id.chatRv);


        Intent userIntent = getIntent();
        String name = userIntent.getStringExtra("name");



    }
}