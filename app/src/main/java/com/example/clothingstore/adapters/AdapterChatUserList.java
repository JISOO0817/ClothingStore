package com.example.clothingstore.adapters;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.clothingstore.R;
import com.example.clothingstore.models.ModelUsers;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterChatUserList  extends RecyclerView.Adapter<AdapterChatUserList.ViewHolder>{


    private Context context;
    private List<ModelUsers> modelUsersList;

    public AdapterChatUserList(Context context, List<ModelUsers> modelUsersList) {
        this.context = context;
        this.modelUsersList = modelUsersList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_chat_user,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ModelUsers users = modelUsersList.get(position);

        String userName = users.getUserName();
        String userProfile = users.getUserImage();

        holder.userNameTv.setText(userName);

        try{
            Picasso.get().load(userProfile).placeholder(R.drawable.ic_person_gray).into(holder.userProfileIv);;

        }catch(Exception e){
           holder.userProfileIv.setImageResource(R.drawable.ic_person_gray);
        }

    }

    @Override
    public int getItemCount() {
        return modelUsersList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private CircularImageView userProfileIv;
        private TextView userNameTv;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            userProfileIv = itemView.findViewById(R.id.userProfileIv);
            userNameTv = itemView.findViewById(R.id.userNameTv);
        }
    }
}
