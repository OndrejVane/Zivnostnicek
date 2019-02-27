package com.example.ondrejvane.zivnostnicek.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ondrejvane.zivnostnicek.helper.UserInformation;
import com.example.ondrejvane.zivnostnicek.model.TypeBill;

import java.util.ArrayList;

public class TypeBillDatabaseHelper extends DatabaseHelper {

    /**
     * Kontruktor type bill database helper
     *
     * @param context kontext aktivity
     */
    public TypeBillDatabaseHelper(Context context) {
        super(context);
    }

    /**
     * Přidání záznamu druhu faktury do tabulky druh. Pokud se jedná o
     * data ze serveru, tak negeneruje nové id.
     *
     * @param typeBill přidáváný prvek
     * @param isFromServer  logická hodnota, která údává, zda jsou data ze serveru
     * @return id přidané položky
     */
    public synchronized long addTypeBill(TypeBill typeBill, boolean isFromServer) {
        //pokud je záznam vkládán při synchronizaci se serverem, tak už id existuje. Nemusím ho generovat.
        if(!isFromServer){
            //získání primárního klíče
            IdentifiersDatabaseHelper identifiersDatabaseHelper = new IdentifiersDatabaseHelper(getContext());
            int typeId = identifiersDatabaseHelper.getFreeId(COLUMN_IDENTIFIERS_TYPE_ID);
            typeBill.setId(typeId);
        }
        SQLiteDatabase db = this.getWritableDatabase();
        long typeBillId;

        ContentValues values = new ContentValues();
        values.put(COLUMN_TYPE_ID, typeBill.getId());
        values.put(COLUMN_TYPE_USER_ID, typeBill.getUserId());
        values.put(COLUMN_TYPE_NAME, typeBill.getName());
        values.put(COLUMN_TYPE_COLOR, typeBill.getColor());
        values.put(COLUMN_TYPE_IS_DIRTY, typeBill.getIsDirty());
        values.put(COLUMN_TYPE_IS_DELETED, typeBill.getIsDeleted());
        typeBillId = db.insert(TABLE_TYPE, null, values);
        db.close();

        return typeBillId;
    }

    /**
     * Získání potžebných dat pro zobrazení typu faktur do spinneru.
     *
     * @param userId id uživatele
     * @return
     */
    public synchronized String[][] getTypeBillData(int userId) {
        String data[][];

        String[] columns = {COLUMN_TYPE_ID, COLUMN_TYPE_NAME};

        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_TYPE_USER_ID + " = ?" + " AND " + COLUMN_TYPE_IS_DELETED + " = ?";


        // selection arguments
        String[] selectionArgs = {Integer.toString(userId), "0"};

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

    /**
     * Získání typu faktury podle id.
     *
     * @param typeId id typu
     * @return typ faktury
     */
    public synchronized TypeBill getTypeBillById(int typeId) {
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

    /**
     *
     * Načtení všech typů podle id uživatele.
     *
     * @param userId id uživatele
     * @return  list typů faktur
     */
    public synchronized ArrayList<TypeBill> getAllTypeByUserId(int userId){
        ArrayList<TypeBill> billTypes = new ArrayList<>();

        String[] columns = {COLUMN_TYPE_ID, COLUMN_TYPE_NAME, COLUMN_TYPE_COLOR};

        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_TYPE_USER_ID + " = ?" + " AND " + COLUMN_TYPE_IS_DELETED + " = ?";

        // selection arguments
        String[] selectionArgs = {Integer.toString(userId), "0"};

        Cursor cursor = db.query(TABLE_TYPE, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);

        TypeBill noType = new TypeBill(-1, UserInformation.getInstance().getUserId(), "Nepřiřazeno", 1);
        billTypes.add(noType);
        if (cursor.moveToFirst()) {
            do {
                TypeBill typeBill = new TypeBill(cursor.getInt(0), userId, cursor.getString(1), cursor.getInt(2));
                billTypes.add(typeBill);
            } while (cursor.moveToNext());
        }
        db.close();
        cursor.close();

        return billTypes;
    }
}
