package com.example.clothingstore.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.clothingstore.R;
import com.example.clothingstore.activities.MessageWriteActivity;
import com.example.clothingstore.models.ModelMessage;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterMessage extends RecyclerView.Adapter<AdapterMessage.MessageViewHolder> {

    private Context context;
    private ArrayList<ModelMessage> modelMessageArrayList;
    private String receiver;
    private String sender;
    private String senderName;

    public AdapterMessage(Context context, ArrayList<ModelMessage> modelMessageArrayList) {
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
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {

        final ModelMessage modelMessage = modelMessageArrayList.get(position);

        receiver = modelMessage.getReceiver();
        String msg = modelMessage.getMsg();
        String time = modelMessage.getTime();
        String messageId = modelMessage.getMessageId();
        sender = modelMessage.getSender();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(time));
        String dateFormat = DateFormat.format("yyyy/MM/dd",calendar).toString();

        loadMessage(modelMessage,holder);

        holder.msgTv.setText(msg);
        holder.dateTv.setText(dateFormat);
        holder.nameTv.setText(sender);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             /*   Intent intent = new Intent(context, MessageWriteActivity.class);
                intent.putExtra("sender",sender);
                context.startActivity(intent);
            */
                sendSender(modelMessage,holder);
                
            }



        });


    }

    private void sendSender(ModelMessage modelMessage, final MessageViewHolder holder) {

        final String sender = modelMessage.getSender();
        Intent intent = new Intent(context,MessageWriteActivity.class);
        intent.putExtra("sender",sender);
        context.startActivity(intent);
    }


    private void loadMessage(ModelMessage modelMessage, final MessageViewHolder holder) {


        final String sender = modelMessage.getSender();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(sender).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String accountType = ""+snapshot.child("accountType").getValue();

                if(accountType.equals("Seller")){

                    String senderName = ""+snapshot.child("marketName").getValue();
                    String senderImage = ""+snapshot.child("profileImage").getValue();


                    holder.nameTv.setText(senderName);
                    try{
                        Picasso.get().load(senderImage).placeholder(R.drawable.ic_person_gray).into(holder.profileIv);
                    }catch (Exception e){}
                }else if(accountType.equals("User")){

                    String senderName = ""+snapshot.child("name").getValue();
                    String senderImage = ""+snapshot.child("profileImage").getValue();


                    holder.nameTv.setText(senderName);
                    try{
                        Picasso.get().load(senderImage).placeholder(R.drawable.ic_person_gray).into(holder.profileIv);
                    }catch (Exception e){}
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    @Override
    public int getItemCount() {
        return modelMessageArrayList.size();
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
