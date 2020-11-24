package com.example.clothingstore.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.clothingstore.Constants;
import com.example.clothingstore.R;
import com.example.clothingstore.adapters.AdapterCartItem;
import com.example.clothingstore.adapters.AdapterProductUser;
import com.example.clothingstore.adapters.AdapterReview;
import com.example.clothingstore.db.DBHelper;
import com.example.clothingstore.models.ModelCartItem;
import com.example.clothingstore.models.ModelMessage;
import com.example.clothingstore.models.ModelProduct;
import com.example.clothingstore.models.ModelReview;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class MarketDetailActivity extends AppCompatActivity {

    private TextView marketNameTv,phoneTv,emailTv,openCloseTv,deliveryFeeTv,
            address1Tv,address2Tv,filteredProductsTv,cartCountTv;
    private ImageView marketIv;
    private ImageButton callBtn,cart_Btn,backBtn,filterBtn,reviewShowBtn,chatBtn;
    private EditText searchProductEt;
    private RecyclerView productsRv;

    private RatingBar ratingBar;

    private String marketUid;
    private String marketName,marketEmail,marketPhone,marketAddress;
    public  String deliveryFee;
    private FirebaseAuth auth;
    private String myPhone;

    private ArrayList<ModelProduct> productsList;
    private AdapterProductUser adapterProductUser;

    private ArrayList<ModelCartItem> cartItemList;
    private AdapterCartItem adapterCartItem;

    private ProgressDialog progressDialog;

   // private EasyDB easyDB;

    private DBHelper dbHelper;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_detail);


        marketNameTv = findViewById(R.id.marketNameTv);
        phoneTv = findViewById(R.id.phoneTv);
        emailTv = findViewById(R.id.emailTv);
      //  openCloseTv = findViewById(R.id.openCloseTv);
       // deliveryFeeTv = findViewById(R.id.deliveryFeeTv);
        address1Tv = findViewById(R.id.address1Tv);
        address2Tv = findViewById(R.id.address2Tv);
        marketIv = findViewById(R.id.marketIv);
        filteredProductsTv = findViewById(R.id.filteredProductsTv);
        callBtn = findViewById(R.id.callBtn);
        cartCountTv = findViewById(R.id.cartCountTv);
       // mapBtn = findViewById(R.id.mapBtn);
        cart_Btn = findViewById(R.id.cart_Btn);
        backBtn = findViewById(R.id.backBtn);
        filterBtn = findViewById(R.id.filterBtn);
        searchProductEt = findViewById(R.id.searchProductEt);
        productsRv = findViewById(R.id.productsRv);
        reviewShowBtn = findViewById(R.id.reviewShowBtn);
        ratingBar = findViewById(R.id.ratingBar);
        chatBtn = findViewById(R.id.chatBtn);

        dbHelper =  new DBHelper(this);


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("잠시만 기다려 주세요...");
        progressDialog.setCanceledOnTouchOutside(false);

        marketUid = getIntent().getStringExtra("marketUid");
        auth = FirebaseAuth.getInstance();

        loadMyInfo();
        loadMarketDetail();
        loadMarketProducts();
        loadReview(); //ratingbar 평점 등록
        searchProductEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try{
                    adapterProductUser.getFilter().filter(s);
                }catch (Exception e){}
            }

            @Override
            public void afterTextChanged(Editable s) {

            }


        });



      /*  easyDB = EasyDB.init(this,"ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id", new String[]{"text", "unique"}))
                .addColumn(new Column("Item_PID", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Name", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price_Each", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Quantity", new String[]{"text", "not null"}))
                .doneTableColumn();*/

        // 마켓별로 다른 장바구니
        deleteCartData();
        cartCount();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        cart_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showCartDialog();
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

        reviewShowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reviewIntent = new Intent(MarketDetailActivity.this,MarketReviewsActivity.class);
                reviewIntent.putExtra("marketUid",marketUid);
                startActivity(reviewIntent);
            }
        });

        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*Intent chatIntent = new Intent(MarketDetailActivity.this,UserMessageWriteActivity.class);
                chatIntent.putExtra("marketUid",marketUid);
                startActivity(chatIntent);*/

                showMessageDialog();
            }
        });


    }

    private void showMessageDialog() {


        View view = LayoutInflater.from(this).inflate(R.layout.dialog_message,null);

        final CircularImageView profileIv = view.findViewById(R.id.profileIv);
        final TextView nameTv = view.findViewById(R.id.nameTv);
        final EditText msgEt = view.findViewById(R.id.msgEt);
        ImageButton cancleBtn = view.findViewById(R.id.cancelBtn);
        Button sendBtn = view.findViewById(R.id.sendBtn);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(marketUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String marketName = ""+snapshot.child("marketName").getValue();
                String marketImage = ""+snapshot.child("profileImage").getValue();

                nameTv.setText(marketName);
                try{
                    Picasso.get().load(marketImage).placeholder(R.drawable.ic_baseline_add_shopping_white).into(profileIv);
                }catch (Exception e){}

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);

        final AlertDialog dialog = builder.create();
        dialog.show();

        cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msg = msgEt.getText().toString();
                String timestamp = ""+System.currentTimeMillis();
                final String messageId = ""+System.currentTimeMillis();
                FirebaseUser user = auth.getCurrentUser();


                if(!msg.equals("")){

                    HashMap<String,Object> hashMap = new HashMap<>();
                    assert user != null;
                    hashMap.put("sender",user.getUid());
                    hashMap.put("receiver",marketUid);
                    hashMap.put("msg",msg);
                    hashMap.put("time",timestamp);
                    hashMap.put("messageId",messageId);

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Message");
                    ref.child(messageId).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Toast.makeText(getApplicationContext(), "쪽지를 전송했습니다.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "전송 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });

                }else{
                    Toast.makeText(getApplicationContext(), "빈 메세지 입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private float ratingSum = 0;
    private void loadReview() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(marketUid).child("Ratings")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ratingSum = 0;

                        for(DataSnapshot ds: snapshot.getChildren()){
                            float rating = Float.parseFloat(""+ds.child("rating").getValue());
                            ratingSum = ratingSum + rating;

                        }

                        long numberOfReviews = snapshot.getChildrenCount();
                        float avgRating = ratingSum/numberOfReviews;

                        ratingBar.setRating(avgRating);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void deleteCartData(){

       // easyDB.deleteAllDataFromTable(); //모든 장바구니 데이터 삭제

        dbHelper.deleteAllData();
    }

    public void cartCount(){
        //public 으로 해서 어댑터에서 접근할 수 있또록 해 줌

       // int count = easyDB.getAllData().getCount();

        int count = dbHelper.getAllData().size();
        if(count <=0){
            cartCountTv.setVisibility(View.GONE);
        }else{
            cartCountTv.setVisibility(View.VISIBLE);
            cartCountTv.setText(""+count);
        }
    }

    public double allTotalPrice = 0;
    public TextView sTotalTv,dFeeTv,allTotalPriceTv,sTotalLabelTv,dFeeLabelTv,totalLabelTv;


    private void showCartDialog() {


    //    Toast.makeText(context, "클릭", Toast.LENGTH_SHORT).show();

       View view = LayoutInflater.from(this).inflate(R.layout.dialog_cart,null);
        cartItemList = new ArrayList<>();

        TextView marketNameTv = view.findViewById(R.id.marketNameTv);
        RecyclerView cartItemsRv = view.findViewById(R.id.cartItemsRv);
        sTotalTv = view.findViewById(R.id.sTotalTv);
        sTotalLabelTv = view.findViewById(R.id.sTotalLabelTv);
        dFeeLabelTv = view.findViewById(R.id.dFeeLavelTv);
        totalLabelTv = view.findViewById(R.id.totalLabelTv);
        TextView totalTv = view.findViewById(R.id.totalTv);
        dFeeTv = view.findViewById(R.id.dFeeTv);
        allTotalPriceTv = view.findViewById(R.id.totalTv);
        Button checkoutBtn = view.findViewById(R.id.checkOutBtn);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);

        marketNameTv.setText(marketName);


      //  dbHelper.getAllData();

     /*   EasyDB easyDB = EasyDB.init(this,"ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id", new String[]{"text", "unique"}))
                .addColumn(new Column("Item_PID", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Name", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price_Each", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Quantity", new String[]{"text", "not null"}))
                .doneTableColumn();

        Cursor cursor = easyDB.getAllData();
        while(cursor.moveToNext()){
            String id = cursor.getString(1);
            String pId = cursor.getString(2);
            String name = cursor.getString(3);
            String price = cursor.getString(4);
            String cost = cursor.getString(5);
            String quantity = cursor.getString(6);



            ModelCartItem modelCartItem = new ModelCartItem(
                    ""+id,
                    ""+pId,
                    ""+name,
                    ""+price,
                    ""+cost,
                    ""+quantity
            );

            cartItemList.add(modelCartItem);
        }*/
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + com.example.clothingstore.db.Constants.TABLE_NAME ;
        Cursor cursor = database.rawQuery(selectQuery,null);
        int recodeCount = cursor.getCount();

        for(int i=0; i<recodeCount; i++){
            cursor.moveToNext();
            String id = cursor.getString(0);
            String pId = cursor.getString(1);
            String name = cursor.getString(2);
            String price = cursor.getString(3);
            String cost = cursor.getString(4);
            String quantity = cursor.getString(5);

            ModelCartItem modelCartItem = new ModelCartItem(
                    ""+id,
                    ""+pId,
                    ""+name,
                    ""+price,
                    ""+cost,
                    ""+quantity
            );

                cartItemList.add(modelCartItem);
                allTotalPrice = allTotalPrice + Double.parseDouble(cost);

            }



            cursor.close();


        adapterCartItem = new AdapterCartItem(this,cartItemList,dbHelper.getAllData());
        cartItemsRv.setAdapter(adapterCartItem);


        dFeeTv.setText(deliveryFee+"원");
        sTotalTv.setText(String.format("%.2f",allTotalPrice));
        allTotalPriceTv.setText((allTotalPrice+Double.parseDouble(deliveryFee.replace("원",""))+"원"));

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                allTotalPrice = 0;
            }
        });


            //주문버튼
        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(myPhone.equals("")||myPhone.equals("null")){
                    Toast.makeText(MarketDetailActivity.this, "프로필 휴대폰번호가 필요합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(cartItemList.size()  == 0 ){
                    Toast.makeText(MarketDetailActivity.this, "장바구니가 비었습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                    
                submitOrder();
            }
        });

    }

    private void submitOrder() {

        progressDialog.setMessage("주문 중입니다...");
        progressDialog.show();

        final String timestamp = ""+System.currentTimeMillis();

        String cost = allTotalPriceTv.getText().toString().trim().replace("원","");

        final HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("orderId", ""+timestamp);
        hashMap.put("orderTime",""+timestamp);
        hashMap.put("orderStatus","상품준비중"); // 진행, 완료, 취소   ->  상품준비중,배송준비중,배송중,배송완료,취소
        hashMap.put("orderCost",""+cost);
        hashMap.put("orderBy",""+auth.getUid());
        hashMap.put("orderTo",""+marketUid);


        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(marketUid).child("Orders");
        ref.child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        for(int i=0; i<cartItemList.size(); i++){
                            String pId = cartItemList.get(i).getpId();
                            String id = cartItemList.get(i).getId();
                            String cost = cartItemList.get(i).getPrice();
                            String price_each = cartItemList.get(i).getPrice_each();
                            String quantity = cartItemList.get(i).getQuantity();
                            String name = cartItemList.get(i).getName();

                            HashMap<String, String> hashMap1 = new HashMap<>();
                            hashMap1.put("pId",pId);
                            hashMap1.put("id",id);
                            hashMap1.put("cost",cost);
                            hashMap1.put("price_each",price_each);
                            hashMap1.put("quantity",quantity);
                            hashMap1.put("name",name);

                            ref.child(timestamp).child("Items").child(pId).setValue(hashMap1);
                        }

                        Toast.makeText(MarketDetailActivity.this, "주문이 완료되었습니다...", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                        prepareNotificationMessage(timestamp);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(MarketDetailActivity.this, "주문이 실패하였습니다...", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MarketDetailActivity.this,OrderDetailsUserActivity.class);
                intent.putExtra("orderTo",marketUid);
                intent.putExtra("orderId",timestamp);
                startActivity(intent);


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
                            myPhone = ""+ds.child("phone").getValue();
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
                deliveryFee = ""+snapshot.child("deliveryFee").getValue();
                String profileImage = ""+snapshot.child("profileImage").getValue();
                String marketOpen = ""+snapshot.child("marketOpen").getValue();
                String address1 = ""+snapshot.child("address1").getValue();
                String address2 = ""+snapshot.child("address2").getValue();
                marketNameTv.setText(marketName);
                emailTv.setText(marketEmail);
              //  deliveryFeeTv.setText(deliveryFee);
                address1Tv.setText(address1);
                address2Tv.setText(address2);
                phoneTv.setText(marketPhone);

               /* if(marketOpen.equals("true")){
                    openCloseTv.setText("마켓 오픈");
                }else{
                    openCloseTv.setText("마켓 닫음");
                }*/
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
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                            productsList.add(modelProduct);
                        }

                        adapterProductUser = new AdapterProductUser(MarketDetailActivity.this, productsList);
                        productsRv.setAdapter(adapterProductUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void prepareNotificationMessage(String orderId){
        // 구매자가 주문을 하면 알림이 판매자에게로 감

        // 알림을 위한 데이터 준비

        String NOTIFICATION_TOPIC = "/topics/" + Constants.FCM_TOPIC; //
        String NOTIFICATION_TITLE = "알림이 도착했어요!";
        String NOTIFICATION_MESSAGE = "새로운 주문이 도착했어요.";
        String NOTIFICATION_TYPE = "판매자에게";

        // json 준비 (어떻게 보내고 어디에 보낼지)

        JSONObject notiObject = new JSONObject();
        JSONObject notiBodyObject = new JSONObject();

        try{
            //무엇을 보낼건지

            notiBodyObject.put("notificationType",NOTIFICATION_TYPE);
            notiBodyObject.put("buyerUid",auth.getUid());
            notiBodyObject.put("sellerUid",marketUid);
            notiBodyObject.put("orderId",orderId);
            notiBodyObject.put("notificationTitle",NOTIFICATION_TITLE);
            notiBodyObject.put("notificationMessage",NOTIFICATION_MESSAGE);

            //어디에 보낼건지

            notiObject.put("to",NOTIFICATION_TOPIC);
            notiObject.put("data",notiBodyObject);


        }catch (Exception e){

        }

        sendFcmNotification(notiObject, orderId);




    }

    private void sendFcmNotification(JSONObject notiObject, final String orderId) {

        //vollery request 보냄

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notiObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // 알림 보내고 나서 detail activity 시작
                Intent intent = new Intent(MarketDetailActivity.this,OrderDetailsUserActivity.class);
                intent.putExtra("orderTo",marketUid);
                intent.putExtra("orderId",orderId);
                startActivity(intent);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //만약에 알림 보내는데에 실패하면
                Intent intent = new Intent(MarketDetailActivity.this,OrderDetailsUserActivity.class);
                intent.putExtra("orderTo",marketUid);
                intent.putExtra("orderId",orderId);
                startActivity(intent);
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

        //enque the volley request
        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }


}