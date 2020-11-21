package com.example.clothingstore.adapters;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clothingstore.R;
import com.example.clothingstore.models.ModelChatList;

import java.util.ArrayList;

public class AdapterChatList extends RecyclerView.Adapter<AdapterChatList.ViewHolder> {

    private Context context;
    private ArrayList<ModelChatList> modelChatLists;


    public AdapterChatList(Context context, ArrayList<ModelChatList> modelChatLists) {
        this.context = context;
        this.modelChatLists = modelChatLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_chat_list,parent,false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ModelChatList modelChat = modelChatLists.get(position);

        String name = modelChat.getName();

        holder.nameTv.setText(name);

    }

    @Override
    public int getItemCount() {
        return modelChatLists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView nameTv;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTv = itemView.findViewById(R.id.nameTv);

        }
    }
}
