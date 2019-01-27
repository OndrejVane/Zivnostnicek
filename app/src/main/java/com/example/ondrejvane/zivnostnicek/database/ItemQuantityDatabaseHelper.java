package com.example.ondrejvane.zivnostnicek.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ondrejvane.zivnostnicek.model.ItemQuantity;
import com.example.ondrejvane.zivnostnicek.model.StorageItem;

import java.util.ArrayList;

public class ItemQuantityDatabaseHelper extends DatabaseHelper {

    /**
     * Constructor
     *
     * @param context
     */
    public ItemQuantityDatabaseHelper(Context context) {
        super(context);
    }

    public void addItemQuantity(ItemQuantity itemQuantity){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_QUANTITY_BILL_ID, itemQuantity.getBillId());
        values.put(COLUMN_ITEM_QUANTITY_STORAGE_ITEM_ID, itemQuantity.getStorageItemId());
        values.put(COLUMN_ITEM_QUANTITY_QUANTITY, itemQuantity.getQuantity());
        db.insert(TABLE_ITEM_QUANTITY, null, values);
        db.close();
    }

    public float getQuantityWithStorageItemId(int storageItemId){
        float quantity = 0;

        String[] columns = { COLUMN_ITEM_QUANTITY_QUANTITY};

        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_ITEM_QUANTITY_STORAGE_ITEM_ID + " = ?";

        // selection arguments
        String[] selectionArgs = {Integer.toString(storageItemId)};

        Cursor cursor = db.query(TABLE_ITEM_QUANTITY,    //Table to query
                columns,                                //columns to return
                selection,                              //columns for the WHERE clause
                selectionArgs,                          //The values for the WHERE clause
                null,                          //group the rows
                null,                           //filter by row groups
                null);

        if (cursor.moveToFirst()){
            do{
                quantity = quantity + cursor.getFloat(0);
            }while (cursor.moveToNext());
        }
        db.close();
        cursor.close();

        return quantity;
    }
}
