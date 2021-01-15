package com.example.runningapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class SecondFragment extends Fragment implements View.OnClickListener{

    View view;
    Button bt_switch_fg2;
    Fragment firstFragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    public SecondFragment() {
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
        view = inflater.inflate(R.layout.fragment_second, container, false);
        bt_switch_fg2 = view.findViewById(R.id.bt_switch_fg2);
        bt_switch_fg2.setOnClickListener(this);

        firstFragment = new FirstFragment();

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_switch_fg2:
                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fl_fragment, firstFragment);
                fragmentTransaction.commit();
                break;
            default:
                break;
        }
    }
}