package com.example.ondrejvane.zivnostnicek.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ondrejvane.zivnostnicek.helper.FormatUtility;
import com.example.ondrejvane.zivnostnicek.helper.UserInformation;
import com.example.ondrejvane.zivnostnicek.model.Bill;

public class BillDatabaseHelper extends DatabaseHelper{

    /**
     * Constructor
     *
     * @param context context
     */
    public BillDatabaseHelper(Context context) {
        super(context);
    }


    public long addBill(Bill bill){
        SQLiteDatabase db = this.getWritableDatabase();
        long billId;

        ContentValues values = new ContentValues();
        values.put(COLUMN_BILL_NUMBER, bill.getName());
        values.put(COLUMN_BILL_AMOUNT, bill.getAmount());
        values.put(COLUMN_BILL_VAT, bill.getVAT());
        values.put(COLUMN_BILL_TRADER_ID, bill.getTraderId());
        values.put(COLUMN_BILL_DATE, bill.getDate());
        values.put(COLUMN_BILL_PHOTO, DbBitmapUtility.getBytes(bill.getPhoto()));
        values.put(COLUMN_BILL_USER_ID, bill.getUserId());
        values.put(COLUMN_BILL_IS_EXPENSE, bill.getIsExpense());
        billId = db.insert(TABLE_BILL, null, values);
        db.close();

        return billId;
    }

    public String[][] getBillData(int userID, int isExpense){
        String data[][];


        String[] columns = { COLUMN_BILL_ID, COLUMN_BILL_NUMBER, COLUMN_BILL_DATE, COLUMN_BILL_AMOUNT};

        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_BILL_USER_ID + " = ?" + " AND " + COLUMN_BILL_IS_EXPENSE + " = ?";

        String orderBy = COLUMN_BILL_NUMBER + " ASC";

        // selection arguments
        String[] selectionArgs = {Integer.toString(userID), Integer.toString(isExpense)};

        Cursor cursor = db.query(TABLE_BILL,        //Table to query
                columns,                            //columns to return
                selection,                          //columns for the WHERE clause
                selectionArgs,                      //The values for the WHERE clause
                null,                      //group the rows
                null,                        //filter by row groups
                orderBy);
        int count = cursor.getCount();
        data = new String[4][count];
        int i = 0;

        if (cursor.moveToFirst()){
            do{
                data[0][i] = cursor.getString(0);
                data[1][i] = cursor.getString(1);
                data[2][i] = cursor.getString(2);
                if(isExpense == 0){
                    data[3][i] = FormatUtility.formatIncomeAmount(cursor.getString(3));
                }else {
                    data[3][i] = FormatUtility.formatExpenseAmount(cursor.getString(3));
                }
                i++;
            }while (cursor.moveToNext());
        }
        db.close();
        cursor.close();

        return data;
    }


}
