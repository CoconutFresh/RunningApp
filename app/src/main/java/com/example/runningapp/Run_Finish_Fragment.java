package com.example.runningapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;

public class Run_Finish_Fragment extends Fragment implements View.OnClickListener{

    TextView tv_time, tv_dist, tv_pace;
    Run_Activity activity;
    RunSession session;
    private static final String TAG = "Run_Finish_Fragment";

    //Button
    Button saveButton;

    public Run_Finish_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_finish_run, container, false);

        //Button
        saveButton = view.findViewById(R.id.fbt_saveRun);
        saveButton.setOnClickListener(this);

        //Shows stats
        DecimalFormat dfRound = new DecimalFormat("#.##");
        DecimalFormat dfZero = new DecimalFormat("00");

        activity = (Run_Activity) getActivity();
        session = activity.runStats;

        tv_time = view.findViewById(R.id.tv_finTime);
        tv_dist = view.findViewById(R.id.tv_finDist);
        tv_pace = view.findViewById(R.id.tv_finPace);

        tv_time.setText(session.getFormatTime());

        tv_dist.setText(dfRound.format(session.getTotalDist()));
        tv_pace.setText(session.getFormatPace());

        return view;
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.menu_delete).setVisible(true); //Shows trashcan
        //((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Shows back button

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fbt_saveRun:
                //TODO: Use session object's data here
                Log.d(TAG, "onClick: TEST!");
                break;
            default:
                Log.d(TAG, "onClick: ERROR! onClick reached default!");
        }
    }
}