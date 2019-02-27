package com.example.ondrejvane.zivnostnicek.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.helper.UserInformation;

public class IdentifiersDatabaseHelper extends DatabaseHelper {

    private static final String TAG = "Identifiers db";

    /**
     * Konstruktor identifiers database helper
     *
     * @param context kontext aktivity
     */
    public IdentifiersDatabaseHelper(Context context) {
        super(context);
    }

    public void addIdentifiersForUser(long currentFreeId, long maxId){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, UserInformation.getInstance().getUserId());
        values.put(COLUMN_IDENTIFIERS_BILL_ID, currentFreeId);
        values.put(COLUMN_IDENTIFIERS_TRADER_ID, currentFreeId);
        values.put(COLUMN_IDENTIFIERS_ITEM_QUANTITY_ID, currentFreeId);
        values.put(COLUMN_IDENTIFIERS_STORAGE_ITEM_ID, currentFreeId);
        values.put(COLUMN_IDENTIFIERS_TYPE_ID, currentFreeId);
        values.put(COLUMN_IDENTIFIERS_NOTE_ID, currentFreeId);
        values.put(COLUMN_IDENTIFIERS_MAX_ID, maxId);

        db.insert(TABLE_IDENTIFIERS, null, values);
        db.close();
    }


    /**
     * Metoda, která vrací aktuální volne id podle zadaného sloupce.
     *
     * @param columnName název sloupce
     * @return          volné id
     */
    public int getFreeId(String columnName) {
        int freeId = 0;
        int maxId = 0;

        String[] columns = {columnName, COLUMN_IDENTIFIERS_MAX_ID};

        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_USER_ID + " = ? ";

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
            maxId = cursor.getInt(1);
        }
        db.close();
        cursor.close();

        //kontrola, zda není id mimo rozsah
        if(freeId > maxId){

            Toast.makeText(getContext().getApplicationContext(), "Záznam není možno vložit do databáze ", Toast.LENGTH_SHORT).show();
            freeId = -1;
        }else {
            //zvyšení id, aby byla unikátnív databázi
            incrementValueOfId(freeId, columnName);
        }

        return freeId;
    }

    /**
     * Metoda, která navyšuje volné id podel zadaného sloupce.
     *
     * @param currentId právě použite id
     * @param columnName název sloupce
     */
    private void incrementValueOfId(int currentId, String columnName) {
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
}
