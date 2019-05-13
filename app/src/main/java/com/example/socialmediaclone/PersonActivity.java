package com.example.socialmediaclone;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
    private DatabaseReference PersonRef;

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

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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

        mAuth = FirebaseAuth.getInstance();
        PersonRef = FirebaseDatabase.getInstance().getReference().child("Users").child(PostKey);

        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        declineRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}
