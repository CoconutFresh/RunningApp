package com.example.runningapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import java.util.Map;

public class RunningFragment extends Fragment implements View.OnClickListener {

    View view;
    TextView tv_totalDist;
    Button bt_stopRun;
    Chronometer timer;
    Fragment initializeRun;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    RunningListener listener;
    double totalDist = 0;
    Handler handler;
    MapsActivity activity;

    public interface RunningListener {
        void onStopRunPressed(boolean isRunning);
    }

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

        activity = (MapsActivity) getActivity();
        tv_totalDist = view.findViewById(R.id.tv_totalDist);
        tv_totalDist.setText(String.valueOf(totalDist));
        updateDistance();


        bt_stopRun = view.findViewById(R.id.bt_stopRun);
        bt_stopRun.setOnClickListener(this);

        timer = view.findViewById(R.id.timer);
        timer.start();

        initializeRun = new InitializeRunFragment();
        return view;
    }

    private void updateDistance() {
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this,1000);
                totalDist = activity.getTotalDist();
                tv_totalDist.setText(String.valueOf(totalDist));
            }
        }, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_stopRun:
                listener.onStopRunPressed(false);

                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.maps_rl_fragment, initializeRun);
                fragmentTransaction.commit();
                break;
            default:
                break;
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof RunningListener) {
            listener = (RunningListener) context;
        }
        else {
            throw new RuntimeException(context.toString()
                + "must implement RunningListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}