package com.example.ondrejvane.zivnostnicek.model;

public class TypeBill {

    private int id;
    private int userId;
    private String name;
    private int color;

    public TypeBill(int id,int userId, String name, int color) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.color = color;
    }

    public TypeBill(int userId, String name, int color) {
        this.userId = userId;
        this.name = name;
        this.color = color;
    }

    public TypeBill(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
