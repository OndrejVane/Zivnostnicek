package com.example.ondrejvane.zivnostnicek.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {

    private Context context;

    // Verze databáze
    public static final int DATABASE_VERSION = 9;

    // Název databáze
    public static final String DATABASE_NAME = "Zivnostnicek.db";

    // Názvy jednotlivých tabulke
    private static final String TABLE_USER = "user";
    private static final String TABLE_TRADER = "trader";
    private static final String TABLE_NOTE = "note";
    private static final String TABLE_BILL = "bill";
    private static final String TABLE_TYPE = "type";
    private static final String TABLE_STORAGE_ITEM = "storage_item";

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

    //názvy atributů v tabulce note
    private static final String COLUMN_NOTE_ID = "note_id";                                 //primární klíč
    private static final String COLUMN_NOTE_TRADER_ID = "note_trader_id";                   //cizí klíč spojuje s tabulkou trader
    private static final String COLUMN_NOTE_TITLE = "note_title";                           //název poznámky
    private static final String COLUMN_NOTE_NOTE = "note_note";                             //obsah poznáky
    private static final String COLUMN_NOTE_DATE = "note_date";                             //datum založení poznámky
    private static final String COLUMN_NOTE_RATING = "note_rating";                         //hodnocení obchodníka

    //názvy atributů v tabulce bill(faktura)
    private static final String COLUMN_BILL_ID = "bill_id";                                 //primární klíč
    private static final String COLUMN_BILL_NUMBER = "bill_number";                         //název nebo číslo faktury
    private static final String COLUMN_BILL_AMOUNT = "bill_amount";                         //částka na faktuře
    private static final String COLUMN_BILL_VAT = "bill_vat";                               //částka DPH
    private static final String COLUMN_BILL_TRADER_ID = "bill_trader_id";                   //cizí klíč obchodníka (faktura od nebo pro)
    private static final String COLUMN_BILL_DATE = "bill_date";                             //datum vystavení faktury
    private static final String COLUMN_BILL_PHOTO = "bill_photo";                           //foto faktury
    private static final String COLUMN_BILL_PLACE = "bill_place";                           //místo, kde byla faktura vydána
    private static final String COLUMN_BILL_TYPE_ID = "bill_type_id";                       //cizí klíč do tabulky typ faktury
    private static final String COLUMN_BILL_USER_ID = "bill_user_id";                       //cizí klíč do tabulky uživatele
    private static final String COLUMN_BILL_IS_EXPENSE = "bill_is_expense";                 //atribut, který určuje, zda se jedná o P=0/V=1

    //názvy atributů v tabulce skladové položky(storage item)
    private static final String COLUMN_STORAGE_ITEM_ID = "storage_item_id";                 //primární klíč
    private static final String COLUMN_STORAGE_ITEM_USER_ID = "storage_item_user_id";       //cizí klíč do tabulky uživatelů
    private static final String COLUMN_STORAGE_ITEM_NAME = "storage_item_name";             //název skladové položky
    private static final String COLUMN_STORAGE_ITEM_QUANTITY = "storage_item_quantity";     //množství skladové položky
    private static final String COLUMN_STORAGE_ITEM_UNIT = "storage_item_unit";             //jednotka skladové položky
    private static final String COLUMN_STORAGE_ITEM_NOTE = "storage_item_note";             //poznámka ke skladové položce

    //názvy atributů tabulky druhů faktur
    private static final String COLUMN_TYPE_ID = "type_id";
    private static final String COLUMN_TYPE_NAME = "type_name";

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

    //SQL pro vytvoření tabulky Note
    private String CREATE_NOTE_TABLE = "CREATE TABLE " + TABLE_NOTE + "("
            + COLUMN_NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_NOTE_TRADER_ID + " INTEGER,"
            + COLUMN_NOTE_TITLE + " TEXT," + COLUMN_NOTE_NOTE + " TEXT,"
            + COLUMN_NOTE_DATE +  " TEXT," + COLUMN_NOTE_RATING + " INTEGER" + ")";

    //SQL pro vytvoření tabulky Bill
    private String CREATE_BILL_TABLE = "CREATE TABLE " + TABLE_BILL + "("
            + COLUMN_BILL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_BILL_NUMBER + " TEXT,"
            + COLUMN_BILL_AMOUNT + " REAL," + COLUMN_BILL_VAT + " INTEGER,"
            + COLUMN_BILL_TRADER_ID +  " INTEGER," + COLUMN_BILL_DATE + " TEXT,"
            + COLUMN_BILL_PHOTO +  " BLOB," + COLUMN_BILL_PLACE + " TEXT,"
            + COLUMN_BILL_TYPE_ID +  " INTEGER," + COLUMN_BILL_USER_ID + " INTEGER,"
            + COLUMN_BILL_IS_EXPENSE +  " INTEGER" + ")";

    //SQL pro vytvoření tabulky type
    private String CREATE_TYPE_TABLE = "CREATE TABLE " + TABLE_TYPE + "("
            + COLUMN_TYPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_TYPE_NAME + " TEXT" + ")";

    //SQL pro vytvoření tabulky Storage Item
    private String CREATE_STORAGE_ITEM_TABLE = "CREATE TABLE " + TABLE_STORAGE_ITEM + "("
            + COLUMN_STORAGE_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_STORAGE_ITEM_USER_ID + " INTEGER,"
            + COLUMN_STORAGE_ITEM_NAME + " TEXT," + COLUMN_STORAGE_ITEM_QUANTITY + " REAL,"
            + COLUMN_STORAGE_ITEM_UNIT + " TEXT," + COLUMN_STORAGE_ITEM_NOTE + " TEXT" + ")";

    // drop table user
    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER;

    // drop table trader
    private String DROP_TRADER_TABLE = "DROP TABLE IF EXISTS " + TABLE_TRADER;

    // drop table note
    private String DROP_NOTE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NOTE;

    //drop table Bill
    private String DROP_TABLE_BILL = "DROP TABLE IF EXISTS " + TABLE_BILL;

    //drop table type
    private String DROP_TABLE_TYPE = "DROP TABLE IF EXISTS " + TABLE_TYPE;

    //drop table storage item
    private String DROP_TABLE_STORAGE_ITEM = "DROP TABLE IF EXISTS " + TABLE_STORAGE_ITEM;
    /**
     * Constructor
     *
     * @param context
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public Context getContext(){
        return this.context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_TRADER_TABLE);
        db.execSQL(CREATE_NOTE_TABLE);
        db.execSQL(CREATE_BILL_TABLE);
        db.execSQL(CREATE_TYPE_TABLE);
        db.execSQL(CREATE_STORAGE_ITEM_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //Drop User Table if exist
        db.execSQL(DROP_USER_TABLE);
        db.execSQL(DROP_TRADER_TABLE);
        db.execSQL(DROP_NOTE_TABLE);
        db.execSQL(DROP_TABLE_BILL);
        db.execSQL(DROP_TABLE_TYPE);
        db.execSQL(DROP_TABLE_STORAGE_ITEM);

        // Create tables again
        onCreate(db);

    }
}
