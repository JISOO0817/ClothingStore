package com.example.clothingstore.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.clothingstore.R;

public class OrderDetailsUserActivity extends AppCompatActivity {

    private String orderTo,orderBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details_user);


        Intent intent = getIntent();
        orderTo = intent.getStringExtra("orderTo");
        orderBy  = intent.getStringExtra("orderId");

    }
}