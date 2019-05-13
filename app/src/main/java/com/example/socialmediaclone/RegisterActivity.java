package com.example.socialmediaclone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    EditText email,password,confirmPassword;
    Button register;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        initialize();
        progressDialog = new ProgressDialog(this);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccountUser();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            sentToHomeActivity();
        }
    }

    private void sentToHomeActivity() {
        Intent homeIntent = new Intent(RegisterActivity.this,MainActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
        finish();
    }

    private void initialize() {
        email = (EditText) findViewById(R.id.registerEmail);
        password = (EditText) findViewById(R.id.registerPassword);
        confirmPassword = (EditText) findViewById(R.id.registerConfirmPassword);
        register = (Button) findViewById(R.id.btnCreateAccount);
    }
    private void createAccountUser(){
        String e = email.getText().toString();
        String pass = password.getText().toString();
        String cp = confirmPassword.getText() .toString();
        if(TextUtils.isEmpty(e)){
            Toast.makeText(getBaseContext(),"Please Enter Your Email ... ",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(pass)){
            Toast.makeText(getBaseContext(),"Please Enter Your Password",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(cp)){
            Toast.makeText(getBaseContext(),"Please confirm Your Password",Toast.LENGTH_SHORT).show();
        }else if(!(pass.equalsIgnoreCase(cp))){
            Toast.makeText(getBaseContext(),"Please doesnt Match",Toast.LENGTH_SHORT).show();
        }else{
            progressDialog.setTitle("Create Account");
            progressDialog.setMessage("Please Wait....");
            progressDialog.setCancelable(true);
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(e,pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                sentToSetupActivity();
                                Toast.makeText(getBaseContext(), "Register Susccess", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }else{
                                String message = task.getException().getMessage();
                                Toast.makeText(RegisterActivity.this, "Register Failed : "+message, Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
        }
    }
    private void sentToSetupActivity(){
        Intent intent = new Intent(RegisterActivity.this,SetupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
