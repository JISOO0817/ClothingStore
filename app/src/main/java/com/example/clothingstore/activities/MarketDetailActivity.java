package com.example.clothingstore.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.clothingstore.Constants;
import com.example.clothingstore.R;
import com.example.clothingstore.adapters.AdapterProductUser;
import com.example.clothingstore.models.ModelProduct;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MarketDetailActivity extends AppCompatActivity {

    private TextView marketNameTv,phoneTv,emailTv,openCloseTv,deliveryFeeTv,
            address1Tv,address2Tv,filteredProductsTv;
    private ImageView marketIv;
    private ImageButton callBtn, mapBtn,cartBtn,backBtn,filterBtn;
    private EditText searchProductEt;
    private RecyclerView productsRv;

    private String marketUid;
    private String marketName,marketEmail,marketPhone,marketAddress;
    private FirebaseAuth auth;

    private ArrayList<ModelProduct> productsList;
    private AdapterProductUser adapterProductUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_detail);


        marketNameTv = findViewById(R.id.marketNameTv);
        phoneTv = findViewById(R.id.phoneTv);
        emailTv = findViewById(R.id.emailTv);
        openCloseTv = findViewById(R.id.openCloseTv);
        deliveryFeeTv = findViewById(R.id.deliveryFeeTv);
        address1Tv = findViewById(R.id.address1Tv);
        address2Tv = findViewById(R.id.address2Tv);
        marketIv = findViewById(R.id.marketIv);
        filteredProductsTv = findViewById(R.id.filteredProductsTv);
        callBtn = findViewById(R.id.callBtn);
       // mapBtn = findViewById(R.id.mapBtn);
        cartBtn = findViewById(R.id.cartBtn);
        backBtn = findViewById(R.id.backBtn);
        filterBtn = findViewById(R.id.filterBtn);
        searchProductEt = findViewById(R.id.searchProductEt);
        productsRv = findViewById(R.id.productsRv);

        marketUid = getIntent().getStringExtra("marketUid");
        auth = FirebaseAuth.getInstance();
        loadMyInfo();
        loadMarketDetail();
        loadMarketProducts();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callPhone();
            }
        });



        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MarketDetailActivity.this);
                builder.setTitle("카테고리를 선택해 주세요:")
                        .setItems(Constants.categories1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String selected = Constants.categories1[which];
                                filteredProductsTv.setText(selected);
                                if (selected.equals("전체")) {
                                    loadMarketProducts();
                                } else {
                                    adapterProductUser.getFilter().filter(selected);
                                }
                            }
                        })
                        .show();
            }
        });



    }


    private void callPhone() {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+Uri.encode(marketPhone))));

    }

    private void loadMyInfo() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(auth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(DataSnapshot ds : snapshot.getChildren()){
                            String name = ""+ds.child("name").getValue();
                            String address1 = ""+ds.child("address1").getValue();
                            String address2 = ""+ds.child("address2").getValue();
                            String email = ""+ds.child("email").getValue();
                            String phone = ""+ds.child("phone").getValue();
                            String profileImage = ""+ds.child("profileImage").getValue();


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {


                    }
                });
    }

    private void loadMarketDetail() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(marketUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = ""+snapshot.child("name").getValue();
                marketName = ""+snapshot.child("marketName").getValue();
                marketEmail = ""+snapshot.child("email").getValue();
                marketPhone = ""+snapshot.child("phone").getValue();
                String deliveryFee = ""+snapshot.child("deliveryFee").getValue();
                String profileImage = ""+snapshot.child("profileImage").getValue();
                String marketOpen = ""+snapshot.child("marketOpen").getValue();
                String address1 = ""+snapshot.child("address1").getValue();
                String address2 = ""+snapshot.child("address2").getValue();
                marketNameTv.setText(marketName);
                emailTv.setText(marketEmail);
                deliveryFeeTv.setText(deliveryFee);
                address1Tv.setText(address1);
                address2Tv.setText(address2);
                phoneTv.setText(marketPhone);

                if(marketOpen.equals("true")){
                    openCloseTv.setText("마켓 오픈");
                }else{
                    openCloseTv.setText("마켓 닫음");
                }
                try{
                    Picasso.get().load(profileImage).into(marketIv);
                }catch (Exception e){

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadMarketProducts() {

        productsList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(marketUid).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        productsList.clear();
                        for(DataSnapshot ds:snapshot.getChildren()){
                            ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                            productsList.add(modelProduct);
                        }

                        adapterProductUser = new AdapterProductUser(MarketDetailActivity.this,productsList);
                        productsRv.setAdapter(adapterProductUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }




}