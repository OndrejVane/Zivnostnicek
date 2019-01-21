package com.example.ondrejvane.zivnostnicek.model;

import android.graphics.Bitmap;

public class Bill {

    private int id;
    private String billNumber;
    private float billAmount;
    private int billVAT;
    private int billTraderId;
    private String billDate;
    private Bitmap billPhoto;
    private String billPlace;
    private int billTypeId;
    private int billUserId;
    private int billIsExpense;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public float getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(float billAmount) {
        this.billAmount = billAmount;
    }

    public int getBillVAT() {
        return billVAT;
    }

    public void setBillVAT(int billVAT) {
        this.billVAT = billVAT;
    }

    public int getBillTraderId() {
        return billTraderId;
    }

    public void setBillTraderId(int billTraderId) {
        this.billTraderId = billTraderId;
    }

    public String getBillDate() {
        return billDate;
    }

    public void setBillDate(String billDate) {
        this.billDate = billDate;
    }

    public Bitmap getBillPhoto() {
        return billPhoto;
    }

    public void setBillPhoto(Bitmap billPhoto) {
        this.billPhoto = billPhoto;
    }

    public String getBillPlace() {
        return billPlace;
    }

    public void setBillPlace(String billPlace) {
        this.billPlace = billPlace;
    }

    public int getBillTypeId() {
        return billTypeId;
    }

    public void setBillTypeId(int billTypeId) {
        this.billTypeId = billTypeId;
    }

    public int getBillUserId() {
        return billUserId;
    }

    public void setBillUserId(int billUserId) {
        this.billUserId = billUserId;
    }

    public int getBillIsExpense() {
        return billIsExpense;
    }

    public void setBillIsExpense(int billIsExpense) {
        this.billIsExpense = billIsExpense;
    }
}
