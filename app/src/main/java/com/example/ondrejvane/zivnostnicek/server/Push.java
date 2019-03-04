package com.example.ondrejvane.zivnostnicek.server;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.ondrejvane.zivnostnicek.database.BillDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.database.NoteDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.database.TraderDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.database.TypeBillDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.database.UserDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.helper.Settings;
import com.example.ondrejvane.zivnostnicek.helper.UserInformation;
import com.example.ondrejvane.zivnostnicek.model.Bill;
import com.example.ondrejvane.zivnostnicek.model.Note;
import com.example.ondrejvane.zivnostnicek.model.Trader;
import com.example.ondrejvane.zivnostnicek.model.TypeBill;
import com.example.ondrejvane.zivnostnicek.model.User;
import com.example.ondrejvane.zivnostnicek.session.MySingleton;
import com.example.ondrejvane.zivnostnicek.utilities.WifiCheckerUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Push {

    private Context context;
    private static final String TAG = "Push";
    private static final String PUSH_URL = "/api/push.php";

    public Push(Context context) {
        this.context = context;
    }

    public void push(boolean onBackground){


        //pokud je uživatel na mobilních datech a má nastavenou zálohu pouze přes wifi => tak se nic neprovede
        if(!checkUserSettings() && onBackground) {
            Log.d(TAG, "Cannot make backup => WIFI settings");
            return;
        }

        JSONArray request = makeMessage();

        String url = Server.getSeverName() + PUSH_URL;
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

        MySingleton.getInstance(this.context).addToRequestQueue(jsonArrayRequest);
    }

    /**
     * Metoda, která kontroluje uživatelské nastavení.
     * Zda chce uživatel zálohovat data pouze na WiFi.
     *
     * @return logická hodnota, která říká, jestli můžu zálohovat
     */
    private boolean checkUserSettings(){

        Settings settings = Settings.getInstance();
        if(settings.isSyncAllowWifi()){
            if(WifiCheckerUtility.isConnected(this.context)){
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }

    private JSONArray makeMessage() {
        JSONObject userInfo = getUserInformation();
        JSONObject traders = getTradersData();
        JSONObject notes = getNotesData();
        JSONObject types = getTypesData();
        JSONObject bills = getBillsData();

        JSONArray allData = new JSONArray();

        try {
            allData.put(0, userInfo);
            allData.put(1, traders);
            allData.put(2, notes);
            allData.put(3, types);
            allData.put(4, bills);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
        }

        Log.d("JSON DATA: ", " " + allData.toString());
        return allData;

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

}
