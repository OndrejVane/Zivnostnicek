package com.example.ondrejvane.zivnostnicek.activities.trader;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.activities.expense.ExpenseActivity;
import com.example.ondrejvane.zivnostnicek.activities.HomeActivity;
import com.example.ondrejvane.zivnostnicek.activities.income.IncomeActivity;
import com.example.ondrejvane.zivnostnicek.activities.info.InfoActivity;
import com.example.ondrejvane.zivnostnicek.activities.storage.StorageActivity;
import com.example.ondrejvane.zivnostnicek.activities.SynchronizationActivity;
import com.example.ondrejvane.zivnostnicek.database.TraderDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.helper.Header;
import com.example.ondrejvane.zivnostnicek.helper.InputValidation;
import com.example.ondrejvane.zivnostnicek.helper.Logout;
import com.example.ondrejvane.zivnostnicek.model.Trader;

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

    /**
     * Metoda, která se provede při spuštění akctivity a porovede nezbytné
     * úkony ke správnému fungování aktivity.
     * @param savedInstanceState
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

        Header header = new Header( navigationView, this);
        header.setTextToHeader();

        initActivity();

        setTextToActivity();
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
    }

    /**
     * Procerdura, která nastaví text do všech políček
     * activity, které se načetli z databáze.
     */
    private void setTextToActivity() {
        inputCompanyNameEdit.setText(trader.getTraderName());
        inputContactPersonEdit.setText(trader.getTraderContactPerson());
        inputTelephoneNumberEdit.setText(trader.getTraderPhoneNumber());
        inputIdentificationNumberEdit.setText(trader.getTraderIN());
        inputTaxIdentificationNumberEdit.setText(trader.getTraderTIN());
        inputCityEdit.setText(trader.getTraderCity());
        inputStreetEdit.setText(trader.getTraderStreet());
        inputHouseNumberEdit.setText(trader.getTraderHouseNumber());
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
     * @param view  view příslušné aktivity
     */
    public void editInformationAboutTrader(View view) {
        if(!InputValidation.validateCompanyName(inputCompanyNameEdit.getText().toString())){
            String message = getString(R.string.company_name_is_empty);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            inputLayoutCompanyNameEdit.setError(getString(R.string.company_name_is_empty));
            return;
        }

        if(!InputValidation.validatePhoneNumber(inputTelephoneNumberEdit.getText().toString())){
            String message = getString(R.string.telephone_has_wrong_format);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            inputLayoutTelephoneNumberEdit.setError(getString(R.string.telephone_has_wrong_format));
            return;
        }

        if(!InputValidation.validateIdentificationNumber(inputIdentificationNumberEdit.getText().toString())){
            String message = getString(R.string.wrong_id_number);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            inputLayoutIdentificationNumberEdit.setError(getString(R.string.wrong_id_number));
            return;
        }

        if(!InputValidation.validateTaxIdentificationNumber(inputTaxIdentificationNumberEdit.getText().toString())){
            String message = getString(R.string.wrong_format_of_tid);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            inputLayoutTaxIdentificationNumberEdit.setError(getString(R.string.wrong_format_of_tid));
            return;
        }

        trader = new Trader();
        trader.setId(traderID);
        trader.setTraderName(inputCompanyNameEdit.getText().toString());
        trader.setTraderContactPerson(inputContactPersonEdit.getText().toString());
        trader.setTraderPhoneNumber(inputTelephoneNumberEdit.getText().toString());
        trader.setTraderIN(inputIdentificationNumberEdit.getText().toString());
        trader.setTraderTIN(inputTaxIdentificationNumberEdit.getText().toString());
        trader.setTraderCity(inputCityEdit.getText().toString());
        trader.setTraderStreet(inputStreetEdit.getText().toString());
        trader.setTraderHouseNumber(inputHouseNumberEdit.getText().toString());

        traderDatabaseHelper.updateTraderById(trader);
        String message = getString(R.string.trader_is_created);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(TraderEditActivity.this, TraderShowActivity.class);
        intent.putExtra("TRADER_ID", traderID);
        startActivity(intent);
        finish();

    }

    /**
     * Metoda, která se stará o hlavní navigační menu aplikace
     * a přechod mezi hlavními aktivitami.
     * @param item  vybraná položka v menu
     * @return  boolean
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        TraderEditActivity thisActivity = TraderEditActivity.this;

        switch (id){

            case R.id.nav_home:
                Intent home = new Intent(thisActivity, HomeActivity.class);
                startActivity(home);
                finish();
                break;

            case R.id.nav_income:
                Intent income = new Intent(thisActivity, IncomeActivity.class);
                startActivity(income);
                finish();
                break;

            case R.id.nav_expense:
                Intent expense = new Intent(thisActivity, ExpenseActivity.class);
                startActivity(expense);
                finish();
                break;

            case R.id.nav_traders:
                Intent traders = new Intent(thisActivity, TraderActivity.class);
                startActivity(traders);
                finish();
                break;

            case R.id.nav_storage:
                Intent storage = new Intent(thisActivity, StorageActivity.class);
                startActivity(storage);
                finish();
                break;

            case R.id.nav_info:
                Intent info = new Intent(thisActivity, InfoActivity.class);
                startActivity(info);
                finish();
                break;

            case R.id.nav_sync:
                Intent sync = new Intent(thisActivity, SynchronizationActivity.class);
                startActivity(sync);
                finish();
                break;

            case R.id.nav_logout:
                Logout logout = new Logout(thisActivity, this);
                logout.logout();
                break;

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
