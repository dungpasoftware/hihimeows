package com.notpad.hihimeow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private EditText mName, mPhone;
    private Button mBack, mConfirm;

    private ImageView mProfileImage;

    private FirebaseAuth mAuth;
    private DatabaseReference mMeowDatabase;

    private String meowID, name, phone, sex, profileImageUrl;

    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mName = (EditText) findViewById(R.id.etName);
        mPhone = (EditText) findViewById(R.id.etPhone);

        mProfileImage = (ImageView) findViewById(R.id.imgProfile);

        mConfirm = (Button) findViewById(R.id.btConfirm);
        mBack = (Button) findViewById(R.id.btBack);

        mAuth = FirebaseAuth.getInstance();
        meowID = mAuth.getCurrentUser().getUid();
        mMeowDatabase = FirebaseDatabase.getInstance().getReference().child("Meows").child(meowID);

        getMeowInfo();
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(SettingsActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(SettingsActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(SettingsActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                100);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                } else {
                    // Permission has already been granted
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, 1);
                }
            }
        });
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                return;
            }
        });
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMeowInfomation();
            }
        });
    }

    private void getMeowInfo() {
        mMeowDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("name") != null){
                        name = map.get("name").toString();
                        mName.setText(name);
                    }
                    if(map.get("phone") != null){
                        phone = map.get("phone").toString();
                        mPhone.setText(phone);
                    }
                    if(map.get("sex") != null){
                        sex = map.get("sex").toString();
                    }
                    if(map.get("profileImageUrl") != null){
                        profileImageUrl = map.get("profileImageUrl").toString();
                        switch (profileImageUrl){
                            case "default":
                                Glide.with(getApplication()).load(R.mipmap.ic_launcher).into(mProfileImage); break;
                            default: Glide.with(getApplication()).load(profileImageUrl).into(mProfileImage); break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveMeowInfomation() {
        name = mName.getText().toString();
        phone = mPhone.getText().toString();

        Map meowInfo = new HashMap();
        meowInfo.put("name", name);
        meowInfo.put("phone", phone);
        mMeowDatabase.updateChildren(meowInfo);
        //upload image to firestore
        if(resultUri != null){
            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profileImages").child(meowID);
            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map newImage = new HashMap();
                            newImage.put("profileImageUrl", uri.toString());
                            mMeowDatabase.updateChildren(newImage);

                            finish();
                            return;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            finish();
                            return;
                        }
                    });
                }
            });
        }else{
            finish();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            mProfileImage.setImageURI(resultUri);
        }

    }
}
