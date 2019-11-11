package com.notpad.hihimeow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.notpad.hihimeow.adapters.MatchesAdapter;
import com.notpad.hihimeow.utils.Matches;

import java.util.ArrayList;
import java.util.List;

public class MatchesActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Matches> resultMatches;
    DatabaseReference mDatabase;

    private String currMeowID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);

        currMeowID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        resultMatches = new ArrayList<>();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(MatchesActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        getMeowMatchID();

        mAdapter = new MatchesAdapter(resultMatches, MatchesActivity.this);
        mRecyclerView.setAdapter(mAdapter);

    }

    private void getMeowMatchID() {


        mDatabase.child("Meows").child(currMeowID).child("connections").child("matches").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot match : dataSnapshot.getChildren()){
                        FetchMatchInfomation(match.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void FetchMatchInfomation(String key) {
        mDatabase.child("Meows").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String meowID = dataSnapshot.getKey();
                    String name = "";
                    String profileImageUrl = "";

                    if(dataSnapshot.child("name").getValue() != null){
                        name = dataSnapshot.child("name").getValue().toString();
                    }
                    if(dataSnapshot.child("profileImageUrl").getValue() != null){
                        profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
                    }
                    Matches matches = new Matches(meowID, name, profileImageUrl);
                    resultMatches.add(matches);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
