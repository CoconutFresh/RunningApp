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

public class RunningFragment extends Fragment implements View.OnClickListener {

    View view;
    Button bt_stopRun;
    Button bt_pauseRun;

    Chronometer timer;
    long whenTimeStopped;
    TextView tv_totalDist;
    double totalDist = 0;

    Fragment initializeRun;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    RunningListener listener;
    Handler handler;
    MapsActivity activity;

    boolean isPaused = false;

    public interface RunningListener {
        void onStopRunPressed(boolean isRunning);
        void onPauseRunPressed(boolean pause);
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

        //Setting up button listeners
        bt_stopRun = view.findViewById(R.id.bt_stopRun);
        bt_pauseRun = view.findViewById(R.id.bt_pauseRun);
        bt_pauseRun.setOnClickListener(this);
        bt_stopRun.setOnClickListener(this);

        //Setting up stopwatch
        timer = view.findViewById(R.id.cm_timer);
        timer.start();

        initializeRun = new InitializeRunFragment();
        return view;
    }

    //Updates distance text view every second
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
                //Log.d("timerLog", timer.getText().toString());
                listener.onStopRunPressed(false);

                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.maps_rl_fragment, initializeRun);
                //fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case R.id.bt_pauseRun:
                if(!isPaused) {
                    isPaused = true;
                    listener.onPauseRunPressed(true);
                    whenTimeStopped = timer.getBase() - SystemClock.elapsedRealtime();
                    timer.stop();
                    bt_pauseRun.setText("Resume");
                }
                else {
                    isPaused = false;
                    listener.onPauseRunPressed(false);
                    timer.setBase(SystemClock.elapsedRealtime() + whenTimeStopped);
                    timer.start();
                    bt_pauseRun.setText("Pause");
                }
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