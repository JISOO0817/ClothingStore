package com.example.clothingstore.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clothingstore.FilterMarket;
import com.example.clothingstore.R;
import com.example.clothingstore.activities.MarketDetailActivity;
import com.example.clothingstore.models.ModelMarket;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterMarket extends RecyclerView.Adapter<AdapterMarket.HolderMarket> implements Filterable {

    private Context context;
    public ArrayList<ModelMarket> marketList,filterList;
    private FilterMarket filter;

    public AdapterMarket(Context context, ArrayList<ModelMarket> marketList) {
        this.context = context;
        this.marketList = marketList;
        this.filterList = marketList;
    }

    @NonNull
    @Override
    public HolderMarket onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_market,parent,false);

        return new HolderMarket(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderMarket holder, int position) {

        ModelMarket modelMarket = marketList.get(position);
        String accountType = modelMarket.getAccountType();
        String address1 = modelMarket.getAddress1();
        String address2 = modelMarket.getAddress2();
        final String uid = modelMarket.getUid();
        String email = modelMarket.getEmail();
        String name = modelMarket.getName();
        String marketName = modelMarket.getMarketName();
        String phone = modelMarket.getPhone();
        String deliveryFee = modelMarket.getDeliveryFee();
        String timestamp = modelMarket.getTimestamp();
        String online = modelMarket.getOnline();
        String marketOpen = modelMarket.getMarketOpen();
        String profileImage = modelMarket.getProfileImage();

        holder.marketNameTv.setText(marketName);
        holder.phoneTv.setText(phone);
        holder.addressTv.setText(address1);
        if(online.equals("true")){
            holder.onlineTv.setImageResource(R.drawable.shape_circle03);
        }else{
            holder.onlineTv.setImageResource(R.drawable.shape_circle04);
        }

        if(marketOpen.equals("true")){
            holder.marketClosedTv.setVisibility(View.GONE);
        }else{
            holder.marketClosedTv.setVisibility(View.VISIBLE);
        }

        try{
            Picasso.get().load(profileImage).placeholder(R.drawable.ic_store_gray).into(holder.marketIv);
        }catch (Exception e){
            holder.marketIv.setImageResource(R.drawable.ic_store_gray);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MarketDetailActivity.class);
                intent.putExtra("marketUid",uid);
                context.startActivity(intent);
            }
        });





    }

    @Override
    public int getItemCount() {
        return marketList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new FilterMarket(this,filterList);
        }

        return filter;
    }


    class HolderMarket extends RecyclerView.ViewHolder{

        ImageView marketIv,onlineTv,nextIv;
        TextView marketClosedTv,marketNameTv,phoneTv,addressTv;
        RatingBar ratingBar;

        public HolderMarket(@NonNull View itemView) {
            super(itemView);

            marketIv = itemView.findViewById(R.id.marketIv);
            onlineTv = itemView.findViewById(R.id.onlineTv);
            nextIv = itemView.findViewById(R.id.nextIv);
            marketClosedTv = itemView.findViewById(R.id.marketClosedTv);
            marketNameTv = itemView.findViewById(R.id.marketNameTv);
            phoneTv = itemView.findViewById(R.id.phoneTv);
            addressTv = itemView.findViewById(R.id.addressTv);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}
