package com.example.runningapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toolbar;

import java.text.DecimalFormat;

public class FinishRunFragment extends Fragment implements View.OnClickListener{

    TextView tv_time, tv_dist, tv_pace;
    MapsActivity activity;
    RunSession session;

    public FinishRunFragment() {
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

        DecimalFormat dfRound = new DecimalFormat("#.##");
        DecimalFormat dfZero = new DecimalFormat("00");

        activity = (MapsActivity) getActivity();
        session = activity.runStats;

        tv_time = view.findViewById(R.id.tv_finTime);
        tv_dist = view.findViewById(R.id.tv_finDist);
        tv_pace = view.findViewById(R.id.tv_finPace);

        tv_time.setText((int)(session.getTotalTime() / 60) + ":" + dfZero.format(session.getTotalTime() % 60));


        tv_dist.setText(dfRound.format(session.getTotalDist()));
        tv_pace.setText(session.getAvgPace());

        return view;
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.menu_delete).setVisible(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onClick(View v) {

    }
}