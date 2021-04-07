package com.example.runningapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Run_Initialize_Fragment extends Fragment  {

    View view;
    //private Spinner spinnerMode;
    //static String type = "ERROR";

    public static TextView modeSelect;

    public Run_Initialize_Fragment() {
        // Required empty public constructor
    }

    public interface initializeRunInterface {
        void modeSelect();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_initialize_run, container, false);

        modeSelect = view.findViewById(R.id.tv_modeSelect);

       /* ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, getResources().getStringArray(R.array.modes));
        spinnerMode = view.findViewById(R.id.spinner_mode);
        spinnerMode.setBackground(null);

        spinnerMode.setAdapter(adapter);

        spinnerMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), spinnerMode.getSelectedItem().toString() + " selected", Toast.LENGTH_SHORT).show();
                type = spinnerMode.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        return view;
    }

    public static void setMode(String mode) {
        modeSelect.setText(mode);
    }
}