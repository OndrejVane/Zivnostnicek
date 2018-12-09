package com.example.ondrejvane.zivnostnicek.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.Settings;

import com.example.ondrejvane.zivnostnicek.helper.UserInformation;
import com.example.ondrejvane.zivnostnicek.model.Trader;
import com.example.ondrejvane.zivnostnicek.model.User;


public class DatabaseHelper extends SQLiteOpenHelper {

    // Verze databáze
    private static final int DATABASE_VERSION = 3;

    // Název databáze
    private static final String DATABASE_NAME = "Zivnostnicek.db";

    // Názvy jednotlivých tabulke
    private static final String TABLE_USER = "user";
    private static final String TABLE_TRADER = "trader";

    // Názvy atributů v tabulce user
    private static final String COLUMN_USER_ID = "user_id";                                 //Primární klíč
    private static final String COLUMN_USER_FULL_NAME = "user_full_name";                   //Jméno uživatele
    private static final String COLUMN_USER_EMAIL = "user_email";                           //Mail uživatele
    private static final String COLUMN_USER_PASSWORD = "user_password";                     //Heslo uživatele

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

    //SQL pro vytvoření tabulky User
    private String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_USER_FULL_NAME + " TEXT,"
            + COLUMN_USER_EMAIL + " TEXT," + COLUMN_USER_PASSWORD + " TEXT" + ")";

    //SQL pro vytvoření tabulky Trader
    private String CREATE_TRADER_TABLE = "CREATE TABLE " + TABLE_TRADER + "("
            + COLUMN_TRADER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_TRADER_USER_ID + " INTEGER,"
            + COLUMN_TRADER_NAME + " TEXT," + COLUMN_TRADER_PHONE_NUMBER + " TEXT," + COLUMN_TRADER_CONTACT_PERSON + " TEXT,"
            + COLUMN_TRADER_IN + " TEXT," + COLUMN_TRADER_TIN + " TEXT," + COLUMN_TRADER_CITY + " TEXT,"
            + COLUMN_TRADER_STREET + " TEXT," + COLUMN_TRADER_HOUSE_NUMBER + " INTEGER" + ")";


    // drop table user
    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER;

    // drop table trader
    private String DROP_TRADER_TABLE = "DROP TABLE IF EXISTS " + TABLE_TRADER;

    /**
     * Constructor
     *
     * @param context
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_TRADER_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //Drop User Table if exist
        db.execSQL(DROP_USER_TABLE);
        db.execSQL(DROP_TRADER_TABLE);

        // Create tables again
        onCreate(db);

    }

    /**
     *
     * @param user
     */
    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_FULL_NAME, user.getFullName());
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());

        // Inserting Row
        db.insert(TABLE_USER, null, values);
        db.close();
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

    public boolean checkUser(String email) {

        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();

        // selection criteria
        String selection = COLUMN_USER_EMAIL + " = ?";

        // selection argument
        String[] selectionArgs = {email};

        // query user table with condition
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com';
         */
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();

        if (cursorCount > 0) {
            return true;
        }

        return false;
    }

    public boolean checkUser(String email, String password) {

        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_USER_EMAIL + " = ?" + " AND " + COLUMN_USER_PASSWORD + " = ?";

        // selection arguments
        String[] selectionArgs = {email, password};

        // query user table with conditions
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com' AND user_password = 'qwerty';
         */
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);                      //The sort order

        int cursorCount = cursor.getCount();



        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }

        return false;
    }

    public User getUserByEmailAddress(String emailAddress){

        User user = new User();

        String[] columns = { COLUMN_USER_ID, COLUMN_USER_FULL_NAME, COLUMN_USER_EMAIL};

        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_USER_EMAIL + " = ?";

        String[] selectionArgs = {emailAddress};

        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);


        if (cursor.moveToFirst()){
            user.setId(cursor.getInt(0));
            user.setFullName(cursor.getString(1));
            user.setEmail(cursor.getString(2));
        }

        db.close();
        cursor.close();

        return user;
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

    public Trader getTraderByTraderId(int traderId){

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



}
