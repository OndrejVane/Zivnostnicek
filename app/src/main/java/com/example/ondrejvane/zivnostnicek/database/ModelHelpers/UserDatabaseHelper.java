package com.example.ondrejvane.zivnostnicek.database.ModelHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ondrejvane.zivnostnicek.database.DatabaseHelper;
import com.example.ondrejvane.zivnostnicek.model.User;
import com.example.ondrejvane.zivnostnicek.utilities.EncryptionUtility;

public class UserDatabaseHelper extends DatabaseHelper {

    /**
     * Konstruktor databázového pomocníka pro tabulku uživatel
     *
     * @param context kontext aktivtiy
     */
    public UserDatabaseHelper(Context context) {
        super(context);
    }

    /**
     * @param user uživatel
     */
    public synchronized long addUser(User user) {
        long inserted = 0;
        if (getUserById(user.getId()) == null) {

            SQLiteDatabase db = this.getWritableDatabase();

            //odstranění speciálních znaků proti SQL injection
            user.removeSpecialChars();

            //zašifrování hesla pro uložení
            String encryptedPass = EncryptionUtility.encrypt(user.getPassword());

            ContentValues values = new ContentValues();
            values.put(COLUMN_USER_ID, user.getId());
            values.put(COLUMN_USER_FULL_NAME, user.getFullName());
            values.put(COLUMN_USER_EMAIL, user.getEmail());
            values.put(COLUMN_USER_PASSWORD, encryptedPass);
            values.put(COLUMN_USER_SYNC_NUMBER, user.getSyncNumber());

            // Inserting Row
            inserted = db.insert(TABLE_USER, null, values);
            db.close();

        }
        return inserted;
    }


    /**
     * Metoda pro získání uživatele podle id.
     *
     * @param userId id uživatelel
     * @return uživatel
     */
    public synchronized User getUserById(int userId) {
        User user = new User();

        String[] columns = {COLUMN_USER_FULL_NAME, COLUMN_USER_EMAIL, COLUMN_USER_PASSWORD, COLUMN_USER_SYNC_NUMBER};

        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_USER_ID + " = ?";

        String[] selectionArgs = {Integer.toString(userId)};

        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);


        if (cursor.moveToFirst()) {
            user.setId(userId);
            user.setFullName(cursor.getString(0));
            user.setEmail(cursor.getString(1));
            user.setPassword(EncryptionUtility.decrypt(cursor.getString(2)));
            user.setSyncNumber(cursor.getInt(3));
        } else {
            user = null;
        }


        db.close();
        cursor.close();

        return user;
    }

    /**
     * Metoda, pro získání synchronizačního čísla uživatele.
     *
     * @param userId id uživatele
     * @return synchronizační číslo
     */
    public int getSyncNumberByUserId(int userId) {
        int syncNumber = 0;

        String[] columns = {COLUMN_USER_SYNC_NUMBER};

        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_USER_ID + " = ?";

        String[] selectionArgs = {Integer.toString(userId)};

        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);


        if (cursor.moveToFirst()) {
            syncNumber = cursor.getInt(0);
        }

        db.close();
        cursor.close();

        return syncNumber;
    }

    /**
     * Metoda, která smaže všechna data uživatele před nahráním dat ze serveru.
     *
     * @param userId id uživatele
     */
    public synchronized void deleteAllUserData(int userId) {
        //inicializace pomocníku databáze
        TraderDatabaseHelper traderDatabaseHelper = new TraderDatabaseHelper(getContext());
        NoteDatabaseHelper noteDatabaseHelper = new NoteDatabaseHelper(getContext());
        TypeBillDatabaseHelper typeBillDatabaseHelper = new TypeBillDatabaseHelper(getContext());
        BillDatabaseHelper billDatabaseHelper = new BillDatabaseHelper(getContext());
        StorageItemDatabaseHelper storageItemDatabaseHelper = new StorageItemDatabaseHelper(getContext());
        ItemQuantityDatabaseHelper itemQuantityDatabaseHelper = new ItemQuantityDatabaseHelper(getContext());

        traderDatabaseHelper.deleteAllTradersByUserId(userId);
        noteDatabaseHelper.deleteAllNotesByUserId(userId);
        typeBillDatabaseHelper.deleteAllTypesByUserId(userId);
        billDatabaseHelper.deleteAllBillsByUserId(userId);
        storageItemDatabaseHelper.deleteAllStorageItemsByUserId(userId);
        itemQuantityDatabaseHelper.deleteAllItemQuantitiesByUserId(userId);
    }

    /**
     * Metoda která volá ostaní metoda databázovýxh pomocníků pro nastavení
     * všech záznamů jako zálohované.
     *
     * @param userId id uživatele
     */
    public synchronized void setAllRecordsClear(int userId) {
        //inicializace pomocníku databáze
        TraderDatabaseHelper traderDatabaseHelper = new TraderDatabaseHelper(getContext());
        NoteDatabaseHelper noteDatabaseHelper = new NoteDatabaseHelper(getContext());
        TypeBillDatabaseHelper typeBillDatabaseHelper = new TypeBillDatabaseHelper(getContext());
        BillDatabaseHelper billDatabaseHelper = new BillDatabaseHelper(getContext());
        StorageItemDatabaseHelper storageItemDatabaseHelper = new StorageItemDatabaseHelper(getContext());
        ItemQuantityDatabaseHelper itemQuantityDatabaseHelper = new ItemQuantityDatabaseHelper(getContext());

        traderDatabaseHelper.setAllTradersClear(userId);
        noteDatabaseHelper.setAllNotesClear(userId);
        typeBillDatabaseHelper.setAllTypeBillsClear(userId);
        billDatabaseHelper.setAllBillsClear(userId);
        storageItemDatabaseHelper.setAllStorageItemsClear(userId);
        itemQuantityDatabaseHelper.setAllItemQuantitiesClear(userId);
    }
}
