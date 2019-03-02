package com.example.ondrejvane.zivnostnicek.activities.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

/**
 * KAtivita, která se stará o uživatelské nastavení aplikace a pozdější
 * uložení nastavení do shared preferences.
 */
public class SettingsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //grafické prvky aktivity
    private CheckBox checkBoxIN;
    private CheckBox checkBoxTIN;
    private CheckBox checkBoxYear;
    private Spinner spinnerYear;
    private CheckBox checkBoxMonth;
    private Spinner spinnerMonth;

    //pomocné globální proměnné
    private boolean isSelectedYear;
    private boolean isSelectedMonth;
    private Settings settings;

    /**
     * Meotda, která je volána pro vytvoření aktivity
     *
     * @param savedInstanceState savedInstanceState
     */
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

        //nastavení textu do headeru
        Header header = new Header( navigationView);
        header.setTextToHeader();

        //inicializace aktivity
        initActivity();

        //nastavení liteneru pro check box roku
        checkBoxYear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isSelectedYear){
                    spinnerYear.setEnabled(false);
                    checkBoxMonth.setEnabled(false);
                    isSelectedYear = false;
                }else {
                    spinnerYear.setEnabled(true);
                    checkBoxMonth.setEnabled(true);
                    isSelectedYear = true;
                }
            }
        });

        //nastavení liteneru pro check box měsíce
        checkBoxMonth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isSelectedMonth){
                    spinnerMonth.setEnabled(false);
                    isSelectedMonth = false;
                }else {
                    spinnerMonth.setEnabled(true);
                    isSelectedMonth = true;
                }
            }
        });
    }

    /**
     * Procedura, která inicializuje aktivitu,
     */
    private void initActivity() {
        checkBoxIN = findViewById(R.id.checkBoxSettingsIN);
        checkBoxTIN = findViewById(R.id.checkBoxSettingsTIN);
        checkBoxYear = findViewById(R.id.checkBoxSettingsYear);
        spinnerYear = findViewById(R.id.spinnerSettingsYear);
        checkBoxMonth = findViewById(R.id.checkBoxSettingsMonth);
        spinnerMonth = findViewById(R.id.spinnerSettingsMonth);
        settings = Settings.getInstance();

        //načtení nastavení z Třídy Settings a zobrazení
        isSelectedYear = settings.isIsPickedOneYear();
        isSelectedMonth = settings.isPickedOneMonth();

        spinnerYear.setEnabled(isSelectedYear);
        spinnerMonth.setEnabled(isSelectedMonth);

        checkBoxIN.setChecked(settings.isIsForeignIdentificationNumberPossible());
        checkBoxTIN.setChecked(settings.isIsForeignTaxIdentificationNumberPossible());
        checkBoxYear.setChecked(isSelectedYear);
        checkBoxMonth.setChecked(isSelectedMonth);
        if (isSelectedYear){
            //nastavit vybraný rok do spinneru
            spinnerYear.setSelection(settings.getArrayYearId());
        }

        if(isSelectedMonth){
            spinnerMonth.setSelection(settings.getArrayMonthId());
        }
    }

    /**
     * Metoda, která načte nastavení a uloží do třídy Settings
     * a pomocí další metody uloží data do shared preferences, aby
     * byla data přístupná i po vypnutí aplikace.
     *
     * @param view view
     */
    public void saveSettings(View view) {
        settings.setIsForeignIdentificationNumberPossible(checkBoxIN.isChecked());
        settings.setIsForeignTaxIdentificationNumberPossible(checkBoxTIN.isChecked());

        //uložení nastavení roku do tříd settings
        if (isSelectedYear){
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

        //uložení nastavní měsíce do třídy settings
        if (isSelectedMonth){
            settings.setIsPickedOneMonth(true);
            if(spinnerMonth.getSelectedItemId() != 0){
                settings.setMonth(spinnerMonth.getSelectedItem().toString());
                settings.setArrayMonthId((int) spinnerMonth.getSelectedItemId());

            }else {
                //pokud není vybrán žádný měsíc
                String message = getString(R.string.select_month);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                return;
            }

        }else {
            settings.setIsPickedOneMonth(false);
            settings.setMonth(null);
            settings.setArrayMonthId(-1);
        }

        //uložení data do shared preferences
        settings.saveSettingsToSharedPreferences(this);

        //vypsání hlášky o úspěšném uložení nastavení
        String message = getString(R.string.settings_saved);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    /**
     * Metoda, která je volána pro stisknutí tlačítka zpět.
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
     * Metoda, která se stará o hlavní navigační menu aplikace.
     * @param item  vybraná položka v menu
     * @return      boolean
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
