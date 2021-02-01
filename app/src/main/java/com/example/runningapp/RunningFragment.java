package com.example.runningapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

public class RunningFragment extends Fragment {


    View view;
    private static long whenTimeStopped;
    private static Chronometer timer;
    static TextView tv_totalDist;
    static double totalDist = 0;

    Handler handler;
    MapsActivity activity;

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

        //Setting up stopwatch
        timer = view.findViewById(R.id.cm_timer);
        timer.start();

        return view;
    }

 /*   //Updates distance text view every second
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
    }*/

    public static void updateDistance(double dist) {
        tv_totalDist.setText(String.valueOf(dist));
    }

    public static void timerPause(boolean pause) {
        if(pause) {
            whenTimeStopped = timer.getBase() - SystemClock.elapsedRealtime();
            Log.d("timeLog", "Time: " + whenTimeStopped);
            timer.stop();
        }
        else {
            timer.setBase(SystemClock.elapsedRealtime() + whenTimeStopped);
            timer.start();
        }
    }
}