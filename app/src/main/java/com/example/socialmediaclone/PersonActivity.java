package com.example.socialmediaclone;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonActivity extends AppCompatActivity {

    TextView userName,userUserName,userDOB,userRelationship,userGender,userCountry,userStatus;
    Button sendRequest,declineRequest;
    CircleImageView userImage;
    private String PostKey;
    private FirebaseAuth mAuth;
    private DatabaseReference PersonRef,FriendRequestRefs;
    private String senderID,receiverID,current_state;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        initialize();
        PersonRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    String username = dataSnapshot.child("username").getValue().toString();
                    String name = dataSnapshot.child("fullname").getValue().toString();
                    String dob = dataSnapshot.child("dob").getValue().toString();
                    String status = dataSnapshot.child("status").getValue().toString();
                    String gender = dataSnapshot.child("gender").getValue().toString();
                    String profileimage = dataSnapshot.child("profileimage").getValue().toString();
                    String relationshipstatus = dataSnapshot.child("relationshipstatus").getValue().toString();
                    String country = dataSnapshot.child("country").getValue().toString();

                    userName.setText(name);
                    userUserName.setText(username);
                    userDOB.setText(dob);
                    userStatus.setText(status);
                    userGender.setText(gender);
                    userCountry.setText(country);
                    userRelationship.setText(relationshipstatus);

                    Picasso.with(PersonActivity.this).load(profileimage).into(userImage);

                    aturButton();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        declineRequest.setVisibility(View.INVISIBLE);
        declineRequest.setEnabled(false);

        senderID = mAuth.getCurrentUser().getUid();
        if(!senderID.equals(PostKey))
        {
            sendRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendRequest.setEnabled(false);
                    if(current_state.equals("tidak_berteman"))
                    {
                        kirimPermintaanPertemanan();
                    }
                }
            });
        }
        else
        {
            declineRequest.setVisibility(View.INVISIBLE);
            sendRequest.setVisibility(View.INVISIBLE);
        }
    }

    private void aturButton() {
        FriendRequestRefs.child(senderID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(PostKey))
                        {
                            String request_type = dataSnapshot.child(PostKey).child("request_type").getValue().toString();
                            if(request_type.equals("sent"))
                            {
                                current_state = "request_sent";
                                sendRequest.setText("Cancel Friend request");

                                declineRequest.setVisibility(View.INVISIBLE);
                                declineRequest.setEnabled(false);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void kirimPermintaanPertemanan() {
        FriendRequestRefs.child(senderID).child(PostKey)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            FriendRequestRefs.child(PostKey).child(senderID)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                sendRequest.setEnabled(true);
                                                current_state = "request_sent";
                                                sendRequest.setText("Cancel Friend Request");

                                                declineRequest.setVisibility(View.INVISIBLE);
                                                declineRequest.setEnabled(false);

                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void initialize() {
        userCountry = (TextView) findViewById(R.id.person_profile_country);
        userName = (TextView) findViewById(R.id.person_profile_name);
        userUserName = (TextView) findViewById(R.id.person_profile_username);
        userDOB = (TextView) findViewById(R.id.person_profile_dob);
        userRelationship = (TextView) findViewById(R.id.person_profile_relationship);
        userGender = (TextView) findViewById(R.id.person_profile_gender);
        userStatus = (TextView) findViewById(R.id.person_profile_status);
        userImage = (CircleImageView) findViewById(R.id.person_profile_image);
        sendRequest = (Button) findViewById(R.id.sendFriendRequest);
        declineRequest = (Button) findViewById(R.id.declineFriendRequest);
        PostKey =  getIntent().getExtras().get("Postkey").toString();
        current_state = "tidak_berteman";
        mAuth = FirebaseAuth.getInstance();
        PersonRef = FirebaseDatabase.getInstance().getReference().child("Users").child(PostKey);
        FriendRequestRefs = FirebaseDatabase.getInstance().getReference().child("FriendRequest");


    }
}
