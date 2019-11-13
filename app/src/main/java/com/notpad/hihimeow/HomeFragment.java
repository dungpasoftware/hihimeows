package com.notpad.hihimeow;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.notpad.hihimeow.adapters.MeowAdapter;
import com.notpad.hihimeow.utils.Meow;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private View view;

    private ArrayList<Meow> meows;
    private MeowAdapter meowAdapter;
    private int i;


    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser currMeow;


    ListView listView;

    SpaceNavigationView spaceNavigationView;

    public HomeFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currMeow = mAuth.getCurrentUser();


        meows = new ArrayList<>();

        checkMeowSex();



        meowAdapter = new MeowAdapter(view.getContext(), R.layout.item_card, meows );

        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) view.findViewById(R.id.frame);


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
                isConnectionMatch(meow);
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

            }
        });
        return view;

    }
    //check khi 2 người match vào nhau
    private void isConnectionMatch(final Meow meow) {
        DatabaseReference currMeowConnectionDb = mDatabase.child("Meows").child(currMeow.getUid()).child("connections").child("yep").child(meow.getMeowID());
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

                    showDialogMatched(meow);

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

                    String profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();

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

    public void showDialogMatched(final Meow meow){
        final Dialog dialog =  new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_match_success);
        dialog.setCanceledOnTouchOutside(false);
        TextView textNotify = dialog.findViewById(R.id.tvMatchedNotify);
        ImageView imageNotify = dialog.findViewById(R.id.imgMatchedNotify);
        Button sayHello = dialog.findViewById(R.id.btMatchedSayHello);
        Button dismiss = dialog.findViewById(R.id.btMatchedDismiss);

        textNotify.setText("Wow, you and "+ meow.getName()+" matched together");
        Glide.with(getActivity().getApplication()).load(meow.getProfileImageUrl()).into(imageNotify);

        sayHello.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MessageActivity.class);
                dialog.dismiss();
                Bundle b = new Bundle();
                b.putString("matchID", meow.getMeowID());
                b.putString("matchName", meow.getName());
                b.putString("matchImage", meow.getProfileImageUrl());
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


}

