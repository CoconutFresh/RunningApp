package com.example.runningapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class PauseButtonFragment extends Fragment implements View.OnClickListener{
    Button pauseRun;
    private PauseButtonListener listener;
    boolean isPaused = false;

    public interface PauseButtonListener {
        void onPausePressed(boolean pause);
    }

    public PauseButtonFragment() {
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
        View view = inflater.inflate(R.layout.fragment_pause_button, container, false);
        pauseRun = view.findViewById(R.id.bt_pauseRun);
        pauseRun.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.bt_pauseRun:
                listener.onPausePressed(true);
                break;
            default:
                break;
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof PauseButtonListener) {
            listener = (PauseButtonListener) context;
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