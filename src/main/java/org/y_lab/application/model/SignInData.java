package org.y_lab.application.model;

public class SignInData {
    String username;
    String password;

    public SignInData(){

    }

    public SignInData(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
