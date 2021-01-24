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

import com.google.firebase.auth.FirebaseAuth;

public class HomePage extends AppCompatActivity implements View.OnClickListener {

    Button testButton, fragmentButton1, fragmentButton2;

    Fragment firstFragment, secondFragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        testButton = findViewById(R.id.bt_test);
        testButton.setOnClickListener(this);

        fragmentButton1 = findViewById(R.id.bt_fragment_1);
        fragmentButton1.setOnClickListener(this);

        fragmentButton2 = findViewById(R.id.bt_fragment_2);
        fragmentButton2.setOnClickListener(this);

        /*bt_switch_fg1 = findViewById(R.id.bt_switch_fg1);
        bt_switch_fg1.setOnClickListener(this);

        bt_switch_fg2 = findViewById(R.id.bt_switch_fg2);
        bt_switch_fg2.setOnClickListener(this);*/

        firstFragment = new FirstFragment();
        secondFragment = new SecondFragment();

        //Test to see how fragments work
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fl_fragment, firstFragment);
        fragmentTransaction.commit();

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
            case R.id.bt_test:
                startActivity(new Intent(this, MapsActivity.class));
                break;
            case R.id.bt_fragment_1:
            case R.id.bt_switch_fg2:
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fl_fragment, firstFragment);
                fragmentTransaction.commit();
                break;
            case R.id.bt_fragment_2:
            case R.id.bt_switch_fg1:
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fl_fragment, secondFragment);
                fragmentTransaction.commit();
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