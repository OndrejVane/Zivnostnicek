package com.example.ondrejvane.zivnostnicek.model.model_helpers;


import com.example.ondrejvane.zivnostnicek.helper.InputValidation;

public class Note {

    private int id;
    private int traderId;
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

    public int getTraderId() {
        return traderId;
    }

    public void setTraderId(int traderId) {
        this.traderId = traderId;
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

    public void removeSpecialChars() {
        if (this.title != null) {
            this.title = InputValidation.removeSpecialChars(this.title);
        }

        if (this.note != null) {
            this.note = InputValidation.removeSpecialChars(this.note);
        }
    }
}
