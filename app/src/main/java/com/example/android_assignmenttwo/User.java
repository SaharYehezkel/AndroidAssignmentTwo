package com.example.android_assignmenttwo;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String email;
    private String name;
    private String password;
    List<Item> items;

    public User() {
    }
    public User(String userEmail, String userName, String userPassword) {
        this.email = userEmail;
        this.name = userName;
        this.password = userPassword;
        this.items = new ArrayList<Item>();
    }

    public String getEmail(){
        return email;
    }

    public String getName(){
        return name;
    }

    public String getPassword(){
        return password;
    }
}
