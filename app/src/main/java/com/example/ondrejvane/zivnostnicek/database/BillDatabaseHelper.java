package com.example.ondrejvane.zivnostnicek.database;

import android.content.Context;

public class BillDatabaseHelper extends DatabaseHelper{

    //názve tabulky faktury
    private static final String TABLE_BILL = "bill";

    //názvy atributů
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

    /**
     * Constructor
     *
     * @param context context
     */
    public BillDatabaseHelper(Context context) {
        super(context);
    }



}
