package com.example.runningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {

    private FirebaseUser user;
    private DatabaseReference reference;

    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        final TextView userFullNameField = (TextView) findViewById(R.id.user_fullname);
        final TextView birthdayField = (TextView) findViewById(R.id.user_birthday);
        final TextView usernameField = (TextView) findViewById(R.id.username);
        final TextView emailField = (TextView) findViewById(R.id.user_email);

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile != null) {
                    String fullname = userProfile.fName + " " + userProfile.lName;
                    String birthday = userProfile.birthday;
                    String username = userProfile.username;
                    String email = userProfile.email;

                    userFullNameField.setText(fullname);
                    birthdayField.setText(birthday);
                    usernameField.setText(username);
                    emailField.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Profile.this, "Uh Oh, stinky!", Toast.LENGTH_LONG).show();
            }
        });
    }
}