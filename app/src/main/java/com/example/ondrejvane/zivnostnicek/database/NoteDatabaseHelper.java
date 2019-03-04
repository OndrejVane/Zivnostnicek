package com.example.ondrejvane.zivnostnicek.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ondrejvane.zivnostnicek.helper.UserInformation;
import com.example.ondrejvane.zivnostnicek.model.Note;
import com.example.ondrejvane.zivnostnicek.model.Trader;

import java.util.ArrayList;

public class NoteDatabaseHelper extends DatabaseHelper {


    /**
     * Kontruktor pro note databse helper
     *
     * @param context context
     */
    public NoteDatabaseHelper(Context context) {
        super(context);
    }

    /**
     * Metoda, pro přidání nové poznámky do databáze.
     *
     * @param note poznámka
     * @param isFromServer  logická hodnota, která údává, zda jde o data ze serveru
     */
    public synchronized void addNote(Note note, boolean isFromServer) {
        //pokud je záznam vkládán při synchronizaci se serverem, tak už id existuje. Nemusím ho generovat.
        if(!isFromServer){
            //získání primárního klíče
            IdentifiersDatabaseHelper identifiersDatabaseHelper = new IdentifiersDatabaseHelper(getContext());
            int noteId = identifiersDatabaseHelper.getFreeId(COLUMN_IDENTIFIERS_NOTE_ID);
            note.setId(noteId);
        }

        //odstranění speciálních znáků z objetu poznámky X SQL injectionX
        note.removeSpecialChars();

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_ID, note.getId());
        values.put(COLUMN_NOTE_USER_ID, UserInformation.getInstance().getUserId());
        values.put(COLUMN_NOTE_TRADER_ID, note.getTraderId());
        values.put(COLUMN_NOTE_TITLE, note.getTitle());
        values.put(COLUMN_NOTE_NOTE, note.getNote());
        values.put(COLUMN_NOTE_DATE, note.getDate());
        values.put(COLUMN_NOTE_RATING, note.getRating());
        values.put(COLUMN_NOTE_IS_DIRTY, note.getIsDirty());
        values.put(COLUMN_NOTE_IS_DELETED, note.getIsDeleted());
        db.insert(TABLE_NOTE, null, values);
        db.close();
    }

    /**
     * Metoda, která "smaže" poznámku z databáze s příslušným id.
     * Nedojde k fyzickému smazání, pouze k nastavení příznaku is delete na
     * 1. Záznam budu potřebovat pro synchronizaci na server.
     *
     * @param noteId id poznámky
     * @return  zda došo ke smazání poznámky
     */
    public synchronized boolean deleteNoteById(int noteId) {
        String where = COLUMN_NOTE_ID + " = ? AND " + COLUMN_NOTE_USER_ID +" = ?";

        String[] updateArgs = {Integer.toString(noteId), Integer.toString(UserInformation.getInstance().getUserId())};

        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_IS_DELETED, 1);
        values.put(COLUMN_NOTE_IS_DIRTY, 1);

        db.update(TABLE_NOTE, values, where, updateArgs);
        db.close();

        return true;
    }

    /**
     * Meotda, která "smaže" poznámky, které přísluší k obchodníkovi.
     *
     * @param traderId id obchodníka
     * @return  zda došlo ke smazání poznámek
     */
    public synchronized boolean deleteNotesByTraderId(int traderId) {

        String where = COLUMN_NOTE_TRADER_ID + " = ? AND " + COLUMN_NOTE_USER_ID +" = ?";

        String[] updateArgs = {Integer.toString(traderId), Integer.toString(UserInformation.getInstance().getUserId())};

        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_IS_DELETED, 1);
        values.put(COLUMN_NOTE_IS_DIRTY, 1);

        db.update(TABLE_NOTE, values, where, updateArgs);
        db.close();

        return true;


    }

    /**
     * Metoda, která aktualizuje poznámku s příslušným
     * id poznámky.
     *
     * @param note  poznámka
     */
    public synchronized void updateNoteById(Note note) {
        String where = COLUMN_NOTE_ID + " = ? AND " + COLUMN_NOTE_USER_ID +" = ?";

        String[] updateArgs = {Integer.toString(note.getId()), Integer.toString(UserInformation.getInstance().getUserId())};

        SQLiteDatabase db = this.getReadableDatabase();

        //odstranění speciálních znáků z objetu poznámky X SQL injectionX
        note.removeSpecialChars();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_TITLE, note.getTitle());
        values.put(COLUMN_NOTE_NOTE, note.getNote());
        values.put(COLUMN_NOTE_RATING, note.getRating());
        values.put(COLUMN_NOTE_DATE, note.getDate());
        values.put(COLUMN_NOTE_IS_DIRTY, note.getIsDirty());
        values.put(COLUMN_NOTE_IS_DELETED, note.getIsDeleted());

        db.update(TABLE_NOTE, values, where, updateArgs);
        db.close();
    }

    /**
     * Získání poznámky podle příslošného id.
     *
     * @param noteId id poznámky
     * @return  poznámka
     */
    public synchronized Note getNoteById(int noteId) {

        Note note = new Note();

        String[] columns = {COLUMN_NOTE_TITLE, COLUMN_NOTE_DATE, COLUMN_NOTE_RATING, COLUMN_NOTE_NOTE};

        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_NOTE_ID + " = ? AND " + COLUMN_NOTE_USER_ID +" = ? ";

        String[] selectionArgs = {Integer.toString(noteId), Integer.toString(UserInformation.getInstance().getUserId())};

        Cursor cursor = db.query(TABLE_NOTE, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);

        if (cursor.moveToFirst()) {

            note.setTitle(cursor.getString(0));
            note.setDate(cursor.getString(1));
            note.setRating(Integer.parseInt(cursor.getString(2)));
            note.setNote(cursor.getString(3));
        }

        db.close();
        cursor.close();

        return note;
    }

    /**
     * Získání dat pro zobrazení poznámek do listu příslušného
     * obchodníka.
     *
     * @param traderID id obchodníka
     * @return  pole s potřebnými informacemi
     */
    public synchronized String[][] getNotesData(int traderID) {
        String data[][];

        String[] columns = {COLUMN_NOTE_ID, COLUMN_NOTE_TITLE, COLUMN_NOTE_RATING};

        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_NOTE_TRADER_ID + " = ?" + " AND " + COLUMN_NOTE_IS_DELETED + " = ? AND "+ COLUMN_NOTE_USER_ID + " = ?";

        String orderBy = COLUMN_NOTE_TITLE + " ASC";

        // selection arguments
        String[] selectionArgs = {Integer.toString(traderID), "0", Integer.toString(UserInformation.getInstance().getUserId())};

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

        if (cursor.moveToFirst()) {
            do {
                data[0][i] = cursor.getString(0);
                data[1][i] = cursor.getString(1);
                data[2][i] = cursor.getString(2);
                i++;
            } while (cursor.moveToNext());
        }
        db.close();
        cursor.close();

        return data;
    }

    /**
     * Získání průměrného hodnocení obchodníka
     *
     * @param traderID id obchodníka
     * @return průměrné hodnocení obchodníka
     */
    public synchronized float getAverageRatingByTraderId(int traderID) {
        double temp = 0;
        int count;

        String[] columns = {COLUMN_NOTE_RATING};

        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_NOTE_TRADER_ID + " = ?" + " AND " + COLUMN_NOTE_IS_DELETED +" = ? AND "+ COLUMN_NOTE_USER_ID + " = ?";

        // selection arguments
        String[] selectionArgs = {Integer.toString(traderID), "0", Integer.toString(UserInformation.getInstance().getUserId())};

        Cursor cursor = db.query(TABLE_NOTE, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);
        count = cursor.getCount();

        if (cursor.moveToFirst()) {
            do {
                temp = temp + cursor.getDouble(0);
            } while (cursor.moveToNext());

            temp = temp / (float)count;
        }
        db.close();
        cursor.close();

        return (float) (Math.round( temp * 100.0) / 100.0);
    }

    public synchronized void deleteAllNotesByUserId(int userId){
        String where = COLUMN_NOTE_USER_ID + " = ?";

        String[] deleteArgs = {Integer.toString(userId)};

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_NOTE, where, deleteArgs);

        db.close();
    }


    public synchronized ArrayList<Note> getAllNotesForSync(){
        ArrayList<Note> arrayList = new ArrayList<>();

        String[] columns = {COLUMN_NOTE_ID, COLUMN_NOTE_TRADER_ID, COLUMN_NOTE_TITLE,
                            COLUMN_NOTE_NOTE, COLUMN_NOTE_DATE, COLUMN_NOTE_RATING, COLUMN_NOTE_IS_DELETED};

        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_NOTE_USER_ID + " = ? AND " + COLUMN_NOTE_IS_DIRTY + " = 1";

        String[] selectionArgs = { Integer.toString(UserInformation.getInstance().getUserId())};

        Cursor cursor = db.query(TABLE_NOTE, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);

        if(cursor.moveToFirst()){
            do{
                Note note = new Note();
                note.setId(cursor.getInt(0));
                note.setTraderId(cursor.getInt(1));
                note.setTitle(cursor.getString(2));
                note.setNote(cursor.getString(3));
                note.setDate(cursor.getString(4));
                note.setRating(cursor.getInt(5));
                note.setIsDeleted(cursor.getInt(6));
                arrayList.add(note);
            }while (cursor.moveToNext());

        }

        db.close();
        cursor.close();

        return arrayList;
    }

    public void setAllNotesClear(int userId) {
        String where = COLUMN_NOTE_USER_ID + " = ? AND "
                + COLUMN_NOTE_IS_DIRTY + " = 1";

        String[] updateArgs = {Integer.toString(userId)};

        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_IS_DIRTY, 0);

        db.update(TABLE_NOTE, values, where, updateArgs);
        db.close();
    }

    public int getMaxId() {

        int maxId = 1;

        String[] columns = {"MAX (" + COLUMN_NOTE_ID + ")"};

        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_NOTE_USER_ID + " = ? ";

        String[] selectionArgs = {Integer.toString(UserInformation.getInstance().getUserId())};

        Cursor cursor = db.query(TABLE_NOTE, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);

        if (cursor.moveToFirst()) {
            maxId = cursor.getInt(0);
        }

        db.close();
        cursor.close();

        return maxId;
    }
}
