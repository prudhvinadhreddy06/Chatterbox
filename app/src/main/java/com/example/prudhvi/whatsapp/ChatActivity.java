package com.example.prudhvi.whatsapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private String messageReceiverId,getMessageReceiverName,messageSenderID;

    private TextView userName,userLastSeen;

    private Toolbar chatToolBar;

    private ImageButton sendmessagebutton;
    private EditText messageInput;

    private FirebaseAuth mAuth;
    private DatabaseReference Rootref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth=FirebaseAuth.getInstance();
        messageSenderID=mAuth.getCurrentUser().getUid();
        Rootref= FirebaseDatabase.getInstance().getReference();


        messageReceiverId=getIntent().getExtras().get("visit_user_id").toString();
        getMessageReceiverName=getIntent().getExtras().get("visit_user_name").toString();

        IntializeControllers();

        userName.setText(getMessageReceiverName);

        sendmessagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               sendMessage();
            }
        });

    }



    private void IntializeControllers()
    {

        chatToolBar=(Toolbar)findViewById(R.id.chat_toolbar);
        setSupportActionBar(chatToolBar);

        ActionBar actionBar =getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);


        LayoutInflater layoutInflater =(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView =layoutInflater.inflate(R.layout.custom_chat_bar,null);
        actionBar.setCustomView(actionBarView);

        userName =(TextView) findViewById(R.id.custom_profile_name);
        userLastSeen=(TextView)findViewById(R.id.custom_user_lastseen);

        sendmessagebutton=(ImageButton)findViewById(R.id.send_message_btn);
        messageInput=(EditText)findViewById(R.id.input_message);

    }
    private void sendMessage()
    {
        String messagetext=messageInput.getText().toString();

        if(TextUtils.isEmpty(messagetext))
        {
            Toast.makeText(this, "enter the essage", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String messageSenderRef ="Messages/"+messageSenderID+"/"+messageReceiverId;
            String messageReceiverRef ="Messages/"+messageReceiverId+"/"+messageSenderID;

            DatabaseReference userMessageRef=Rootref.child("Messages")
                    .child(messageSenderRef).child(messageReceiverRef).push();

            String messagePushId =userMessageRef.getKey();

            Map messageTextBody=new HashMap();
            messageTextBody.put("message",messagetext);
            messageTextBody.put("type","text");
            messageTextBody.put("from",messageSenderID);

            Map messageBodyDetails=new HashMap();
            messageBodyDetails.put(messageSenderRef+"/"+messagePushId,messageTextBody);
            messageBodyDetails.put(messageReceiverRef+"/"+messagePushId,messageTextBody);

            Rootref.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(ChatActivity.this, "Message sent Succesfully", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                    messageInput.setText("");
                }
            });



        }
    }
}
