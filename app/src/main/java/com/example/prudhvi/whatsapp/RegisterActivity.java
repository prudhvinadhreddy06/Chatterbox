package com.example.prudhvi.whatsapp;

import android.accounts.AccountAuthenticatorActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText userEmail, userPassword;
    private Button createNewAccount;
    private TextView alreadyHaveAccount;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private DatabaseReference rootRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth=FirebaseAuth.getInstance();
        rootRef=FirebaseDatabase.getInstance().getReference();

        InitializeFields();

        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToLoginActivity();
            }
        });


        createNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
            }
        });
    }

    private void CreateNewAccount() {
        String email=userEmail.getText().toString();
        String password=userPassword.getText().toString();

        if(email.contentEquals("")||password.contentEquals(""))
        {
            Toast.makeText(this, "Enter both email and password", Toast.LENGTH_SHORT).show();
        }
        else
        {
             loadingBar.setTitle("Creating new Account");
             loadingBar.setMessage("Please wait while we are creating account for you");
             loadingBar.setCanceledOnTouchOutside(true);
             loadingBar.show();

             mAuth.createUserWithEmailAndPassword(email,password)
                     .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                         @Override
                         public void onComplete(@NonNull Task<AuthResult> task) {

                             if(task.isSuccessful())
                             {
                                 String currentUserID=mAuth.getCurrentUser().getUid();
                                 rootRef.child("Users").child(currentUserID).setValue("");
                                 SendUserToMainActivity();
                                 Toast.makeText(RegisterActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                                 loadingBar.dismiss();
                             }
                             else
                             {

                                 String message=task.getException().toString();
                                 Toast.makeText(RegisterActivity.this, "Error"+message, Toast.LENGTH_LONG).show();
                                 loadingBar.dismiss();


                             }
                         }
                     });
        }

    }

    private void SendUserToMainActivity() {
        Intent mainIntent =new Intent(RegisterActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }


    private void InitializeFields() {
        userEmail = (EditText) findViewById(R.id.register_email);
        userPassword = (EditText) findViewById(R.id.register_password);
        createNewAccount = (Button) findViewById(R.id.register_button);
        alreadyHaveAccount =(TextView) findViewById(R.id.already_have_account_link);
        loadingBar=new ProgressDialog(this);
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent =new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(loginIntent);
    }
}
