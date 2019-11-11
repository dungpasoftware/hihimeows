package com.notpad.hihimeow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.notpad.hihimeow.adapters.MeowAdapter;
import com.notpad.hihimeow.utils.Meow;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button mSignOut, mSettings, mMatches;

    private ArrayList<Meow> meows;
    private MeowAdapter meowAdapter;
    private int i;


    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser currMeow;

    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currMeow = mAuth.getCurrentUser();

        mSignOut = (Button) findViewById(R.id.btSignOut);
        mSettings = (Button) findViewById(R.id.btSettings);
        mMatches = (Button) findViewById(R.id.btMatches);

        meows = new ArrayList<>();


        mMatches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MatchesActivity.class);
                startActivity(intent);
                return;
            }
        });

        mSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return;
            }
        });

        checkMeowSex();



        meowAdapter = new MeowAdapter(this, R.layout.item_card, meows );

        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);


        flingContainer.setAdapter(meowAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                meows.remove(0);
                meowAdapter.notifyDataSetChanged();
            }
            @Override
            public void onLeftCardExit(Object dataObject) {
                Meow meow = (Meow) dataObject;
                String meowID = meow.getMeowID();
                mDatabase.child("Meows").child(meowID).child("connections").child("nope").child(currMeow.getUid()).setValue(true);

            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Meow meow = (Meow) dataObject;
                String meowID = meow.getMeowID();
                mDatabase.child("Meows").child(meowID).child("connections").child("yep").child(currMeow.getUid()).setValue(true);
                isConnectionMatch(meowID);
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here
            }

            @Override
            public void onScroll(float scrollProgressPercent) {

            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(MainActivity.this, "Click!", Toast.LENGTH_SHORT).show();
            }
        });

    }
    //check khi 2 người match vào nhau
    private void isConnectionMatch(String meowID) {
        DatabaseReference currMeowConnectionDb = mDatabase.child("Meows").child(currMeow.getUid()).child("connections").child("yep").child(meowID);
        currMeowConnectionDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    //tạo key phòng chat
                    String key = mDatabase.child("Messages").push().getKey();
                    //khi cả 2 người match vào nhau thì tạo key của mình sang bên đối phương, và tạo key của đối phương vào mình
                    mDatabase.child("Meows").child(dataSnapshot.getKey()).child("connections").child("matches").child(currMeow.getUid()).setValue(true);
                    mDatabase.child("Meows").child(currMeow.getUid()).child("connections").child("matches").child(dataSnapshot.getKey()).setValue(true);
                    //set key định nghĩa phòng chat chung của 2 đứa
                    mDatabase.child("Meows").child(dataSnapshot.getKey()).child("connections").child("matches").child(currMeow.getUid()).child("RoomID").setValue(key);
                    mDatabase.child("Meows").child(currMeow.getUid()).child("connections").child("matches").child(dataSnapshot.getKey()).child("RoomID").setValue(key);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private String currMeowSex;
    private  String oppositeMeowSex;
    public void checkMeowSex(){

        DatabaseReference meowDb = mDatabase.child("Meows").child(currMeow.getUid());
        meowDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getKey().equals(currMeow.getUid())){
                    if(dataSnapshot.exists()){
                        if(dataSnapshot.child("sex") != null){
                            currMeowSex = dataSnapshot.child("sex").getValue().toString();
                            oppositeMeowSex = "Female";
                            switch (currMeowSex){
                                case "Male":
                                    oppositeMeowSex = "Female";
                                    break;
                                case "Female":
                                    oppositeMeowSex = "Male";
                                    break;
                            }
                            //khi có giới tính của mình rồi thì bắt đầu vào hàm quét để lấy đối phương
                            findingCoupleMeows();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void findingCoupleMeows(){
        mDatabase.child("Meows").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //chỉ lấy khi nó không trong danh sách unlike và like và đặc biệt là phải khác giới
                if(dataSnapshot.exists() && !dataSnapshot.child("connections").child("nope").hasChild(currMeow.getUid()) &&
                        !dataSnapshot.child("connections").child("yep").hasChild(currMeow.getUid()) &&
                        dataSnapshot.child("sex").getValue().toString().equals(oppositeMeowSex)){
                    String profileImageUrl = "default";
                    if(!dataSnapshot.child("profileImageUrl").getValue().toString().equals("default")){
                        profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
                    }
                    meows.add(new Meow(dataSnapshot.getKey(), dataSnapshot.child("name").getValue().toString(),  profileImageUrl));
                    meowAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
