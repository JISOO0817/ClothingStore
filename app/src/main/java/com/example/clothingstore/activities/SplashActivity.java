package com.example.clothingstore.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.example.clothingstore.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //전체 화면

        requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_splash);

        auth = FirebaseAuth.getInstance();




        //2초 후 로그인 액티비티로 넘어감

       new Handler().postDelayed(new Runnable() {
           @Override
           public void run() {
               FirebaseUser user = auth.getCurrentUser();
               if(user==null){
                   //user not Togged in start login activity
                   startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                   finish();
               }
               else{
                    checkUserType();
               }
           }
       },1000);
    }


    private void checkUserType() {

        //판매자면 판매자 스크린, 구매자면 구매자 스크린
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(auth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String accountType = ""+snapshot.child("accountType").getValue();
                        if(accountType.equals("Seller")){

                            startActivity(new Intent(SplashActivity.this, SellerActivity.class));
                            finish();
                        }else {

                            startActivity(new Intent(SplashActivity.this, UserActivity.class));
                            finish();
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });


    }

}