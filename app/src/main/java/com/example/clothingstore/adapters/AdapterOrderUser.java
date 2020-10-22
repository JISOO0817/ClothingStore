package com.example.clothingstore.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clothingstore.R;
import com.example.clothingstore.activities.OrderDetailsUserActivity;
import com.example.clothingstore.models.ModelOrderUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;


public class AdapterOrderUser extends RecyclerView.Adapter<AdapterOrderUser.MyViewHolder> {

    private ArrayList<ModelOrderUser> modelOrderUserArrayList;
    private Context context;


    public AdapterOrderUser(ArrayList<ModelOrderUser> modelOrderUserArrayList, Context context) {
        this.modelOrderUserArrayList = modelOrderUserArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_order_user,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ModelOrderUser modelOrderUser = modelOrderUserArrayList.get(position);

        final String orderId = modelOrderUser.getOrderId();
        String orderBy = modelOrderUser.getOrderBy();
        String orderCost = modelOrderUser.getOrderCost();
        String orderStatus = modelOrderUser.getOrderStatus();
        String orderTime = modelOrderUser.getOrderTime();
        final String orderTo = modelOrderUser.getOrderTo();

        loadMarketInfo(modelOrderUser, holder);



       // holder.amountTv.setText("가격:" + orderCost);
        holder.statusTv.setText(orderStatus);
       // holder.orderIdTv.setText("주문 아이디: "+orderId);

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

        holder.dateTv.setText(formatedDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderDetailsUserActivity.class);
                intent.putExtra("orderTo",orderTo);
                intent.putExtra("orderId",orderId);
                context.startActivity(intent);
            }
        });
    }

    private void loadMarketInfo(ModelOrderUser modelOrderUser, final MyViewHolder holder) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(modelOrderUser.getOrderTo()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String marketName = ""+snapshot.child("marketName").getValue();
                holder.marketNameTv.setText(marketName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return modelOrderUserArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView orderIdTv,dateTv,marketNameTv,amountTv,statusTv;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            orderIdTv = itemView.findViewById(R.id.orderIdTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            marketNameTv = itemView.findViewById(R.id.marketNameTv);
            amountTv = itemView.findViewById(R.id.amountTv);
            statusTv = itemView.findViewById(R.id.statusTv);

        }
    }
}
