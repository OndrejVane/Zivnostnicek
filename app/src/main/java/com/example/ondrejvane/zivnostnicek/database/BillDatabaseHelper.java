package com.example.ondrejvane.zivnostnicek.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ondrejvane.zivnostnicek.utilities.FormatUtility;
import com.example.ondrejvane.zivnostnicek.helper.UserInformation;
import com.example.ondrejvane.zivnostnicek.model.Bill;
import com.example.ondrejvane.zivnostnicek.model.TypeBill;

import java.util.ArrayList;

public class BillDatabaseHelper extends DatabaseHelper {

    /**
     * Constructor
     *
     * @param context context
     */
    public BillDatabaseHelper(Context context) {
        super(context);
    }


    public long addBill(Bill bill) {
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

    public String[][] getBillData(int userID, int isExpense) {
        String data[][];
        TypeBillDatabaseHelper typeBillDatabaseHelper = new TypeBillDatabaseHelper(getContext());

        String[] columns = {COLUMN_BILL_ID, COLUMN_BILL_NUMBER, COLUMN_BILL_DATE, COLUMN_BILL_AMOUNT, COLUMN_BILL_TYPE_ID};

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

        if (cursor.moveToFirst()) {
            do {
                data[0][i] = cursor.getString(0);
                data[1][i] = cursor.getString(1);
                data[2][i] = cursor.getString(2);
                if (isExpense == 0) {
                    data[3][i] = cursor.getString(3);
                } else {
                    data[3][i] = cursor.getString(3);
                }

                if (cursor.getInt(4) != -1) {
                    TypeBill typeBill = typeBillDatabaseHelper.getTypeBillById(cursor.getInt(4));
                    data[4][i] = typeBill.getName();
                    data[5][i] = Integer.toString(typeBill.getColor());
                } else {
                    data[4][i] = "Žándý typ";
                    data[5][i] = "-9901676";    //primary color number
                }
                i++;
            } while (cursor.moveToNext());
        }
        db.close();
        cursor.close();

        return data;
    }

    public Bill getBillById(int billId) {

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


        if (cursor.moveToFirst()) {
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

    public boolean deleteBillWithId(int billId) {

        boolean result;

        String where = COLUMN_BILL_ID + " = ?";

        String[] deleteArgs = {Integer.toString(billId)};

        SQLiteDatabase db = this.getReadableDatabase();

        ItemQuantityDatabaseHelper itemQuantityDatabaseHelper = new ItemQuantityDatabaseHelper(getContext());
        itemQuantityDatabaseHelper.deleteItemQuantityByBillId(billId);

        result = db.delete(TABLE_BILL, where, deleteArgs) > 0;
        return result;

    }

    public float getBillDataByDate(int year, int month, int isExpense) {
        int userId = UserInformation.getInstance().getUserId();
        float result = 0;

        String[] columns = {COLUMN_BILL_AMOUNT, COLUMN_BILL_DATE};

        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_BILL_USER_ID + " = ?" + " AND " + COLUMN_BILL_IS_EXPENSE + " = ?";

        String[] selectionArgs = {Integer.toString(userId), Integer.toString(isExpense)};

        Cursor cursor = db.query(TABLE_BILL, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);


        if (cursor.moveToFirst()) {
            do {
                String date = cursor.getString(1);
                int foundYear = Integer.parseInt(FormatUtility.getYearFromDate(date));
                int foundMonth = Integer.parseInt(FormatUtility.getMonthFromDate(date));

                //nevybrán měsíc ani rok
                if (year == -1 && month == -1) {
                    result = result + cursor.getFloat(0);
                    //vybrán pouze rok
                } else if (year != -1 && month == -1) {

                    if(year == foundYear){
                        result = result + cursor.getFloat(0);
                    }

                    //vybrán rok i měsíc
                } else if (year != -1 && month != -1) {

                    if( year == foundYear && month == foundMonth){
                        result = result + cursor.getFloat(0);
                    }

                    //vybrán měsíc ale ne rok
                } else {
                    result = 0;
                }
            } while (cursor.moveToNext());

        }

        db.close();
        cursor.close();

        return result;
    }

    public double getBillVatByDate(int year, int month, int isExpense) {
        int userId = UserInformation.getInstance().getUserId();
        double coefficient21 = 0.1736;
        double coefficient15 = 0.1304;
        double coefficient10 = 0.0909;
        double result = 0;
        double temp = 0;

        String[] columns = {COLUMN_BILL_AMOUNT, COLUMN_BILL_DATE, COLUMN_BILL_VAT};

        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_BILL_USER_ID + " = ?" + " AND " + COLUMN_BILL_IS_EXPENSE + " = ?";

        String[] selectionArgs = {Integer.toString(userId), Integer.toString(isExpense)};

        Cursor cursor = db.query(TABLE_BILL,         //Table to query
                columns,                             //columns to return
                selection,                           //columns for the WHERE clause
                selectionArgs,                       //The values for the WHERE clause
                null,                       //group the rows
                null,                        //filter by row groups
                null);


        if (cursor.moveToFirst()) {
            do {
                String date = cursor.getString(1);
                int foundYear = Integer.parseInt(FormatUtility.getYearFromDate(date));
                int foundMonth = Integer.parseInt(FormatUtility.getMonthFromDate(date));
                int vat = cursor.getInt(2);

                //nevybrán měsíc ani rok
                if (year == -1 && month == -1) {

                    temp =  cursor.getDouble(0);
                    //vybrán pouze rok
                } else if (year != -1 && month == -1) {

                    if(year == foundYear){
                        temp =  cursor.getDouble(0);
                    }

                    //vybrán rok i měsíc
                } else if (year != -1 && month != -1) {

                    if( year == foundYear && month == foundMonth){
                        temp = cursor.getDouble(0);
                    }

                    //vybrán měsíc ale ne rok
                } else {
                    temp = 0;
                }

                switch (vat){
                    case 21:
                        result = result + (temp * coefficient21);
                        break;
                    case 15:
                        result = result + (temp * coefficient15);
                        break;
                    case 10:
                        result = result + (temp * coefficient10);
                        break;
                    case 0:
                        break;
                }
            } while (cursor.moveToNext());

        }

        db.close();
        cursor.close();

        //zaokrouhlení na 2 desetinná místa
        return Math.round(result * 100.0) / 100.0;
    }

    public float getTotalAmountByTypeId(int year, int month,int typeId, boolean isExpense){
        int userId = UserInformation.getInstance().getUserId();
        int isExpenseInt;
        float totalAmount = 0.0f;
        if(isExpense){
            isExpenseInt = 1;
        }else {
            isExpenseInt = 0;
        }
        String[] columns = { COLUMN_BILL_AMOUNT, COLUMN_BILL_DATE};

        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_BILL_USER_ID + " = ?" + " AND " + COLUMN_BILL_IS_EXPENSE + " = ?" + " AND " + COLUMN_BILL_TYPE_ID + " = ?";

        String[] selectionArgs = {Integer.toString(userId), Integer.toString(isExpenseInt), Integer.toString(typeId)};

        Cursor cursor = db.query(TABLE_BILL, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);

        if (cursor.moveToFirst()) {
            do {
                String date = cursor.getString(1);
                int foundYear = Integer.parseInt(FormatUtility.getYearFromDate(date));
                int foundMonth = Integer.parseInt(FormatUtility.getMonthFromDate(date));

                if(isRightDate(year, month, foundYear, foundMonth)){
                    totalAmount = totalAmount + cursor.getFloat(0);
                }
            } while (cursor.moveToNext());

        }

        db.close();
        cursor.close();

        return totalAmount;
    }

    public void updateBill(Bill bill){
        String where = COLUMN_BILL_ID + " = ?";

        String[] updateArgs = {Integer.toString(bill.getId())};

        SQLiteDatabase db = this.getReadableDatabase();

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
        db.update(TABLE_BILL, values, where, updateArgs);
        db.close();
    }

    private boolean isRightDate(int year, int month, int foundYear, int foundMonth) {
        //nevybrán měsíc ani rok
        if (year == -1 && month == -1) {
            return true;
            //vybrán pouze rok
        } else if (year != -1 && month == -1) {

            if(year == foundYear){
                return true;
            }

            //vybrán rok i měsíc
        } else if (year != -1 && month != -1) {

            if( year == foundYear && month == foundMonth){
                return true;
            }

            //vybrán měsíc ale ne rok
        } else {
            return false;
        }

        return false;
    }


}
