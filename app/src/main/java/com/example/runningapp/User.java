package com.example.runningapp;

public class User {

    public String fName, lName, birthday, username, email;
    public int level;
    
    public User() {}

    public User(String fName, String lName, String birthday, String username, String email, int level) {
        this.fName = fName;
        this.lName = lName;
        this.birthday = birthday;
        this.username = username;
        this.email = email;
        this.level = level;
    }
}
