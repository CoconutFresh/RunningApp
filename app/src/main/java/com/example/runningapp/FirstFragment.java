package com.example.runningapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class FirstFragment extends Fragment implements View.OnClickListener {


    View view;
    Button bt_switch_fg1;
    Fragment secondFragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    public FirstFragment() {
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
        view = inflater.inflate(R.layout.fragment_first, container, false);

        bt_switch_fg1 = view.findViewById(R.id.bt_switch_fg1);
        bt_switch_fg1.setOnClickListener(this);

        secondFragment = new SecondFragment();

        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bt_switch_fg1:
                //Toast.makeText(getActivity().getBaseContext(), "I clicked the button", Toast.LENGTH_SHORT).show();
                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fl_fragment, secondFragment);
                fragmentTransaction.commit();
                break;
            default:
                break;
        }
    }
}