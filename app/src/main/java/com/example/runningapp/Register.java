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
import com.google.firebase.database.FirebaseDatabase;

 public class Register extends AppCompatActivity implements View.OnClickListener {
    private TextView banner, registerUser, loginLink;
    private EditText fNameField, lNameField, birthdayField, usernameField, emailField, passwordField;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        banner = (TextView) findViewById(R.id.register_banner);
        banner.setOnClickListener(this);

        registerUser = (Button) findViewById(R.id.rg_button);
        registerUser.setOnClickListener(this);

        fNameField = (EditText) findViewById(R.id.user_firstname);
        lNameField = (EditText) findViewById(R.id.user_lastname);
        birthdayField = (EditText) findViewById(R.id.birthday_date);
        usernameField = (EditText) findViewById(R.id.rg_username);
        emailField = (EditText) findViewById(R.id.rg_email);
        passwordField = (EditText) findViewById(R.id.rg_password);

        loginLink = (TextView) findViewById(R.id.login_link);
        loginLink.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

     @Override
     public void onClick(View v) {
        switch (v.getId()) {
            case R.id.banner:
                startActivity(new Intent(this, Register.class));
                break;
            case R.id.rg_button:
                registerUser();
                break;
            case R.id.login_link:
                startActivity(new Intent(this, Login.class));
                break;
        }
     }

     private void registerUser() {
        String fName = fNameField.getText().toString().trim();
        String lName = lNameField.getText().toString().trim();
        String birthday = birthdayField.getText().toString().trim();
        String username = usernameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if(fName.isEmpty()) {
            fNameField.setError("First Name is required!");
            fNameField.requestFocus();
            return;
        }

        if(lName.isEmpty()) {
            lNameField.setError("Last Name is required!");
            lNameField.requestFocus();
            return;
        }

        if(birthday.isEmpty()) {
            birthdayField.setError("Date of Birth is required!");
            birthdayField.requestFocus();
            return;
        }

        if(username.isEmpty()) {
            usernameField.setError("Username is required!");
            usernameField.requestFocus();
            return;
        }

        if(email.isEmpty()) {
            emailField.setError("Email is required!");
            emailField.requestFocus();
            return;
        }
        // email pattern validation e.g. jollibee.must@die.com
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.setError("Please provide a valid email!");
            emailField.requestFocus();
            return;
        }

        if(password.isEmpty()) {
            passwordField.setError("Password is required!");
            passwordField.requestFocus();
            return;
        }
        // need a minimum of 6 characters because firebase says to git fukd;
        if(password.length() < 6) {
            passwordField.setError("Minimum password length is 6 characters!");
            passwordField.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            User user = new User(fName, lName, birthday, username, email);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Toast.makeText(Register.this, "Account has been created successfully! Please check your email and verify your account!", Toast.LENGTH_LONG).show();
                                        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
                                        startActivity(new Intent(Register.this, Login.class));
                                        finish();
                                    }
                                    else {
                                        Toast.makeText(Register.this, "Registration failed, please try again", Toast.LENGTH_LONG).show();
                                    }
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        }
                        else {
                            Toast.makeText(Register.this, "Registration failed.", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

     }
 }