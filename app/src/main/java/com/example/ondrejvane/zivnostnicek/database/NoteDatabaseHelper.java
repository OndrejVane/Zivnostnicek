package com.example.ondrejvane.zivnostnicek.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ondrejvane.zivnostnicek.model.Note;

public class NoteDatabaseHelper extends DatabaseHelper {


    /**
     * Constructor
     *
     * @param context context
     */
    public NoteDatabaseHelper(Context context) {
        super(context);
    }

    public synchronized void addNote(Note note) {
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

    public synchronized boolean deleteNoteById(int noteId) {
        boolean result;

        String where = COLUMN_NOTE_ID + " = ?";

        String[] deleteArgs = {Integer.toString(noteId)};

        SQLiteDatabase db = this.getReadableDatabase();

        result = db.delete(TABLE_NOTE, where, deleteArgs) > 0;

        return result;
    }

    public synchronized boolean deleteNotesByTraderId(int traderId) {
        boolean result;

        String where = COLUMN_NOTE_TRADER_ID + " = ?";

        String[] deleteArgs = {Integer.toString(traderId)};

        SQLiteDatabase db = this.getReadableDatabase();

        result = db.delete(TABLE_NOTE, where, deleteArgs) > 0;

        return result;


    }

    public synchronized void updateNoteById(Note note) {
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

    public synchronized Note getNoteById(int noteId) {

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

    public synchronized String[][] getNotesData(int traderID) {
        String data[][];

        String[] columns = {COLUMN_NOTE_ID, COLUMN_NOTE_TITLE, COLUMN_NOTE_RATING};

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

    public synchronized float getAverageRatingByTraderId(int traderID) {
        double temp = 0;
        int count;

        String[] columns = {COLUMN_NOTE_RATING};

        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_NOTE_TRADER_ID + " = ?";


        // selection arguments
        String[] selectionArgs = {Integer.toString(traderID)};

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

}
