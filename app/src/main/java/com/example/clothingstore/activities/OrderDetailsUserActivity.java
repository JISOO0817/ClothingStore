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
import android.widget.Button;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class OrderDetailsUserActivity extends AppCompatActivity {

    private String orderTo,orderId;
    private TextView orderIdTv,dateTv,orderStatusTv,marketNameTv,totalItemsTv,amountTv,addressTv;
    private ImageButton backBtn,writeReviewBtn;
    private RecyclerView itemsRv;
    private Button orderCancleBtn;


    private FirebaseAuth auth;

    private ArrayList<ModelOrderedItem> modelOrderedItemArrayList;
    private AdapterOrderedItem adapterOrderedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details_user);

        orderIdTv = findViewById(R.id.orderIdTv);
        dateTv = findViewById(R.id.dateTv);
        orderStatusTv = findViewById(R.id.orderStatusTv);
        marketNameTv = findViewById(R.id.marketNameTv);
        totalItemsTv = findViewById(R.id.totalItemsTv);
        amountTv = findViewById(R.id.amountTv);
        addressTv = findViewById(R.id.addressTv);
        backBtn = findViewById(R.id.backBtn);
        writeReviewBtn = findViewById(R.id.writeReviewBtn);
        itemsRv = findViewById(R.id.itemsRv);
        orderCancleBtn = findViewById(R.id.orderCancleBtn);


        Intent intent = getIntent();
        orderTo = intent.getStringExtra("orderTo");
        orderId  = intent.getStringExtra("orderId");

        auth = FirebaseAuth.getInstance();
     //   loadMarketInfo();
        loadOrderDetail();
        loadOrderItems();


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        writeReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent writeIntent = new Intent(OrderDetailsUserActivity.this,WriteReviewActivity.class);
                writeIntent.putExtra("marketUid", orderTo);
                startActivity(writeIntent);
            }
        });

        orderCancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareNotificationMessage(orderId);
                Toast.makeText(OrderDetailsUserActivity.this, "주문 취소를 요청했습니다.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void prepareNotificationMessage(String orderId) {

        String NOTIFICATION_TOPIC = "/topics" + Constants.FCM_TOPIC;
        String NOTIFICATION_TITLE = "알림이 도착했어요!";
        String NOTIFICATION_MESSAGE = "주문 취소 요청이 왔어요.";
        String NOTIFICATION_TYPE = "주문취소";

        JSONObject notiObject = new JSONObject();
        JSONObject notiBodyObject = new JSONObject();

        try{

            //무엇을 보낼건지

            notiBodyObject.put("notificationType",NOTIFICATION_TYPE);
            notiBodyObject.put("buyerUid",auth.getUid());
            notiBodyObject.put("sellerUid",orderTo);
            notiBodyObject.put("orderId",orderId);
            notiBodyObject.put("notificationTitle",NOTIFICATION_TITLE);
            notiBodyObject.put("notificationMessage",NOTIFICATION_MESSAGE);


            //어디에
            notiObject.put("to",NOTIFICATION_TOPIC);
            notiObject.put("data",notiBodyObject);

        }catch (Exception e){}

        sendFcmNotification(notiObject,orderId);



    }

    private void sendFcmNotification(JSONObject notiObject, String orderId) {

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
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "key="+ Constants.FCM_KEY);

                return headers;
            }
        };

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void loadOrderItems() {
        modelOrderedItemArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderTo).child("Orders").child(orderId).child("Items")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        modelOrderedItemArrayList.clear();

                        for(DataSnapshot ds:snapshot.getChildren()){
                            ModelOrderedItem modelOrderedItem = ds.getValue(ModelOrderedItem.class);

                            modelOrderedItemArrayList.add(modelOrderedItem);
                        }

                        adapterOrderedItem = new AdapterOrderedItem( modelOrderedItemArrayList,OrderDetailsUserActivity.this);
                        itemsRv.setAdapter(adapterOrderedItem);

                        totalItemsTv.setText(""+snapshot.getChildrenCount()+"개");

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void loadOrderDetail() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderTo).child("Orders").child(orderId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String orderBy = ""+snapshot.child("orderBy").getValue();
                        String orderCost = ""+snapshot.child("orderCost").getValue();
                        String orderId = ""+snapshot.child("orderId").getValue();
                        String orderStatus = ""+snapshot.child("orderStatus").getValue();
                        String orderTime = ""+snapshot.child("orderTime").getValue();
                        String orderTo = ""+snapshot.child("orderTo").getValue();
                       // String deliveryFee = ""+snapshot.child("deliveryFee").getValue();

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(Long.parseLong(orderTime));
                        String formateDate = DateFormat.format("yyyy/MM/dd hh:mm a",calendar).toString();

                        if(orderStatus.equals("진행중")){
                            orderStatusTv.setTextColor(getResources().getColor(R.color.colorPrimary));
                        }else if(orderStatus.equals("완료")){
                            orderStatusTv.setTextColor(getResources().getColor(R.color.colorGreen));
                        }else if(orderStatus.equals("취소")){
                            orderStatusTv.setTextColor(getResources().getColor(R.color.colorRed));
                        }

                        orderIdTv.setText(orderId);
                        orderStatusTv.setText(orderStatus);
                        amountTv.setText(orderCost+"원(배송료 포함)");
                        dateTv.setText(formateDate);



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    /*
    private void loadMarketInfo() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderTo).child("Orders").child("orderId")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String marketName = ""+snapshot.child("marketName").getValue();
                        marketNameTv.setText(marketName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }*/
}