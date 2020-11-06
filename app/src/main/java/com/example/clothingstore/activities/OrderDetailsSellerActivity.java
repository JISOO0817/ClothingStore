package com.example.clothingstore.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clothingstore.R;
import com.example.clothingstore.adapters.AdapterOrderedItem;
import com.example.clothingstore.models.ModelOrderedItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class OrderDetailsSellerActivity extends AppCompatActivity {

    String orderId, orderBy;
    private ImageButton backBtn,editBtn;
    private TextView orderIdTv,dateTv,orderStatusTv,nameTv,emailTv,phoneTv,addressTv,amountTv,totalItemsTv;

    private FirebaseAuth auth;

    private ArrayList<ModelOrderedItem> orderedItemsList;
    private AdapterOrderedItem adapterOrderedItem;
    private RecyclerView itemsRv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details_seller);

        // 데이터 가져옴
        orderId = getIntent().getStringExtra("orderId");
        orderBy = getIntent().getStringExtra("orderBy");


        backBtn = findViewById(R.id.backBtn);
        orderIdTv = findViewById(R.id.orderIdTv);
        dateTv = findViewById(R.id.dateTv);
        orderStatusTv = findViewById(R.id.orderStatusTv);
        nameTv = findViewById(R.id.nameTv);
        emailTv = findViewById(R.id.emailTv);
        phoneTv = findViewById(R.id.phoneTv);
        addressTv = findViewById(R.id.addressTv);
        amountTv = findViewById(R.id.amountTv);
        totalItemsTv = findViewById(R.id.totalItemsTv);
        itemsRv = findViewById(R.id.itemsRv);
        editBtn = findViewById(R.id.editBtn);

        auth = FirebaseAuth.getInstance();

        loadBuyerInfo();
        loadOrderInfo();
        loadOrderedItems();




        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editOrderStatus();
            }
        });


    }

    private void editOrderStatus() {

        final String[] options = {"진행중","완료","취소"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedOption = options[which];
                editOrderStatus2(selectedOption);
            }
        }).show();

    }

    private void editOrderStatus2(String selectedOption){

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("orderStatus",""+selectedOption);

        DatabaseReference ref  = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(auth.getUid()).child("Orders").child(orderId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(OrderDetailsSellerActivity.this, "변경 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OrderDetailsSellerActivity.this, "변경 실패하였습니다..", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void loadOrderedItems() {

        orderedItemsList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(auth.getUid()).child("Orders").child(orderId).child("Items").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderedItemsList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    ModelOrderedItem modelOrderedItem = ds.getValue(ModelOrderedItem.class);
                    orderedItemsList.add(modelOrderedItem);
                }

                adapterOrderedItem = new AdapterOrderedItem(orderedItemsList,OrderDetailsSellerActivity.this);
                itemsRv.setAdapter(adapterOrderedItem);
                totalItemsTv.setText("총 수량: " + snapshot.getChildrenCount());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void loadOrderInfo() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(auth.getUid()).child("Orders").child(orderId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String orderBy = ""+snapshot.child("orderBy").getValue();
                String orderCost = ""+snapshot.child("orderCost").getValue();
                String orderId = ""+snapshot.child("orderId").getValue();
                String orderStatus = ""+snapshot.child("orderStatus").getValue();
                String orderTo = ""+snapshot.child("orderTo").getValue();
                String orderTime = ""+snapshot.child("orderTime").getValue();

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Long.parseLong(orderTime));
                String dateFormated = DateFormat.format("yyyy/MM/dd", calendar).toString();

                if(orderStatus.equals("진행중")){
                    orderStatusTv.setTextColor(getResources().getColor(R.color.colorPrimary));
                }else if(orderStatus.equals("완료")){
                    orderStatusTv.setTextColor(getResources().getColor(R.color.colorGreen));
                }else if(orderStatus.equals("취소")){
                    orderStatusTv.setTextColor(getResources().getColor(R.color.colorRed));
                }

                orderIdTv.setText(orderId);
                orderStatusTv.setText(orderStatus);
                amountTv.setText("총 금액 : " +orderCost+"원 (배송비 포함)");
                dateTv.setText(dateFormated);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadBuyerInfo() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderBy).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = ""+snapshot.child("name").getValue();
                String email = ""+snapshot.child("email").getValue();
                String phone = ""+snapshot.child("phone").getValue();
                String address1 = ""+snapshot.child("address1").getValue();
                String address2 = ""+snapshot.child("address2").getValue();

                nameTv.setText(name);
                emailTv.setText(email);
                phoneTv.setText(phone);
                addressTv.setText(address1);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}