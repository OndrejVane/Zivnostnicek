package com.example.ondrejvane.zivnostnicek.model;

import com.example.ondrejvane.zivnostnicek.helper.InputValidation;

public class StorageItem {

    private int id;
    private int userId;
    private String name;
    private String unit;
    private String note;
    private int isDirty;
    private int isDeleted;

    public StorageItem(int userId, String name, String unit){
        this.userId = userId;
        this.name = name;
        this.unit = unit;
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

        if (this.name != null) {
            this.name = InputValidation.removeSpecialChars(this.name);
        }

        if (this.note != null) {
            this.note = InputValidation.removeSpecialChars(this.note);
        }

        if (this.unit != null) {
            this.unit = InputValidation.removeSpecialChars(this.unit);
        }

    }
}
