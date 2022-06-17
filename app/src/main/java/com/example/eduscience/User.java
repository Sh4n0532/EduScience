package com.example.eduscience;

// store the user information in the form of object
// to register user: send the user object to firebase

public class User {

    public String username, email;

    public User() {} // used to create empty user object to get access to the variables

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

}
