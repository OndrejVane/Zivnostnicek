package com.example.ondrejvane.zivnostnicek.model.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ondrejvane.zivnostnicek.session.UserInformation;
import com.example.ondrejvane.zivnostnicek.model.model_helpers.StorageItem;

import java.util.ArrayList;

public class StorageItemDatabaseHelper extends DatabaseHelper {


    /**
     * Konstruktor databázového pomocníka pro tabulku skladových položek
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
     * @param storageItem  skladová položka
     * @param isFromServer pokud jde o data ze serveru
     * @return id sladové položky
     */
    public synchronized long addStorageItem(StorageItem storageItem, boolean isFromServer) {
        //pokud je záznam vkládán při synchronizaci se serverem, tak už id existuje. Nemusím ho generovat.
        if (!isFromServer) {
            //získání primárního klíče
            IdentifiersDatabaseHelper identifiersDatabaseHelper = new IdentifiersDatabaseHelper(getContext());
            int storageItemId = identifiersDatabaseHelper.getFreeId(COLUMN_IDENTIFIERS_STORAGE_ITEM_ID);
            storageItem.setId(storageItemId);
        }

        SQLiteDatabase db = this.getWritableDatabase();
        long returnValue;

        //odstranění speciálních znaků kvůli SQL injection
        storageItem.removeSpecialChars();

        ContentValues values = new ContentValues();
        values.put(COLUMN_STORAGE_ITEM_ID, storageItem.getId());
        values.put(COLUMN_STORAGE_ITEM_USER_ID, storageItem.getUserId());
        values.put(COLUMN_STORAGE_ITEM_NAME, storageItem.getName());
        values.put(COLUMN_STORAGE_ITEM_UNIT, storageItem.getUnit());
        values.put(COLUMN_STORAGE_ITEM_NOTE, storageItem.getNote());
        values.put(COLUMN_STORAGE_ITEM_IS_DIRTY, storageItem.getIsDirty());
        values.put(COLUMN_STORAGE_ITEM_IS_DELETED, storageItem.getIsDeleted());
        db.insert(TABLE_STORAGE_ITEM, null, values);
        db.close();
        return storageItem.getId();
    }


