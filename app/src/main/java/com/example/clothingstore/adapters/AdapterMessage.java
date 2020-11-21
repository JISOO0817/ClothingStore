package com.example.clothingstore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.clothingstore.R;
import com.example.clothingstore.models.ModelChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class AdapterMessage  extends  RecyclerView.Adapter<AdapterMessage.MessageViewHolder>{

    private Context context;
    private List<ModelChat> modelChatList;
    private String imageUrl;

    public static final int MSG_LEFT = 0;
    public static final int MSG_RIGHT = 1;

    public AdapterMessage(Context context, List<ModelChat> modelChatList,String imageUrl) {
        this.context = context;
        this.modelChatList = modelChatList;
        this.imageUrl = imageUrl;

    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == MSG_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right,parent,false);
            return new MessageViewHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left,parent,false);
            return new MessageViewHolder(view);
        }
    }



    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {

        ModelChat chat = modelChatList.get(position);

        String msg = chat.getMsg();
        String time = chat.getTime();



        holder.msgTv.setText(msg);
        holder.timeTv.setText(time);


     /*   try{
            Picasso.get().load(image).placeholder(R.drawable.ic_person_gray).into(holder.profileIv);;

        }catch(Exception e){
            holder.profileIv.setImageResource(R.drawable.ic_person_gray);
        } */

    }

    @Override
    public int getItemCount() {
        return modelChatList.size();
    }


    class MessageViewHolder extends RecyclerView.ViewHolder{


        private CircularImageView profileIv;
        private TextView msgTv,timeTv;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            profileIv = itemView.findViewById(R.id.profileIv);
            msgTv = itemView.findViewById(R.id.msgTv);
            timeTv = itemView.findViewById(R.id.timeTv);
        }
    }

    @Override
    public int getItemViewType(int position) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(modelChatList.get(position).getSender().equals(user.getUid())){
            return MSG_RIGHT;
        }else{
            return MSG_LEFT;
        }
    }
}
