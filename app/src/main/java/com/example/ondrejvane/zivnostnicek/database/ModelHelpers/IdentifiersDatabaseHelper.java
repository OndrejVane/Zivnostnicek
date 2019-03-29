package com.example.ondrejvane.zivnostnicek.database.ModelHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.ondrejvane.zivnostnicek.database.DatabaseHelper;
import com.example.ondrejvane.zivnostnicek.session.UserInformation;

public class IdentifiersDatabaseHelper extends DatabaseHelper {

    private static final String TAG = "Identifiers db";

    /**
     * Konstruktor pro databázového pomocníka identifikátorů
     *
     * @param context kontext aktivity
     */
    public IdentifiersDatabaseHelper(Context context) {
        super(context);
    }

    public synchronized void addIdentifiersForUser(long currentFreeId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_IDENTIFIERS_USER_ID, UserInformation.getInstance().getUserId());
        values.put(COLUMN_IDENTIFIERS_BILL_ID, currentFreeId);
        values.put(COLUMN_IDENTIFIERS_TRADER_ID, currentFreeId);
        values.put(COLUMN_IDENTIFIERS_ITEM_QUANTITY_ID, currentFreeId);
        values.put(COLUMN_IDENTIFIERS_STORAGE_ITEM_ID, currentFreeId);
        values.put(COLUMN_IDENTIFIERS_TYPE_ID, currentFreeId);
        values.put(COLUMN_IDENTIFIERS_NOTE_ID, currentFreeId);

        db.insert(TABLE_IDENTIFIERS, null, values);
        db.close();
    }


    /**
     * Metoda, která vrací aktuální volne id podle zadaného sloupce.
     *
     * @param columnName název sloupce
     * @return volné id
     */
    public synchronized int getFreeId(String columnName) {
        int freeId = 0;

        String[] columns = {columnName};

        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_IDENTIFIERS_USER_ID + " = ? ";

        // vybírá vždy první záznam tabulky
        String[] selectionArgs = {Integer.toString(UserInformation.getInstance().getUserId())};

        Cursor cursor = db.query(TABLE_IDENTIFIERS,    //Table to query
                columns,                                //columns to return
                selection,                              //columns for the WHERE clause
                selectionArgs,                          //The values for the WHERE clause
                null,                          //group the rows
                null,                           //filter by row groups
                null);

        if (cursor.moveToFirst()) {
            freeId = cursor.getInt(0);
        }
        db.close();
        cursor.close();

        incrementValueOfId(freeId, columnName);

        return freeId;
    }

    /**
     * Metoda, která navyšuje volné id podel zadaného sloupce.
     *
     * @param currentId  právě použite id
     * @param columnName název sloupce
     */
    private synchronized void incrementValueOfId(int currentId, String columnName) {
        String where = COLUMN_IDENTIFIERS_USER_ID + " = ?";

        currentId++;

        String[] updateArgs = {Integer.toString(UserInformation.getInstance().getUserId())};

        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();

        Log.d(TAG, "Free " + columnName + " id: " + currentId);
        values.put(columnName, currentId);

        db.update(TABLE_IDENTIFIERS, values, where, updateArgs);
        db.close();
    }

    /**
     * Procedura, která obnoví aktuální identifikátory podle
     * všech ostatních tabulek.
     */
    public synchronized void refreshIdentifiers() {
        TraderDatabaseHelper traderDatabaseHelper = new TraderDatabaseHelper(getContext());
        BillDatabaseHelper billDatabaseHelper = new BillDatabaseHelper(getContext());
        ItemQuantityDatabaseHelper itemQuantityDatabaseHelper = new ItemQuantityDatabaseHelper(getContext());
        NoteDatabaseHelper noteDatabaseHelper = new NoteDatabaseHelper(getContext());
        StorageItemDatabaseHelper storageItemDatabaseHelper = new StorageItemDatabaseHelper(getContext());
        TypeBillDatabaseHelper typeBillDatabaseHelper = new TypeBillDatabaseHelper(getContext());


        int maxTraderId = traderDatabaseHelper.getMaxId() + 1;
        int maxBillId = billDatabaseHelper.getMaxId() + 1;
        int maxItemQuantityId = itemQuantityDatabaseHelper.getMaxId() + 1;
        int maxNoteId = noteDatabaseHelper.getMaxId() + 1;
        int maxStorageItemId = storageItemDatabaseHelper.getMaxId() + 1;
        int maxTypeBillId = typeBillDatabaseHelper.getMaxId() + 1;


        String where = COLUMN_IDENTIFIERS_USER_ID + " = ? ";

        int userId = UserInformation.getInstance().getUserId();

        String[] updateArgs = {Integer.toString(userId)};

        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_IDENTIFIERS_TRADER_ID, maxTraderId);
        values.put(COLUMN_IDENTIFIERS_BILL_ID, maxBillId);
        values.put(COLUMN_IDENTIFIERS_ITEM_QUANTITY_ID, maxItemQuantityId);
        values.put(COLUMN_IDENTIFIERS_NOTE_ID, maxNoteId);
        values.put(COLUMN_IDENTIFIERS_STORAGE_ITEM_ID, maxStorageItemId);
        values.put(COLUMN_IDENTIFIERS_TYPE_ID, maxTypeBillId);

        db.update(TABLE_IDENTIFIERS, values, where, updateArgs);
        db.close();


    }
}
