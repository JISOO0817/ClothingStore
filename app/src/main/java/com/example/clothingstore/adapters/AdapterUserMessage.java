package com.example.clothingstore.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.clothingstore.R;
import com.example.clothingstore.models.ModelMessage;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterUserMessage extends RecyclerView.Adapter<AdapterUserMessage.MessageViewHolder> {

    private Context context;
    private ArrayList<ModelMessage> modelMessageArrayList;

    public AdapterUserMessage(Context context, ArrayList<ModelMessage> modelMessageArrayList) {
        this.context = context;
        this.modelMessageArrayList = modelMessageArrayList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_message_item,parent,false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {

        ModelMessage modelMessage = modelMessageArrayList.get(position);

        String sender = modelMessage.getSender();
        String receiver = modelMessage.getReceiver();
        String msg = modelMessage.getMsg();
        String time = modelMessage.getTime();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(time));
        String dateFormat = DateFormat.format("yyyy/MM/dd",calendar).toString();

        loadMessage(modelMessage,holder);

        holder.msgTv.setText(msg);
        holder.dateTv.setText(dateFormat);

    }

    private void loadMessage(ModelMessage modelMessage, MessageViewHolder holder) {


        String sender = modelMessage.getSender();


    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class MessageViewHolder extends RecyclerView.ViewHolder{

        private CircularImageView profileIv;
        private TextView nameTv,msgTv,dateTv;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            profileIv = itemView.findViewById(R.id.profileIv);
            nameTv = itemView.findViewById(R.id.nameTv);
            msgTv = itemView.findViewById(R.id.msgTv);
            dateTv = itemView.findViewById(R.id.dateTv);

        }
    }
}
