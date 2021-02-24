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

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class RunningFragment extends Fragment {

    View view;
    private static long whenTimeStopped;
    private static Chronometer timer, timer_s;
    static TextView tv_totalDist, tv_avgPace, tv_dist_s, tv_pace_s, tv_totalDistUnit;

    static long totalTime;
    static float avgPace, secondsPacePerc, secondsPace;;
    static int minutePace;

    static DecimalFormat dfRound = new DecimalFormat("#.##");
    static DecimalFormat dfZero = new DecimalFormat("00");

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

        //Initializing text views
        tv_totalDist = view.findViewById(R.id.tv_totalDist);
        tv_avgPace = view.findViewById(R.id.tv_avgPace);
        tv_dist_s = view.findViewById(R.id.tv_dist_s);
        tv_pace_s = view.findViewById(R.id.tv_pace_s);
        tv_totalDistUnit = view.findViewById(R.id.tv_totalDistUnit);

        tv_totalDist.setText(dfRound.format(activity.totalDistRan));
        tv_dist_s.setText(dfRound.format(activity.totalDistRan));
        tv_totalDistUnit.setText(activity.unit);

        //Setting up stopwatch
        timer = view.findViewById(R.id.cm_timer);
        timer_s = view.findViewById(R.id.cm_time_s);

        if (whenTimeStopped != 0) {//Addition added to consider stop --> resume action
            timer.setBase(SystemClock.elapsedRealtime() + whenTimeStopped);
            timer_s.setBase(SystemClock.elapsedRealtime() + whenTimeStopped);
        }
        else {
            timer.start();
            timer_s.start();
        }

        return view;
    }

    //Dist is either measured in kilometers or miles
    public static void updateDistance(float dist) {

        tv_totalDist.setText(dfRound.format(dist));
        tv_dist_s.setText(dfRound.format(dist));

        //TODO: Find a better way to update it (feels like it updates too often?), try to find a way to make it more accurate
        if(dist < 0.001) {
            tv_avgPace.setText("0:00");
        }
        else {
            //Timer is measured in milliseconds
            totalTime = timer.getBase() - SystemClock.elapsedRealtime();
            avgPace = totalTime / (Float.parseFloat(dfRound.format(dist)) * -60000);
            minutePace = (int) avgPace; //Takes whole minutes
            secondsPacePerc = avgPace % 1; //Takes the percentage of the next minute
            secondsPace = 60 * secondsPacePerc; //Converts the percentage into seconds

            tv_avgPace.setText(minutePace + ":" + dfZero.format(secondsPace));
            tv_pace_s.setText(minutePace + ":" + dfZero.format(secondsPace));
        }
    }

    //Controls the behavior of pause/resume
    public static void timerPause(boolean pause) {
        if(pause) { //pause
            whenTimeStopped = timer.getBase() - SystemClock.elapsedRealtime();
            Log.d("timeLog", "Time: " + whenTimeStopped);
            timer.stop();
            timer_s.stop();
        }
        else { //resume
            timer.setBase(SystemClock.elapsedRealtime() + whenTimeStopped);
            timer_s.setBase(SystemClock.elapsedRealtime() + whenTimeStopped);
            timer.start();
            timer_s.start();
        }
    }

    public static void timerReset() {
        whenTimeStopped = 0;
    }
}