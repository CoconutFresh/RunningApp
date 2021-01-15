package com.example.runningapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class RunningFragment extends Fragment implements View.OnClickListener {

    View view;
    Button bt_stopRun;
    Fragment initializeRun;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    public RunningFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_running, container, false);

        bt_stopRun = view.findViewById(R.id.bt_stopRun);
        bt_stopRun.setOnClickListener(this);

        initializeRun = new InitializeRunFragment();
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_stopRun:
                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.maps_rl_fragment, initializeRun);
                fragmentTransaction.commit();
                break;
            default:
                break;
        }
    }
}