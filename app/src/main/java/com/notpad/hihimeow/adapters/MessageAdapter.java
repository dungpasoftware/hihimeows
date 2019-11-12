package com.notpad.hihimeow.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
            holder.mSimpleText.setGravity(Gravity.END);
            holder.mMessage.setBackgroundColor(Color.GRAY);
            holder.mMessage.setBackgroundResource(R.drawable.im_message_background);

        }else {
            holder.mSimpleText.setGravity(Gravity.START);
            holder.mMessage.setBackgroundResource(R.drawable.your_message_background);
            holder.mMessage.setTextColor(Color.BLACK);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
