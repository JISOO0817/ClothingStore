package com.example.clothingstore.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clothingstore.R;
import com.example.clothingstore.adapters.AdapterMarket;
import com.example.clothingstore.adapters.AdapterOrderUser;
import com.example.clothingstore.models.ModelMarket;
import com.example.clothingstore.models.ModelOrderUser;
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

import java.util.ArrayList;
import java.util.HashMap;

public class UserActivity extends AppCompatActivity {

    private TextView nameTv,emailTv,phoneTv,tabMarketsTv,tabOrdersTv;
    private ImageButton logoutBtn,editProfileBtn,settingBtn,chatBtn;
    private ImageView profileIv;
    private EditText searchMarketEt;
    private RelativeLayout marketsRl,ordersRl;
    private FirebaseAuth auth;
    ProgressDialog progressDialog;
    private RecyclerView marketsRv,ordersRv;

    private ArrayList<ModelMarket> marketsList;
    private AdapterMarket adapterMarket;

    private ArrayList<ModelOrderUser> ordersList;
    private AdapterOrderUser adapterOrderUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        nameTv = findViewById(R.id.nameTv);
        logoutBtn = findViewById(R.id.logoutBtn);
        editProfileBtn = findViewById(R.id.editProfileBtn);
        profileIv = findViewById(R.id.profileIv);
        emailTv = findViewById(R.id.emailTv);
        phoneTv = findViewById(R.id.phoneTv);
        tabMarketsTv = findViewById(R.id.tabMarketsTv);
        tabOrdersTv = findViewById(R.id.tabOrdersTv);
        marketsRl = findViewById(R.id.marketsRl);
        ordersRl = findViewById(R.id.ordersRl);
        marketsRv = findViewById(R.id.marketsRv);
        ordersRv = findViewById(R.id.ordersRv);
        searchMarketEt = findViewById(R.id.searchMarketsEt);
        settingBtn = findViewById(R.id.settingBtn);
        chatBtn = findViewById(R.id.chatBtn);


        auth = FirebaseAuth.getInstance();
        checkUser();

        showMarketsUI();
        searchMarketEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    adapterMarket.getFilter().filter(s);
                }catch (Exception e){

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("잠시만 기다려 주세요...");
        progressDialog.setCanceledOnTouchOutside(false);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                makeMeOffline();
            }
        });

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserActivity.this, ProfileEditUserActivity.class));
            }
        });

        tabMarketsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMarketsUI();
            }
        });

        tabOrdersTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrdersUI();
            }
        });

        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserActivity.this,PushSettingActivity.class));
            }
        });

        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserActivity.this,UserMessageActivity.class));
            }
        });
    }

    private void showMarketsUI() {

        marketsRl.setVisibility(View.VISIBLE);
        ordersRl.setVisibility(View.GONE);

        tabMarketsTv.setTextColor(getResources().getColor(R.color.colorBlack));
        tabMarketsTv.setBackgroundResource(R.drawable.shape_rect04);

        tabOrdersTv.setTextColor(getResources().getColor(R.color.colorWhite));
        tabOrdersTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));


    }

    private void showOrdersUI() {

        marketsRl.setVisibility(View.GONE);
        ordersRl.setVisibility(View.VISIBLE);

        tabOrdersTv.setTextColor(getResources().getColor(R.color.colorBlack));
        tabOrdersTv.setBackgroundResource(R.drawable.shape_rect04);

        tabMarketsTv.setTextColor(getResources().getColor(R.color.colorWhite));
        tabMarketsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));


    }

    private void makeMeOffline() {
            progressDialog.setMessage("로그아웃 중입니다...");

            HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put("online","false");

            //데이터 베이스 업데이트

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
            databaseReference.child(auth.getUid()).updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //update successfully

                            auth.signOut();
                            checkUser();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            progressDialog.dismiss();
                            Toast.makeText(UserActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

    }

    private void checkUser() {
        FirebaseUser user = auth.getCurrentUser();
        if(user == null){
            startActivity(new Intent(UserActivity.this, LoginActivity.class));
            finish();
        }else{
            loadMyInfo();
        }
    }

    private void loadMyInfo() {

        //addValueEventListener Firebase 데이터베이스에서 데이터 읽기기

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



                            nameTv.setText(name);
                            emailTv.setText(email);
                            phoneTv.setText(phone);

                            try{
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_gray).into(profileIv);
                            }catch (Exception e){
                                profileIv.setImageResource(R.drawable.ic_person_gray);
                            }

                            loadMarkets();
                            loadOrders();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {


                    }
                });
    }

    private void loadOrders() {

        ordersList = new ArrayList<>();

        // 주문 가져옴

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ordersList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    String uid = ""+ds.getRef().getKey();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Orders");
                    ref.orderByChild("orderBy").equalTo(auth.getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        for(DataSnapshot ds: snapshot.getChildren()){
                                            ModelOrderUser modelOrderUser = ds.getValue(ModelOrderUser.class);

                                            ordersList.add(modelOrderUser);
                                        }

                                        adapterOrderUser = new AdapterOrderUser(ordersList,UserActivity.this);

                                        ordersRv.setAdapter(adapterOrderUser);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadMarkets() {

        marketsList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("accountType").equalTo("Seller")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        marketsList.clear();
                        for(DataSnapshot ds:snapshot.getChildren()){
                            ModelMarket modelMarket = ds.getValue(ModelMarket.class);

                                marketsList.add(modelMarket);

                        }

                        adapterMarket = new AdapterMarket(UserActivity.this,marketsList);
                        marketsRv.setAdapter(adapterMarket);
                        adapterMarket.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}