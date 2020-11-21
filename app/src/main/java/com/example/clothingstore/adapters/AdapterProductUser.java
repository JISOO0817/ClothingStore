package com.example.clothingstore.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clothingstore.FilterProductUser;
import com.example.clothingstore.R;
import com.example.clothingstore.activities.MarketDetailActivity;
import com.example.clothingstore.db.Constants;
import com.example.clothingstore.db.DBHelper;
import com.example.clothingstore.models.ModelProduct;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterProductUser extends RecyclerView.Adapter<AdapterProductUser.HolderProductUser> implements Filterable {

    private Context context;
    public ArrayList<ModelProduct> productsList,filterList;
    private FilterProductUser filter;

    DBHelper dbHelper;


    public AdapterProductUser(Context context, ArrayList<ModelProduct> productList) {
        this.context = context;
        this.productsList = productList;
        this.filterList = productList;
        dbHelper = new DBHelper(context);

    }

    @NonNull
    @Override
    public HolderProductUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_product_user,parent,false);


        return new HolderProductUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductUser holder, int position) {

        final ModelProduct modelProduct = productsList.get(position);



        String discountAvailable = modelProduct.getDiscountAvailable();
        String discountNote = modelProduct.getDiscountNote();
        String discountPrice = modelProduct.getDiscountPrice();
        String productCategory = modelProduct.getProductCategory();
        String originalPrice = modelProduct.getOriginalPrice();
        String productDescription = modelProduct.getProductDescription();
        String productTitle = modelProduct.getProductTitle();
        String productQuantity = modelProduct.getProductQuantity();
        String productId = modelProduct.getProductId();
        String timestamp = modelProduct.getTimestamp();
        String productIcon = modelProduct.getProductIcon();

        holder.titleTv.setText(productTitle);

        holder.originalPriceTv.setText(originalPrice+"원");
        holder.discountedPriceTv.setText(discountPrice+"원");

        if(discountAvailable.equals("true")){
            holder.discountedPriceTv.setVisibility(View.VISIBLE);
            holder.rela_user.setBackgroundResource(R.drawable.shape_back);

            holder.originalPriceTv.setPaintFlags(holder.originalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }else{

            holder.discountedPriceTv.setVisibility(View.GONE);
        }

        try{
            Picasso.get().load(productIcon).placeholder(R.drawable.ic_baseline_add_shopping_primary).into(holder.productIconIv);
        }catch (Exception e){
            holder.productIconIv.setImageResource(R.drawable.ic_baseline_add_shopping_primary);
        }

        holder.showDetailTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDetailDialog(modelProduct);

            }
        });

    }

    private double cost = 0;
    private double finalCost = 0;
    private int quantity = 0;
    private void showDetailDialog(ModelProduct modelProduct) {
        // 레이아웃 inflate 해줌(다이얼로그)

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_quantity,null);

        //뷰 inflate
        final ImageView productIv = view.findViewById(R.id.productIv);
        final TextView titleTv = view.findViewById(R.id.titleTv);
        TextView pQuantityTv = view.findViewById(R.id.pQuantitiyTv);
        TextView descriptionTv = view.findViewById(R.id.descriptionTv);
        TextView discountedNoteTv = view.findViewById(R.id.discountedNoteTv);
        final TextView originalPriceTv = view.findViewById(R.id.originalPriceTv);
        TextView priceDiscountedTv = view.findViewById(R.id.priceDiscountedTv);
        final TextView finalPriceTv = view.findViewById(R.id.finalPriceTv);
        final TextView quantitiyTv = view.findViewById(R.id.quantitiyTv);
        ImageButton descrementBtn = view.findViewById(R.id.descrementBtn);
        ImageButton incrementBtn = view.findViewById(R.id.incrementBtn);
        Button continueBtn = view.findViewById(R.id.continueBtn);
        ImageButton closeBtn = view.findViewById(R.id.closeBtn);

        // model 에서 데이터 get

        final String productId = modelProduct.getProductId();
        String title = modelProduct.getProductTitle();
        String productQuantity= modelProduct.getProductQuantity();
        String description = modelProduct.getProductDescription();
        String discountNote = modelProduct.getDiscountNote();
        String icon = modelProduct.getProductIcon();
        final String price;
        if(modelProduct.getDiscountAvailable().equals("true")){
            price = modelProduct.getDiscountPrice();
            discountedNoteTv.setVisibility(View.VISIBLE);
            originalPriceTv.setPaintFlags(originalPriceTv.getPaintFlags()  | Paint.STRIKE_THRU_TEXT_FLAG);
        }else{
            price = modelProduct.getOriginalPrice();
            priceDiscountedTv.setVisibility(View.GONE);
            discountedNoteTv.setVisibility(View.GONE);

        }

        cost = Double.parseDouble(price.replaceAll("원",""));
        finalCost = Double.parseDouble(price.replaceAll("원",""));
        quantity = 1;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        try{
            Picasso.get().load(icon).placeholder(R.drawable.ic_cart_gray).into(productIv);
        }catch (Exception e){
            productIv.setImageResource(R.drawable.ic_cart_gray);
        }
        titleTv.setText(""+title);
        pQuantityTv.setText(""+productQuantity);
        descriptionTv.setText(""+description);
        discountedNoteTv.setText(""+discountNote);
        quantitiyTv.setText(""+quantity);
        originalPriceTv.setText(""+modelProduct.getOriginalPrice());
        priceDiscountedTv.setText(""+modelProduct.getDiscountPrice());
        finalPriceTv.setText(""+finalCost);

        final AlertDialog dialog = builder.create();
        dialog.show();

        incrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalCost +=  cost;
                quantity++;

                finalPriceTv.setText(finalCost+"원");
                quantitiyTv.setText(quantity+"");
            }
        });

        descrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(quantity>1){
                    finalCost -=cost;
                    quantity--;

                    finalPriceTv.setText(finalCost+"원");
                    quantitiyTv.setText(""+quantity);
                }
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemId++;
                String title = titleTv.getText().toString().trim();
                String priceEach = price;
                String totalPrice = finalPriceTv.getText().toString().trim().replace("원","");
                String quantity = quantitiyTv.getText().toString().trim();
                String timestamp = ""+System.currentTimeMillis();


                dbHelper.insertData(productId,title,priceEach,totalPrice,quantity);
                ((MarketDetailActivity)context).cartCount();
                Toast.makeText(context,"추가하였습니다.", Toast.LENGTH_SHORT).show();

              //  addToCart(productId,title,priceEach,totalPrice,quantity,image);


                dialog.dismiss();

            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });



    }
    private int itemId = 1;
    /*private void addToCart(String productId, String title, String priceEach, String totalPrice, String quantity, String image) {

        itemId++;

        EasyDB easyDB = EasyDB.init(context,"ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id", new String[]{"text", "unique"}))
                .addColumn(new Column("Item_PID", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Name", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price_Each", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Quantity", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Image", new String[]{"text", "not null"}))
                .doneTableColumn();

        Boolean b = easyDB.addData("Item_Id",itemId)
                .addData("Item_PID",productId)
                .addData("Item_Name",title)
                .addData("Item_Price_Each",priceEach)
                .addData("Item_Price",totalPrice)
                .addData("Item_Quantity",quantity)
                .addData("Item_Image",image)
                .doneDataAdding();

        Toast.makeText(context, "추가하였습니다.", Toast.LENGTH_SHORT).show();


        ((MarketDetailActivity)context).cartCount();

    }*/




    @Override
    public int getItemCount() {
        return productsList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new FilterProductUser(this,filterList);
        }
        return filter;
    }

    class HolderProductUser extends RecyclerView.ViewHolder{

        private ImageView productIconIv;
        private TextView titleTv,discountedPriceTv,originalPriceTv,showDetailTv;
        private RelativeLayout rela_user;

        public HolderProductUser(@NonNull View itemView) {

            super(itemView);

            productIconIv = itemView.findViewById(R.id.productIconIv);

            titleTv = itemView.findViewById(R.id.titleTv);
            discountedPriceTv = itemView.findViewById(R.id.discountedPriceTv);
            originalPriceTv = itemView.findViewById(R.id.originalPriceTv);
            showDetailTv = itemView.findViewById(R.id.showDetailTv);
            rela_user = itemView.findViewById(R.id.rela_user);
        }
    }
}
