package com.example.ondrejvane.zivnostnicek.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ondrejvane.zivnostnicek.model.StorageItem;

import java.util.ArrayList;

public class StorageItemDatabaseHelper extends DatabaseHelper {

    //název tabulky skladové položky
    private static final String TABLE_STORAGE_ITEM = "storage_item";

    //názvy atributů v tabulce skladové položky(storage item)
    private static final String COLUMN_STORAGE_ITEM_ID = "storage_item_id";                 //primární klíč
    private static final String COLUMN_STORAGE_ITEM_USER_ID = "storage_item_user_id";       //cizí klíč do tabulky uživatelů
    private static final String COLUMN_STORAGE_ITEM_NAME = "storage_item_name";             //název skladové položky
    private static final String COLUMN_STORAGE_ITEM_QUANTITY = "storage_item_quantity";     //množství skladové položky
    private static final String COLUMN_STORAGE_ITEM_UNIT = "storage_item_unit";             //jednotka skladové položky
    private static final String COLUMN_STORAGE_ITEM_NOTE = "storage_item_note";             //poznámka ke skladové položce
    /**
     * Constructor
     *
     * @param context
     */
    public StorageItemDatabaseHelper(Context context) {
        super(context);
    }

    public void addStorageItem(StorageItem storageItem){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_STORAGE_ITEM_USER_ID, storageItem.getUserId());
        values.put(COLUMN_STORAGE_ITEM_NAME, storageItem.getName());
        values.put(COLUMN_STORAGE_ITEM_QUANTITY, storageItem.getQuantity());
        values.put(COLUMN_STORAGE_ITEM_UNIT, storageItem.getUnit());
        values.put(COLUMN_STORAGE_ITEM_NOTE, storageItem.getNote());
        db.insert(TABLE_STORAGE_ITEM, null, values);
        db.close();
    }


    public ArrayList<StorageItem> getStorageItemByUserId(int userID){

        ArrayList<StorageItem> storageItemsList = new ArrayList<>();

        String[] columns = { COLUMN_STORAGE_ITEM_ID, COLUMN_STORAGE_ITEM_NAME, COLUMN_STORAGE_ITEM_QUANTITY, COLUMN_STORAGE_ITEM_UNIT, COLUMN_STORAGE_ITEM_NOTE};

        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_STORAGE_ITEM_USER_ID + " = ?";

        String orderBy = COLUMN_STORAGE_ITEM_NAME + " ASC";

        // selection arguments
        String[] selectionArgs = {Integer.toString(userID)};

        Cursor cursor = db.query(TABLE_STORAGE_ITEM,    //Table to query
                columns,                                //columns to return
                selection,                              //columns for the WHERE clause
                selectionArgs,                          //The values for the WHERE clause
                null,                          //group the rows
                null,                           //filter by row groups
                orderBy);

        if (cursor.moveToFirst()){
            do{
                StorageItem storageItem = new StorageItem();
                storageItem.setId(cursor.getInt(0));
                storageItem.setName(cursor.getString(1));
                storageItem.setQuantity(cursor.getFloat(2));
                storageItem.setUnit(cursor.getString(3));
                storageItem.setNote(cursor.getString(4));
                storageItemsList.add(storageItem);
            }while (cursor.moveToNext());
        }
        db.close();
        cursor.close();

        return storageItemsList;
    }

    public StorageItem getStorageItemById(int storageItemId){
        String[] columns = { COLUMN_STORAGE_ITEM_NAME, COLUMN_STORAGE_ITEM_QUANTITY, COLUMN_STORAGE_ITEM_UNIT, COLUMN_STORAGE_ITEM_NOTE};

        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_STORAGE_ITEM_ID + " = ?";

        String[] selectionArgs = {Integer.toString(storageItemId)};

        Cursor cursor = db.query(TABLE_STORAGE_ITEM,    //Table to query
                columns,                                //columns to return
                selection,                              //columns for the WHERE clause
                selectionArgs,                          //The values for the WHERE clause
                null,                          //group the rows
                null,                           //filter by row groups
                null);

        StorageItem storageItem = new StorageItem();

        if(cursor.moveToFirst()){
            storageItem.setId(storageItemId);
            storageItem.setName(cursor.getString(0));
            storageItem.setQuantity(cursor.getFloat(1));
            storageItem.setUnit(cursor.getString(2));
            storageItem.setNote(cursor.getString(3));
        }else {
            storageItem = null;

        }
        db.close();
        cursor.close();
        return storageItem;
    }

    public boolean deleteStorageItembyId(int storageItemId){
        boolean result;

        String where = COLUMN_STORAGE_ITEM_ID + " = ?";

        String[] deleteArgs = {Integer.toString(storageItemId)};

        SQLiteDatabase db = this.getReadableDatabase();

        result = db.delete(TABLE_STORAGE_ITEM, where, deleteArgs) > 0;

        return result;
    }

}
