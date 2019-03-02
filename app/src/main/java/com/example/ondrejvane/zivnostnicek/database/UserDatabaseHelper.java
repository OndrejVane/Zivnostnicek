package com.example.ondrejvane.zivnostnicek.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ondrejvane.zivnostnicek.model.User;

public class UserDatabaseHelper extends DatabaseHelper {

    /**
     * Constructor
     *
     * @param context kontext aktivtiy
     */
    public UserDatabaseHelper(Context context) {
        super(context);
    }

    /**
     *
     * @param user uživatel
     */
    public synchronized long addUser(User user) {
        long inserted = 0;
        if(getUserById(user.getId()) == null) {

            SQLiteDatabase db = this.getWritableDatabase();

            //odstranění speciálních znaků proti SQL injection
            user.removeSpecialChars();

            ContentValues values = new ContentValues();
            values.put(COLUMN_USER_ID, user.getId());
            values.put(COLUMN_USER_FULL_NAME, user.getFullName());
            values.put(COLUMN_USER_EMAIL, user.getEmail());
            values.put(COLUMN_USER_PASSWORD, user.getPassword());
            values.put(COLUMN_USER_SYNC_NUMBER, user.getSyncNumber());

            // Inserting Row
            inserted = db.insert(TABLE_USER, null, values);
            db.close();

        }
        return inserted;
    }


    public synchronized User getUserById(int userId){
        User user = new User();

        String[] columns = { COLUMN_USER_FULL_NAME, COLUMN_USER_EMAIL, COLUMN_USER_PASSWORD, COLUMN_USER_SYNC_NUMBER};

        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_USER_ID + " = ?";

        String[] selectionArgs = {Integer.toString(userId)};

        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);


        if (cursor.moveToFirst()){
            user.setId(userId);
            user.setFullName(cursor.getString(0));
            user.setEmail(cursor.getString(1));
            user.setPassword(cursor.getString(2));
            user.setSyncNumber(cursor.getInt(3));
        }else {
            user = null;
        }


        db.close();
        cursor.close();

        return user;
    }

    public int getSyncNumberByUserId(int userId){
        int syncNumber = 0;

        String[] columns = { COLUMN_USER_SYNC_NUMBER};

        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_USER_ID + " = ?";

        String[] selectionArgs = {Integer.toString(userId)};

        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);


        if (cursor.moveToFirst()){
            syncNumber = cursor.getInt(0);
        }

        db.close();
        cursor.close();

        return syncNumber;
    }
}
