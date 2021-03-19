package com.example.runningapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.TextView;

import java.text.DecimalFormat;

public class Run_Running_Fragment extends Fragment {

    View view;
    private static long whenTimeStopped;
    private static Chronometer timer, timer_s;
    static TextView tv_totalDist, tv_avgPace, tv_dist_s, tv_pace_s, tv_totalDistUnit;

    static long totalTime;
    static float avgPace, secondsPacePerc, secondsPace = 0;
    static int minutePace = 0;

    static String formatTime, formatPace;

    static DecimalFormat dfRound = new DecimalFormat("#.##");
    static DecimalFormat dfZero = new DecimalFormat("00");

    Run_Activity activity;

    public Run_Running_Fragment() {
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

        activity = (Run_Activity) getActivity();

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
    public static void updateDistance(float totalDist) {

        tv_totalDist.setText(dfRound.format(totalDist));
        tv_dist_s.setText(dfRound.format(totalDist));

        totalTime = timer.getBase() - SystemClock.elapsedRealtime(); //Timer is measured in milliseconds

        formatData(totalDist); //Formats data into Strings that the user will see

        //TODO: Find a better way to update it (feels like it updates too often?), try to find a way to make it more accurate
        if(totalDist < 0.01) {
            tv_avgPace.setText("0:00");
        }
        else {
            /*avgPace = totalTime / (Float.parseFloat(dfRound.format(totalDist)) * -60000);
            minutePace = (int) avgPace; //Takes whole minutes
            secondsPacePerc = avgPace % 1; //Takes the percentage of the next minute
            secondsPace = 60 * secondsPacePerc; //Converts the percentage into seconds*/

            tv_avgPace.setText(minutePace + ":" + dfZero.format(secondsPace));
            tv_pace_s.setText(minutePace + ":" + dfZero.format(secondsPace));
        }
    }

    //Formats data into String variables that the user will see
    private static void formatData(float totalDist) {
        //Time
        formatTime = ((int)totalTime / -60000) + ":" + dfZero.format((totalTime / -1000) % 60);

        if(totalDist < 0.01) {
        formatPace = "0:00";
        }
        else {
            //Pace
            avgPace = totalTime / (Float.parseFloat(dfRound.format(totalDist)) * -60000);
            minutePace = (int) avgPace; //Takes whole minutes
            secondsPacePerc = avgPace % 1; //Takes the percentage of the next minute
            secondsPace = 60 * secondsPacePerc; //Converts the percentage into seconds

            formatPace = minutePace + ":" + dfZero.format(secondsPace);
        }

        Log.d("formatData", formatTime);
        Log.d("formatData", formatPace);
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