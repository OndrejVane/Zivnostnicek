package com.example.ondrejvane.zivnostnicek.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ondrejvane.zivnostnicek.helper.UserInformation;
import com.example.ondrejvane.zivnostnicek.model.Trader;

public class TraderDatabaseHelper extends DatabaseHelper {


    /**
     * Konstruktor pro trader database helper
     *
     * @param context context
     */
    public TraderDatabaseHelper(Context context) {
        super(context);
    }

    /**
     * Přidání obchodníka do databázace
     *
     * @param trader trader
     */
    public synchronized void addTrader(Trader trader){
        SQLiteDatabase db = this.getWritableDatabase();

        UserInformation userInformation = UserInformation.getInstance();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TRADER_USER_ID, userInformation.getUserId());     //cizí klíč
        values.put(COLUMN_TRADER_NAME, trader.getName());
        values.put(COLUMN_TRADER_CONTACT_PERSON, trader.getContactPerson());
        values.put(COLUMN_TRADER_PHONE_NUMBER, trader.getPhoneNumber());
        values.put(COLUMN_TRADER_IN, trader.getIN());
        values.put(COLUMN_TRADER_TIN, trader.getTIN());
        values.put(COLUMN_TRADER_CITY, trader.getCity());
        values.put(COLUMN_TRADER_STREET, trader.getStreet());
        values.put(COLUMN_TRADER_HOUSE_NUMBER, trader.getHouseNumber());
        values.put(COLUMN_TRADER_IS_DIRTY, trader.getIsDirty());
        values.put(COLUMN_TRADER_IS_DELETED, trader.getIsDeleted());
        db.insert(TABLE_TRADER, null, values);
        db.close();
    }

    /**
     * Získání dat pro zobrazení do listu.
     *
     * @param userID    id uživatele
     * @return          pole všech potřebných údajů do listu
     */
    public synchronized String[][] getTradersData(int userID){
        String data[][];

        String[] columns = { COLUMN_TRADER_ID, COLUMN_TRADER_NAME, COLUMN_TRADER_CONTACT_PERSON};

        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_TRADER_USER_ID + " = ? AND " + COLUMN_TRADER_IS_DELETED + " = ?";

        String orderBy = COLUMN_TRADER_NAME + " ASC";

        // selection arguments
        String[] selectionArgs = {Integer.toString(userID), "0"};

        Cursor cursor = db.query(TABLE_TRADER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                orderBy);
        int count = cursor.getCount();
        data = new String[3][count];
        int i = 0;

        if (cursor.moveToFirst()){
            do{
                data[0][i] = cursor.getString(0);
                data[1][i] = cursor.getString(1);
                data[2][i] = cursor.getString(2);
                i++;
            }while (cursor.moveToNext());
        }
        db.close();
        cursor.close();

        return data;
    }

    /**
     * Metoda, která nalezne obchodníka se příslušnám id.
     *
     * @param traderId id obchodníka
     * @return  obchodník
     */
    public synchronized Trader getTraderById(int traderId){

        Trader trader = new Trader();

        String[] columns = {COLUMN_TRADER_NAME, COLUMN_TRADER_CONTACT_PERSON,
                COLUMN_TRADER_PHONE_NUMBER, COLUMN_TRADER_IN, COLUMN_TRADER_TIN,
                COLUMN_TRADER_CITY, COLUMN_TRADER_STREET, COLUMN_TRADER_HOUSE_NUMBER};

        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_TRADER_ID + " = ?";

        String[] selectionArgs = {Integer.toString(traderId)};

        Cursor cursor = db.query(TABLE_TRADER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);

        if(cursor.moveToFirst()){
            trader.setName(cursor.getString(0));
            trader.setContactPerson(cursor.getString(1));
            trader.setPhoneNumber(cursor.getString(2));
            trader.setIN(cursor.getString(3));
            trader.setTIN(cursor.getString(4));
            trader.setCity(cursor.getString(5));
            trader.setStreet(cursor.getString(6));
            trader.setHouseNumber(cursor.getString(7));
        }

        db.close();
        cursor.close();

        return trader;

    }

    /**
     * Meotda, která "smaže" obchodníka z databáze s příslušným id.
     * Nedojde k fyzickému smazání, pouze k nastavení příznaku is delete na
     * 1. Záznam budu potřebovat pro synchronizaci na server. Dojde také
     * ke "smazání" všech poznámek, které patří obhodníkovi.
     *
     * @param traderId id obchodníka
     * @return  zda došo ke smazání obchodníka
     */
    public synchronized boolean deleteTraderById(int traderId){
        String where = COLUMN_TRADER_ID + " = ?";

        String[] updateArgs = {Integer.toString(traderId)};

        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TRADER_IS_DELETED, 1);
        values.put(COLUMN_TRADER_IS_DIRTY, 1);

        db.update(TABLE_TRADER, values, where, updateArgs);
        db.close();

        NoteDatabaseHelper noteDatabaseHelper = new NoteDatabaseHelper(getContext());
        noteDatabaseHelper.deleteNotesByTraderId(traderId);

        return true;
    }

    /**
     * Aktualizace obchodníka.
     *
     * @param trader aktualizovaný obchodník
     */
    public synchronized void updateTraderById(Trader trader){

        String where = COLUMN_TRADER_ID + " = ?";

        String[] updateArgs = {Integer.toString(trader.getId())};

        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TRADER_NAME, trader.getName());
        values.put(COLUMN_TRADER_CONTACT_PERSON, trader.getContactPerson());
        values.put(COLUMN_TRADER_PHONE_NUMBER, trader.getPhoneNumber());
        values.put(COLUMN_TRADER_IN, trader.getIN());
        values.put(COLUMN_TRADER_TIN, trader.getTIN());
        values.put(COLUMN_TRADER_CITY, trader.getCity());
        values.put(COLUMN_TRADER_STREET, trader.getStreet());
        values.put(COLUMN_TRADER_HOUSE_NUMBER, trader.getHouseNumber());
        values.put(COLUMN_TRADER_IS_DIRTY, trader.getIsDirty());
        values.put(COLUMN_TRADER_IS_DELETED, trader.getIsDeleted());

        db.update(TABLE_TRADER, values, where, updateArgs);
        db.close();

    }
}
