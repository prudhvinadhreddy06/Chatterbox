package com.example.prudhvi.whatsapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private String receiverUserId ,sender_user_id,currentState;
    private TextView userprofilename,profilestatus;
    private Button send,declineMessageButton;
    private DatabaseReference userRef,friendRequestRef,contactsRef;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth=FirebaseAuth.getInstance();
        friendRequestRef=FirebaseDatabase.getInstance().getReference().child("Friend Request");
        contactsRef= FirebaseDatabase.getInstance().getReference().child("Contacts");

        receiverUserId=getIntent().getExtras().get("visit_user_id").toString();
        sender_user_id=mAuth.getCurrentUser().getUid();

        userprofilename=(TextView)findViewById(R.id.visit_user_name);
        profilestatus=(TextView)findViewById(R.id.visit_profile_status);
        send=(Button)findViewById(R.id.send_friend_request);
        declineMessageButton=(Button)findViewById(R.id.decline_friend_request);
        currentState="new";

        reteriveUserInfo();


    }

    private void reteriveUserInfo() {
        userRef.child(receiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                   String username=dataSnapshot.child("name").getValue().toString();
                   String userStatus=dataSnapshot.child("status").getValue().toString();

                   userprofilename.setText(username);
                   profilestatus.setText(userStatus);

                   manageChatRequest();
                }
                else
                {
                    String username=dataSnapshot.child("name").getValue().toString();
                    String userStatus=dataSnapshot.child("status").getValue().toString();

                    userprofilename.setText(username);
                    profilestatus.setText(userStatus);

                    manageChatRequest();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void manageChatRequest()
    {
        friendRequestRef.child(sender_user_id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(receiverUserId))
                        {
                            String request_type=dataSnapshot.child(receiverUserId).child("request_type").getValue().toString();
                            if(request_type.equals("sent"))
                            {
                                currentState="request_sent";
                                send.setText("Cancel Friend Request");
                            }
                            else if(request_type.equals("received"))
                            {
                                currentState="request_received";
                                send.setText("Accept Friend Request");
                                declineMessageButton.setVisibility(View.VISIBLE);
                                declineMessageButton.setEnabled(true);

                                declineMessageButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                      CancelFriendRequest();
                                    }
                                });

                            }
                        }
                        else
                        {
                            contactsRef.child(sender_user_id)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                           if(dataSnapshot.hasChild(receiverUserId))
                                           {
                                               currentState="friends";
                                               send.setText("Remove");
                                           }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

       if(!sender_user_id.equals(receiverUserId))
       {
          send.setOnClickListener( new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  send.setEnabled(false);

                  if(currentState.equals("new"))
                  {
                      sendFriendRequest();
                  }
                  if(currentState.equals("request_sent")) {
                      CancelFriendRequest();
                  }
                  if(currentState.equals("request_received"))
                  {
                      AcceptFriendRequest();
                  }
                  if(currentState.equals("friends"))
                  {
                      RemoveSpecificContacts();
                  }
              }
          });
       }
       else
       {
           send.setVisibility(View.INVISIBLE);
       }
    }

    private void RemoveSpecificContacts()
    {
        contactsRef.child(sender_user_id).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            contactsRef.child(receiverUserId).child(sender_user_id)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                send.setEnabled(true);
                                                currentState="new";
                                                send.setText("Send Request");

                                                declineMessageButton.setVisibility(View.INVISIBLE);
                                                declineMessageButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void AcceptFriendRequest()
    {
      contactsRef.child(sender_user_id).child(receiverUserId)
              .child("Contacts").setValue("Saved")
              .addOnCompleteListener(new OnCompleteListener<Void>() {
                  @Override
                  public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        contactsRef.child(receiverUserId).child(sender_user_id)
                                .child("Contacts").setValue("Saved")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                           friendRequestRef.child(sender_user_id).child(receiverUserId)
                                                   .removeValue()
                                                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                       @Override
                                                       public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful())
                                                            {
                                                                friendRequestRef.child(receiverUserId).child(sender_user_id)
                                                                        .removeValue()
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if(task.isSuccessful())
                                                                                {
                                                                                    send.setEnabled(true);
                                                                                    currentState="friends";
                                                                                    send.setText("Remove");
                                                                                    declineMessageButton.setVisibility(View.INVISIBLE);
                                                                                    declineMessageButton.setEnabled(false);
                                                                                }
                                                                            }
                                                                        });
                                                            }
                                                       }
                                                   });
                                        }
                                    }
                                });
                    }
                  }
              });
    }

    private void CancelFriendRequest()
    {
       friendRequestRef.child(sender_user_id).child(receiverUserId)
       .removeValue()
       .addOnCompleteListener(new OnCompleteListener<Void>() {
           @Override
           public void onComplete(@NonNull Task<Void> task) {
              if(task.isSuccessful())
              {
                  friendRequestRef.child(receiverUserId).child(sender_user_id)
                          .removeValue()
                          .addOnCompleteListener(new OnCompleteListener<Void>() {
                              @Override
                              public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    send.setEnabled(true);
                                    currentState="new";
                                    send.setText("Send Request");

                                    declineMessageButton.setVisibility(View.INVISIBLE);
                                    declineMessageButton.setEnabled(false);
                                }
                              }
                          });
              }
           }
       });
    }

    private void sendFriendRequest()
    {

        friendRequestRef.child(sender_user_id).child(receiverUserId)
                .child("request_type")
                .setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                        friendRequestRef.child(receiverUserId).child(sender_user_id)
                                .child("request_type").setValue("received")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {


                                        if (task.isSuccessful()) {
                                            send.setEnabled(true);
                                            currentState = "request_sent";
                                            send.setText("Cancel Friend Request");
                                        }
                                    }
                                });

                    }
                    }
                });
    }
}
