package com.notpad.hihimeow.adapters;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.notpad.hihimeow.MessageActivity;
import com.notpad.hihimeow.R;

public class MatchesViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView mMatchId, mMatchName;
    public ImageView mMatchImage;
    public String imageUrl;
    public MatchesViewHolders(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mMatchId = (TextView) itemView.findViewById(R.id.tvMatchesId);
        mMatchName = (TextView) itemView.findViewById(R.id.tvMatchesName);
        mMatchImage = (ImageView) itemView.findViewById(R.id.imgMatchesImage);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), MessageActivity.class);
        Bundle b = new Bundle();
        b.putString("matchID", mMatchId.getText().toString());
        b.putString("matchName", mMatchName.getText().toString());
        b.putString("matchImage", imageUrl);
        intent.putExtras(b);
        v.getContext().startActivity(intent);
    }

}
