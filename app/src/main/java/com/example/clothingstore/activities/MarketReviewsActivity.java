package com.example.clothingstore.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.clothingstore.R;
import com.example.clothingstore.adapters.AdapterReview;
import com.example.clothingstore.models.ModelReview;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MarketReviewsActivity extends AppCompatActivity {

    private String marketUid;
    private ImageButton backBtn;
    private CircularImageView marketProfileIv;
    private TextView marketNameTv,ratingsTv;
    private RatingBar ratingBar;
    private RecyclerView reviewsRv;

    private ArrayList<ModelReview> reviewArrayList;
    private AdapterReview adapterReview;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_reviews);

        marketUid = getIntent().getStringExtra("marketUid");
        backBtn = findViewById(R.id.backBtn);
        marketProfileIv = findViewById(R.id.marketProfileIv);
        marketNameTv = findViewById(R.id.marketNameTv);
        ratingsTv = findViewById(R.id.ratingsTv);
        ratingBar = findViewById(R.id.ratingBar);
        reviewsRv  = findViewById(R.id.reviewsRv);

        auth = FirebaseAuth.getInstance();
        loadMarketDetails();
        loadReview();


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    private float ratingSum = 0;
    private void loadReview() {

        reviewArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(marketUid).child("Ratings")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        reviewArrayList.clear();
                        ratingSum = 0;

                        for(DataSnapshot ds: snapshot.getChildren()){
                            float rating = Float.parseFloat(""+ds.child("rating").getValue());
                            ratingSum = ratingSum + rating;

                            ModelReview modelReview = ds.getValue(ModelReview.class);
                            reviewArrayList.add(modelReview);
                        }

                        adapterReview = new AdapterReview(MarketReviewsActivity.this,reviewArrayList);
                        reviewsRv.setAdapter(adapterReview);

                        long numberOfReviews = snapshot.getChildrenCount();
                        float avgRating = ratingSum/numberOfReviews;

                        ratingsTv.setText(String.format("%.2f",avgRating)+"["+numberOfReviews+"]");
                        ratingBar.setRating(avgRating);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadMarketDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(marketUid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String marketName = ""+snapshot.child("marketName").getValue();
                        String profileImage = ""+snapshot.child("profileImage").getValue();

                        marketNameTv.setText(marketName);

                        try{
                            Picasso.get().load(profileImage).placeholder(R.drawable.ic_store_gray).into(marketProfileIv);

                        }catch (Exception e){

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }


}