package com.example.prudhvi.whatsapp;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPaswwordActivity extends AppCompatActivity {

    private EditText email;
    private Button send;
    private ProgressDialog loadingBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_paswword);

        send = (Button) findViewById(R.id.forgot_password_button);
        email = (EditText) findViewById(R.id.forgot_password_email);
        auth = FirebaseAuth.getInstance();
        loadingBar=new ProgressDialog(this);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPasswordLink();
            }
        });
    }

    private void resetPasswordLink() {
        String emailAddress;
        emailAddress = email.getText().toString();
        if (emailAddress.contentEquals("")) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Sending Reset Link");
            loadingBar.setMessage("Please wait ");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            auth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgotPaswwordActivity.this, "Reset Password has been sent to your mail", Toast.LENGTH_LONG).show();
                                loadingBar.dismiss();
                            }
                            else
                            {
                                Toast.makeText(ForgotPaswwordActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }
}
