package com.example.dtanp.masoi.model;

public class User {
    private String id;
    private String birthday;
    private String  email;
    private String user;

    public User() {
    }

    public User(String id, String birthday, String email, String user) {
        this.id = id;
        this.birthday = birthday;
        this.email = email;
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
