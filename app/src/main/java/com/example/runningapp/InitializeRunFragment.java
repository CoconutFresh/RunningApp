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
import android.widget.Button;

public class InitializeRunFragment extends Fragment implements View.OnClickListener {

    View view;
    Button bt_startRun;
    Fragment runningFragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    private InitRunListener listener;

    public interface InitRunListener {
        void onStartRunPressed(boolean isRunning);
    }

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
                listener.onStartRunPressed(true);

                //Switches from InitializeRun to Running
                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.maps_rl_fragment, runningFragment);
                //fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            default:
                break;
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof InitRunListener) {
            listener = (InitRunListener) context;
        }
        else {
            throw new RuntimeException(context.toString()
                + "must implement initRunListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}