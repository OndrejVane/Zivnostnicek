package com.example.ondrejvane.zivnostnicek.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ondrejvane.zivnostnicek.model.BillBox;
import com.example.ondrejvane.zivnostnicek.utilities.FormatUtility;
import com.example.ondrejvane.zivnostnicek.session.UserInformation;
import com.example.ondrejvane.zivnostnicek.model.Bill;
import com.example.ondrejvane.zivnostnicek.model.TypeBill;

import java.util.ArrayList;

public class BillDatabaseHelper extends DatabaseHelper {

    /**
     * Kontruktor pro bill database helper.
     *
     * @param context context
     */
    public BillDatabaseHelper(Context context) {
        super(context);
    }


    /**
     * Metoda pro přidání nové faktruy do databáze.
     *
     * @param bill         faktura
     * @param isFromServer logická hodnota, která určuje, že jde o data ze serveru
     * @return id_faktury
     */
    public synchronized long addBill(Bill bill, boolean isFromServer) {

        //pokud je záznam vkládán při synchronizaci se serverem, tak už id existuje. Nemusím ho generovat.
        if (!isFromServer) {
            //získání primárního klíče
            IdentifiersDatabaseHelper identifiersDatabaseHelper = new IdentifiersDatabaseHelper(getContext());
            int billId = identifiersDatabaseHelper.getFreeId(COLUMN_IDENTIFIERS_BILL_ID);
            bill.setId(billId);
        }

        SQLiteDatabase db = this.getWritableDatabase();
        long billId;

        //odstranění speciálních zanku v objektu bill kvůli SQLinjection
        bill.removeSpecialCharsBill();

        ContentValues values = new ContentValues();
        values.put(COLUMN_BILL_ID, bill.getId());
        values.put(COLUMN_BILL_NUMBER, bill.getName());
        values.put(COLUMN_BILL_AMOUNT, bill.getAmount());
        values.put(COLUMN_BILL_VAT, bill.getVAT());
        values.put(COLUMN_BILL_TRADER_ID, bill.getTraderId());
        values.put(COLUMN_BILL_DATE, bill.getDate());
        values.put(COLUMN_BILL_PHOTO, bill.getPhoto());
        values.put(COLUMN_BILL_USER_ID, bill.getUserId());
        values.put(COLUMN_BILL_TYPE_ID, bill.getTypeId());
        values.put(COLUMN_BILL_IS_EXPENSE, bill.getIsExpense());
        values.put(COLUMN_BILL_IS_DIRTY, bill.getIsDirty());
        values.put(COLUMN_BILL_IS_DELETED, bill.getIsDeleted());
        db.insert(TABLE_BILL, null, values);
        db.close();

        return bill.getId();
    }

    /**
     * Metoda pro získání dat pro zobrazení do grafu.
     *
     * @param year      hledaný rok
     * @param month     heldaný měsic
     * @param isExpense příjem/výdaj
     * @return list všech typů a součet částek k nim
     */
    public synchronized ArrayList<BillBox> getAllBillsWithTypesByDate(int year, int month, int isExpense) {
        ArrayList<BillBox> arrayList = new ArrayList<>();
        TypeBillDatabaseHelper typeBillDatabaseHelper = new TypeBillDatabaseHelper(getContext());
        int userId = UserInformation.getInstance().getUserId();

        String[] columns = {COLUMN_BILL_ID, COLUMN_BILL_NUMBER, COLUMN_BILL_AMOUNT, COLUMN_BILL_VAT, COLUMN_BILL_DATE, COLUMN_BILL_TRADER_ID, COLUMN_BILL_PHOTO, COLUMN_BILL_IS_EXPENSE, COLUMN_BILL_TYPE_ID};

        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_BILL_USER_ID + " = ?" + " AND " + COLUMN_BILL_IS_EXPENSE + " = ?" + " AND " + COLUMN_BILL_IS_DELETED + " = 0";

        String orderBy = COLUMN_BILL_NUMBER + " ASC";

        String[] selectionArgs = {Integer.toString(userId), Integer.toString(isExpense)};

        Cursor cursor = db.query(TABLE_BILL,        //Table to query
                columns,                            //columns to return
                selection,                          //columns for the WHERE clause
                selectionArgs,                      //The values for the WHERE clause
                null,                      //group the rows
                null,                        //filter by row groups
                orderBy);

        if (cursor.moveToFirst()) {
            do {
                //zjištení data faktury
                String date = cursor.getString(4);
                int foundYear = Integer.parseInt(FormatUtility.getYearFromDate(date));
                int foundMonth = Integer.parseInt(FormatUtility.getMonthFromDate(date));

                //Kontrola, zda faktura spadá do vybraného data
                if (FormatUtility.isRightDate(year, month, foundYear, foundMonth)) {
                    Bill bill = new Bill();
                    BillBox billBox;

                    //vytvoření faktury a načtení všech dat
                    bill.setId(cursor.getInt(0));
                    bill.setName(cursor.getString(1));
                    bill.setAmount(cursor.getFloat(2));
                    bill.setVAT(cursor.getInt(3));
                    bill.setDate(cursor.getString(4));
                    bill.setTraderId(cursor.getInt(5));
                    bill.setPhoto(cursor.getString(6));
                    bill.setIsExpense(cursor.getInt(7));
                    bill.setTypeId(cursor.getInt(8));

                    //načtení typu faktury
                    if (bill.getTypeId() != -1) {
                        TypeBill typeBill = typeBillDatabaseHelper.getTypeBillById(bill.getTypeId());
                        billBox = new BillBox(bill, typeBill);

                    } else {
                        TypeBill typeBill = new TypeBill();
                        typeBill.setName("Žándý typ");
                        typeBill.setColor(-9901676);
                        typeBill.setId(-1);
                        typeBill.setUserId(userId);
                        billBox = new BillBox(bill, typeBill);
                    }

                    //přidání do listu
                    arrayList.add(billBox);
                }
            } while (cursor.moveToNext());
        }

        db.close();
        cursor.close();

        return arrayList;
    }

