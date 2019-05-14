package com.example.socialmediaclone;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    ImageButton sendImage,sendMessage;
    Toolbar mToolbar;
    EditText message;
    FirebaseAuth mAuth;
    DatabaseReference messageRefs;
    RecyclerView chatRecycle;
    private String userNameReceiver, ReceiverUserID,senderUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initialize();
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kirimPesan();
            }
        });
    }

    private void kirimPesan() {
        String pesan = message.getText().toString();
        if(TextUtils.isEmpty(pesan))
        {
            Toast.makeText(this, "Masukin pesan dulu", Toast.LENGTH_SHORT).show();
        }
        else
        {
          String message_sender_ref = "Messages/"+senderUserId+"/"+ReceiverUserID;
          String message_receiver_ref = "Messages/"+ReceiverUserID+"/"+senderUserId;

          DatabaseReference user_message_key = messageRefs
                  .child("Messages").child(senderUserId)
                  .child(ReceiverUserID).push();

          String message_push_id = user_message_key.getKey();

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            final String saveCurrentDate = currentDate.format(calendar.getTime());

            Calendar calendar1 = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
            final String saveCurrentTime = currentTime.format(calendar1.getTime());

            Map messageText = new HashMap();
            messageText.put("message",pesan);
            messageText.put("time",saveCurrentTime);
            messageText.put("time",saveCurrentTime);
            messageText.put("date",saveCurrentDate);
            messageText.put("type","text");
            messageText.put("from",senderUserId);

            Map messageDetails = new HashMap();
            messageDetails.put(message_sender_ref + "/" + message_push_id,messageText);
            messageDetails.put(message_receiver_ref +"/"+message_push_id,messageText);

            messageRefs.updateChildren(messageDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(ChatActivity.this, "Pesan terkirim", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        String mEroor = task.getException().getMessage();
                        Toast.makeText(ChatActivity.this, ""+mEroor, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void initialize() {
        ReceiverUserID = getIntent().getExtras().get("Postkey").toString();
        userNameReceiver = getIntent().getExtras().get("username").toString();
        sendImage = (ImageButton) findViewById(R.id.sendImage);
        sendMessage = (ImageButton) findViewById(R.id.sendMessage);
        message = (EditText) findViewById(R.id.input_message);
        chatRecycle = (RecyclerView) findViewById(R.id.message_list_user);
        messageRefs = FirebaseDatabase.getInstance().getReference();
        setToolbar();
        mAuth = FirebaseAuth.getInstance();
        senderUserId = mAuth.getCurrentUser().getUid();
        chatRecycle.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

    }

    private void setToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(userNameReceiver);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
}
