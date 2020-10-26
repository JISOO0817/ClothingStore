package com.example.clothingstore.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clothingstore.R;
import com.example.clothingstore.models.ModelOrderSeller;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterOrderSeller extends RecyclerView.Adapter<AdapterOrderSeller.AdapterOrderSellerViewHolder>{

    public ArrayList<ModelOrderSeller> modelOrderSellerArrayList;
    private Context context;


    public AdapterOrderSeller(Context context, ArrayList<ModelOrderSeller> modelOrderSellerArrayList) {
        this.context = context;
        this.modelOrderSellerArrayList = modelOrderSellerArrayList;
    }

    @NonNull
    @Override
    public AdapterOrderSellerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_order_seller,parent,false);

        return new AdapterOrderSellerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterOrderSellerViewHolder holder, int position) {

        ModelOrderSeller modelOrderSeller = new ModelOrderSeller();

        String orderId = modelOrderSeller.getOrderId();
        String orderTime = modelOrderSeller.getOrderTime();
        String orderStatus = modelOrderSeller.getOrderStatus();
        String orderCost = modelOrderSeller.getOrderCost();
        String orderBy = modelOrderSeller.getOrderBy();
        String orderTo = modelOrderSeller.getOrderTo();

        //구매자 정보
        loadUserInfo(modelOrderSeller,holder);

        holder.amountTv.setText(orderCost+"원");
        holder.statusTv.setText(orderStatus);

        if(orderStatus.equals("진행중")){
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }else if(orderStatus.equals("완료")){
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.colorGreen));
        }else if(orderStatus.equals("취소")){
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.colorRed));
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(orderTime));
        String formatedDate = DateFormat.format("yyyy/MM/dd",calendar).toString();


        holder.orderDateTv.setText(formatedDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void loadUserInfo(ModelOrderSeller modelOrderSeller, final AdapterOrderSellerViewHolder holder) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        //modelOrderSeller.getOrderBy() 사용자 uid 포함
        ref.child(modelOrderSeller.getOrderBy())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String email = ""+snapshot.child("email").getValue();
                        holder.emailTv.setText(email);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return modelOrderSellerArrayList.size();
    }

    class AdapterOrderSellerViewHolder extends RecyclerView.ViewHolder{


        private TextView orderIdTv,orderDateTv,emailTv,amountTv,statusTv;
        private ImageView nextIv;


        public AdapterOrderSellerViewHolder(@NonNull View itemView) {
            super(itemView);

            orderIdTv = itemView.findViewById(R.id.orderIdTv);
            orderDateTv = itemView.findViewById(R.id.orderDateTv);
            emailTv = itemView.findViewById(R.id.emailTv);
            amountTv = itemView.findViewById(R.id.amountTv);
            statusTv = itemView.findViewById(R.id.statusTv);
            nextIv = itemView.findViewById(R.id.nextIv);
        }
    }
}
