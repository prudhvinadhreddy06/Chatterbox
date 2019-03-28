package com.example.prudhvi.whatsapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private Button update;
    private EditText username,status;
    private CircleImageView profile_Image;
    private  String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mAuth=FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();

        rootRef=FirebaseDatabase.getInstance().getReference();
        InitializeFields();

        retriveUserInfo();
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSettings();
            }
        });
    }




    private void InitializeFields() {
        update=(Button) findViewById(R.id.update_button);
        username=(EditText)findViewById(R.id.set_username);
        status=(EditText) findViewById(R.id.set_status);
        profile_Image=(CircleImageView) findViewById(R.id.set_profile_image);
    }

    private void updateSettings() {
        String setUserName=username.getText().toString();
        String setStatus=status.getText().toString();

        if(TextUtils.isEmpty(setUserName))
        {
            Toast.makeText(this, "Enter the username", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(setStatus))
        {
            Toast.makeText(this, "Enter the status", Toast.LENGTH_SHORT).show();
        }

        else
        {
            HashMap<String,String> profileMap =new HashMap<>();
            profileMap.put("uid",currentUserID);
            profileMap.put("name",setUserName);
            profileMap.put("status",setStatus);
            rootRef.child("Users").child(currentUserID).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {

                                SendUserToMainActivity();
                                Toast.makeText(SettingsActivity.this, "Profile Update is succesful", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                String error=task.getException().toString();
                                Toast.makeText(SettingsActivity.this, "Error: "+error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void SendUserToMainActivity() {
        Intent mainIntent =new Intent(SettingsActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void retriveUserInfo() {

        rootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.hasChild("name")&& dataSnapshot.hasChild("image"))
                {
                    String retriveUsername=dataSnapshot.child("name").getValue().toString();
                    String retriveUserStatus=dataSnapshot.child("status").getValue().toString();
                    String retriveUserProfileImage=dataSnapshot.child("image").getValue().toString();

                    username.setText(retriveUsername);
                    status.setText(retriveUserStatus);

                }
                else if(dataSnapshot.exists() && dataSnapshot.hasChild("name"))
                {
                    String retriveUsername=dataSnapshot.child("name").getValue().toString();
                    String retriveUserStatus=dataSnapshot.child("status").getValue().toString();


                    username.setText(retriveUsername);
                    status.setText(retriveUserStatus);
                }
                else
                {
                    Toast.makeText(SettingsActivity.this, "Update profile settings", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
