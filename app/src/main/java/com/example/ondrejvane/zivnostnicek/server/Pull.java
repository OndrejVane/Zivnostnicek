package com.example.ondrejvane.zivnostnicek.server;

import android.content.Context;
import android.util.Log;

import com.example.ondrejvane.zivnostnicek.database.BillDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.database.IdentifiersDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.database.ItemQuantityDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.database.NoteDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.database.StorageItemDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.database.TraderDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.database.TypeBillDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.database.UserDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.session.UserInformation;
import com.example.ondrejvane.zivnostnicek.model.Bill;
import com.example.ondrejvane.zivnostnicek.model.ItemQuantity;
import com.example.ondrejvane.zivnostnicek.model.Note;
import com.example.ondrejvane.zivnostnicek.model.StorageItem;
import com.example.ondrejvane.zivnostnicek.model.Trader;
import com.example.ondrejvane.zivnostnicek.model.TypeBill;
import com.example.ondrejvane.zivnostnicek.utilities.PictureUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Pull {

    private static final String TAG = "Pull class";

    private Context context;
    private static final String NULL = "null";

    public Pull(Context context) {
        this.context = context;
    }


    public void pull(JSONArray response) {
        deleteAllUserData();
        saveDataFromServer(response);
        refreshAllIdentifiers();
    }

    /**
     * Smazání všech lokálních dat přihlášeného uživatelel.
     */
    public void deleteAllUserData() {
        UserDatabaseHelper userDatabaseHelper = new UserDatabaseHelper(this.context);
        userDatabaseHelper.deleteAllUserData(UserInformation.getInstance().getUserId());
    }

    /**
     * Metoda, která po načtení dat
     * ze serveru aktualizuje lokální identifikátory.
     */
    public void refreshAllIdentifiers() {
        IdentifiersDatabaseHelper identifiersDatabaseHelper = new IdentifiersDatabaseHelper(this.context);
        identifiersDatabaseHelper.refreshIdentifiers();
    }

    public void saveDataFromServer(JSONArray jsonArray) {

        try {

            JSONObject temp;

            temp = jsonArray.getJSONObject(1);
            JSONArray traders = temp.getJSONArray("traders");

            temp = jsonArray.getJSONObject(2);
            JSONArray notes = temp.getJSONArray("notes");

            temp = jsonArray.getJSONObject(3);
            JSONArray types = temp.getJSONArray("types");

            temp = jsonArray.getJSONObject(4);
            JSONArray bills = temp.getJSONArray("bills");

            temp = jsonArray.getJSONObject(5);
            JSONArray storageItems = temp.getJSONArray("storage_items");

            temp = jsonArray.getJSONObject(6);
            JSONArray itemQuantities = temp.getJSONArray("item_quantities");


            saveTradersDataFromServer(traders);
            saveNotesDataFromServer(notes);
            saveTypesDataFromServer(types);
            saveBillsDataFromServer(bills);
            saveStorageItemsDataFromServer(storageItems);
            saveItemQuantityDataFromServer(itemQuantities);

            Log.d("Pull", "Traders: " + traders.toString());
            Log.d("Pull", "Notes: " + notes.toString());
            Log.d("Pull", "Types: " + types.toString());
            Log.d("Pull", "Bills: " + bills.toString());
            Log.d("Pull", "Storage items: " + storageItems.toString());
            Log.d("Pull", "Item quantities: " + itemQuantities.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveItemQuantityDataFromServer(JSONArray itemQuantities) {
        ItemQuantityDatabaseHelper itemQuantityDatabaseHelper = new ItemQuantityDatabaseHelper(this.context);

        for (int i = 0; i < itemQuantities.length(); i++) {
            JSONObject temp;

            try {
                temp = itemQuantities.getJSONObject(i);

                ItemQuantity itemQuantity = new ItemQuantity();
                itemQuantity.setId(temp.getInt("id"));
                itemQuantity.setUserId(temp.getInt("user_id"));
                itemQuantity.setStorageItemId(temp.getInt("storage_item_id"));
                itemQuantity.setQuantity((float) temp.getDouble("quantity"));
                itemQuantity.setBillId(temp.getLong("bill_id"));
                itemQuantity.setIsDeleted(temp.getInt("is_deleted"));
                itemQuantity.setIsDirty(0);


                itemQuantityDatabaseHelper.addItemQuantity(itemQuantity, true);

            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("Pull", "SaveItemQuantityDataEx");
            }
        }
    }

    private void saveStorageItemsDataFromServer(JSONArray storageItems) {
        StorageItemDatabaseHelper storageItemDatabaseHelper = new StorageItemDatabaseHelper(this.context);

        for (int i = 0; i < storageItems.length(); i++) {
            JSONObject temp;

            try {
                temp = storageItems.getJSONObject(i);

                StorageItem storageItem = new StorageItem();
                storageItem.setId(temp.getInt("id"));
                storageItem.setUserId(temp.getInt("user_id"));
                storageItem.setName(temp.getString("name"));
                storageItem.setUnit(temp.getString("unit"));
                storageItem.setIsDeleted(temp.getInt("is_deleted"));
                storageItem.setIsDirty(0);


                //kontrola, zda je položka nulová nebo ne
                if (!temp.getString("note").equals(NULL)) {
                    storageItem.setNote(temp.getString("note"));
                }

                storageItemDatabaseHelper.addStorageItem(storageItem, true);

            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("Pull", "SaveStorageItemDataEx");
            }
        }
    }

    private void saveBillsDataFromServer(JSONArray bills) {
        BillDatabaseHelper billDatabaseHelper = new BillDatabaseHelper(this.context);

        for (int i = 0; i < bills.length(); i++) {
            JSONObject temp;

            try {
                temp = bills.getJSONObject(i);

                Bill bill = new Bill();
                bill.setId(temp.getInt("id"));
                bill.setUserId(temp.getInt("user_id"));
                bill.setName(temp.getString("number"));
                bill.setAmount((float) temp.getDouble("amount"));
                bill.setVAT(temp.getInt("vat"));
                bill.setDate(temp.getString("date"));
                bill.setIsExpense(temp.getInt("is_expense"));
                bill.setIsDeleted(temp.getInt("is_deleted"));
                bill.setTraderId(temp.getInt("trader_id"));
                bill.setTypeId(temp.getInt("type_id"));
                bill.setIsDirty(0);

                //kontrola, zda je položka nulová nebo ne
                if (!temp.getString("photo").equals(NULL)) {
                    bill.setPhoto(temp.getString("photo"));

                    //pokud soubor lokálně neexistuje, tak je třeba soubor uložit
                    if (!PictureUtility.isFileExists(bill.getPhoto())) {
                        Log.d(TAG, "Picture is not exist");
                    }else {
                        Log.d(TAG, "Picture is exist");
                    }
                }

                billDatabaseHelper.addBill(bill, true);

            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("Pull", "SaveBillsDataEx");
            }
        }
    }

    private void saveTypesDataFromServer(JSONArray types) {
        TypeBillDatabaseHelper typeBillDatabaseHelper = new TypeBillDatabaseHelper(this.context);

        for (int i = 0; i < types.length(); i++) {
            JSONObject temp;
            try {
                temp = types.getJSONObject(i);

                //vytvoření nového typu
                TypeBill typeBill = new TypeBill();
                typeBill.setId(temp.getInt("id"));
                typeBill.setUserId(temp.getInt("user_id"));
                typeBill.setColor(temp.getInt("color"));
                typeBill.setName(temp.getString("name"));
                typeBill.setIsDeleted(temp.getInt("is_deleted"));
                typeBill.setIsDirty(0);


                //vložení záznamu do databáze
                typeBillDatabaseHelper.addTypeBill(typeBill, true);

            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("Pull", "SaveTypesDataEx");
            }
        }
    }

    private void saveNotesDataFromServer(JSONArray notes) {
        NoteDatabaseHelper noteDatabaseHelper = new NoteDatabaseHelper(this.context);

        for (int i = 0; i < notes.length(); i++) {
            JSONObject temp;
            try {
                temp = notes.getJSONObject(i);

                //vytvoření nové poznámky
                Note note = new Note();
                note.setId(temp.getInt("id"));
                note.setTraderId(temp.getInt("trader_id"));
                note.setTitle(temp.getString("title"));
                note.setDate(temp.getString("date"));
                note.setRating(temp.getInt("rating"));
                note.setIsDirty(0);
                note.setIsDeleted(0);

                //kontrola, zda je položka nulová nebo ne
                if (!temp.getString("note").equals(NULL)) {
                    note.setNote(temp.getString("note"));
                }

                //vložení záznamu do databáze
                noteDatabaseHelper.addNote(note, true);

            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("Pull", "SaveNotesDataEx");
            }
        }
    }

    private void saveTradersDataFromServer(JSONArray traders) {
        TraderDatabaseHelper traderDatabaseHelper = new TraderDatabaseHelper(this.context);

        for (int i = 0; i < traders.length(); i++) {

            JSONObject temp;
            try {
                temp = traders.getJSONObject(i);


                //vytvoření nového obchodníka
                Trader trader = new Trader();
                trader.setId(temp.getInt("id"));
                trader.setName(temp.getString("name"));
                trader.setIsDeleted(temp.getInt("is_deleted"));
                trader.setUserId(UserInformation.getInstance().getUserId());
                trader.setIsDirty(0);

                if (!temp.getString("house_number").equals(NULL)) {
                    trader.setHouseNumber(temp.getString("house_number"));
                }

                if (!temp.getString("phone_number").equals(NULL)) {
                    trader.setPhoneNumber(temp.getString("phone_number"));
                }

                if (!temp.getString("street").equals(NULL)) {
                    trader.setStreet(temp.getString("street"));
                }

                if (!temp.getString("contact_person").equals(NULL)) {
                    trader.setContactPerson(temp.getString("contact_person"));
                }

                if (!temp.getString("tin").equals(NULL)) {
                    trader.setTIN(temp.getString("tin"));
                }

                if (!temp.getString("i_n").equals(NULL)) {
                    trader.setIN(temp.getString("i_n"));
                }

                if (!temp.getString("city").equals(NULL)) {
                    trader.setCity(temp.getString("city"));
                }


                //vložení záznamu do databáze
                traderDatabaseHelper.addTrader(trader, true);

            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("Pull", "SaveTradersDataEx");
            }
        }
    }
}
