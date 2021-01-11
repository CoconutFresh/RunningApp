package com.example.runningapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Login extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private Button lg_Button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        username = findViewById(R.id.lg_username);
        password = findViewById(R.id.lg_password);
        lg_Button = findViewById(R.id.lg_button);

        lg_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate(username.getText().toString(), password.getText().toString());
            }
        });
    }

    //TODO: Create actual login process
    //Garbage method for testing. Will delete later.
    private void validate(String un, String pw) {
        if(un.equals("test") && pw.equals("test")) {

            //Switches over control of the app to the next activity
            Intent intent = new Intent(Login.this, HomePage.class);
            startActivity(intent);
            finish(); //Makes it so that the user can't use the back button to return to this page.
        }
    }
}