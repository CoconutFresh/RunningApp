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
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.CompletableFuture;

public class ForgotPassword extends AppCompatActivity {

    private EditText emailField;
    private TextView loginReturn;
    private Button resetButton;
    private ProgressBar progressBar;

    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailField = (EditText) findViewById(R.id.reset_password_email_field);
        loginReturn = (TextView) findViewById(R.id.login_link);
        resetButton = (Button) findViewById(R.id.reset_button);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();

        resetButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

        loginReturn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgotPassword.this, Login.class));
            }
        });
    }

    private void resetPassword() {
        String email = emailField.getText().toString().trim();

        if(email.isEmpty()) {
            emailField.setError("Email field is required!");
            emailField.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.setError("Email is invalid!");
            emailField.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ForgotPassword.this, "Please check your email for your password reset link!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ForgotPassword.this, Login.class));
                    finish();
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ForgotPassword.this, "Uh Oh Stinky, Something went wrong, please try again!", Toast.LENGTH_LONG).show();
                }
            }
        });


    }
}