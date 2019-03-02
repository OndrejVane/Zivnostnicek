package com.example.ondrejvane.zivnostnicek.model;

import com.example.ondrejvane.zivnostnicek.helper.InputValidation;

public class TypeBill {

    private int id;
    private int userId;
    private String name;
    private int color;
    private int isDirty;
    private int isDeleted;

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

    public int getIsDirty() {
        return isDirty;
    }

    public void setIsDirty(int isDirty) {
        this.isDirty = isDirty;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void removeSpecialChars(){
        this.name = InputValidation.removeSpecialChars(this.name);
    }
}
