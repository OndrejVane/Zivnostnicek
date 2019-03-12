package com.example.ondrejvane.zivnostnicek.activities.sync;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.database.UserDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.helper.Header;
import com.example.ondrejvane.zivnostnicek.server.HttpsTrustManager;
import com.example.ondrejvane.zivnostnicek.session.Logout;
import com.example.ondrejvane.zivnostnicek.helper.Settings;
import com.example.ondrejvane.zivnostnicek.session.UserInformation;
import com.example.ondrejvane.zivnostnicek.model.User;
import com.example.ondrejvane.zivnostnicek.server.Pull;
import com.example.ondrejvane.zivnostnicek.server.Push;
import com.example.ondrejvane.zivnostnicek.server.Server;
import com.example.ondrejvane.zivnostnicek.server.RequestQueue;

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
    private TextView textViewIsAllSynced;
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

        //kontrola, zda je vše zálohované nebo ne
        checkIfIsAllSynced();

        //listener pro zobrazení informačního textu o zálohování
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

    private void checkIfIsAllSynced() {
        Push push = new Push(SynchronizationActivity.this);
        if (push.isAllSynced()) {
            textViewIsAllSynced.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_cloud_on, 0, 0, 0);
            textViewIsAllSynced.setText(R.string.all_data_is_backed_up);
        }
    }

    /**
     * Procedura pro inicializaci
     * potřebných prvků v aktivitě
     */
    private void initActivity() {
        checkBoxSyncTurnOn = findViewById(R.id.checkBoxSyncOn);
        checkBoxAllowOnWifi = findViewById(R.id.checkBoxSyncWiFi);
        textViewSyncInfo1 = findViewById(R.id.textViewSyncInfo1);
        textViewIsAllSynced = findViewById(R.id.textViewIsAllSynced);


        settings = Settings.getInstance();
        userDatabaseHelper = new UserDatabaseHelper(this);
    }

    /**
     * Načtení uživatelského nastavení.
     */
    private void setSettings() {

        checkBoxSyncTurnOn.setChecked(settings.isSyncOn());
        checkBoxAllowOnWifi.setChecked(settings.isSyncAllowWifi());

        //pokud je vybrána automatická záloha => zobrazit informační text
        if (checkBoxSyncTurnOn.isChecked()) {
            textViewSyncInfo1.setTextColor(getResources().getColor(R.color.grey));
        }

        if (checkBoxAllowOnWifi.isChecked()) {
            checkBoxAllowOnWifi.setEnabled(true);
        }
    }

    /**
     * Metoda, která obnoví všechny data, která
     * jsou uložena v serverové databázi. Data jsou
     * přijata ve formátu JSON a následně jsou uloženy
     * do lokální databáze. Před uložení dojde ke smazání
     * všech lokálních záznamů uživatele, aby nedošlo ke konfliktům
     * primárních klíčů.
     */
    public void syncPull() {
        displayLoader(getString(R.string.recovering_data_from_the_server));
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

        //povolení nedůvěryhodných certifikátu pro zabezpečené spojení
        HttpsTrustManager.allowAllSSL();

        String url = Server.getInstance().getPullUrl();
        //poslání JSONu na server a čekání na odpověd
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.POST, url, request, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            Log.d(TAG, "JSON: " + response.toString());

                            JSONObject jsonObject1 = response.getJSONObject(0);
                            int status = jsonObject1.getInt(KEY_STATUS);

                            Log.d(TAG, "KEY_STATUS = " + status);

                            //uživatel byl správně ověřen a byla poslána data
                            if (status == 0) {

                                //zpracování přijatých dat a uložení dat do databáze
                                Pull pull = new Pull(SynchronizationActivity.this);
                                pull.pull(response);

                                //skyrytí progress dialogu
                                progressDialog.dismiss();

                                //informování uživatele o správném výsledku
                                //vypsání uživateli
                                Toast.makeText(getApplicationContext(),
                                        getResources().getString(R.string.data_successfully_restored),
                                        Toast.LENGTH_SHORT).show();

                                //uživatel nebyl dobře ověřen => přístup zamítnut
                            } else {
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

        RequestQueue.getInstance(this).addToRequestQueue(jsonArrayRequest);

    }


    /**
     * Metoda, která zazálohuje všechna
     * data, která nejsou uloženy na serveru. Z lokální databáze
     * jsou načteny všechny záznamy, které byly přidány nebo upravovány.
     * Následně je vytvořen JSON se všemy daty a tento JSON je poslán
     * na server. Od serveru dostaneme odpověď, zda byla data uložena.
     *
     * @param view view aktivity
     */
    public void syncPush(View view) {
        displayLoader(getString(R.string.saving_data_to_the_server));
        final Push push = new Push(SynchronizationActivity.this);
        JSONArray request = push.makeMessage();

        //povolení nedůvěryhodných certifikátu pro zabezpečené spojení
        HttpsTrustManager.allowAllSSL();

        String url = Server.getInstance().getPushUrl();
        //poslání JSONu na server a čekání na odpověd
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.POST, url, request, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {

                            Log.d(TAG, "JSON: " + response.toString());

                            JSONObject jsonObject1 = response.getJSONObject(0);
                            int status = jsonObject1.getInt(KEY_STATUS);

                            Log.d(TAG, "KEY_STATUS = " + status);

                            //uživatel byl správně ověřen a byla poslána data
                            if (status == 0) {

                                //označení všech zálohovaných dat jako čisté
                                push.setAllRecordsClear();

                                //nastavení, že všechna data jsou zálohovaná
                                textViewIsAllSynced.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_cloud_on, 0, 0, 0);
                                textViewIsAllSynced.setText(R.string.all_data_is_backed_up);

                                //skyrytí progres dialogu
                                progressDialog.dismiss();

                                //informování uživatele o správném výsledku
                                //vypsání uživateli
                                Toast.makeText(getApplicationContext(),
                                        getResources().getString(R.string.data_successfully_saved_to_the_server),
                                        Toast.LENGTH_SHORT).show();

                                //uživatel nebyl dobře ověřen => přístup zamítnut
                            } else {
                                //pokud ověření uživatele neproběhlo správně
                                Toast.makeText(getApplicationContext(),
                                        getResources().getString(R.string.permission_denied),
                                        Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();

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

        RequestQueue.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }

    /**
     * Progress dialog pro v průběhu synchronizace.
     */
    private void displayLoader(String message) {
        progressDialog = new ProgressDialog(SynchronizationActivity.this);
        progressDialog.setMessage(message);
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


    /**
     * Metoda, která je volána po stisknutí tlačítka zpět.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Při ukončení aktivity dojde k u uložení aktuálního
     * nastavení do SP.
     */
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
