package com.example.ondrejvane.zivnostnicek.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ondrejvane.zivnostnicek.model.StorageItemBox;
import com.example.ondrejvane.zivnostnicek.model.ItemQuantity;
import com.example.ondrejvane.zivnostnicek.model.StorageItem;

import java.util.ArrayList;

public class ItemQuantityDatabaseHelper extends DatabaseHelper {

    /**
     * Konstruktor item quantity database helper
     *
     * @param context kontext aktivity
     */
    public ItemQuantityDatabaseHelper(Context context) {
        super(context);
    }

    public synchronized void addItemQuantity(ItemQuantity itemQuantity) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_QUANTITY_BILL_ID, itemQuantity.getBillId());
        values.put(COLUMN_ITEM_QUANTITY_STORAGE_ITEM_ID, itemQuantity.getStorageItemId());
        values.put(COLUMN_ITEM_QUANTITY_QUANTITY, itemQuantity.getQuantity());
        values.put(COLUMN_ITEM_QUANTITY_IS_DIRTY, itemQuantity.getIsDirty());
        values.put(COLUMN_ITEM_QUANTITY_IS_DELETED, itemQuantity.getIsDeleted());
        db.insert(TABLE_ITEM_QUANTITY, null, values);
        db.close();
    }

    /**
     * Získání množství skladové položky na základě id skladové položky
     *
     * @param storageItemId id skladové položky
     * @return  množství skladové položky
     */
    public synchronized float getQuantityWithStorageItemId(int storageItemId) {
        float quantity = 0;

        String[] columns = {COLUMN_ITEM_QUANTITY_QUANTITY};

        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_ITEM_QUANTITY_STORAGE_ITEM_ID + " = ?" + " AND "
                + COLUMN_ITEM_QUANTITY_IS_DELETED + " = ?";

        // selection arguments
        String[] selectionArgs = {Integer.toString(storageItemId), "0"};

        Cursor cursor = db.query(TABLE_ITEM_QUANTITY,    //Table to query
                columns,                                //columns to return
                selection,                              //columns for the WHERE clause
                selectionArgs,                          //The values for the WHERE clause
                null,                          //group the rows
                null,                           //filter by row groups
                null);

        if (cursor.moveToFirst()) {
            do {
                quantity = quantity + cursor.getFloat(0);
            } while (cursor.moveToNext());
        }
        db.close();
        cursor.close();

        return quantity;
    }

    /**
     * Metoda, která získá množství skladových položek na základě id faktury.
     *
     * @param billId id faktury
     * @return  list množství skladové položky
     */
    public synchronized ArrayList<ItemQuantity> getItemQuantityByBillId(int billId) {
        ArrayList<ItemQuantity> arrayList = new ArrayList<>();

        String[] columns = {COLUMN_ITEM_QUANTITY_ID, COLUMN_ITEM_QUANTITY_QUANTITY, COLUMN_ITEM_QUANTITY_STORAGE_ITEM_ID};

        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_ITEM_QUANTITY_BILL_ID + " = ?" + " AND " + COLUMN_ITEM_QUANTITY_IS_DELETED + " = ?";

        // selection arguments
        String[] selectionArgs = {Integer.toString(billId), "0"};

        Cursor cursor = db.query(TABLE_ITEM_QUANTITY,    //Table to query
                columns,                                //columns to return
                selection,                              //columns for the WHERE clause
                selectionArgs,                          //The values for the WHERE clause
                null,                          //group the rows
                null,                           //filter by row groups
                null);

        if (cursor.moveToFirst()) {
            do {
                ItemQuantity itemQuantity = new ItemQuantity();
                itemQuantity.setBillId(billId);
                itemQuantity.setId(cursor.getInt(0));
                itemQuantity.setQuantity(cursor.getFloat(1));
                itemQuantity.setStorageItemId(cursor.getInt(2));
                arrayList.add(itemQuantity);
            } while (cursor.moveToNext());
        }
        db.close();
        cursor.close();

        return arrayList;
    }


    /**
     * Metoda, která nastaví atribut is deleted na jedničku a tím bude množství
     * skladové položky označena za smazanou.
     *
     * @param itemId množství skladové položky  id
     * @return smazáno
     */
    public boolean deleteItemQuantityById(int itemId){
        String where = COLUMN_ITEM_QUANTITY_ID + " = ?";

        String[] updateArgs = {Integer.toString(itemId)};

        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_QUANTITY_IS_DELETED, 1);
        values.put(COLUMN_ITEM_QUANTITY_IS_DIRTY, 1);

        db.update(TABLE_ITEM_QUANTITY, values, where, updateArgs);
        db.close();

        return true;
    }

    /**
     * Získání listu skladových položek, které patří k faktuře.
     * K němu nadjde odpovídající množství.
     *
     * @param billId id faktury
     * @return  list skladových položek a množství
     */
    public synchronized ArrayList<StorageItemBox> getItemQuantityAndStorageItemByBillId(int billId) {

        ArrayList<ItemQuantity> arrayItemQuantity = getItemQuantityByBillId(billId);
        ArrayList<StorageItemBox> arrayStorageItemBox = new ArrayList<>();
        StorageItemDatabaseHelper storageItemDatabaseHelper = new StorageItemDatabaseHelper(getContext());

        for (int i = 0; i < arrayItemQuantity.size(); i++) {
            long storageItemId = arrayItemQuantity.get(i).getStorageItemId();
            ItemQuantity itemQuantity = arrayItemQuantity.get(i);
            StorageItem storageItem = storageItemDatabaseHelper.getStorageItemById((int) storageItemId);
            StorageItemBox storageItemBox = new StorageItemBox(storageItem, itemQuantity, false);
            arrayStorageItemBox.add(storageItemBox);
        }

        return arrayStorageItemBox;
    }

    /**
     * Metoda, která smaže všechny skladové množství na základě odpovídající
     * skladové položky.
     *
     * @param storageItemId id sladové položky
     */
    public synchronized void deleteAllItemQuantityByStorageItemId(int storageItemId){
        String where = COLUMN_ITEM_QUANTITY_ID + " = ?";

        String[] updateArgs = {Integer.toString(storageItemId)};

        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_QUANTITY_IS_DELETED, 1);
        values.put(COLUMN_ITEM_QUANTITY_IS_DIRTY, 1);

        db.update(TABLE_ITEM_QUANTITY, values, where, updateArgs);
        db.close();

    }
}
