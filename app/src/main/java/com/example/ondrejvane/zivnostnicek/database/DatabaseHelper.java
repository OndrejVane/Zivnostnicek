package com.example.ondrejvane.zivnostnicek.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.Settings;

import com.example.ondrejvane.zivnostnicek.helper.UserInformation;
import com.example.ondrejvane.zivnostnicek.model.Note;
import com.example.ondrejvane.zivnostnicek.model.Trader;
import com.example.ondrejvane.zivnostnicek.model.User;


public class DatabaseHelper extends SQLiteOpenHelper {

    // Verze databáze
    public static final int DATABASE_VERSION = 4;

    // Název databáze
    public static final String DATABASE_NAME = "Zivnostnicek.db";

    // Názvy jednotlivých tabulke
    private static final String TABLE_USER = "user";
    private static final String TABLE_TRADER = "trader";
    private static final String TABLE_NOTE = "note";

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


    // drop table user
    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER;

    // drop table trader
    private String DROP_TRADER_TABLE = "DROP TABLE IF EXISTS " + TABLE_TRADER;

    // drop table note
    private String DROP_NOTE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NOTE;

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
        db.execSQL(CREATE_NOTE_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //Drop User Table if exist
        db.execSQL(DROP_USER_TABLE);
        db.execSQL(DROP_TRADER_TABLE);
        db.execSQL(DROP_NOTE_TABLE);

        // Create tables again
        onCreate(db);

    }



    public void addNote(Note note){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_TRADER_ID, note.getTrader_id());
        values.put(COLUMN_NOTE_TITLE, note.getTitle());
        values.put(COLUMN_NOTE_NOTE, note.getNote());
        values.put(COLUMN_NOTE_DATE, note.getDate());
        values.put(COLUMN_NOTE_RATING, note.getRating());
        db.insert(TABLE_NOTE, null, values);
        db.close();
    }


    public String[][] getNotesData(int traderID){
        String data[][];

        String[] columns = { COLUMN_NOTE_ID, COLUMN_NOTE_TITLE, COLUMN_NOTE_RATING};

        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_NOTE_TRADER_ID + " = ?";

        String orderBy = COLUMN_NOTE_TITLE + " ASC";

        // selection arguments
        String[] selectionArgs = {Integer.toString(traderID)};

        Cursor cursor = db.query(TABLE_NOTE, //Table to query
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


    public Note getNoteById(int noteId){

        Note note = new Note();

        String[] columns = {COLUMN_NOTE_TITLE, COLUMN_NOTE_DATE, COLUMN_NOTE_RATING, COLUMN_NOTE_NOTE};

        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_NOTE_ID + " = ?";

        String[] selectionArgs = {Integer.toString(noteId)};

        Cursor cursor = db.query(TABLE_NOTE, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);

        if(cursor.moveToFirst()){

            note.setTitle(cursor.getString(0));
            note.setDate(cursor.getString(1));
            note.setRating(Integer.parseInt(cursor.getString(2)));
            note.setNote(cursor.getString(3));
        }

        db.close();
        cursor.close();

        return note;
    }

    public boolean deleteNoteById(int noteId){
        boolean result;

        String where = COLUMN_NOTE_ID + " = ?";

        String[] deleteArgs = {Integer.toString(noteId)};

        SQLiteDatabase db = this.getReadableDatabase();

        result = db.delete(TABLE_NOTE, where, deleteArgs) > 0;

        return result;
    }

    public void updateNoteById(Note note){
        String where = COLUMN_NOTE_ID + " = ?";

        String[] updateArgs = {Integer.toString(note.getId())};

        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_TITLE, note.getTitle());
        values.put(COLUMN_NOTE_NOTE, note.getNote());
        values.put(COLUMN_NOTE_RATING, note.getRating());
        values.put(COLUMN_NOTE_DATE, note.getDate());

        db.update(TABLE_NOTE, values, where, updateArgs);
        db.close();
    }

}
