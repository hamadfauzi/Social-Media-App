package com.example.socialmediaclone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ResetPasswordActivity extends AppCompatActivity {

    Toolbar mToolbar;
    EditText email;
    Button sendEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        initialize();
    }

    private void initialize() {
        setToolbar();;
        email = (EditText) findViewById(R.id.editTextEmailResetPassword);
        sendEmail = (Button) findViewById(R.id.btnSendEmail);

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void setToolbar()
    {
        mToolbar = (Toolbar) findViewById(R.id.resetPassword_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Reset Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
}
