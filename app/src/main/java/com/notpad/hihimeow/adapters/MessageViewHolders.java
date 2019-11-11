package com.notpad.hihimeow.adapters;


import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.notpad.hihimeow.R;

public class MessageViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView mMessage;
    public LinearLayout mSimpleText;

    public MessageViewHolders(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mMessage = itemView.findViewById(R.id.tvMessage);
        mSimpleText = itemView.findViewById(R.id.llSimpleText);

    }

    @Override
    public void onClick(View v) {

    }

}
