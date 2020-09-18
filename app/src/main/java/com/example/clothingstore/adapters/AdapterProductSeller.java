package com.example.clothingstore.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clothingstore.activities.EditProductActivity;
import com.example.clothingstore.FilterProduct;
import com.example.clothingstore.R;
import com.example.clothingstore.models.ModelProduct;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterProductSeller extends RecyclerView.Adapter<AdapterProductSeller.HolderProductSeller> implements Filterable {

    private Context context;
    public ArrayList<ModelProduct> productList,filterList;
    private FilterProduct filter;

    public AdapterProductSeller(Context context, ArrayList<ModelProduct> productList) {
        this.context = context;
        this.productList = productList;
        this.filterList = productList;
    }

    @NonNull
    @Override
    public HolderProductSeller onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_product_seller,parent,false);

        return new HolderProductSeller(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductSeller holder, int position) {
        final ModelProduct modelProduct = productList.get(position);
        String id = modelProduct.getProductId();
        String discountNote = modelProduct.getDiscountNote();
        String uid = modelProduct.getUid();
        String discountAvailable = modelProduct.getDiscountAvailable();
        String discountPrice = modelProduct.getDiscountPrice();
        String productCategory = modelProduct.getProductCategory();
        String productDescription = modelProduct.getProductDescription();
        String icon = modelProduct.getProductIcon();
        String quantity = modelProduct.getProductQuantity();
        String title = modelProduct.getProductTitle();
        String timestamp = modelProduct.getTimestamp();
        String originalPrice = modelProduct.getOriginalPrice();


        holder.titleTv.setText(title);
      //  holder.quantityTv.setText(quantity);
        holder.discountedPriceTv.setText(discountPrice+"won");
        holder.originalPriceTv.setText(originalPrice+"won");
        if(discountAvailable.equals("true")){
            holder.discountedPriceTv.setVisibility(View.VISIBLE);
            holder.rela.setBackgroundResource(R.drawable.shape_back);

            //취소선
            holder.originalPriceTv.setPaintFlags(holder.originalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }else{
            holder.discountedPriceTv.setVisibility(View.GONE);
            //holder.originalPriceTv.setPaintFlags(0);

        }

        try{
            Picasso.get().load(icon).placeholder(R.drawable.ic_baseline_add_shopping_primary).into(holder.productIconIv);
        }catch (Exception e){
            holder.productIconIv.setImageResource(R.drawable.ic_baseline_add_shopping_primary);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailsBottom(modelProduct);
            }
        });
    }

    private void detailsBottom(ModelProduct modelProduct) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.bs_product_details_seller,null);

        bottomSheetDialog.setContentView(view);


        final ImageButton backBtn = view.findViewById(R.id.backBtn);
        ImageButton deleteBtn = view.findViewById(R.id.deleteBtn);
        ImageButton editBtn = view.findViewById(R.id.editBtn);
        ImageView productIconIv = view.findViewById(R.id.productIconIv);
        TextView discountNoteTv = view.findViewById(R.id.discountNoteTv);
        TextView titleTv = view.findViewById(R.id.titleTv);
        TextView descriptionTv = view.findViewById(R.id.descriptionTv);
        TextView categoryTv = view.findViewById(R.id.categoryTv);
        TextView quantityTv = view.findViewById(R.id.quantityTv);
        TextView discountedPriceTv = view.findViewById(R.id.discountedPriceTv);
        TextView originalPriceTv = view.findViewById(R.id.originalPriceTv);

        final String id = modelProduct.getProductId();
        String discountNote = modelProduct.getDiscountNote();
        String uid = modelProduct.getUid();
        String discountAvailable = modelProduct.getDiscountAvailable();
        String discountPrice = modelProduct.getDiscountPrice();
        String productCategory = modelProduct.getProductCategory();
        String productDescription = modelProduct.getProductDescription();
        String icon = modelProduct.getProductIcon();
        String quantity = modelProduct.getProductQuantity();
        final String title = modelProduct.getProductTitle();
        String timestamp = modelProduct.getTimestamp();
        String originalPrice = modelProduct.getOriginalPrice();


        titleTv.setText(title);
        descriptionTv.setText(productDescription);
        categoryTv.setText(productCategory);
        quantityTv.setText(quantity);
        discountedPriceTv.setText(discountPrice);
        discountNoteTv.setText(discountNote);
        originalPriceTv.setText(originalPrice);

        if(discountAvailable.equals("true")){
            discountedPriceTv.setVisibility(View.VISIBLE);
            discountNoteTv.setVisibility(View.VISIBLE);
            //취소선
            originalPriceTv.setPaintFlags(originalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }else{
            discountedPriceTv.setVisibility(View.GONE);
            discountNoteTv.setVisibility(View.GONE);

        }
        try{
            Picasso.get().load(icon).placeholder(R.drawable.ic_baseline_add_shopping_primary).into(productIconIv);
        }catch (Exception e){
            productIconIv.setImageResource(R.drawable.ic_baseline_add_shopping_primary);
        }

        bottomSheetDialog.show();

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(context, EditProductActivity.class);
               intent.putExtra("productId",id);
               context.startActivity(intent);
                bottomSheetDialog.dismiss();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("삭제")
                        .setMessage(title+"상품을 삭제하시겠습니까?")
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteProduct(id); // 아이디 => 상품 아이디
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                bottomSheetDialog.dismiss();

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });


    }

    private void deleteProduct(String id) {

        FirebaseAuth  auth = FirebaseAuth.getInstance();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(auth.getUid()).child("Products").child(id).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public Filter getFilter() {

        if(filter == null){
            filter = new FilterProduct(this,filterList);
        }
        return filter;

    }

    class HolderProductSeller extends RecyclerView.ViewHolder{


        private ImageView productIconIv;
        private RelativeLayout rela;
        private TextView titleTv, quantityTv,discountedPriceTv,originalPriceTv;
        public HolderProductSeller(@NonNull View itemView) {
            super(itemView);

            productIconIv = itemView.findViewById(R.id.productIconIv);
            titleTv = itemView.findViewById(R.id.titleTv);
           // quantityTv = itemView.findViewById(R.id.quantityTv);
            discountedPriceTv = itemView.findViewById(R.id.discountedPriceTv);
            originalPriceTv = itemView.findViewById(R.id.originalPriceTv);
            rela = itemView.findViewById(R.id.rela);
        }
    }
}
