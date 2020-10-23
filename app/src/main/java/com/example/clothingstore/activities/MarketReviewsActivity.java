package com.example.clothingstore.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.clothingstore.R;

public class MarketReviewsActivity extends AppCompatActivity {

    private String marketUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_reviews);

        marketUid = getIntent().getStringExtra("marketUid");
    }
}