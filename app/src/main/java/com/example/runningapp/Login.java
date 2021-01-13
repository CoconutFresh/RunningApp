package com.example.runningapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private EditText username;
    private EditText password;
    private Button login;
    private TextView register;
    private TextView forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        register = (TextView) findViewById(R.id.new_user);
        register.setOnClickListener(this);

        forgotPassword = (TextView) findViewById(R.id.forgot_password);
        forgotPassword.setOnClickListener(this);

        username = findViewById(R.id.lg_username);
        password = findViewById(R.id.lg_password);

        login = findViewById(R.id.lg_button);
        login.setOnClickListener(this);
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
                //TODO: Create a proper login service
                validate(username.getText().toString(), password.getText().toString()); //temporary
                break;
        }
    }

    //TODO: Create actual login process
    //Garbage method for testing. Will delete later.
    private void validate(String un, String pw) {
        if(un.equals("test") && pw.equals("test")) {

            //Switches over control of the app to the next activity
            startActivity(new Intent(this, HomePage.class));
            finish(); //Makes it so that the user can't use the back button to return to this page.
        }
    }
}