package com.example.ondrejvane.zivnostnicek.activities.trader;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.ondrejvane.zivnostnicek.helper.Logout;
import com.example.ondrejvane.zivnostnicek.helper.Settings;
import com.example.ondrejvane.zivnostnicek.menu.Menu;
import com.example.ondrejvane.zivnostnicek.model.Trader;

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
     * @param savedInstanceState    savedInstanceState
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

        Header header = new Header( navigationView, this);
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
        Intent income = new Intent(TraderNewActivity.this, TraderActivity.class);
        startActivity(income);
        finish();
    }


    /**
     * Metoda, která načte všechny vstupní pole. Zvaliduje data
     * vložená uživatelem a vloží záznam obchodníka do databáze.
     * @param view  view příslušné aktivity
     */
    public void submitTraderForm(View view) {
        if(!InputValidation.validateCompanyName(inputCompanyName.getText().toString())){
            String message = getString(R.string.company_name_is_empty);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            inputLayoutCompanyName.setError(getString(R.string.company_name_is_empty));
            return;
        }

        if(!InputValidation.validatePhoneNumber(inputTelephoneNumber.getText().toString())){
            String message = getString(R.string.telephone_has_wrong_format);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            inputLayoutTelephoneNumber.setError(getString(R.string.telephone_has_wrong_format));
            return;
        }

        if(!InputValidation.validateIdentificationNumber(inputIdentificationNumber.getText().toString())){
            //implementace nastavení
            if(!Settings.getInstance().isIsForeignIdentificationNumberPossible()){
                String message = getString(R.string.wrong_id_number);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                inputLayoutIdentificationNumber.setError(getString(R.string.wrong_id_number));
                return;
            }

        }

        if(!InputValidation.validateTaxIdentificationNumber(inputTaxIdentificationNumber.getText().toString())){
            //implementace nastavení
            if(!Settings.getInstance().isIsForeignTaxIdentificationNumberPossible()){
                String message = getString(R.string.wrong_format_of_tid);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                inputLayoutTaxIdentificationNumber.setError(getString(R.string.wrong_format_of_tid));
                return;
            }
        }

        Trader trader = new Trader();
        trader.setTraderName(inputCompanyName.getText().toString());
        trader.setTraderContactPerson(inputContactPerson.getText().toString());
        trader.setTraderPhoneNumber(inputTelephoneNumber.getText().toString());
        trader.setTraderIN(inputIdentificationNumber.getText().toString());
        trader.setTraderTIN(inputTaxIdentificationNumber.getText().toString());
        trader.setTraderCity(inputCity.getText().toString());
        trader.setTraderStreet(inputStreet.getText().toString());
        trader.setTraderHouseNumber(inputHouseNumber.getText().toString());

        traderDatabaseHelper.addTrader(trader);

        String message = getString(R.string.trader_is_created);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Intent income = new Intent(TraderNewActivity.this, TraderActivity.class);
        startActivity(income);
        finish();

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

        TraderNewActivity thisActivity = TraderNewActivity.this;
        Intent newIntent;

        //inicializace třídy menu, kde jsou definovány jednotlivé aktivity
        Menu menu = new Menu(thisActivity);
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
