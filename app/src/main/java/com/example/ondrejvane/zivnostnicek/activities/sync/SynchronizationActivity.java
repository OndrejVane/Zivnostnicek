package com.example.ondrejvane.zivnostnicek.activities.sync;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.helper.Header;
import com.example.ondrejvane.zivnostnicek.helper.Logout;
import com.example.ondrejvane.zivnostnicek.helper.Settings;
import com.example.ondrejvane.zivnostnicek.utilities.WifiCheckerUtility;

public class SynchronizationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "SyncActivity";

    //prvky aktivity
    private Button buttonSyncNow;
    private CheckBox checkBoxSyncTurnOn;
    private CheckBox checkBoxAllowOnWifi;
    private TextView textViewSyncInfo1;
    private TextView textViewSyncInfo2;

    //pomocné globální proměnné
    private Settings settings;

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
                if(checkBoxSyncTurnOn.isChecked()){
                    textViewSyncInfo1.setTextColor(getResources().getColor(R.color.grey));
                    textViewSyncInfo2.setTextColor(getResources().getColor(R.color.grey));
                    checkBoxAllowOnWifi.setEnabled(true);
                }else {
                    textViewSyncInfo1.setTextColor(getResources().getColor(R.color.white));
                    textViewSyncInfo2.setTextColor(getResources().getColor(R.color.white));
                    checkBoxAllowOnWifi.setEnabled(false);
                }

            }
        });
    }

    private void initActivity() {
        buttonSyncNow = findViewById(R.id.buttonPushNow);
        checkBoxSyncTurnOn = findViewById(R.id.checkBoxSyncOn);
        checkBoxAllowOnWifi = findViewById(R.id.checkBoxSyncWiFi);
        textViewSyncInfo1 = findViewById(R.id.textViewSyncInfo1);
        textViewSyncInfo2 = findViewById(R.id.textViewSyncInfo2);

        settings = Settings.getInstance();
    }

    private void setSettings() {

        checkBoxSyncTurnOn.setChecked(settings.isSyncOn());
        checkBoxAllowOnWifi.setChecked(settings.isSyncAllowWifi());



        if(checkBoxSyncTurnOn.isChecked()) {
            textViewSyncInfo1.setTextColor(getResources().getColor(R.color.grey));
            textViewSyncInfo2.setTextColor(getResources().getColor(R.color.grey));
        }
    }

    private void saveSettings(){

    }
    public void startSync(View view) {
        Log.d(TAG, "Start synchronization after click");
        if(WifiCheckerUtility.isConnected(this)){
            Log.d(TAG, "Connected to wifi");
        }else {
            Log.d(TAG, "Not connected to wifi");
        }
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
