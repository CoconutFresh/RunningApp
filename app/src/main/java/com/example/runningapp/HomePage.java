package com.example.runningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class HomePage extends AppCompatActivity implements View.OnClickListener {

    Button profileButton, runButton, feedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        profileButton = findViewById(R.id.bt_profileActivity);
        runButton = findViewById(R.id.bt_runActivity);
        feedButton = findViewById(R.id.bt_feedActivity);

        profileButton.setOnClickListener(this);
        runButton.setOnClickListener(this);
        feedButton.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.profile_link:
                startActivity(new Intent(HomePage.this, Profile.class));
                return true;
            case R.id.settings_link:
                Intent intent = new Intent(HomePage.this, Settings.class);
                startActivity(intent);
                return true;
            case R.id.logout_btn:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_feedActivity:
                Toast.makeText(this, "feed activity", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bt_runActivity:
                startActivity(new Intent(HomePage.this, MapsActivity.class));
                break;
            case R.id.bt_profileActivity:
                Toast.makeText(this, "profile activity", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(HomePage.this, Login.class));
        finish();
    }
}