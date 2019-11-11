package com.notpad.hihimeow.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.notpad.hihimeow.R;
import com.notpad.hihimeow.utils.Message;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageViewHolders> {

    private List<Message> messageList;
    private Context context;

    public MessageAdapter(List<Message> messageList, Context context) {
        this.messageList = messageList;
        this.context = context;
    }

    @NonNull
    @Override
    public MessageViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, null, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(layoutParams);
        MessageViewHolders matchesViewHolders = new MessageViewHolders((layoutView));

        return matchesViewHolders;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolders holder, int position) {
        holder.mMessage.setText(messageList.get(position).getMessage());
        if(messageList.get(position).isMessOfCurrMeow()){
            holder.mMessage.setGravity(Gravity.END);
            holder.mMessage.setTextColor(Color.parseColor("#000000"));
            holder.mSimpleText.setBackgroundColor(Color.parseColor("#ffff91"));
        }else {
            holder.mMessage.setGravity(Gravity.START);
            holder.mMessage.setTextColor(Color.parseColor("#000000"));
            holder.mSimpleText.setBackgroundColor(Color.parseColor("#ffff91"));
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