    /**
     * Získání faktury podle id.
     *
     * @param billId id faktury
     * @return faktury
     */
    public synchronized Bill getBillById(int billId) {

        Bill bill = new Bill();

        String[] columns = {COLUMN_BILL_NUMBER, COLUMN_BILL_AMOUNT, COLUMN_BILL_VAT, COLUMN_BILL_DATE, COLUMN_BILL_TRADER_ID, COLUMN_BILL_PHOTO, COLUMN_BILL_IS_EXPENSE, COLUMN_BILL_TYPE_ID};

        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_BILL_ID + " = ? AND " + COLUMN_BILL_USER_ID + " = ?";

        String[] selectionArgs = {Integer.toString(billId), Integer.toString(UserInformation.getInstance().getUserId())};

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

    /**
     * Metoda, která "smaže" záznam faktury z databáze.
     *
     * @param billId id faktury
     * @return smazáno
     */
    public synchronized boolean deleteBillWithId(int billId) {

        String where = COLUMN_BILL_ID + " = ? AND " + COLUMN_BILL_USER_ID + " = ?";

        String[] updateArgs = {Integer.toString(billId), Integer.toString(UserInformation.getInstance().getUserId())};

        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_BILL_IS_DELETED, 1);
        values.put(COLUMN_BILL_IS_DIRTY, 1);

        db.update(TABLE_BILL, values, where, updateArgs);
        db.close();

        return true;

    }

