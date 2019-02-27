package com.example.ondrejvane.zivnostnicek.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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


    /**
     * Metoda, která vrací aktuální volne id podle zadaného sloupce.
     *
     * @param columnName název sloupce
     * @return          volné id
     */
    public int getFreeId(String columnName) {
        int freeId = 0;

        String[] columns = {columnName};

        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_IDENTIFIERS_ID + " = ?";

        // vybírá vždy první záznam tabulky
        String[] selectionArgs = {"1"};

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

        //zvyšení id, aby byla unikátnív databázi
        incrementValueOfTraderId(freeId, columnName);

        return freeId;
    }

    /**
     * Metoda, která navyšuje volné id podel zadaného sloupce.
     *
     * @param currentId právě použite id
     * @param columnName název sloupce
     */
    private void incrementValueOfTraderId(int currentId, String columnName) {
        String where = COLUMN_IDENTIFIERS_ID + " = ?";

        currentId++;

        String[] updateArgs = {"1"};

        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        Log.d(TAG, "Free " + columnName + " id: " + currentId);
        values.put(columnName, currentId);

        db.update(TABLE_IDENTIFIERS, values, where, updateArgs);
        db.close();
    }
}
