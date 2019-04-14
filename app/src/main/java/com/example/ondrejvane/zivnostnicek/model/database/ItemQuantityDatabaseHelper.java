package com.example.ondrejvane.zivnostnicek.model.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ondrejvane.zivnostnicek.session.UserInformation;
import com.example.ondrejvane.zivnostnicek.model.model_helpers.StorageItemBox;
import com.example.ondrejvane.zivnostnicek.model.model_helpers.ItemQuantity;
import com.example.ondrejvane.zivnostnicek.model.model_helpers.StorageItem;

import java.util.ArrayList;

public class ItemQuantityDatabaseHelper extends DatabaseHelper {

    /**
     * Konstruktor databázového pomocníka pro tabulku skladového množství
     *
     * @param context kontext aktivity
     */
    public ItemQuantityDatabaseHelper(Context context) {
        super(context);
    }

    /**
     * Přidání skladového množsví. Pokud jsou data přidávána zeserveru, tak se negeruje
     * id, protože již existuje.
     *
     * @param itemQuantity skladové množství
     * @param isFromServer logická hodnota, která označuje, zda jde o data ze serveru
     */
    public synchronized void addItemQuantity(ItemQuantity itemQuantity, boolean isFromServer) {
        //pokud je záznam vkládán při synchronizaci se serverem, tak už id existuje. Nemusím ho generovat.
        if (!isFromServer) {
            //získání primárního klíče
            IdentifiersDatabaseHelper identifiersDatabaseHelper = new IdentifiersDatabaseHelper(getContext());
            int itemQuantityId = identifiersDatabaseHelper.getFreeId(COLUMN_IDENTIFIERS_ITEM_QUANTITY_ID);
            itemQuantity.setId(itemQuantityId);
        }

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_QUANTITY_ID, itemQuantity.getId());
        values.put(COLUMN_ITEM_QUANTITY_USER_ID, UserInformation.getInstance().getUserId());
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
     * @return množství skladové položky
     */
    public synchronized float getQuantityWithStorageItemId(int storageItemId) {
        float quantity = 0;

        int userId = UserInformation.getInstance().getUserId();

        String[] columns = {COLUMN_ITEM_QUANTITY_QUANTITY};

        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_ITEM_QUANTITY_STORAGE_ITEM_ID + " = ?" + " AND "
                + COLUMN_ITEM_QUANTITY_IS_DELETED + " = ? AND " + COLUMN_ITEM_QUANTITY_USER_ID + " = ?";

        // selection arguments
        String[] selectionArgs = {Integer.toString(storageItemId), "0", Integer.toString(userId)};

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
     * @return list množství skladové položky
     */
    public synchronized ArrayList<ItemQuantity> getItemQuantityByBillId(int billId) {
        ArrayList<ItemQuantity> arrayList = new ArrayList<>();

        int userId = UserInformation.getInstance().getUserId();

        String[] columns = {COLUMN_ITEM_QUANTITY_ID, COLUMN_ITEM_QUANTITY_QUANTITY, COLUMN_ITEM_QUANTITY_STORAGE_ITEM_ID};

        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_ITEM_QUANTITY_BILL_ID + " = ?" + " AND "
                + COLUMN_ITEM_QUANTITY_IS_DELETED + " = ? AND " + COLUMN_ITEM_QUANTITY_USER_ID + " = ?";

        // selection arguments
        String[] selectionArgs = {Integer.toString(billId), "0", Integer.toString(userId)};

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
    public boolean deleteItemQuantityById(int itemId) {
        String where = COLUMN_ITEM_QUANTITY_ID + " = ? AND " + COLUMN_ITEM_QUANTITY_USER_ID + " = ?";

        int userId = UserInformation.getInstance().getUserId();

        String[] updateArgs = {Integer.toString(itemId), Integer.toString(userId)};

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
     * @return list skladových položek a množství
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
    public synchronized void deleteAllItemQuantityByStorageItemId(int storageItemId) {
        String where = COLUMN_ITEM_QUANTITY_ID + " = ? AND " + COLUMN_ITEM_QUANTITY_USER_ID + " = ?";

        int userId = UserInformation.getInstance().getUserId();

        String[] updateArgs = {Integer.toString(storageItemId), Integer.toString(userId)};

        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_QUANTITY_IS_DELETED, 1);
        values.put(COLUMN_ITEM_QUANTITY_IS_DIRTY, 1);

        db.update(TABLE_ITEM_QUANTITY, values, where, updateArgs);
        db.close();

    }


    /**
     * Metoda, která vytvoří spojový seznam všech nesynchronizovaných záznamů
     * v databázi a vrátí jej jako návratovou hodnotu.
     *
     * @return spojový seznam skladového množství k synchronizaci
     */
    public synchronized ArrayList<ItemQuantity> getAllItemQuantitiesForSync() {
        ArrayList<ItemQuantity> arrayList = new ArrayList<>();

        int userId = UserInformation.getInstance().getUserId();

        String[] columns = {COLUMN_ITEM_QUANTITY_ID, COLUMN_ITEM_QUANTITY_STORAGE_ITEM_ID,
                COLUMN_ITEM_QUANTITY_BILL_ID, COLUMN_ITEM_QUANTITY_QUANTITY,
                COLUMN_ITEM_QUANTITY_IS_DELETED};

        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_ITEM_QUANTITY_USER_ID + " = ? AND " + COLUMN_ITEM_QUANTITY_IS_DIRTY + " = 1";

        String[] selectionArgs = {Integer.toString(userId)};

        Cursor cursor = db.query(TABLE_ITEM_QUANTITY, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);

        if (cursor.moveToFirst()) {
            do {
                ItemQuantity itemQuantity = new ItemQuantity();
                itemQuantity.setId(cursor.getInt(0));
                itemQuantity.setStorageItemId(cursor.getInt(1));
                itemQuantity.setBillId(cursor.getLong(2));
                itemQuantity.setQuantity(cursor.getFloat(3));
                itemQuantity.setIsDeleted(cursor.getInt(4));
                itemQuantity.setUserId(userId);

                arrayList.add(itemQuantity);
            } while (cursor.moveToNext());

        }

        db.close();
        cursor.close();

        return arrayList;
    }

    /**
     * Smazání všech položek skladového množství z databáze.
     *
     * @param userId id přihlášeného uživatele
     */
    public synchronized void deleteAllItemQuantitiesByUserId(int userId) {
        String where = COLUMN_ITEM_QUANTITY_USER_ID + " = ?";

        String[] deleteArgs = {Integer.toString(userId)};

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_ITEM_QUANTITY, where, deleteArgs);

        db.close();
    }

    /**
     * Nastavení všech záznamu v databázi jako zálohovaných.
     *
     * @param userId id uživatele
     */
    public synchronized void setAllItemQuantitiesClear(int userId) {
        String where = COLUMN_ITEM_QUANTITY_USER_ID + " = ? AND "
                + COLUMN_ITEM_QUANTITY_IS_DIRTY + " = 1";

        String[] updateArgs = {Integer.toString(userId)};

        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_QUANTITY_IS_DIRTY, 0);

        db.update(TABLE_ITEM_QUANTITY, values, where, updateArgs);
        db.close();
    }

    /**
     * Metoda, která vrátí maximální id v tabulce skladového množství.
     *
     * @return maximální id v tabulce
     */
    public synchronized int getMaxId() {

        int maxId = 1;

        String[] columns = {"MAX (" + COLUMN_ITEM_QUANTITY_ID + ")"};

        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_ITEM_QUANTITY_USER_ID + " = ? ";

        String[] selectionArgs = {Integer.toString(UserInformation.getInstance().getUserId())};

        Cursor cursor = db.query(TABLE_ITEM_QUANTITY, //Table to query
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
