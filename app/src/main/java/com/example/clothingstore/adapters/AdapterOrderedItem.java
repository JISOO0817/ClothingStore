package com.example.clothingstore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clothingstore.R;
import com.example.clothingstore.models.ModelOrderedItem;

import java.util.ArrayList;

public class AdapterOrderedItem  extends RecyclerView.Adapter<AdapterOrderedItem.MyViewHolder>{


    private ArrayList<ModelOrderedItem> orderedItemsList;
    private Context context;

    public AdapterOrderedItem(ArrayList<ModelOrderedItem> orderedItemsList, Context context) {
        this.orderedItemsList = orderedItemsList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.row_ordereditem,parent,false);

       return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ModelOrderedItem modelOrderedItem = orderedItemsList.get(position);
        String getpId = modelOrderedItem.getpId();
        String name = modelOrderedItem.getName();
        String price = modelOrderedItem.getPrice();
        String priceEach = modelOrderedItem.getPriceEach();
        String quantity = modelOrderedItem.getQuantity();

        holder.itemTitleTv.setText(name);
        holder.itemPriceTv.setText(price+"원");
        holder.itemPriceEachTv.setText(priceEach+"원");
        holder.itemQuantityTv.setText("[" +quantity+"]");

    }

    @Override
    public int getItemCount() {
        return orderedItemsList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView itemTitleTv,itemPriceTv,itemPriceEachTv,itemQuantityTv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            itemTitleTv = itemView.findViewById(R.id.itemTitleTv);
            itemPriceTv = itemView.findViewById(R.id.itemPriceTv);
            itemPriceEachTv = itemView.findViewById(R.id.itemPriceEachTv);
            itemQuantityTv = itemView.findViewById(R.id.itemQuantityTv);

        }
    }
}