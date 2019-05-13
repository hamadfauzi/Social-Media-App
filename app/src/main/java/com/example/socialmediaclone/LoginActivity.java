package com.example.socialmediaclone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    EditText email,password;
    TextView register;
    ImageView facebook,twitter,google;
    Button login;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleSignInClient;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialize();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(LoginActivity.this, "Connected to google sign in", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();


    }
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if(result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                Toast.makeText(this, "Please wait, while we are getting yout auth result....", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
            else
            {
                Toast.makeText(this, "Can't get Auth ", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            sentToHomeActivity();
                        } else {
                            sendUserToLoginActivity();
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Error : "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void sendUserToLoginActivity() {
        Intent homeIntent = new Intent(LoginActivity.this,LoginActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
        finish();
    }

    public void initialize(){
        email = (EditText) findViewById(R.id.loginEmail);
        password = (EditText) findViewById(R.id.loginPassword);
        register = (TextView) findViewById(R.id.textRegister);
        facebook = (ImageView) findViewById(R.id.loginWithFacebook);
        twitter = (ImageView) findViewById(R.id.loginWithTwitter);
        login = (Button) findViewById(R.id.btnLogin);
        google = (ImageView) findViewById(R.id.loginWithGoogle);
        TextView forgot = (TextView) findViewById(R.id.textForgotPassword);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        login.setOnClickListener(this);
        forgot.setOnClickListener(this);
        register.setOnClickListener(this);
        facebook.setOnClickListener(this);
        twitter.setOnClickListener(this);
        google.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnLogin:
                loginUserWithEmailandPassword();
                break;
            case R.id.loginWithFacebook:

                break;
            case R.id.textRegister:
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.loginWithGoogle:
                loginUserWithGoogle();
                break;
            case R.id.loginWithTwitter:

                break;
            case R.id.textForgotPassword:
                Intent resetIntent = new Intent(LoginActivity.this,ResetPasswordActivity.class);
                startActivity(resetIntent);
                finish();
                break;
        }
    }

    private void loginUserWithGoogle() {
        progressDialog.setTitle("Login Google");
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        signIn();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            sentToHomeActivity();
        }
    }

    private void loginUserWithEmailandPassword() {
        String e = email.getText().toString();
        String p = password.getText().toString();
        if(TextUtils.isEmpty(e)){
            Toast.makeText(this, "Insert Your Email", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(p)){
            Toast.makeText(this, "Insert Yout Password", Toast.LENGTH_SHORT).show();
        }else{
            progressDialog.setTitle("Login");
            progressDialog.setMessage("Please Wait ... ");
            progressDialog.setCancelable(true);
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(e,p)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                sentToHomeActivity();
                                progressDialog.dismiss();
                            }else{
                                String message = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, "Error : "+ message, Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
        }
    }

    private void sentToHomeActivity(){
        Intent homeIntent = new Intent(LoginActivity.this,MainActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
        finish();
    }

}
