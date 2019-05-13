package com.example.socialmediaclone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    CircleImageView foto;
    EditText username,fullname,country;
    Button save;
    FirebaseAuth mAuth;
    DatabaseReference userRef;
    String currentUserID;
    StorageReference mStorage;
    ProgressDialog progressDialog;
    final int galleryPick = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        initialize();
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        mStorage = FirebaseStorage.getInstance().getReference().child("Profile Image");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        progressDialog = new ProgressDialog(this);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savingUserInformation();
            }
        });
        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent =  new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent,"SELECT IMAGE"),1);
            }
        });
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.hasChild("profileimage")){
                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.with(SetupActivity.this).load(image).placeholder(R.drawable.profile).into(foto);
                    }else{
                        Toast.makeText(SetupActivity.this, "Please Select profile image", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            CropImage.activity(imageUri).setAspectRatio(1,1).start(this);

        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK){
                Uri resultUri = result.getUri();
                final StorageReference filepath = mStorage.child(currentUserID + " .jpg");

                progressDialog.setTitle("Uploading Image");
                progressDialog.setMessage("Uploading ... ");
                progressDialog.setCancelable(true);
                progressDialog.show();

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadUrl = uri.toString();
                                    userRef.child("profileimage").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                progressDialog.dismiss();
                                                Toast.makeText(SetupActivity.this,"Your picture Saved successfully",Toast.LENGTH_SHORT) .show();

                                            }else{
                                                progressDialog.dismiss();
                                                Toast.makeText(SetupActivity.this,"Problem occurred while tryng to save your picture..",Toast.LENGTH_SHORT) .show();
                                            }
                                        }
                                    });
                                }
                            });
                        }else{
                            Toast.makeText(SetupActivity.this,"Your picture did NOT saved",Toast.LENGTH_SHORT) .show();

                        }
                    }
                });
            }else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }
        }
    }

    public void initialize(){
        foto = (CircleImageView) findViewById(R.id.setup_profile_image);
        username = (EditText) findViewById(R.id.setup_username);
        fullname = (EditText) findViewById(R.id.setup_full_name);
        country = (EditText) findViewById(R.id.setup_country);
        save = (Button) findViewById(R.id.btnSave);

    }

    private void savingUserInformation(){
        String u = username.getText().toString();
        String f = fullname.getText().toString();
        String c = country.getText().toString();

        if(TextUtils.isEmpty(u)){
            Toast.makeText(this, "Please insert your username", Toast.LENGTH_SHORT).show();
        }if(TextUtils.isEmpty(f)){
            Toast.makeText(this, "Please insert your full name", Toast.LENGTH_SHORT).show();
        }if(TextUtils.isEmpty(c)){
            Toast.makeText(this, "Please insert your country", Toast.LENGTH_SHORT).show();
        }else{
            HashMap userMap = new HashMap();
            userMap.put("username",u);
            userMap.put("fullname",f);
            userMap.put("country",c);
            userMap.put("status","hey I Using your App");
            userMap.put("gender","none");
            userMap.put("dob","none");
            userMap.put("relationshipstatus","none");

            progressDialog.setTitle("Saving");
            progressDialog.setMessage("Please Wait ... ");
            progressDialog.setCancelable(true);
            progressDialog.show();

            userRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        progressDialog.dismiss();
                        sentToHomeActivity();
                        Toast.makeText(SetupActivity.this, "Your Account Succes registered", Toast.LENGTH_SHORT).show();
                    }else{
                        String message = task.getException().getMessage();
                        Toast.makeText(SetupActivity.this, "Error : "+ message, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });
        }
    }

    private void sentToHomeActivity(){
        Intent homeIntent = new Intent(SetupActivity.this,MainActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
        finish();
    }

}
