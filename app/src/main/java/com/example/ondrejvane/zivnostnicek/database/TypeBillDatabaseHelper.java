package com.example.ondrejvane.zivnostnicek.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ondrejvane.zivnostnicek.model.TypeBill;

public class TypeBillDatabaseHelper extends DatabaseHelper {

    /**
     * Constructor
     *
     * @param context context
     */
    public TypeBillDatabaseHelper(Context context) {
        super(context);
    }

    /**
     * Přidání záznamu druhu faktury do tabulky druh.
     *
     * @param typeBill přidáváný prvek
     * @return id přidané položky
     */
    public long addTypeBill(TypeBill typeBill) {
        SQLiteDatabase db = this.getWritableDatabase();
        long typeBillId;

        ContentValues values = new ContentValues();
        values.put(COLUMN_TYPE_USER_ID, typeBill.getUserId());
        values.put(COLUMN_TYPE_NAME, typeBill.getName());
        values.put(COLUMN_TYPE_COLOR, typeBill.getColor());
        typeBillId = db.insert(TABLE_TYPE, null, values);
        db.close();

        return typeBillId;
    }

    public String[][] getTypeBillData(int userId) {
        String data[][];

        String[] columns = {COLUMN_TYPE_ID, COLUMN_TYPE_NAME};

        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_TYPE_USER_ID + " = ?";


        // selection arguments
        String[] selectionArgs = {Integer.toString(userId)};

        Cursor cursor = db.query(TABLE_TYPE, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);

        int count = cursor.getCount();
        data = new String[2][count];
        int i = 0;

        if (cursor.moveToFirst()) {
            do {
                data[0][i] = cursor.getString(0);
                data[1][i] = cursor.getString(1);
                i++;
            } while (cursor.moveToNext());
        }
        db.close();
        cursor.close();

        return data;
    }

    public TypeBill getTypeBillById(int typeId) {
        TypeBill typeBill = new TypeBill();

        String[] columns = {COLUMN_TYPE_ID, COLUMN_TYPE_NAME, COLUMN_TYPE_COLOR, COLUMN_TYPE_USER_ID};

        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_TYPE_ID + " = ?";

        // selection arguments
        String[] selectionArgs = {Integer.toString(typeId)};

        Cursor cursor = db.query(TABLE_TYPE, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);



        if (cursor.moveToFirst()) {
            typeBill.setId(typeId);
            typeBill.setName(cursor.getString(1));
            typeBill.setColor(cursor.getInt(2));
            typeBill.setUserId(cursor.getInt(3));
        }
        db.close();
        cursor.close();

        return typeBill;
    }
}