    /**
     * Získání všech skladových položek uživatele podle id.
     *
     * @param userID id uživatele
     * @return list skladových položek
     */
    public synchronized ArrayList<StorageItem> getStorageItemByUserId(int userID) {

        ArrayList<StorageItem> storageItemsList = new ArrayList<>();

        String[] columns = {COLUMN_STORAGE_ITEM_ID, COLUMN_STORAGE_ITEM_NAME, COLUMN_STORAGE_ITEM_UNIT, COLUMN_STORAGE_ITEM_NOTE};

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

        if (cursor.moveToFirst()) {
            do {
                StorageItem storageItem = new StorageItem();
                storageItem.setId(cursor.getInt(0));
                storageItem.setName(cursor.getString(1));
                storageItem.setUnit(cursor.getString(2));
                storageItem.setNote(cursor.getString(3));
                storageItemsList.add(storageItem);
            } while (cursor.moveToNext());
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
    public synchronized StorageItem getStorageItemById(int storageItemId) {
        String[] columns = {COLUMN_STORAGE_ITEM_NAME, COLUMN_STORAGE_ITEM_UNIT, COLUMN_STORAGE_ITEM_NOTE};

        int userId = UserInformation.getInstance().getUserId();

        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_STORAGE_ITEM_ID + " = ? AND " + COLUMN_STORAGE_ITEM_USER_ID + " = ?";

        String[] selectionArgs = {Integer.toString(storageItemId), Integer.toString(userId)};

        Cursor cursor = db.query(TABLE_STORAGE_ITEM,    //Table to query
                columns,                                //columns to return
                selection,                              //columns for the WHERE clause
                selectionArgs,                          //The values for the WHERE clause
                null,                          //group the rows
                null,                           //filter by row groups
                null);

        StorageItem storageItem = new StorageItem();

        if (cursor.moveToFirst()) {
            storageItem.setId(storageItemId);
            storageItem.setName(cursor.getString(0));
            storageItem.setUnit(cursor.getString(1));
            storageItem.setNote(cursor.getString(2));
        } else {
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
     * @return logická hodnota, která určuje zda došlo ke smazání
     */
    public synchronized boolean deleteStorageItemById(int storageItemId) {
        String where = COLUMN_STORAGE_ITEM_ID + " = ? AND " + COLUMN_STORAGE_ITEM_USER_ID + " = ?";

        int userId = UserInformation.getInstance().getUserId();

        String[] updateArgs = {Integer.toString(storageItemId), Integer.toString(userId)};

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
    public synchronized void updateStorageItemById(StorageItem storageItem) {

        String where = COLUMN_STORAGE_ITEM_ID + " = ? AND " + COLUMN_STORAGE_ITEM_USER_ID + " = ?";

        int userId = UserInformation.getInstance().getUserId();

        String[] updateArgs = {Integer.toString(storageItem.getId()), Integer.toString(userId)};

        SQLiteDatabase db = this.getReadableDatabase();

        //odstranění speciálních znaků kvůli SQL injection
        storageItem.removeSpecialChars();

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
     * @return pole s daty o skladových položkách
     */
    public synchronized String[][] getStorageItemData(int userId) {
        ArrayList<StorageItem> arrayList = getStorageItemByUserId(userId);
        String[][] storageData = new String[2][arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            storageData[0][i] = Integer.toString(arrayList.get(i).getId());
            storageData[1][i] = arrayList.get(i).getName();
        }
        return storageData;
    }

    public synchronized ArrayList<StorageItem> getAllStorageItemsForSync() {

        ArrayList<StorageItem> arrayList = new ArrayList<>();
        int userId = UserInformation.getInstance().getUserId();

        String[] columns = {COLUMN_STORAGE_ITEM_ID, COLUMN_STORAGE_ITEM_NAME, COLUMN_STORAGE_ITEM_UNIT,
                COLUMN_STORAGE_ITEM_NOTE, COLUMN_STORAGE_ITEM_IS_DELETED};

        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_STORAGE_ITEM_USER_ID + " = ? AND " + COLUMN_STORAGE_ITEM_IS_DIRTY + " = 1";

        String[] selectionArgs = {Integer.toString(userId)};

        Cursor cursor = db.query(TABLE_STORAGE_ITEM, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);

        if (cursor.moveToFirst()) {
            do {
                StorageItem storageItem = new StorageItem();
                storageItem.setId(cursor.getInt(0));
                storageItem.setName(cursor.getString(1));
                storageItem.setUnit(cursor.getString(2));
                storageItem.setNote(cursor.getString(3));
                storageItem.setIsDeleted(cursor.getInt(4));
                storageItem.setUserId(userId);

                //přidání záznamu do listu
                arrayList.add(storageItem);

            } while (cursor.moveToNext());
        }

        db.close();
        cursor.close();

        return arrayList;
    }

    /**
     * Smazaní všech záznamu z tabulky skladového množství
     * uživatele s userId.
     *
     * @param userId id uživatelel
     */
    public synchronized void deleteAllStorageItemsByUserId(int userId) {
        String where = COLUMN_STORAGE_ITEM_USER_ID + " = ?";

        String[] deleteArgs = {Integer.toString(userId)};

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_STORAGE_ITEM, where, deleteArgs);

        db.close();
    }

    /**
     * Procedura pro označení všech zálohovaných
     * položek z tabulky skladových položek jako zálohované.
     *
     * @param userId id uživatele
     */
    public synchronized void setAllStorageItemsClear(int userId) {
        String where = COLUMN_STORAGE_ITEM_USER_ID + " = ? AND "
                + COLUMN_STORAGE_ITEM_IS_DIRTY + " = 1";

        String[] updateArgs = {Integer.toString(userId)};

        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_STORAGE_ITEM_IS_DIRTY, 0);

        db.update(TABLE_STORAGE_ITEM, values, where, updateArgs);
        db.close();
    }

    /**
     * Metoda pro získání maximální id v tabulce skladové položky.
     *
     * @return maximální id položkys
     */
    public synchronized int getMaxId() {

        int maxId = 1;

        String[] columns = {"MAX (" + COLUMN_STORAGE_ITEM_ID + ")"};

        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_STORAGE_ITEM_USER_ID + " = ? ";

        String[] selectionArgs = {Integer.toString(UserInformation.getInstance().getUserId())};

        Cursor cursor = db.query(TABLE_STORAGE_ITEM, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);

        if (cursor.moveToFirst()) {
            maxId = cursor.getInt(0);
        }

        db.close();
        cursor.close();

        return maxId;
    }
}
