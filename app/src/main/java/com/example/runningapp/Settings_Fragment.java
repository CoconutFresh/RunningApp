package com.example.runningapp;

import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;

public class Settings_Fragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        //ListPreference distUnits = findPreference("distance_units");

        androidx.preference.EditTextPreference distLengthPref = getPreferenceManager().findPreference(getString(R.string.pref_segLength));
        distLengthPref.setOnBindEditTextListener(new androidx.preference.EditTextPreference.OnBindEditTextListener() {
            @Override
            public void onBindEditText(@NonNull EditText editText) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            }
        });

        androidx.preference.EditTextPreference intervalLengthPref = getPreferenceManager().findPreference(getString(R.string.pref_segLength));
        intervalLengthPref.setOnBindEditTextListener(new androidx.preference.EditTextPreference.OnBindEditTextListener() {
            @Override
            public void onBindEditText(@NonNull EditText editText) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            }
        });

    }
}