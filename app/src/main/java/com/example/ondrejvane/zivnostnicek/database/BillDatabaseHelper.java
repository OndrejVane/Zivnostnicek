package com.example.ondrejvane.zivnostnicek.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.helper.FormatUtility;
import com.example.ondrejvane.zivnostnicek.helper.UserInformation;
import com.example.ondrejvane.zivnostnicek.model.Bill;
import com.example.ondrejvane.zivnostnicek.model.Note;
import com.example.ondrejvane.zivnostnicek.model.TypeBill;

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
        values.put(COLUMN_BILL_PHOTO, bill.getPhoto());
        values.put(COLUMN_BILL_USER_ID, bill.getUserId());
        values.put(COLUMN_BILL_TYPE_ID, bill.getTypeId());
        values.put(COLUMN_BILL_IS_EXPENSE, bill.getIsExpense());
        billId = db.insert(TABLE_BILL, null, values);
        db.close();

        return billId;
    }

    public String[][] getBillData(int userID, int isExpense){
        String data[][];
        TypeBillDatabaseHelper typeBillDatabaseHelper = new TypeBillDatabaseHelper(getContext());

        String[] columns = { COLUMN_BILL_ID, COLUMN_BILL_NUMBER, COLUMN_BILL_DATE, COLUMN_BILL_AMOUNT, COLUMN_BILL_TYPE_ID};

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
        data = new String[6][count];
        int i = 0;

        if (cursor.moveToFirst()){
            do{
                data[0][i] = cursor.getString(0);
                data[1][i] = cursor.getString(1);
                data[2][i] = cursor.getString(2);
                if(isExpense == 0){
                    data[3][i] = cursor.getString(3);
                }else {
                    data[3][i] = cursor.getString(3);
                }

                if(cursor.getInt(4) != -1){
                    TypeBill typeBill = typeBillDatabaseHelper.getTypeBillById(cursor.getInt(4));
                    data[4][i] = typeBill.getName();
                    data[5][i] = Integer.toString(typeBill.getColor());
                }else {
                    data[4][i] = "Žándý typ";
                    data[5][i] = "-9901676";    //primary color number
                }
                i++;
            }while (cursor.moveToNext());
        }
        db.close();
        cursor.close();

        return data;
    }

    public Bill getBillById(int billId){

        Bill bill = new Bill();

        String[] columns = {COLUMN_BILL_NUMBER, COLUMN_BILL_AMOUNT, COLUMN_BILL_VAT, COLUMN_BILL_DATE, COLUMN_BILL_TRADER_ID, COLUMN_BILL_PHOTO, COLUMN_BILL_IS_EXPENSE, COLUMN_BILL_TYPE_ID};

        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_BILL_ID + " = ?";

        String[] selectionArgs = {Integer.toString(billId)};

        Cursor cursor = db.query(TABLE_BILL, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);


        if(cursor.moveToFirst()){
            bill.setId(billId);
            bill.setName(cursor.getString(0));
            bill.setAmount(cursor.getFloat(1));
            bill.setVAT(cursor.getInt(2));
            bill.setDate(cursor.getString(3));
            bill.setTraderId(cursor.getInt(4));
            bill.setPhoto(cursor.getString(5));
            bill.setIsExpense(cursor.getInt(6));
            bill.setTypeId(cursor.getInt(7));
        }

        db.close();
        cursor.close();

        return bill;


    }

    public boolean deleteBillWithId(int billId){

        boolean result;

        String where = COLUMN_BILL_ID + " = ?";

        String[] deleteArgs = {Integer.toString(billId)};

        SQLiteDatabase db = this.getReadableDatabase();

        ItemQuantityDatabaseHelper itemQuantityDatabaseHelper = new ItemQuantityDatabaseHelper(getContext());
        itemQuantityDatabaseHelper.deleteItemQuantityByBillId(billId);

        result = db.delete(TABLE_BILL, where, deleteArgs) > 0;
        return result;

    }

}
