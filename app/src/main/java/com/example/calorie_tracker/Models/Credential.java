package com.example.calorie_tracker.Models;

import java.util.Date;

public class Credential {
    private String username;
    private Date signUpDate;
    private String password;
    private Users uid;

    public Credential(String username, Date signUpDate, String password, Users uid) {
        this.username = username;
        this.signUpDate = signUpDate;
        this.password = password;
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
