package com.example.ondrejvane.zivnostnicek.activities.trader;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.database.TraderDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.helper.Header;
import com.example.ondrejvane.zivnostnicek.helper.InputValidation;
import com.example.ondrejvane.zivnostnicek.helper.TextInputLength;
import com.example.ondrejvane.zivnostnicek.session.Logout;
import com.example.ondrejvane.zivnostnicek.helper.Settings;
import com.example.ondrejvane.zivnostnicek.menu.Menu;
import com.example.ondrejvane.zivnostnicek.model.Trader;
import com.example.ondrejvane.zivnostnicek.server.Push;

/**
 * Aktivita, která se stará o vytvoření nového obchodníka.
 */
public class TraderNewActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private EditText inputCompanyName, inputContactPerson, inputTelephoneNumber;
    private EditText inputIdentificationNumber, inputTaxIdentificationNumber;
    private EditText inputCity, inputStreet, inputHouseNumber;
    private TextInputLayout inputLayoutCompanyName, inputLayoutTelephoneNumber;
    private TextInputLayout inputLayoutIdentificationNumber, inputLayoutTaxIdentificationNumber;

    private TraderDatabaseHelper traderDatabaseHelper;

    /**
     * Metoda, která se provede při spuštění akctivity a provede nezbytné
     * úkony ke správnému fungování aktivity.
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trader_new);
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

        initView();
    }

    /**
     * Procedura, která inicializuje všechny potřebné prvky
     * aktivity
     */
    private void initView() {
        inputCompanyName = findViewById(R.id.textInputEditTextCompanyName);
        inputContactPerson = findViewById(R.id.textInputEditTextContactPerson);
        inputTelephoneNumber = findViewById(R.id.textInputEditTextTelephoneNumber);
        inputIdentificationNumber = findViewById(R.id.textInputEditTextIdentificationNumber);
        inputTaxIdentificationNumber = findViewById(R.id.textInputEditTextTaxIdentificationNumber);
        inputCity = findViewById(R.id.textInputEditTextCity);
        inputStreet = findViewById(R.id.textInputEditTextStreet);
        inputHouseNumber = findViewById(R.id.textInputEditTextHouseNumber);
        inputLayoutCompanyName = findViewById(R.id.textInputLayoutCompanyName);
        inputLayoutTelephoneNumber = findViewById(R.id.textInputLayoutTelephoneNumber);
        inputLayoutIdentificationNumber = findViewById(R.id.textInputLayoutIdentificationNumber);
        inputLayoutTaxIdentificationNumber = findViewById(R.id.textInputLayoutTaxIdentificationNumber);
        traderDatabaseHelper = new TraderDatabaseHelper(TraderNewActivity.this);

    }


    /**
     * Metoda, která po stisknutí tlačítka zpět nastartuje příslušnou
     * aktivitu a přiloží potřebné informace.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

        finish();
    }


    /**
     * Metoda, která načte všechny vstupní pole. Zvaliduje data
     * vložená uživatelem a vloží záznam obchodníka do databáze.
     *
     * @param view view příslušné aktivity
     */
    public void submitTraderForm(View view) {

        if (!inputValidation()) {
            return;
        }

        Trader trader = new Trader();
        trader.setName(inputCompanyName.getText().toString());
        trader.setContactPerson(inputContactPerson.getText().toString());
        trader.setPhoneNumber(inputTelephoneNumber.getText().toString());
        trader.setIN(inputIdentificationNumber.getText().toString());
        trader.setTIN(inputTaxIdentificationNumber.getText().toString());
        trader.setCity(inputCity.getText().toString());
        trader.setStreet(inputStreet.getText().toString());
        trader.setHouseNumber(inputHouseNumber.getText().toString());
        trader.setIsDirty(1);
        trader.setIsDeleted(0);

        //uložení obchodníka do databáze
        traderDatabaseHelper.addTrader(trader, false);

        //pokus o automatickou záluhu dat na server
        Push push = new Push(this);
        push.push();

        String message = getString(R.string.trader_is_created);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Intent income = new Intent(TraderNewActivity.this, TraderActivity.class);
        startActivity(income);
        finish();

    }

    /**
     * Kontrola všechn vstupních hodnot
     *
     * @return logická hodnota, která označuje zda je vše v pořádku
     */
    private boolean inputValidation() {
        //načtení vstupních hodnot
        String tradeName = inputCompanyName.getText().toString();
        String contactPerson = inputContactPerson.getText().toString();
        String phoneNumber = inputTelephoneNumber.getText().toString();
        String identificationNumber = inputIdentificationNumber.getText().toString();
        String taxIdentificationNumber = inputTaxIdentificationNumber.getText().toString();
        String traderCity = inputCity.getText().toString();
        String traderStreet = inputStreet.getText().toString();
        String traderHouseNumber = inputHouseNumber.getText().toString();


        //validace názvu obchodníka
        if (tradeName.isEmpty()) {
            String message = getString(R.string.company_name_is_empty);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            inputLayoutCompanyName.setError(message);
            return false;
        } else if (tradeName.length() > TextInputLength.TRADER_NAME_LENGTH) {
            String message = getString(R.string.input_is_too_long);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            inputLayoutCompanyName.setError(message);
            return false;
        }

        //validace kontaktní osoby
        if (contactPerson.length() > TextInputLength.TRADER_CONTACT_PERSON_LENGTH) {
            String message = getString(R.string.input_is_too_long);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            inputContactPerson.setError(message);
            return false;
        }


        //validace telefoního čísla
        if (!InputValidation.validatePhoneNumber(phoneNumber)) {
            String message = getString(R.string.telephone_has_wrong_format);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            inputLayoutTelephoneNumber.setError(message);
            return false;
        }

        //validace IČO
        if (!InputValidation.validateIdentificationNumber(identificationNumber)) {
            //implementace nastavení
            if (!Settings.getInstance().isIsForeignIdentificationNumberPossible()) {
                String message = getString(R.string.wrong_id_number);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                inputLayoutIdentificationNumber.setError(message);
                return false;
            }
        }

        //validace českého dič
        if (!InputValidation.validateCzechTaxIdentificationNumber(taxIdentificationNumber)) {
            //implementace nastavení
            if (!Settings.getInstance().isIsForeignTaxIdentificationNumberPossible()) {
                String message = getString(R.string.wrong_format_of_tid);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                inputLayoutTaxIdentificationNumber.setError(message);
                return false;
                //pokud je nastavena volba povolit zahraniční DIČ, tak je použita tato validace
            } else if (!InputValidation.validateForeignTaxIdentificationNumber(taxIdentificationNumber)) {
                String message = getString(R.string.wrong_format_of_tid);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                inputLayoutTaxIdentificationNumber.setError(getString(R.string.wrong_format_of_tid));
                return false;
            }
        }

        if (!traderCity.isEmpty() && traderCity.length() > TextInputLength.TRADER_CITY_LENGTH) {
            String message = getString(R.string.input_is_too_long);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            inputCity.setError(message);
            return false;
        }

        if (!traderStreet.isEmpty() && traderStreet.length() > TextInputLength.TRADER_STREET_LENGTH) {
            String message = getString(R.string.input_is_too_long);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            inputStreet.setError(message);
            return false;
        }

        if (!traderHouseNumber.isEmpty() && traderHouseNumber.length() > TextInputLength.TRADER_HOUSE_NUMBER_LENGTH) {
            String message = getString(R.string.input_is_too_long);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            inputHouseNumber.setError(message);
            return false;
        }

        return true;
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

        TraderNewActivity thisActivity = TraderNewActivity.this;
        Intent newIntent;

        //inicializace třídy menu, kde jsou definovány jednotlivé aktivity
        Menu menu = new Menu(thisActivity);
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
