package com.example.ondrejvane.zivnostnicek.model;


import com.example.ondrejvane.zivnostnicek.helper.InputValidation;

public class Note {

    private int id;
    private int trader_id;
    private String title;
    private String note;
    private String date;
    private int rating;
    private int isDirty;
    private int isDeleted;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTrader_id() {
        return trader_id;
    }

    public void setTrader_id(int trader_id) {
        this.trader_id = trader_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
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
        this.title = InputValidation.removeSpecialChars(this.title);
        this.note = InputValidation.removeSpecialChars(this.note);
    }
}
