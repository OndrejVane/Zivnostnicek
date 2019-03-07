package com.example.ondrejvane.zivnostnicek.server;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.ondrejvane.zivnostnicek.database.BillDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.database.ItemQuantityDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.database.NoteDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.database.StorageItemDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.database.TraderDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.database.TypeBillDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.database.UserDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.helper.Settings;
import com.example.ondrejvane.zivnostnicek.session.UserInformation;
import com.example.ondrejvane.zivnostnicek.model.Bill;
import com.example.ondrejvane.zivnostnicek.model.ItemQuantity;
import com.example.ondrejvane.zivnostnicek.model.Note;
import com.example.ondrejvane.zivnostnicek.model.StorageItem;
import com.example.ondrejvane.zivnostnicek.model.Trader;
import com.example.ondrejvane.zivnostnicek.model.TypeBill;
import com.example.ondrejvane.zivnostnicek.model.User;
import com.example.ondrejvane.zivnostnicek.utilities.WifiCheckerUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Push {

    private Context context;
    private static final String TAG = "Push";

    public Push(Context context) {
        this.context = context;
    }

    public void push(){
        //push probíhá v jiném vlákně, aby nespomalovala odezvu aplikace
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                pushAsync();
            }
        });
    }

    private void pushAsync(){


        //kontrola uživatelského nastavení, zda může zálohovat
        if(!checkUserSettings()) {
            Log.d(TAG, "Cannot make backup or auto sync is not allowed");
            return;
        }

        JSONArray request = makeMessage();

        String url = Server.getInstance().getPushUrl();
        //poslání JSONu na server a čekání na odpověd
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.POST, url, request, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            Log.d(TAG, "JSON: "+ response.toString());

                            JSONObject jsonObject1 = response.getJSONObject(0);
                            int status = jsonObject1.getInt("status");

                            Log.d(TAG, "KEY_STATUS = " + status);

                            //záloha dat na server proběhla v pořádku
                            if (status == 0) {

                                Log.d(TAG, "Data successfully pushed");
                                setAllRecordsClear();
                                //přístup serverem byl odepřepřen
                            } else{

                                Log.d(TAG, "Permission denied");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d(TAG, "Cannot connect to the server");
                        Log.d(TAG, "ERROR: "+ error.getMessage());


                    }
                });

        RequestQueue.getInstance(this.context).addToRequestQueue(jsonArrayRequest);
    }


    /**
     * Metoda, která kontroluje uživatelské nastavení.
     * Zda má uživatel zapnuté automatické zálohování.
     * Zda chce uživatel zálohovat data pouze na WiFi.
     *
     * @return logická hodnota, která říká, jestli mám zálohovat
     */
    private boolean checkUserSettings(){

        Settings settings = Settings.getInstance();

        //kontrola, zda má nastavenou automatickou zálohu dat
        if(settings.isSyncOn()){

            if(settings.isSyncAllowWifi()){
                return WifiCheckerUtility.isConnected(this.context);
            }else {
                return false;
            }
        }else {
            return false;
        }

    }

    public void setAllRecordsClear(){
        UserDatabaseHelper userDatabaseHelper = new UserDatabaseHelper(this.context);
        userDatabaseHelper.setAllRecordsClear(UserInformation.getInstance().getUserId());
    }

    public JSONArray makeMessage() {

        JSONObject userInfo = getUserInformation();
        JSONObject traders = getTradersData();
        JSONObject notes = getNotesData();
        JSONObject types = getTypesData();
        JSONObject bills = getBillsData();
        JSONObject storageItems = getStorageItemsData();
        JSONObject itemQuantities =  getItemQuantitiesData();


        JSONArray allData = new JSONArray();

        try {
            allData.put(0, userInfo);
            allData.put(1, traders);
            allData.put(2, notes);
            allData.put(3, types);
            allData.put(4, bills);
            allData.put(5, storageItems);
            allData.put(6, itemQuantities);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
        }

        Log.d("JSON DATA: ", " " + allData.toString());
        return allData;

    }

    private JSONObject getItemQuantitiesData() {
        ItemQuantityDatabaseHelper itemQuantityDatabaseHelper = new ItemQuantityDatabaseHelper(this.context);
        ArrayList<ItemQuantity> itemQuantities = itemQuantityDatabaseHelper.getAllItemQuantitiesForSync();
        JSONArray jsonItemQuantities = new JSONArray();
        JSONObject jsonResult = new JSONObject();

        try{
            for (int i = 0; i < itemQuantities.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                ItemQuantity itemQuantity = itemQuantities.get(i);

                jsonObject.put("id", itemQuantity.getId());
                jsonObject.put("user_id", itemQuantity.getUserId());
                jsonObject.put("storage_item_id", itemQuantity.getStorageItemId());
                jsonObject.put("is_deleted", itemQuantity.getIsDeleted());
                jsonObject.put("quantity", itemQuantity.getQuantity());
                jsonObject.put("bill_id", itemQuantity.getBillId());

                jsonItemQuantities.put(i, jsonObject);
            }
            jsonResult.put("item_quantities", jsonItemQuantities);

        }catch (JSONException e){
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
        }
        return jsonResult;
    }

    private JSONObject getStorageItemsData() {
        StorageItemDatabaseHelper storageItemDatabaseHelper = new StorageItemDatabaseHelper(this.context);
        ArrayList<StorageItem> storageItems = storageItemDatabaseHelper.getAllStorageItemsForSync();
        JSONArray jsonStorageItems = new JSONArray();
        JSONObject jsonResult = new JSONObject();

        try{
            for (int i = 0; i < storageItems.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                StorageItem storageItem = storageItems.get(i);

                jsonObject.put("id", storageItem.getId());
                jsonObject.put("user_id", storageItem.getUserId());
                jsonObject.put("name", storageItem.getName());
                jsonObject.put("unit", storageItem.getUnit());
                jsonObject.put("note", storageItem.getNote());
                jsonObject.put("is_deleted", storageItem.getIsDeleted());

                jsonStorageItems.put(i, jsonObject);
            }
            jsonResult.put("storage_items", jsonStorageItems);

        }catch (JSONException e){
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
        }
        return jsonResult;
    }

    private JSONObject getBillsData() {
        BillDatabaseHelper billDatabaseHelper = new BillDatabaseHelper(this.context);
        ArrayList<Bill> bills = billDatabaseHelper.getAllBillsForSync();
        JSONArray jsonBills = new JSONArray();
        JSONObject jsonResult = new JSONObject();

        try {
            for (int i = 0; i < bills.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                Bill bill = bills.get(i);

                jsonObject.put("id", bill.getId());
                jsonObject.put("user_id", bill.getUserId());
                jsonObject.put("number", bill.getName());
                jsonObject.put("amount", bill.getAmount());
                jsonObject.put("vat", bill.getVAT());
                jsonObject.put("date", bill.getDate());
                jsonObject.put("photo", bill.getPhoto());
                jsonObject.put("is_expense", bill.getIsExpense());
                jsonObject.put("type_id", bill.getTypeId());
                jsonObject.put("trader_id", bill.getTraderId());
                jsonObject.put("is_deleted", bill.getIsDeleted());

                jsonBills.put(i, jsonObject);

            }
            jsonResult.put("bills", jsonBills);

        }catch (JSONException e){
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
        }

        return jsonResult;
    }

    private JSONObject getTypesData(){
        TypeBillDatabaseHelper typeBillDatabaseHelper = new TypeBillDatabaseHelper(this.context);
        ArrayList<TypeBill> typeBills = typeBillDatabaseHelper.getAllTypesForSync();
        JSONArray jsonTypes = new JSONArray();
        JSONObject jsonResult = new JSONObject();

        try {

            for (int i = 0; i < typeBills.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                TypeBill typeBill = typeBills.get(i);

                jsonObject.put("id", typeBill.getId());
                jsonObject.put("user_id", UserInformation.getInstance().getUserId());
                jsonObject.put("name", typeBill.getName());
                jsonObject.put("color", typeBill.getColor());
                jsonObject.put("is_deleted", typeBill.getIsDeleted());
                jsonTypes.put(i, jsonObject);
            }

            jsonResult.put("types", jsonTypes);
        }catch (JSONException e){
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
        }
        return jsonResult;
    }

    private JSONObject getUserInformation() {
        UserDatabaseHelper userDatabaseHelper = new UserDatabaseHelper(this.context);
        User user = userDatabaseHelper.getUserById(UserInformation.getInstance().getUserId());
        JSONObject userInformation = new JSONObject();

        try {
            userInformation.put("email", user.getEmail());
            userInformation.put("password", user.getPassword());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
        }

        return userInformation;
    }

    private JSONObject getNotesData() {
        NoteDatabaseHelper noteDatabaseHelper = new NoteDatabaseHelper(this.context);
        ArrayList<Note> notes = noteDatabaseHelper.getAllNotesForSync();
        JSONArray jsonNotes = new JSONArray();
        JSONObject jsonResult = new JSONObject();

        try{

            for (int i = 0; i < notes.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                Note temp = notes.get(i);

                jsonObject.put("id", temp.getId());
                jsonObject.put("user_id", UserInformation.getInstance().getUserId());
                jsonObject.put("trader_id", temp.getTraderId());
                jsonObject.put("title", temp.getTitle());
                jsonObject.put("note", temp.getNote());
                jsonObject.put("date", temp.getDate());
                jsonObject.put("rating", temp.getRating());
                jsonObject.put("is_deleted", temp.getIsDeleted());
                jsonNotes.put(i, jsonObject);
            }

            jsonResult.put("notes", jsonNotes);

        }catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
        }
        return jsonResult;
    }

    private JSONObject getTradersData() {
        TraderDatabaseHelper traderDatabaseHelper = new TraderDatabaseHelper(this.context);
        ArrayList<Trader> traders = traderDatabaseHelper.getAllTradersForSync();
        JSONArray jsonTraders = new JSONArray();
        JSONObject jsonResult = new JSONObject();

        try {
            for (int i = 0; i < traders.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                Trader temp = traders.get(i);

                jsonObject.put("id", temp.getId());
                jsonObject.put("name", temp.getName());
                jsonObject.put("contact_person", temp.getContactPerson());
                jsonObject.put("is_deleted", temp.getIsDeleted());
                jsonObject.put("user_id", temp.getUserId());
                jsonObject.put("house_number", temp.getHouseNumber());
                jsonObject.put("phone_number", temp.getPhoneNumber());
                jsonObject.put("street", temp.getStreet());
                jsonObject.put("tin", temp.getTIN());
                jsonObject.put("in", temp.getIN());
                jsonObject.put("city", temp.getCity());
                jsonTraders.put(i, jsonObject);
            }

            jsonResult.put("traders", jsonTraders);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
        }

        return jsonResult;
    }

    public boolean isAllSynced(){
        JSONArray temp;
        JSONObject traders = getTradersData();
        JSONObject notes = getNotesData();
        JSONObject types = getTypesData();
        JSONObject bills = getBillsData();
        JSONObject storageItems = getStorageItemsData();
        JSONObject itemQuantities =  getItemQuantitiesData();;

        try {
            temp = traders.getJSONArray("traders");
            if(temp.length() != 0){
                return false;
            }

            temp = notes.getJSONArray("notes");
            if(temp.length() != 0){
                return false;
            }

            temp = types.getJSONArray("types");
            if(temp.length() != 0){
                return false;
            }

            temp = bills.getJSONArray("bills");
            if(temp.length() != 0){
                return false;
            }

            temp = storageItems.getJSONArray("storage_items");
            if(temp.length() != 0){
                return false;
            }

            temp = itemQuantities.getJSONArray("item_quantities");
            if(temp.length() != 0){
                return false;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return true;
    }

}
