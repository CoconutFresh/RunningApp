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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private EditText emailField, passwordField;
    private TextView register,  forgotPassword;
    private Button login;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        register = (TextView) findViewById(R.id.new_user);
        register.setOnClickListener(this);

        forgotPassword = (TextView) findViewById(R.id.forgot_password);
        forgotPassword.setOnClickListener(this);

        emailField = (EditText) findViewById(R.id.lg_email);
        passwordField = (EditText) findViewById(R.id.lg_password);

        login = (Button) findViewById(R.id.lg_button);
        login.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_user:
                startActivity(new Intent(this, Register.class));
                break;
            case R.id.forgot_password:
                //TODO: Create password link
                break;
            case R.id.lg_button:
                //validate(username.getText().toString(), password.getText().toString()); //temporary
                login();
                break;
        }
    }

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
    //Garbage method for testing. Will delete later. Left it in case shit goes wrong with Firebase
    /*private void validate(String un, String pw) {
        if(un.equals("test") && pw.equals("test")) {

            //Switches over control of the app to the next activity
            startActivity(new Intent(this, HomePage.class));
            finish(); //Makes it so that the user can't use the back button to return to this page.
        }
    }*/
}