package com.example.ondrejvane.zivnostnicek.activities.trader;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.database.ModelHelpers.TraderDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.helper.Header;
import com.example.ondrejvane.zivnostnicek.helper.InputValidation;
import com.example.ondrejvane.zivnostnicek.helper.TextInputLength;
import com.example.ondrejvane.zivnostnicek.session.Logout;
import com.example.ondrejvane.zivnostnicek.helper.Settings;
import com.example.ondrejvane.zivnostnicek.menu.Menu;
import com.example.ondrejvane.zivnostnicek.model.Trader;
import com.example.ondrejvane.zivnostnicek.server.Push;

/**
 * Aktivita, která se stará o editaci existujícího obchodníka.
 */
public class TraderEditActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private int traderID;
    private TraderDatabaseHelper traderDatabaseHelper;
    private Trader trader;

    private EditText inputCompanyNameEdit, inputContactPersonEdit, inputTelephoneNumberEdit;
    private EditText inputIdentificationNumberEdit, inputTaxIdentificationNumberEdit;
    private EditText inputCityEdit, inputStreetEdit, inputHouseNumberEdit;
    private TextInputLayout inputLayoutCompanyNameEdit, inputLayoutTelephoneNumberEdit;
    private TextInputLayout inputLayoutIdentificationNumberEdit, inputLayoutTaxIdentificationNumberEdit;
    private CheckBox checkBoxSameINasTIN;

    /**
     * Metoda, která se provede při spuštění akctivity a porovede nezbytné
     * úkony ke správnému fungování aktivity.
     *
     * @param savedInstanceState saved instace state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trader_edit);
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

        initActivity();

        setTextToActivity();

        //listener, který kopíruje IČO na pozici DIČ
        checkBoxSameINasTIN.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkBoxSameINasTIN.isChecked()) {
                    String IN = "CZ" + inputIdentificationNumberEdit.getText().toString();
                    inputTaxIdentificationNumberEdit.setText(IN);
                } else {
                    inputTaxIdentificationNumberEdit.setText("");
                }

            }
        });
    }

    /**
     * Procedura, která inicializuje všechny potřebné prvky
     * aktivity
     */
    private void initActivity() {
        traderID = Integer.parseInt(getIntent().getExtras().get("TRADER_ID").toString());
        traderDatabaseHelper = new TraderDatabaseHelper(TraderEditActivity.this);
        trader = traderDatabaseHelper.getTraderById(traderID);
        inputCompanyNameEdit = findViewById(R.id.textInputEditTextCompanyNameEdit);
        inputContactPersonEdit = findViewById(R.id.textInputEditTextContactPersonEdit);
        inputTelephoneNumberEdit = findViewById(R.id.textInputEditTextTelephoneNumberEdit);
        inputIdentificationNumberEdit = findViewById(R.id.textInputEditTextIdentificationNumberEdit);
        inputTaxIdentificationNumberEdit = findViewById(R.id.textInputEditTextTaxIdentificationNumberEdit);
        inputCityEdit = findViewById(R.id.textInputEditTextCityEdit);
        inputStreetEdit = findViewById(R.id.textInputEditTextStreetEdit);
        inputHouseNumberEdit = findViewById(R.id.textInputEditTextHouseNumberEdit);
        inputLayoutCompanyNameEdit = findViewById(R.id.textInputLayoutCompanyNameEdit);
        inputLayoutTelephoneNumberEdit = findViewById(R.id.textInputLayoutTelephoneNumberEdit);
        inputLayoutIdentificationNumberEdit = findViewById(R.id.textInputLayoutIdentificationNumberEdit);
        inputLayoutTaxIdentificationNumberEdit = findViewById(R.id.textInputLayoutTaxIdentificationNumberEdit);
        checkBoxSameINasTIN = findViewById(R.id.checkBoxSameINasTINEdit);
    }

    /**
     * Procerdura, která nastaví text do všech políček
     * activity, které se načetli z databáze.
     */
    private void setTextToActivity() {
        inputCompanyNameEdit.setText(trader.getName());
        inputContactPersonEdit.setText(trader.getContactPerson());
        inputTelephoneNumberEdit.setText(trader.getPhoneNumber());
        inputIdentificationNumberEdit.setText(trader.getIN());
        inputTaxIdentificationNumberEdit.setText(trader.getTIN());
        inputCityEdit.setText(trader.getCity());
        inputStreetEdit.setText(trader.getStreet());
        inputHouseNumberEdit.setText(trader.getHouseNumber());
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
        Intent intent = new Intent(TraderEditActivity.this, TraderShowActivity.class);
        intent.putExtra("TRADER_ID", traderID);
        startActivity(intent);
        finish();
    }


    /**
     * Metoda, která provede editaci obchodníka. Načte vstupní pole,
     * následně je zvaliduje a změny uloží do databáze.
     *
     * @param view view příslušné aktivity
     */
    public void editInformationAboutTrader(View view) {

        if (!inputValidation()) {
            return;
        }

        trader = new Trader();
        trader.setId(traderID);
        trader.setName(inputCompanyNameEdit.getText().toString());
        trader.setContactPerson(inputContactPersonEdit.getText().toString());
        trader.setPhoneNumber(inputTelephoneNumberEdit.getText().toString());
        trader.setIN(inputIdentificationNumberEdit.getText().toString());
        trader.setTIN(inputTaxIdentificationNumberEdit.getText().toString());
        trader.setCity(inputCityEdit.getText().toString());
        trader.setStreet(inputStreetEdit.getText().toString());
        trader.setHouseNumber(inputHouseNumberEdit.getText().toString());
        trader.setIsDirty(1);
        trader.setIsDeleted(0);

        //update záznamu v databázi
        traderDatabaseHelper.updateTraderById(trader);

        //pokus o zálohu
        Push push = new Push(TraderEditActivity.this);
        push.push();

        String message = getString(R.string.trader_has_been_edited);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(TraderEditActivity.this, TraderShowActivity.class);
        intent.putExtra("TRADER_ID", traderID);
        startActivity(intent);
        finish();

    }

    /**
     * Metoda, která validuje vstupní hodnoty.
     *
     * @return logická hodnota, která označuje, zda validace proběhla v pořádku
     */
    private boolean inputValidation() {
        //načtení vstupních hodnot
        String tradeName = inputCompanyNameEdit.getText().toString();
        String contactPerson = inputContactPersonEdit.getText().toString();
        String phoneNumber = inputTelephoneNumberEdit.getText().toString();
        String identificationNumber = inputIdentificationNumberEdit.getText().toString();
        String taxIdentificationNumber = inputTaxIdentificationNumberEdit.getText().toString();
        String traderCity = inputCityEdit.getText().toString();
        String traderStreet = inputStreetEdit.getText().toString();
        String traderHouseNumber = inputHouseNumberEdit.getText().toString();

        //validace názvu obchodníka
        if (tradeName.isEmpty()) {
            String message = getString(R.string.company_name_is_empty);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            inputLayoutCompanyNameEdit.setError(message);
            return false;
        } else if (tradeName.length() > TextInputLength.TRADER_NAME_LENGTH) {
            String message = getString(R.string.input_is_too_long);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            inputLayoutCompanyNameEdit.setError(message);
            return false;
        }

        //validace kontaktní osoby
        if (contactPerson.length() > TextInputLength.TRADER_CONTACT_PERSON_LENGTH) {
            String message = getString(R.string.input_is_too_long);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            inputContactPersonEdit.setError(message);
            return false;
        }


        //validace telefoního čísla
        if (!InputValidation.validatePhoneNumber(phoneNumber)) {
            String message = getString(R.string.telephone_has_wrong_format);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            inputLayoutTelephoneNumberEdit.setError(message);
            return false;
        }

        //validace IČO
        if (!InputValidation.validateIdentificationNumber(identificationNumber)) {
            //implementace nastavení
            if (!Settings.getInstance().isIsForeignIdentificationNumberPossible()) {
                String message = getString(R.string.wrong_id_number);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                inputLayoutIdentificationNumberEdit.setError(message);
                return false;
            }
        }

        //validace českého dič
        if (!InputValidation.validateCzechTaxIdentificationNumber(taxIdentificationNumber)) {
            //implementace nastavení
            if (!Settings.getInstance().isIsForeignTaxIdentificationNumberPossible()) {
                String message = getString(R.string.wrong_format_of_tid);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                inputLayoutTaxIdentificationNumberEdit.setError(message);
                return false;
                //pokud je nastavena volba povolit zahraniční DIČ, tak je použita tato validace
            } else if (!InputValidation.validateForeignTaxIdentificationNumber(taxIdentificationNumber)) {
                String message = getString(R.string.wrong_format_of_tid);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                inputLayoutTaxIdentificationNumberEdit.setError(getString(R.string.wrong_format_of_tid));
                return false;
            }
        }

        if (!traderCity.isEmpty() && traderCity.length() > TextInputLength.TRADER_CITY_LENGTH) {
            String message = getString(R.string.input_is_too_long);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            inputCityEdit.setError(message);
            return false;
        }

        if (!traderStreet.isEmpty() && traderStreet.length() > TextInputLength.TRADER_STREET_LENGTH) {
            String message = getString(R.string.input_is_too_long);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            inputStreetEdit.setError(message);
            return false;
        }

        if (!traderHouseNumber.isEmpty() && traderHouseNumber.length() > TextInputLength.TRADER_HOUSE_NUMBER_LENGTH) {
            String message = getString(R.string.input_is_too_long);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            inputHouseNumberEdit.setError(message);
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

        TraderEditActivity thisActivity = TraderEditActivity.this;
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
