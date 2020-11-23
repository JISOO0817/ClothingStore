package com.example.clothingstore.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.clothingstore.R;
import com.example.clothingstore.activities.MarketReviewsActivity;
import com.example.clothingstore.models.ModelReview;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterReview extends RecyclerView.Adapter<AdapterReview.reviewViewHolder>{

    private Context context;
    private ArrayList<ModelReview> modelReviewArrayList;
    private FirebaseUser user;
    private String marketUid;

    public AdapterReview(Context context, ArrayList<ModelReview> modelReviewArrayList) {
        this.context = context;
        this.modelReviewArrayList = modelReviewArrayList;
    }

    @NonNull
    @Override
    public reviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_review,parent,false);

        user = FirebaseAuth.getInstance().getCurrentUser();

        Bundle bundle = ((MarketReviewsActivity)context).getIntent().getExtras();

        marketUid = bundle.getString("marketUid");


        return new reviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull reviewViewHolder holder, int position) {

        ModelReview modelReview = modelReviewArrayList.get(position);

        String rating = modelReview.getRating();
        String review = modelReview.getReview();
        String timestamp = modelReview.getTimestamp();
        String image = modelReview.getImage();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String dateFormat = DateFormat.format("yyyy/MM/dd",calendar).toString();

        loadUserDetail(modelReview,holder);

        holder.ratingBar.setRating(Float.parseFloat(rating));
        holder.reviewTv.setText(review);
        holder.dateTv.setText(dateFormat);

        if(image.equals("")){
            holder.imageIv.setVisibility(View.GONE);
        }else{
            try{
                Picasso.get().load(image).placeholder(R.drawable.ic_phone_gray).into(holder.imageIv);
                holder.imageIv.setVisibility(View.VISIBLE);
            }catch (Exception e){}
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Toast.makeText(context, "리뷰답변하기", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showReviewWriteDialog() {
    }

    private void loadUserDetail(ModelReview modelReview, final reviewViewHolder holder) {

        String uid = modelReview.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = ""+snapshot.child("name").getValue();
                        String profileImage = ""+snapshot.child("profileImage").getValue();

                        holder.nameTv.setText(name);

                        try{
                            Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_gray).into(holder.profileIv);

                        }catch (Exception e){

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return modelReviewArrayList.size();
    }

    class reviewViewHolder extends RecyclerView.ViewHolder{

        private CircularImageView profileIv;
        private TextView nameTv,dateTv,reviewTv;
        private RatingBar ratingBar;
        private ImageView imageIv;


        public reviewViewHolder(@NonNull View itemView) {
            super(itemView);

            profileIv = itemView.findViewById(R.id.profileIv);
            nameTv = itemView.findViewById(R.id.nameTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            reviewTv = itemView.findViewById(R.id.reviewTv);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            imageIv = itemView.findViewById(R.id.imageIv);

        }
    }
}
