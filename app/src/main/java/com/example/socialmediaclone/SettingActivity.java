package com.example.socialmediaclone;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    EditText status,gender,name,username,relationship,country,date;
    Button update_account_setting;
    CircleImageView profile_image;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    StorageReference mStorage;
    DatabaseReference settingRef;
    String currentUserId;
    Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mStorage = FirebaseStorage.getInstance().getReference().child("Profile Image");
        mToolbar = (Toolbar) findViewById(R.id.setting_toolbar);
        setSupportActionBar(mToolbar);
        progressDialog = new ProgressDialog(SettingActivity.this);
        getSupportActionBar().setTitle("Account Setting");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        initialize();

        mAuth = FirebaseAuth.getInstance();
        currentUserId  = mAuth.getCurrentUser().getUid();
        settingRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        settingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    String coun = dataSnapshot.child("country").getValue().toString();
                    String dob = dataSnapshot.child("dob").getValue().toString();
                    String full = dataSnapshot.child("fullname").getValue().toString();
                    String gen = dataSnapshot.child("gender").getValue().toString();
                    String image = dataSnapshot.child("profileimage").getValue().toString();
                    String rela = dataSnapshot.child("relationshipstatus").getValue().toString();
                    String stat = dataSnapshot.child("status").getValue().toString();
                    String usern = dataSnapshot.child("username").getValue().toString();

                    username.setText(usern);
                    status.setText(stat);
                    country.setText(coun);
                    date.setText(dob);
                    name.setText(full);
                    gender.setText(gen);
                    relationship.setText(rela);

                    Picasso.with(SettingActivity.this).load(image).placeholder(R.drawable.profile).into(profile_image);

                    profile_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openGallery();
                        }
                    });

                    update_account_setting.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EventUpdateAccountSetting();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void EventUpdateAccountSetting() {



        final String Country = country.getText().toString();
        final String Username = username.getText().toString();
        final String FullName = name.getText().toString();
        final String Date = date.getText().toString();
        final String Relationship = relationship.getText().toString();
        final String Gender = gender.getText().toString();
        final String Status = status.getText().toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setTitle("Update Account Setting");
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                settingRef.child("username").setValue(Username);
                settingRef.child("country").setValue(Country);
                settingRef.child("fullname").setValue(FullName);
                settingRef.child("dob").setValue(Date);
                settingRef.child("relationshipstatus").setValue(Relationship);
                settingRef.child("gender").setValue(Gender);
                settingRef.child("status").setValue(Status);
                Toast.makeText(SettingActivity.this, "Account has been updated", Toast.LENGTH_SHORT).show();
                sentToHomeActivity();
            }
        });

        Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_green_dark);

    }


    private void openGallery() {
        Intent galleryIntent =  new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent,"SELECT IMAGE"),1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri).setAspectRatio(1, 1).start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                final StorageReference filepath = mStorage.child(currentUserId + " .jpg");

                progressDialog.setTitle("Uploading Image");
                progressDialog.setMessage("Uploading ... ");
                progressDialog.setCancelable(true);
                progressDialog.show();

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadUrl = uri.toString();
                                    settingRef.child("profileimage").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                Toast.makeText(SettingActivity.this, "Your picture Saved successfully", Toast.LENGTH_SHORT).show();

                                            } else {
                                                progressDialog.dismiss();
                                                Toast.makeText(SettingActivity.this, "Problem occurred while tryng to save your picture..", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        } else {
                            Toast.makeText(SettingActivity.this, "Your picture did NOT saved", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    private void initialize() {
        status = (EditText) findViewById(R.id.setting_profile_status);
        gender = (EditText) findViewById(R.id.setting_profile_Gender);
        name = (EditText) findViewById(R.id.setting_profile_name);
        username = (EditText) findViewById(R.id.setting_profile_username);
        relationship = (EditText) findViewById(R.id.setting_profile_relationship);
        country = (EditText) findViewById(R.id.setting_profile_country);
        date = (EditText) findViewById(R.id.setting_profile_Date_of_Birth);
        update_account_setting = (Button) findViewById(R.id.btn_update_account_setting);
        profile_image = (CircleImageView) findViewById(R.id.setting_profile_image);

    }

    private void sentToHomeActivity(){
        Intent homeIntent = new Intent(SettingActivity.this,MainActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
        finish();
    }
}
