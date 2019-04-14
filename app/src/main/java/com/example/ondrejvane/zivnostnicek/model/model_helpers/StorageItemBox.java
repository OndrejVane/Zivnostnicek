package com.example.ondrejvane.zivnostnicek.model.model_helpers;

public class StorageItemBox {

    private StorageItem storageItem;
    private ItemQuantity itemQuantity;
    private boolean isNew;

    public StorageItemBox(StorageItem storageItem, ItemQuantity itemQuantity ,boolean isNew){
        this.storageItem = storageItem;
        this.itemQuantity = itemQuantity;
        this.isNew = isNew;
    }

    public StorageItem getStorageItem() {
        return storageItem;
    }

    public void setStorageItem(StorageItem storageItem) {
        this.storageItem = storageItem;
    }

    public ItemQuantity getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(ItemQuantity itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}
