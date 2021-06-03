package com.example.runningapp;

public class User {

    public String fName, lName, birthday, username, email;
    public String [] runData;
    
    public User() {}

    public User(String fName, String lName, String birthday, String username, String email, String [] runData) {
        this.fName = fName;
        this.lName = lName;
        this.birthday = birthday;
        this.username = username;
        this.email = email;
        this.runData = runData;
    }
}
