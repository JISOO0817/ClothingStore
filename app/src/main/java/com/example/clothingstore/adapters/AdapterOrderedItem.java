package com.example.clothingstore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clothingstore.R;
import com.example.clothingstore.models.ModelOrderedItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

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
        String cost = modelOrderedItem.getCost();
        String price_each = modelOrderedItem.getPrice_each();
        String quantity = modelOrderedItem.getQuantity();
        String itemImage = modelOrderedItem.getImage();


        holder.itemTitleTv.setText("상품명: " +name);
        holder.itemPriceEachTv.setText("개당 가격 : " + price_each+"원");
        holder.itemQuantityTv.setText("수량 : [" +quantity+"]");
        holder.itemPriceTv.setText("총 가격: " + cost + "원");

        try{
            Picasso.get().load(itemImage).placeholder(R.drawable.ic_person_gray).into(holder.itemImageIv);
        }catch (Exception e){

        }
    }



    @Override
    public int getItemCount() {
        return orderedItemsList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView itemTitleTv,itemPriceTv,itemPriceEachTv,itemQuantityTv;
        ImageView itemImageIv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            itemTitleTv = itemView.findViewById(R.id.itemTitleTv);
            itemPriceTv = itemView.findViewById(R.id.itemPriceTv);
            itemPriceEachTv = itemView.findViewById(R.id.itemPriceEachTv);
            itemQuantityTv = itemView.findViewById(R.id.itemQuantityTv);
            itemImageIv = itemView.findViewById(R.id.itemImageIv);


        }
    }
}
