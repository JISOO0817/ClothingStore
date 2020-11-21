package com.example.clothingstore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clothingstore.R;
import com.example.clothingstore.activities.MarketDetailActivity;
import com.example.clothingstore.db.DBHelper;
import com.example.clothingstore.models.ModelCartItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterCartItem  extends RecyclerView.Adapter<AdapterCartItem.HolderCartItem> {

    private Context context;
    private ArrayList<ModelCartItem> cartItems;

    DBHelper dbHelper;

    public AdapterCartItem(Context context, ArrayList<ModelCartItem> cartItemList, ArrayList<ModelCartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
        dbHelper = new DBHelper(context);

    }

    @NonNull
    @Override
    public HolderCartItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_cart_item,parent,false);


        return new HolderCartItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCartItem holder, final int position) {

        ModelCartItem modelCartItem = cartItems.get(position);

        final String id = modelCartItem.getId();
        final String pId = modelCartItem.getpId();
        String title = modelCartItem.getName();
        final String price = modelCartItem.getPrice();
        String price_each = modelCartItem.getPrice_each();
        String quantity = modelCartItem.getQuantity();


        holder.itemTitleTv.setText(""+title);
        holder.itemPriceTv.setText(""+price);
        holder.itemQuantityTv.setText("["+quantity+"]");
        holder.itemPriceEachTv.setText(""+price_each);



        holder.itemRemoveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  EasyDB easyDB = EasyDB.init(context,"ITEMS_DB")
                        .setTableName("ITEMS_TABLE")
                        .addColumn(new Column("Item_Id", new String[]{"text", "unique"}))
                        .addColumn(new Column("Item_PID", new String[]{"text", "not null"}))
                        .addColumn(new Column("Item_Name", new String[]{"text", "not null"}))
                        .addColumn(new Column("Item_Price_Each", new String[]{"text", "not null"}))
                        .addColumn(new Column("Item_Price", new String[]{"text", "not null"}))
                        .addColumn(new Column("Item_Quantity", new String[]{"text", "not null"}))
                        .doneTableColumn();

                easyDB.deleteRow(1,id);*/

                dbHelper.delete(id);

                Toast.makeText(context, "취소하였습니다.", Toast.LENGTH_SHORT).show();
                cartItems.remove(position);
                notifyItemChanged(position);
                notifyDataSetChanged();
                double tx = Double.parseDouble((((MarketDetailActivity)context).allTotalPriceTv.getText().toString().trim().replace("원","")));
                double totalPrice = tx - Double.parseDouble(price.replace("원",""));
                double deliveryFee = Double.parseDouble((((MarketDetailActivity)context).deliveryFee.replace("원","")));
                double sTotalPrice = Double.parseDouble(String.format("%.2f",totalPrice)) - Double.parseDouble(String.format("%.2f",deliveryFee));
                ((MarketDetailActivity)context).allTotalPrice=0.00;
                ((MarketDetailActivity)context).sTotalTv.setText(String.format("%.2f",sTotalPrice)+"원");
                ((MarketDetailActivity)context).allTotalPriceTv.setText(String.format("%.2f",Double.parseDouble(String.format("%.2f",totalPrice)))+"원");

                ((MarketDetailActivity)context).cartCount();



            }
        });


    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class HolderCartItem extends RecyclerView.ViewHolder{


        TextView itemTitleTv,itemPriceTv,itemPriceEachTv,itemQuantityTv,itemRemoveTv;


        public HolderCartItem(@NonNull View itemView) {
            super(itemView);

            itemTitleTv  = itemView.findViewById(R.id.itemTitleTv);
            itemPriceTv = itemView.findViewById(R.id.itemPriceTv);
            itemPriceEachTv = itemView.findViewById(R.id.itemPriceEachTv);
            itemQuantityTv = itemView.findViewById(R.id.itemQuantityTv);
            itemRemoveTv = itemView.findViewById(R.id.itemRemoveTv);
        }
    }
}
