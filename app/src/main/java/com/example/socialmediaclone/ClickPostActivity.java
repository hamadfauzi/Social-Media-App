package com.example.socialmediaclone;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ClickPostActivity extends AppCompatActivity {

    ImageView editImage;
    TextView editTextView;
    Button editPost,deletePost;

    private String PostKey,currentID,userID,description,image;
    private FirebaseAuth mAuth;
    private DatabaseReference ClickPostRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);
        initialize();
        PostKey = getIntent().getExtras().get("Postkey").toString();
        ClickPostRef = FirebaseDatabase.getInstance().getReference().child("Post").child(PostKey);
        mAuth = FirebaseAuth.getInstance();
        currentID = mAuth.getCurrentUser().getUid();

        editPost.setVisibility(View.INVISIBLE);
        deletePost.setVisibility(View.INVISIBLE);

        ClickPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(dataSnapshot.exists()){
                   description = dataSnapshot.child("description").getValue().toString();
                   image = dataSnapshot.child("postimage").getValue().toString();
                   userID = dataSnapshot.child("uid").getValue().toString();

                   if(userID.equalsIgnoreCase(currentID)){

                       editPost.setVisibility(View.VISIBLE);
                       deletePost.setVisibility(View.VISIBLE);

                   }

                   editTextView.setText(description);
                   Picasso.with(ClickPostActivity.this).load(image).into(editImage);

                   editPost.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           btnEditEvent(description);
                       }
                   });
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initialize() {
        editImage = (ImageView) findViewById(R.id.editPostImage);
        editTextView = (TextView) findViewById(R.id.editPostDescription);
        editPost = (Button) findViewById(R.id.btnEditPost);
        deletePost = (Button) findViewById(R.id.btnDeletePost);



        deletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnDeleteEvent();
            }
        });
    }

    private void btnDeleteEvent()
    {
        ClickPostRef.removeValue();
        sentToHomeActivity();
        Toast.makeText(this, "Post has been Deleted", Toast.LENGTH_SHORT).show();

    }
    private void btnEditEvent(String d)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ClickPostActivity.this);
        builder.setTitle("Edit Post");

        final EditText inputField = new EditText(ClickPostActivity.this);
        inputField.setText(d);
        builder.setView(inputField);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ClickPostRef.child("description").setValue(inputField.getText().toString());
                Toast.makeText(ClickPostActivity.this, "Post has been updated", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_green_dark);

    }

    private void sentToHomeActivity(){
        Intent homeIntent = new Intent(ClickPostActivity.this,MainActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
        finish();
    }


}
