package com.example.ondrejvane.zivnostnicek.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ondrejvane.zivnostnicek.model.StorageItem;

import java.util.ArrayList;

public class StorageItemDatabaseHelper extends DatabaseHelper {


    /**
     * Konstruktor storage item databse helper
     *
     * @param context kontext aktivity
     */
    public StorageItemDatabaseHelper(Context context) {
        super(context);
    }

    /**
     * Přidání skladové položky do databáze. Pokud se jedná o data ze serveru
     * tak negeneruju nové id.
     *
     * @param storageItem skladová položka
     * @param isFromServer pokud jde o data ze serveru
     * @return id sladové položky
     */
    public synchronized long addStorageItem(StorageItem storageItem, boolean isFromServer){
        //pokud je záznam vkládán při synchronizaci se serverem, tak už id existuje. Nemusím ho generovat.
        if(!isFromServer){
            //získání primárního klíče
            IdentifiersDatabaseHelper identifiersDatabaseHelper = new IdentifiersDatabaseHelper(getContext());
            int storageItemId = identifiersDatabaseHelper.getFreeId(COLUMN_IDENTIFIERS_STORAGE_ITEM_ID);
            storageItem.setId(storageItemId);
        }

        SQLiteDatabase db = this.getWritableDatabase();
        long returnValue;

        ContentValues values = new ContentValues();
        values.put(COLUMN_STORAGE_ITEM_ID, storageItem.getId());
        values.put(COLUMN_STORAGE_ITEM_USER_ID, storageItem.getUserId());
        values.put(COLUMN_STORAGE_ITEM_NAME, storageItem.getName());
        values.put(COLUMN_STORAGE_ITEM_UNIT, storageItem.getUnit());
        values.put(COLUMN_STORAGE_ITEM_NOTE, storageItem.getNote());
        values.put(COLUMN_STORAGE_ITEM_IS_DIRTY, storageItem.getIsDirty());
        values.put(COLUMN_STORAGE_ITEM_IS_DELETED, storageItem.getIsDeleted());
        returnValue = db.insert(TABLE_STORAGE_ITEM, null, values);
        db.close();
        return returnValue;
    }


    /**
     * Získání všech skladových položek uživatele podle id.
     *
     * @param userID id uživatele
     * @return list skladových položek
     */
    public synchronized ArrayList<StorageItem> getStorageItemByUserId(int userID){

        ArrayList<StorageItem> storageItemsList = new ArrayList<>();

        String[] columns = { COLUMN_STORAGE_ITEM_ID, COLUMN_STORAGE_ITEM_NAME, COLUMN_STORAGE_ITEM_UNIT, COLUMN_STORAGE_ITEM_NOTE};

        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_STORAGE_ITEM_USER_ID + " = ?" + " AND " + COLUMN_STORAGE_ITEM_IS_DELETED + " = ?";

        String orderBy = COLUMN_STORAGE_ITEM_NAME + " ASC";

        // selection arguments
        String[] selectionArgs = {Integer.toString(userID), "0"};

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
                storageItem.setUnit(cursor.getString(2));
                storageItem.setNote(cursor.getString(3));
                storageItemsList.add(storageItem);
            }while (cursor.moveToNext());
        }
        db.close();
        cursor.close();

        return storageItemsList;
    }


    /**
     * Získání skladové položky podle id.
     *
     * @param storageItemId id skladové položky
     * @return skladová položka
     */
    public synchronized StorageItem getStorageItemById(int storageItemId){
        String[] columns = { COLUMN_STORAGE_ITEM_NAME, COLUMN_STORAGE_ITEM_UNIT, COLUMN_STORAGE_ITEM_NOTE};

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
            storageItem.setUnit(cursor.getString(1));
            storageItem.setNote(cursor.getString(2));
        }else {
            storageItem = null;

        }
        db.close();
        cursor.close();
        return storageItem;
    }

    /**
     * Metoda, která nastaví atribut is deleted na jedničku a tím bude skladová
     * položka označena za smazanou.
     *
     * @param storageItemId id skladové položky
     * @return
     */
    public synchronized boolean deleteStorageItemById(int storageItemId){
        String where = COLUMN_STORAGE_ITEM_ID + " = ?";

        String[] updateArgs = {Integer.toString(storageItemId)};

        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_STORAGE_ITEM_IS_DELETED, 1);
        values.put(COLUMN_STORAGE_ITEM_IS_DIRTY, 1);

        db.update(TABLE_STORAGE_ITEM, values, where, updateArgs);
        db.close();

        //smazání všech množství, které patří ke skladové položce
        ItemQuantityDatabaseHelper itemQuantityDatabaseHelper = new ItemQuantityDatabaseHelper(getContext());
        itemQuantityDatabaseHelper.deleteAllItemQuantityByStorageItemId(storageItemId);

        return true;
    }

    /**
     * Aktualizace skladové položky.
     *
     * @param storageItem skladová položka
     */
    public synchronized void updateStorageItemById(StorageItem storageItem){

        String where = COLUMN_STORAGE_ITEM_ID + " = ?";

        String[] updateArgs = {Integer.toString(storageItem.getId())};

        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_STORAGE_ITEM_NAME, storageItem.getName());
        values.put(COLUMN_STORAGE_ITEM_UNIT, storageItem.getUnit());
        values.put(COLUMN_STORAGE_ITEM_NOTE, storageItem.getNote());
        values.put(COLUMN_STORAGE_ITEM_IS_DIRTY, storageItem.getIsDirty());
        values.put(COLUMN_STORAGE_ITEM_IS_DELETED, storageItem.getIsDeleted());

        int result = db.update(TABLE_STORAGE_ITEM, values, where, updateArgs);
        System.out.print(result);
        db.close();

    }

    /**
     * Získání dato pro zobrazení skladových položek
     * do spinneru.
     *
     * @param userId id uživatele
     * @return  pole s daty o skladových položkách
     */
    public synchronized String[][] getStorageItemData(int userId){
        ArrayList<StorageItem> arrayList = getStorageItemByUserId(userId);
        String[][] storageData = new String[2][arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            storageData[0][i] = Integer.toString(arrayList.get(i).getId());
            storageData[1][i] = arrayList.get(i).getName();
        }
        return storageData;
    }

}
