package com.example.ondrejvane.zivnostnicek.activities.sync;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.activities.LoginActivity;
import com.example.ondrejvane.zivnostnicek.activities.home.HomeActivity;
import com.example.ondrejvane.zivnostnicek.database.DatabaseHelper;
import com.example.ondrejvane.zivnostnicek.database.IdentifiersDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.database.UserDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.helper.Header;
import com.example.ondrejvane.zivnostnicek.helper.Logout;
import com.example.ondrejvane.zivnostnicek.helper.SecurePassword;
import com.example.ondrejvane.zivnostnicek.helper.Settings;
import com.example.ondrejvane.zivnostnicek.helper.UserInformation;
import com.example.ondrejvane.zivnostnicek.model.User;
import com.example.ondrejvane.zivnostnicek.server.Pull;
import com.example.ondrejvane.zivnostnicek.server.Push;
import com.example.ondrejvane.zivnostnicek.server.Server;
import com.example.ondrejvane.zivnostnicek.session.MySingleton;
import com.example.ondrejvane.zivnostnicek.utilities.WifiCheckerUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SynchronizationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "SyncActivity";

    //prvky aktivity
    private CheckBox checkBoxSyncTurnOn;
    private CheckBox checkBoxAllowOnWifi;
    private TextView textViewSyncInfo1;
    private ProgressDialog progressDialog;

    //pomocné globální proměnné
    private Settings settings;
    private UserDatabaseHelper userDatabaseHelper;

    //klíče
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_STATUS = "status";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synchronization);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Header header = new Header(navigationView);
        header.setTextToHeader();

        //iniciaalizace prvků ativity
        initActivity();

        //zobrazení předchozího nastavení
        setSettings();

        checkBoxSyncTurnOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkBoxSyncTurnOn.isChecked()) {
                    textViewSyncInfo1.setTextColor(getResources().getColor(R.color.grey));
                    checkBoxAllowOnWifi.setEnabled(true);
                } else {
                    textViewSyncInfo1.setTextColor(getResources().getColor(R.color.white));
                    checkBoxAllowOnWifi.setEnabled(false);
                }

            }
        });

    }

    private void initActivity() {
        checkBoxSyncTurnOn = findViewById(R.id.checkBoxSyncOn);
        checkBoxAllowOnWifi = findViewById(R.id.checkBoxSyncWiFi);
        textViewSyncInfo1 = findViewById(R.id.textViewSyncInfo1);

        settings = Settings.getInstance();
        userDatabaseHelper = new UserDatabaseHelper(this);
    }

    private void setSettings() {

        checkBoxSyncTurnOn.setChecked(settings.isSyncOn());
        checkBoxAllowOnWifi.setChecked(settings.isSyncAllowWifi());

        if (checkBoxSyncTurnOn.isChecked()) {
            textViewSyncInfo1.setTextColor(getResources().getColor(R.color.grey));
        }
    }

    public void syncPull() {
        displayLoader();
        User user = userDatabaseHelper.getUserById(UserInformation.getInstance().getUserId());
        JSONObject jsonObject = new JSONObject();
        JSONArray request = new JSONArray();
        try {
            //vložení uživatelských dat do JSONu
            jsonObject.put(KEY_EMAIL, user.getEmail());
            jsonObject.put(KEY_PASSWORD, user.getPassword());
            request.put(0, jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        String url = Server.getPullUrl();
        //poslání JSONu na server a čekání na odpověd
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.POST, url, request, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            Log.d(TAG, "JSON: "+ response.toString());

                            JSONObject jsonObject1 = response.getJSONObject(0);
                            int status = jsonObject1.getInt(KEY_STATUS);

                            Log.d(TAG, "KEY_STATUS = " + status);

                            //uživatel byl správně ověřen a byla poslána data
                            if (status == 0) {

                                //zpracování přijatých dat a uložení dat do databáze
                                Pull pull = new Pull(SynchronizationActivity.this);
                                pull.deleteAllUserData();
                                pull.saveDataFromServer(response);
                                pull.refreshAllIdentifiers();
                                progressDialog.dismiss();

                                //informování uživatele o správném výsledku
                                //vypsání uživateli
                                Toast.makeText(getApplicationContext(),
                                        getResources().getString(R.string.data_successfully_restored),
                                        Toast.LENGTH_SHORT).show();

                            //uživatel nebyl dobře ověřen => přístup zamítnut
                            } else{
                                progressDialog.dismiss();
                                //pokud ověření uživatele neproběhlo správně
                                Toast.makeText(getApplicationContext(),
                                        getResources().getString(R.string.permission_denied),
                                        Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();

                        //zobrazení informace uživateli, pokud došlo k chybě
                        Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.can_not_connect_to_the_server), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Error message: " + error.getMessage());

                    }
                });

        MySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);

    }


    public void syncPush(View view){
        displayLoader();
        final Push push = new Push(SynchronizationActivity.this);
        JSONArray request = push.makeMessage();

        String url = Server.getPushUrl();
        //poslání JSONu na server a čekání na odpověd
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.POST, url, request, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            progressDialog.dismiss();
                            Log.d(TAG, "JSON: "+ response.toString());

                            JSONObject jsonObject1 = response.getJSONObject(0);
                            int status = jsonObject1.getInt(KEY_STATUS);

                            Log.d(TAG, "KEY_STATUS = " + status);

                            //uživatel byl správně ověřen a byla poslána data
                            if (status == 0) {

                                push.setAllRecordsClear();
                                //informování uživatele o správném výsledku
                                //vypsání uživateli
                                Toast.makeText(getApplicationContext(),
                                        getResources().getString(R.string.data_successfully_restored),
                                        Toast.LENGTH_SHORT).show();

                                //uživatel nebyl dobře ověřen => přístup zamítnut
                            } else{
                                //pokud ověření uživatele neproběhlo správně
                                Toast.makeText(getApplicationContext(),
                                        getResources().getString(R.string.permission_denied),
                                        Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();

                        //zobrazení informace uživateli, pokud došlo k chybě
                        Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.can_not_connect_to_the_server), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Error message: " + error.getMessage());

                    }
                });

        MySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }

    /*
    public void syncPush(View view) {
        Log.d(TAG, "Start synchronization after click");
        if (WifiCheckerUtility.isConnected(this)) {
            Log.d(TAG, "Connected to wifi");
        } else {
            Log.d(TAG, "Not connected to wifi");
        }

        //vytvoření třídy pro nahrání dat na server
        Push push = new Push(this);
        //nahrání dat na server není prováděno na pozadí
        push.push(false);
    }

*/
    /**
     * Progress dialog pro v průběhu synchronizace.
     */
    private void displayLoader() {
        progressDialog = new ProgressDialog(SynchronizationActivity.this);
        progressDialog.setMessage(getString(R.string.login_in_progress));
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

    }


    /**
     * Procedura, která zobrazí dialogové okno.
     */
    public void syncAlertDialog(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(R.string.pull_question).setCancelable(false)
                .setPositiveButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        syncPull();
                    }
                });
        AlertDialog alertDialog = alert.create();
        alertDialog.show();

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onStop() {
        Log.d(TAG, "Saving to the share pref");

        //načtení aktuálního nastavení
        settings.setSyncOn(checkBoxSyncTurnOn.isChecked());
        settings.setSyncAllowWifi(checkBoxAllowOnWifi.isChecked());

        //uložení nastavení o synchronizaci do nastavení
        settings.saveSettingsToSharedPreferences(this);
        super.onStop();
    }


    /**
     * Metoda, která se stará o hlavní navigační menu aplikace.
     *
     * @param item vybraná položka v menu
     * @return boolean
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //id vybrané položky v menu
        int id = item.getItemId();

        SynchronizationActivity thisActivity = SynchronizationActivity.this;
        Intent newIntent;

        //inicializace třídy menu, kde jsou definovány jednotlivé aktivity
        com.example.ondrejvane.zivnostnicek.menu.Menu menu = new com.example.ondrejvane.zivnostnicek.menu.Menu(thisActivity);
        newIntent = menu.getMenu(id);

        //pokud jedná o nějakou aktivitu, tak se spustí
        if (newIntent != null) {
            startActivity(menu.getMenu(id));
            finish();
        } else {
            //pokud byla stisknuta položka odhlášení
            Logout logout = new Logout(thisActivity, this);
            logout.logout();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
