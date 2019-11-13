package com.notpad.hihimeow.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.notpad.hihimeow.R;
import com.notpad.hihimeow.utils.Matches;

import java.util.List;

public class MatchesAdapter  extends RecyclerView.Adapter<MatchesViewHolders> {

    private List<Matches> matchesList;
    private Context context;

    public MatchesAdapter(List<Matches> matchesList, Context context) {
        this.matchesList = matchesList;
        this.context = context;
    }

    @NonNull
    @Override
    public MatchesViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_matches, null, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(layoutParams);
        MatchesViewHolders matchesViewHolders = new MatchesViewHolders((layoutView));

        return matchesViewHolders;
    }

    @Override
    public void onBindViewHolder(@NonNull MatchesViewHolders holder, int position) {
        holder.mMatchId.setText((matchesList.get(position).getLastMessage()));
        holder.mMatchName.setText((matchesList.get(position).getMeowName()));
        String imageProfile = matchesList.get(position).getMeowImageProfile();
        holder.imageUrl = imageProfile;
        Glide.with(context).load(imageProfile).into(holder.mMatchImage);

    }

    @Override
    public int getItemCount() {
        return matchesList.size();
    }
}
