package com.example.ondrejvane.zivnostnicek.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ondrejvane.zivnostnicek.activities.trader.TraderNewActivity;
import com.example.ondrejvane.zivnostnicek.helper.UserInformation;
import com.example.ondrejvane.zivnostnicek.model.Trader;

public class TraderDatabaseHelper extends DatabaseHelper {

    private static final String TABLE_TRADER = "trader";

    //Názvy atributů v tabulce trader
    private static final String COLUMN_TRADER_ID = "trader_id";                             //Primární klíč
    private static final String COLUMN_TRADER_USER_ID = "trader_user_id";                   //Cizí klíč
    private static final String COLUMN_TRADER_NAME = "trader_name";                         //Název firmy
    private static final String COLUMN_TRADER_PHONE_NUMBER = "trader_phone_number";         //Telefoní číslo
    private static final String COLUMN_TRADER_CONTACT_PERSON = "trader_contact_person";     //Kontaktní osoba
    private static final String COLUMN_TRADER_IN = "trader_in";                             //IČO
    private static final String COLUMN_TRADER_TIN = "trader_tin";                           //DIČ
    private static final String COLUMN_TRADER_CITY = "trader_city";                         //Město obchodníka
    private static final String COLUMN_TRADER_STREET = "trader_street";                     //Ulice obchodníka
    private static final String COLUMN_TRADER_HOUSE_NUMBER = "trader_house_number";         //Číslo popisné obchodníka


    /**
     * Constructor
     *
     * @param context context
     */
    public TraderDatabaseHelper(Context context) {
        super(context);
    }

    public void addTrader(Trader trader){
        SQLiteDatabase db = this.getWritableDatabase();

        UserInformation userInformation = UserInformation.getInstance();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TRADER_USER_ID, userInformation.getUserId());     //cizí klíč
        values.put(COLUMN_TRADER_NAME, trader.getTraderName());
        values.put(COLUMN_TRADER_CONTACT_PERSON, trader.getTraderContactPerson());
        values.put(COLUMN_TRADER_PHONE_NUMBER, trader.getTraderPhoneNumber());
        values.put(COLUMN_TRADER_IN, trader.getTraderIN());
        values.put(COLUMN_TRADER_TIN, trader.getTraderTIN());
        values.put(COLUMN_TRADER_CITY, trader.getTraderCity());
        values.put(COLUMN_TRADER_STREET, trader.getTraderStreet());
        values.put(COLUMN_TRADER_HOUSE_NUMBER, trader.getTraderHouseNumber());
        db.insert(TABLE_TRADER, null, values);
        db.close();
    }

    public String[][] getTradersData(int userID){
        String data[][];

        String[] columns = { COLUMN_TRADER_ID, COLUMN_TRADER_NAME, COLUMN_TRADER_CONTACT_PERSON};

        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_TRADER_USER_ID + " = ?";

        String orderBy = COLUMN_TRADER_NAME + " ASC";

        // selection arguments
        String[] selectionArgs = {Integer.toString(userID)};

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

    public Trader getTraderById(int traderId){

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
            trader.setTraderName(cursor.getString(0));
            trader.setTraderContactPerson(cursor.getString(1));
            trader.setTraderPhoneNumber(cursor.getString(2));
            trader.setTraderIN(cursor.getString(3));
            trader.setTraderTIN(cursor.getString(4));
            trader.setTraderCity(cursor.getString(5));
            trader.setTraderStreet(cursor.getString(6));
            trader.setTraderHouseNumber(cursor.getString(7));
        }

        db.close();
        cursor.close();

        return trader;

    }

    public boolean deleteTraderById(int traderId){

        boolean result;

        String where = COLUMN_TRADER_ID + " = ?";

        String[] deleteArgs = {Integer.toString(traderId)};

        SQLiteDatabase db = this.getReadableDatabase();

        NoteDatabaseHelper noteDatabaseHelper =  new NoteDatabaseHelper(getContext());
        noteDatabaseHelper.deleteNotesByTraderId(traderId);

        result = db.delete(TABLE_TRADER, where, deleteArgs) > 0;

        return result;
    }

    public void updateTraderById(Trader trader){

        String where = COLUMN_TRADER_ID + " = ?";

        String[] updateArgs = {Integer.toString(trader.getId())};

        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TRADER_NAME, trader.getTraderName());
        values.put(COLUMN_TRADER_CONTACT_PERSON, trader.getTraderContactPerson());
        values.put(COLUMN_TRADER_PHONE_NUMBER, trader.getTraderPhoneNumber());
        values.put(COLUMN_TRADER_IN, trader.getTraderIN());
        values.put(COLUMN_TRADER_TIN, trader.getTraderTIN());
        values.put(COLUMN_TRADER_CITY, trader.getTraderCity());
        values.put(COLUMN_TRADER_STREET, trader.getTraderStreet());
        values.put(COLUMN_TRADER_HOUSE_NUMBER, trader.getTraderHouseNumber());

        db.update(TABLE_TRADER, values, where, updateArgs);
        db.close();

    }
}
