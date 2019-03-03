package com.example.ondrejvane.zivnostnicek.server;

import android.content.Context;
import android.util.Log;

import com.example.ondrejvane.zivnostnicek.database.NoteDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.database.TraderDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.database.UserDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.helper.UserInformation;
import com.example.ondrejvane.zivnostnicek.model.Note;
import com.example.ondrejvane.zivnostnicek.model.Trader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Pull {

    private Context context;
    private static final String NULL = "null";

    public Pull(Context context) {
        this.context = context;
    }


    /**
     * Smazání všech lokálních dat přihlášeného uživatelel.
     */
    public void deleteAllUserData() {
        UserDatabaseHelper userDatabaseHelper = new UserDatabaseHelper(this.context);
        userDatabaseHelper.deleteAllUserData(UserInformation.getInstance().getUserId());
    }

    public void saveDataFromServer(JSONArray jsonArray) {

        try {

            JSONObject temp;

            temp = jsonArray.getJSONObject(1);
            JSONArray traders = temp.getJSONArray("traders");

            temp = jsonArray.getJSONObject(2);
            JSONArray notes = temp.getJSONArray("notes");

            saveTradersDataFromServer(traders);
            saveNotesDataFromServer(notes);

            Log.d("Pull", "Traders: " + traders.toString());
            Log.d("Pull", "Notes: " + notes.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveNotesDataFromServer(JSONArray notes) {
        NoteDatabaseHelper noteDatabaseHelper = new NoteDatabaseHelper(this.context);

        for (int i = 0; i < notes.length(); i++) {
            JSONObject temp = null;
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
                    trader.setTIN(temp.getString("i_n"));
                }

                if (!temp.getString("city").equals(NULL)) {
                    trader.setTIN(temp.getString("city"));
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
