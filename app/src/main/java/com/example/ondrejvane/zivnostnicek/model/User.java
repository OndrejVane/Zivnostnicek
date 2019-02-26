package com.example.ondrejvane.zivnostnicek.model;


public class User {

    private int id;
    private String fullName;
    private String email;
    private String password;
    private int syncNumber;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String name) {
        this.fullName = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getSyncNumber() {
        return syncNumber;
    }

    public void setSyncNumber(int syncNumber) {
        this.syncNumber = syncNumber;
    }
}
