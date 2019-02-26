package com.example.ondrejvane.zivnostnicek.model;

public class ItemQuantity {

    private int id;
    private long billId;
    private long storageItemId;
    private float quantity;
    private int isDirty;
    private int isDeleted;

    public ItemQuantity(int id, long billId, long storageItemId, float quantity){
        this.id = id;
        this.billId = billId;
        this.storageItemId = storageItemId;
        this.quantity = quantity;
    }

    public ItemQuantity(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getBillId() {
        return billId;
    }

    public void setBillId(long billId) {
        this.billId = billId;
    }

    public long getStorageItemId() {
        return storageItemId;
    }

    public void setStorageItemId(long storageItemId) {
        this.storageItemId = storageItemId;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
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
}
