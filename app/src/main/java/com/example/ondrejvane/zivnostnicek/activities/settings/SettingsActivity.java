package com.example.ondrejvane.zivnostnicek.activities.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.helper.Header;
import com.example.ondrejvane.zivnostnicek.helper.Logout;
import com.example.ondrejvane.zivnostnicek.helper.Settings;

public class SettingsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private CheckBox checkBoxIN;
    private CheckBox checkBoxTIN;
    private CheckBox checkBoxYear;
    private Spinner spinnerYear;


    private boolean isSelected;
    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Header header = new Header( navigationView, this);
        header.setTextToHeader();

        initActivity();
        checkBoxYear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isSelected){
                    spinnerYear.setEnabled(false);
                    isSelected = false;
                }else {
                    spinnerYear.setEnabled(true);
                    isSelected = true;
                }
            }
        });
    }

    private void initActivity() {
        checkBoxIN = findViewById(R.id.checkBoxSettingsIN);
        checkBoxTIN = findViewById(R.id.checkBoxSettingsTIN);
        checkBoxYear = findViewById(R.id.checkBoxSettingsYear);
        spinnerYear = findViewById(R.id.spinnerSettingsYear);
        settings = Settings.getInstance();

        //načtení nastavení z Třídy Settings a zobrazení
        isSelected = settings.isIsPickedOneYear();
        spinnerYear.setEnabled(isSelected);
        checkBoxIN.setChecked(settings.isIsForeignIdentificationNumberPossible());
        checkBoxTIN.setChecked(settings.isIsForeignTaxIdentificationNumberPossible());
        checkBoxYear.setChecked(isSelected);
        if (isSelected){
            //nastavit vybraný rok do spinneru
            spinnerYear.setSelection(settings.getArrayYearId());
        }
    }

    /**
     * Metoda, která načte nastavení a uloží do třídy Settings
     * a pomocí další metody uloží data do shared preferences, aby
     * byla data přístupná i po vypnutí aplikace.
     * @param view
     */
    public void saveSettings(View view) {
        settings.setIsForeignIdentificationNumberPossible(checkBoxIN.isChecked());
        settings.setIsForeignTaxIdentificationNumberPossible(checkBoxTIN.isChecked());

        if (isSelected){
            settings.setIsPickedOneYear(true);
            if(spinnerYear.getSelectedItemId() != 0){
                settings.setYear(spinnerYear.getSelectedItem().toString());
                settings.setArrayYearId((int) spinnerYear.getSelectedItemId());

            }else {
                //pokud není vybrán žádný rok
                String message = getString(R.string.select_year);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                return;
            }

        }else {
            settings.setIsPickedOneYear(false);
            settings.setYear(null);
            settings.setArrayYearId(-1);
        }

        //uložení data do shared preferences
        settings.saveSettingsToSharedPreferences(this);

        //vypsání hlášky o úspěšném uložení nastavení
        String message = getString(R.string.settings_saved);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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

    /**
     * Metoda, která se stará o hlavní navigační menu aplikace.
     * @param item  vybraná položka v menu
     * @return      boolean
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //id vybrané položky v menu
        int id = item.getItemId();

        SettingsActivity thisActivity = SettingsActivity.this;
        Intent newIntent;

        //inicializace třídy menu, kde jsou definovány jednotlivé aktivity
        com.example.ondrejvane.zivnostnicek.menu.Menu menu = new com.example.ondrejvane.zivnostnicek.menu.Menu(thisActivity);
        newIntent = menu.getMenu(id);

        //pokud jedná o nějakou aktivitu, tak se spustí
        if(newIntent != null){
            startActivity(menu.getMenu(id));
            finish();
        }else {
            //pokud byla stisknuta položka odhlášení
            Logout logout = new Logout(thisActivity, this);
            logout.logout();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
