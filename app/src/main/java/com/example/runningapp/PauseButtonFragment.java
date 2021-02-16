package com.example.runningapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

public class PauseButtonFragment extends Fragment implements View.OnClickListener{
    Button pauseRun;
    ImageButton showMap;
    //boolean mapShown = false;
    private PauseButtonListener listener;

    public interface PauseButtonListener {
        void onPausePressed(boolean pause);
        //void onMapShownPressed(boolean mapShown);
        void onMapShownPressed();
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

        //Initializing button listeners
        pauseRun = view.findViewById(R.id.bt_pauseRun);
        showMap = view.findViewById(R.id.bt_map);
        pauseRun.setOnClickListener(this);
        showMap.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.bt_pauseRun:
                listener.onPausePressed(true);
                break;
            case R.id.bt_map:
                /*if(!mapShown) {
                    mapShown = true;
                }
                else {
                    mapShown = false;
                }
                listener.onMapShownPressed(mapShown);*/
                listener.onMapShownPressed();
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