    /**
     * Získání dat o příjmech/výdajích za zadaný časovýc úseků
     *
     * @param year      vybraný rok
     * @param month     vybraný měsíc
     * @param isExpense příjem / výdaje
     * @return hodnota příjmu/výdajů
     */
    public synchronized float getBillDataByDate(int year, int month, int isExpense) {
        int userId = UserInformation.getInstance().getUserId();
        float result = 0;

        String[] columns = {COLUMN_BILL_AMOUNT, COLUMN_BILL_DATE};

        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_BILL_USER_ID + " = ?"
                + " AND " + COLUMN_BILL_IS_EXPENSE + " = ?"
                + " AND " + COLUMN_BILL_IS_DELETED + " = ?";

        String[] selectionArgs = {Integer.toString(userId), Integer.toString(isExpense), "0"};

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

                if (FormatUtility.isRightDate(year, month, foundYear, foundMonth)) {
                    result = result + cursor.getFloat(0);
                }
            } while (cursor.moveToNext());

        }

        db.close();
        cursor.close();

        return result;
    }

    /**
     * Získání DPH z faktur za danné období.
     *
     * @param year      vybranný rok
     * @param month     vybranný měsíc
     * @param isExpense příjem/výdaj
     * @return výsledná hodnota
     */
    public synchronized float getBillVatByDate(int year, int month, int isExpense) {
        int userId = UserInformation.getInstance().getUserId();
        float coefficient21 = 0.1736f;
        float coefficient15 = 0.1304f;
        float coefficient10 = 0.0909f;
        float result = 0;
        float temp = 0;

        String[] columns = {COLUMN_BILL_AMOUNT, COLUMN_BILL_DATE, COLUMN_BILL_VAT};

        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_BILL_USER_ID + " = ?" + " AND "
                + COLUMN_BILL_IS_EXPENSE + " = ?" + " AND "
                + COLUMN_BILL_IS_DELETED + " = ?";

        String[] selectionArgs = {Integer.toString(userId), Integer.toString(isExpense), "0"};

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

                if (FormatUtility.isRightDate(year, month, foundYear, foundMonth)) {
                    temp = cursor.getFloat(0);
                }
                switch (vat) {
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
        return (float) (Math.round(result * 100.0) / 100.0);
    }

    /**
     * Získání celkové částky pro zadaný typ a zadané období.
     *
     * @param year      vybraný rok
     * @param month     vybraný měsíc
     * @param typeId    id typu
     * @param isExpense příjem/ výdaj
     * @return hodnota součtu všech příjmu/výdajů za období
     */
    public synchronized float getTotalAmountByTypeId(int year, int month, int typeId, boolean isExpense) {
        int userId = UserInformation.getInstance().getUserId();
        int isExpenseInt;
        float totalAmount = 0.0f;
        if (isExpense) {
            isExpenseInt = 1;
        } else {
            isExpenseInt = 0;
        }
        String[] columns = {COLUMN_BILL_AMOUNT, COLUMN_BILL_DATE};

        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_BILL_USER_ID + " = ?"
                + " AND " + COLUMN_BILL_IS_EXPENSE + " = ?"
                + " AND " + COLUMN_BILL_TYPE_ID + " = ?"
                + " AND " + COLUMN_BILL_IS_DELETED + " = ?";

        String[] selectionArgs = {Integer.toString(userId), Integer.toString(isExpenseInt), Integer.toString(typeId), "0"};

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

                if (FormatUtility.isRightDate(year, month, foundYear, foundMonth)) {
                    totalAmount = totalAmount + cursor.getFloat(0);
                }
            } while (cursor.moveToNext());

        }

        db.close();
        cursor.close();

        return totalAmount;
    }

    /**
     * Aktualizace faktruy se příslušným id.
     *
     * @param bill aktualizovaná faktury
     */
    public synchronized void updateBill(Bill bill) {
        String where = COLUMN_BILL_ID + " = ? AND " + COLUMN_BILL_USER_ID + " = ?";

        String[] updateArgs = {Integer.toString(bill.getId()), Integer.toString(UserInformation.getInstance().getUserId())};

        SQLiteDatabase db = this.getReadableDatabase();

        //odstranění speciálních znaků z objetu bill
        bill.removeSpecialCharsBill();

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
        values.put(COLUMN_BILL_IS_DIRTY, bill.getIsDirty());
        values.put(COLUMN_BILL_IS_DELETED, bill.getIsDeleted());
        db.update(TABLE_BILL, values, where, updateArgs);
        db.close();
    }

    public ArrayList<Bill> getAllBillsForSync() {
        ArrayList<Bill> arrayList = new ArrayList<>();
        int userId = UserInformation.getInstance().getUserId();

        String[] columns = {COLUMN_BILL_ID, COLUMN_BILL_NUMBER, COLUMN_BILL_AMOUNT,
                            COLUMN_BILL_DATE, COLUMN_BILL_VAT, COLUMN_BILL_PHOTO,
                            COLUMN_BILL_TYPE_ID, COLUMN_BILL_TRADER_ID, COLUMN_BILL_IS_DELETED,
                            COLUMN_BILL_IS_EXPENSE};

        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_BILL_USER_ID + " = ? AND " + COLUMN_BILL_IS_DIRTY + " = 1";

        String[] selectionArgs = { Integer.toString(userId)};

        Cursor cursor = db.query(TABLE_BILL, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);

        if(cursor.moveToFirst()){
            do{
                Bill bill = new Bill();
                bill.setId(cursor.getInt(0));
                bill.setName(cursor.getString(1));
                bill.setAmount(cursor.getFloat(2));
                bill.setDate(cursor.getString(3));
                bill.setVAT(cursor.getInt(4));
                bill.setPhoto(cursor.getString(5));
                bill.setTypeId(cursor.getInt(6));
                bill.setTraderId(cursor.getInt(7));
                bill.setIsDeleted(cursor.getInt(8));
                bill.setIsExpense(cursor.getInt(9));
                bill.setUserId(userId);

                //přidání záznamu do listu
                arrayList.add(bill);

            }while (cursor.moveToNext());
        }

        db.close();
        cursor.close();

        return arrayList;
    }

    public void deleteAllBillsByUserId(int userId) {
        String where = COLUMN_BILL_USER_ID + " = ?";

        String[] deleteArgs = {Integer.toString(userId)};

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_BILL, where, deleteArgs);

        db.close();
    }

    public void setAllBillsClear(int userId) {
        String where = COLUMN_BILL_USER_ID + " = ? AND "
                + COLUMN_BILL_IS_DIRTY + " = 1";

        String[] updateArgs = {Integer.toString(userId)};

        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_BILL_IS_DIRTY, 0);

        db.update(TABLE_BILL, values, where, updateArgs);
        db.close();
    }

    public int getMaxId() {

        int maxId = 1;

        String[] columns = {"MAX (" + COLUMN_BILL_ID + ")"};

        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_BILL_USER_ID + " = ? ";

        String[] selectionArgs = {Integer.toString(UserInformation.getInstance().getUserId())};

        Cursor cursor = db.query(TABLE_BILL, //Table to query
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
