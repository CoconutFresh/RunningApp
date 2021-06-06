package com.example.runningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private EditText emailField, passwordField;
    private TextView register,  forgotPassword;
    private Button login;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    private GoogleSignInClient mGoogleSignInClient;
    private final int RC_SIGN_IN = 123;

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null) {
            Intent intent = new Intent(getApplicationContext(), HomePage.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        createGRequest();

        register = (TextView) findViewById(R.id.new_user);
        register.setOnClickListener(this);

        forgotPassword = (TextView) findViewById(R.id.forgot_password);
        forgotPassword.setOnClickListener(this);

        emailField = (EditText) findViewById(R.id.lg_email);
        passwordField = (EditText) findViewById(R.id.lg_password);

        login = (Button) findViewById(R.id.lg_button);
        login.setOnClickListener(this);

        findViewById(R.id.google_button).setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

    }

    private void createGRequest() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_user:
                startActivity(new Intent(this, Register.class));
                break;
            case R.id.forgot_password:
                startActivity(new Intent(this, ForgotPassword.class));
                break;
            case R.id.lg_button:
                login();
                startActivity(new Intent(Login.this, HomePage.class));
                break;
            case R.id.google_button:
                signInGoogle();
                break;
        }
    }
    // Google Sign In
    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // ...
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(getApplicationContext(), HomePage.class);
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Login.this, "Google Sign In failed. Please try again!", Toast.LENGTH_LONG).show();
                        }

                        // ...
                    }
                });
    }

    // Non Google Sign In
    private void login(){
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if(email.isEmpty()) {
            emailField.setError("Email is required!");
            emailField.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.setError("Email is invalid!");
            emailField.requestFocus();
            return;
        }

        if(password.isEmpty()) {
            passwordField.setError("Password is required!");
            passwordField.requestFocus();
            return;
        }

        if(password.length() < 6) {
            passwordField.setError("Minimum password length is 6 characters!");
            passwordField.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if(user.isEmailVerified()) {
                        // redirect to profile home
                        startActivity(new Intent(Login.this, HomePage.class));
                    }
                    else {
                        user.sendEmailVerification();
                        Toast.makeText(Login.this, "Email Verification required! Please check your email for the link", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
                else{
                    Toast.makeText(Login.this, "Login Failed! Please check your credentials.", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}