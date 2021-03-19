package com.example.runningapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;


/*TODO: Add more settings in the future
  Night Mode
  Arcade View(?)
 */

public class Settings_Activity extends AppCompatActivity {

    Fragment settingsFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settingsFragment = new Settings_Fragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_settings, settingsFragment)
                .commit();

    }
}