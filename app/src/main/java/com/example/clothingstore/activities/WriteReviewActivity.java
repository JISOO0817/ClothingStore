package com.example.clothingstore.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.clothingstore.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class WriteReviewActivity extends AppCompatActivity {

    private String marketUid;
    private CircularImageView marketIv;
    private TextView marketNameTv;
    private RatingBar ratingBar;
    private EditText reviewEt;
    private Button saveBtn;
    private ImageButton backBtn;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        marketUid = getIntent().getStringExtra("marketUid");

        auth = FirebaseAuth.getInstance();
        loadMarkeyInfo();
        loadMyReview();  //해당 마켓에 리뷰를 단 적이 있다면 가져옴


        marketIv = findViewById(R.id.marketIv);
        marketNameTv = findViewById(R.id.marketNameTv);
        ratingBar =findViewById(R.id.ratingBar);
        reviewEt = findViewById(R.id.reviewEt);
        saveBtn = findViewById(R.id.saveBtn);
        backBtn = findViewById(R.id.backBtn);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });


    }

    private void loadMarkeyInfo() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(marketUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String marketName = ""+snapshot.child("marketName").getValue();
                String marketImage = ""+snapshot.child("profileImage").getValue();

                marketNameTv.setText(marketName);

                try{
                    Picasso.get().load(marketImage).placeholder(R.drawable.ic_store_gray).into(marketIv);
                }catch (Exception e){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadMyReview() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(marketUid).child("Ratings").child(auth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        //리뷰를 이미 작성했다면
                        if(snapshot.exists()){

                            String uid = ""+snapshot.child("uid").getValue();
                            String rating = ""+snapshot.child("rating").getValue();
                            String review = ""+snapshot.child("review").getValue();
                            String timestamp = ""+snapshot.child("timestamp").getValue();

                            float myRating = Float.parseFloat(rating);
                            ratingBar.setRating(myRating);
                            reviewEt.setText(review);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void inputData() {
        String rating = ""+ratingBar.getRating();
        String review = reviewEt.getText().toString().trim();

        String timestamp = ""+System.currentTimeMillis();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid",""+auth.getUid());
        hashMap.put("rating",rating);
        hashMap.put("review",review);
        hashMap.put("timestamp",timestamp);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(marketUid).child("Ratings").child(auth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(WriteReviewActivity.this, "리뷰가 추가되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(WriteReviewActivity.this, "오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                    }
        });

    }
}