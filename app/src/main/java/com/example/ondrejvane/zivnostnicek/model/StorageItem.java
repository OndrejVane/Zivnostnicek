package com.example.ondrejvane.zivnostnicek.model;

public class StorageItem {

    private int id;
    private int userId;
    private String name;
    private float quantity;
    private String unit;
    private String note;

    public StorageItem(int userId, String name, float quantity, String unit, String note){
        this.userId = userId;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.note = note;
    }

    public StorageItem(){

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

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
