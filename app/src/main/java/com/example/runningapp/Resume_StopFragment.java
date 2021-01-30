package com.example.runningapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class Resume_StopFragment extends Fragment implements View.OnClickListener{

    Button resume;
    Button stop;

    private Resume_StopListener listener;

    public interface Resume_StopListener {
        void onResumePressed(boolean pause);
        void onStopPressed(boolean stop);
    }

    public Resume_StopFragment() {
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
        View view = inflater.inflate(R.layout.fragment_resume_stop, container, false);

        //Initializing button listeners
        resume = view.findViewById(R.id.bt_resumeRun);
        stop = view.findViewById(R.id.bt_stopRun);
        resume.setOnClickListener(this);
        stop.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bt_resumeRun:
                listener.onResumePressed(false);
                break;
            case R.id.bt_stopRun:
                listener.onStopPressed(true);
                break;
            default:
                break;
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof Resume_StopListener) {
            listener = (Resume_StopListener) context;
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