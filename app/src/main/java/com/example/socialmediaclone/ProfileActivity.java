package com.example.socialmediaclone;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    CircleImageView circleImageView;
    TextView nama,username,status,country,dob,relationship,gender;
    Toolbar mToolbar;
    FirebaseAuth mAuth;
    String currentUserID;
    DatabaseReference profileRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initialize();
    }
    private void initialize()
    {
        setToolbar();
        circleImageView = (CircleImageView) findViewById(R.id.profile_image);
        nama = (TextView) findViewById(R.id.profile_name);
        username = (TextView) findViewById(R.id.profile_username);
        status = (TextView) findViewById(R.id.profile_status);
        country = (TextView) findViewById(R.id.profile_country);
        dob = (TextView) findViewById(R.id.profile_dob);
        relationship = (TextView) findViewById(R.id.profile_relationship);
        gender = (TextView) findViewById(R.id.profile_gender);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        profileRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        profileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.child("profileimage").exists())
                    {
                        String Name = dataSnapshot.child("fullname").getValue().toString();
                        String Username = dataSnapshot.child("username").getValue().toString();
                        String Gender = dataSnapshot.child("gender").getValue().toString();
                        String Status = dataSnapshot.child("status").getValue().toString();
                        String Image = dataSnapshot.child("profileimage").getValue().toString();
                        String DOB = dataSnapshot.child("dob").getValue().toString();
                        String Country = dataSnapshot.child("country").getValue().toString();
                        String Relationship = dataSnapshot.child("relationshipstatus").getValue().toString();

                        nama.setText(Name);
                        username.setText(Username);
                        country.setText(Country);
                        dob.setText(DOB);
                        relationship.setText(Relationship);
                        gender.setText(Gender);
                        status.setText(Status);

                        Picasso.with(ProfileActivity.this).load(Image).placeholder(R.drawable.profile).into(circleImageView);
                    }
                    else
                    {
                        Toast.makeText(ProfileActivity.this, "Masukin foto lu dulu coy", Toast.LENGTH_SHORT).show();
                        Intent setupIntent = new Intent(ProfileActivity.this, SetupActivity.class);
                        startActivity(setupIntent);
                        finish();

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void setToolbar()
    {
        mToolbar = (Toolbar) findViewById(R.id.profile_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
}
