package com.example.prudhvi.whatsapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class ChatActivity extends AppCompatActivity {

    private String messageReceiverId,getMessageReceiverName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageReceiverId=getIntent().getExtras().get("visit_user_id").toString();
        getMessageReceiverName=getIntent().getExtras().get("visit_user_name").toString();

        Toast.makeText(ChatActivity.this,getMessageReceiverName,Toast.LENGTH_SHORT).show();

    }
}
