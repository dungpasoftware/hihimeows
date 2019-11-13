package com.notpad.hihimeow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener fireAuthStateListener;
    private DatabaseReference currentUserDb;

    private Button mRegister;
    private EditText mEmail, mPassword, mName;
    private RadioGroup mSex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        currentUserDb = FirebaseDatabase.getInstance().getReference();
        fireAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null){
                    Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        mRegister = (Button) findViewById(R.id.btRegister);
        mEmail = (EditText) findViewById(R.id.etEmail);
        mPassword = (EditText) findViewById(R.id.etPassword);
        mName = (EditText) findViewById(R.id.etName);
        mSex = (RadioGroup) findViewById(R.id.rgSex);


        // xử lý khi bấm vào nút register
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectId = mSex.getCheckedRadioButtonId();

                final RadioButton radioButton = (RadioButton) findViewById(selectId);

                if(radioButton.getText() == null){
                    return;
                }
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                final String name = mName.getText().toString();
                final String gender = radioButton.getText().toString();
                final String imageUrl = gender.equals("Male") ? "https://firebasestorage.googleapis.com/v0/b/hihimeow-460fa.appspot.com/o/profileImages%2Fmale_cat.jpg?alt=media&token=ce38aef0-1720-4e6d-803d-118b9d804149"
                        : "https://firebasestorage.googleapis.com/v0/b/hihimeow-460fa.appspot.com/o/profileImages%2Ffemale_cat.jpg?alt=media&token=ec39a436-d9ef-407b-b7cb-025d05ed660a";
                //tạo account trên firebase bằng email
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(RegistrationActivity.this, "Sign Up Error", Toast.LENGTH_SHORT);
                        }else{
                            String userId = mAuth.getCurrentUser().getUid();
                            Map meowInfo = new HashMap<>();
                            meowInfo.put("name", name);
                            meowInfo.put("sex", gender );
                            meowInfo.put("profileImageUrl", imageUrl);

                            currentUserDb.child("Meows").child(userId).updateChildren(meowInfo);

                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(fireAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(fireAuthStateListener);
    }
}
