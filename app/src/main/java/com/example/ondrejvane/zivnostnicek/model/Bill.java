package com.example.ondrejvane.zivnostnicek.model;

import android.graphics.Bitmap;

public class Bill {

    private int id;
    private String name;
    private float amount;
    private int VAT;
    private int traderId;
    private String date;
    private Bitmap photo;
    private String place;
    private int typeId;
    private int userId;
    private int isExpense;


    public Bill(int id, String name, float amount, int VAT, int traderId, String date, Bitmap photo, String place, int typeId, int userId, int isExpense) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.VAT = VAT;
        this.traderId = traderId;
        this.date = date;
        this.photo = photo;
        this.place = place;
        this.typeId = typeId;
        this.userId = userId;
        this.isExpense = isExpense;
    }

    public Bill() {
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

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public int getVAT() {
        return VAT;
    }

    public void setVAT(int VAT) {
        this.VAT = VAT;
    }

    public int getTraderId() {
        return traderId;
    }

    public void setTraderId(int traderId) {
        this.traderId = traderId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getIsExpense() {
        return isExpense;
    }

    public void setIsExpense(int isExpense) {
        this.isExpense = isExpense;
    }
}
