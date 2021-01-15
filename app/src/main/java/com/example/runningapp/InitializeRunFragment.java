package com.example.runningapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class InitializeRunFragment extends Fragment implements View.OnClickListener {

    View view;
    Button bt_startRun;
    Fragment runningFragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    public InitializeRunFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_initialize_run, container, false);

        bt_startRun = view.findViewById(R.id.bt_startRun);
        bt_startRun.setOnClickListener(this);

        runningFragment = new RunningFragment();

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_startRun:
                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.maps_rl_fragment, runningFragment);
                fragmentTransaction.commit();
                break;
            default:
                break;
        }
    }
}