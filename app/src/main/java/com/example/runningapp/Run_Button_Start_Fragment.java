package com.example.runningapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class Run_Button_Start_Fragment extends Fragment implements View.OnClickListener {

    Button startButton;
    private StartButtonListener listener;


    public interface StartButtonListener {
        void onStartPressed(boolean startPressed);
    }

    public Run_Button_Start_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_start_button, container, false);

        //Initialize button listener
        startButton = view.findViewById(R.id.bt_startRun);
        startButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_startRun:
                listener.onStartPressed(true);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof StartButtonListener) {
            listener = (StartButtonListener) context;
        }
        else {
            throw new RuntimeException(context.toString()
                    + "must implement startButtonListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}