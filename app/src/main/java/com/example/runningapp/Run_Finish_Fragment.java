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
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class Run_Finish_Fragment extends Fragment implements View.OnClickListener{
    private FirebaseUser user;
    private String key, title, desc;
    private EditText titleField, descField;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

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

        // Title and Description fields
        titleField = (EditText) view.findViewById(R.id.edt_runTitle);
        descField = (EditText) view.findViewById(R.id.edt_runDesc);

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
        user = FirebaseAuth.getInstance().getCurrentUser();
        // prevents break if some fucky things happen with user auth
        if(user != null) { key = user.getUid(); }

        // Setting default placeholder text
        if(titleField.getText().length() == 0) {
            title = "Untitled Activity";
        }
        else {
            title = titleField.getText().toString();
        }
        if(descField.getText().length() == 0) {
            desc = "No Description";
        }
        else {
            desc = descField.getText().toString();
        }
        
        switch (v.getId()) {
            case R.id.fbt_saveRun:
                Map<String, Object> data = new HashMap<>();
                data.put("key", key);
                data.put("title", title);
                data.put("desc", desc);
                data.put("time", tv_time.getText().toString());
                data.put("dist", tv_dist.getText().toString());
                data.put("pace", tv_pace.getText().toString());

                db.collection("runData")
                        .add(data)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });
                break;
            default:
                Log.d(TAG, "onClick: ERROR! onClick reached default!");
        }
    }
}