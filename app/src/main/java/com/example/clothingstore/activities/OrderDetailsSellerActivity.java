package com.example.clothingstore.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.clothingstore.Constants;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class OrderDetailsSellerActivity extends AppCompatActivity {

    String orderId, orderBy;
    private ImageButton backBtn,editBtn;
    private TextView orderIdTv,dateTv,orderStatusTv,nameTv,emailTv,phoneTv,addressTv,amountTv,totalItemsTv,addressTv2;

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
        addressTv2 = findViewById(R.id.addressTv2);

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

        final String[] options = {"상품준비중","배송준비중","배송중","배송완료","취소"};
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
                    String message = "주문 상황이 변경되었어요!";
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(OrderDetailsSellerActivity.this, message, Toast.LENGTH_SHORT).show();
                        prepareNotificationMessage(orderId,message);
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
                String dateFormated = DateFormat.format("yyyy/MM/dd hh:mm a", calendar).toString();

                if(orderStatus.equals("상품준비중")){
                    orderStatusTv.setTextColor(getResources().getColor(R.color.colorGray02));
                }else if(orderStatus.equals("배송준비중")){
                    orderStatusTv.setTextColor(getResources().getColor(R.color.colorBlack));
                }else if(orderStatus.equals("배송중")){
                    orderStatusTv.setTextColor(getResources().getColor(R.color.colorPrimary));
                }else if(orderStatus.equals("배송완료")){
                    orderStatusTv.setTextColor(getResources().getColor(R.color.colorGreen));
                }
                else if(orderStatus.equals("취소")){
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
                addressTv2.setText(address2);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void prepareNotificationMessage(String orderId,String message){

        // 판매자가 주문 상황을 변경하면 구매자에게 알림이 감

        // 알림을 위한 데이터 준비

        String NOTIFICATION_TOPIC = "/topics/" + Constants.FCM_TOPIC; //
        String NOTIFICATION_TITLE = "알림이 도착했어요!";
        String NOTIFICATION_MESSAGE = ""+message;
        String NOTIFICATION_TYPE = "구매자에게";

        // json 준비 (어떻게 보내고 어디에 보낼지)

        JSONObject notiObject = new JSONObject();
        JSONObject notiBodyObject = new JSONObject();

        try{
            //무엇을 보낼건지

            notiBodyObject.put("notificationType",NOTIFICATION_TYPE);
            notiBodyObject.put("buyerUid",orderBy);
            notiBodyObject.put("sellerUid",auth.getUid());
            notiBodyObject.put("orderId",orderId);
            notiBodyObject.put("notificationTitle",NOTIFICATION_TITLE);
            notiBodyObject.put("notificationMessage",NOTIFICATION_MESSAGE);

            //어디에 보낼건지

            notiObject.put("to",NOTIFICATION_TOPIC);
            notiObject.put("data",notiBodyObject);


        }catch (Exception e){

        }

        sendFcmNotification(notiObject);




    }

    private void sendFcmNotification(JSONObject notiObject) {

        //vollery request 보냄

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notiObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //put required heades
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type","application/json");
                headers.put("Authorization","key="+Constants.FCM_KEY);

                return headers;
            }
        };

        //enque the volley request
        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }


}