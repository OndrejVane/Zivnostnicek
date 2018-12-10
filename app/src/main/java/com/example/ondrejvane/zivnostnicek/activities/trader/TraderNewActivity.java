package com.example.ondrejvane.zivnostnicek.activities.trader;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.ondrejvane.zivnostnicek.activities.ExpenseActivity;
import com.example.ondrejvane.zivnostnicek.activities.HomeActivity;
import com.example.ondrejvane.zivnostnicek.activities.IncomeActivity;
import com.example.ondrejvane.zivnostnicek.activities.InfoActivity;
import com.example.ondrejvane.zivnostnicek.activities.StorageActivity;
import com.example.ondrejvane.zivnostnicek.activities.SynchronizationActivity;
import com.example.ondrejvane.zivnostnicek.database.DatabaseHelper;
import com.example.ondrejvane.zivnostnicek.helper.Header;
import com.example.ondrejvane.zivnostnicek.helper.Logout;
import com.example.ondrejvane.zivnostnicek.model.Trader;

public class TraderNewActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private EditText inputCompanyName, inputContactPerson, inputTelephoneNumber;
    private EditText inputIdentificationNumber, inputTaxIdentificationNumber;
    private EditText inputCity, inputStreet, inputHouseNumber;
    private TextInputLayout inputLayoutCompanyName, inputLayoutTelephoneNumber;
    private TextInputLayout inputLayoutIdentificationNumber, inputLayoutTaxIdentificationNumber;

    private DatabaseHelper databaseHelper;
    private Handler mHandler;

    private Trader trader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trader_new);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Header header = new Header( navigationView, this);
        header.setTextToHeader();

        initView();
    }

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
        databaseHelper = new DatabaseHelper(TraderNewActivity.this);
        mHandler = new Handler();

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        Intent income = new Intent(TraderNewActivity.this, TraderActivity.class);
        startActivity(income);
        finish();
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){

            case R.id.nav_home:
                Intent home = new Intent(TraderNewActivity.this, HomeActivity.class);
                startActivity(home);
                finish();
                break;

            case R.id.nav_income:
                Intent income = new Intent(TraderNewActivity.this, IncomeActivity.class);
                startActivity(income);
                finish();
                break;

            case R.id.nav_expense:
                Intent expense = new Intent(TraderNewActivity.this, ExpenseActivity.class);
                startActivity(expense);
                finish();
                break;

            case R.id.nav_traders:
                Intent traders = new Intent(TraderNewActivity.this, TraderActivity.class);
                startActivity(traders);
                finish();
                break;

            case R.id.nav_storage:
                Intent storage = new Intent(TraderNewActivity.this, StorageActivity.class);
                startActivity(storage);
                finish();
                break;

            case R.id.nav_info:
                Intent info = new Intent(TraderNewActivity.this, InfoActivity.class);
                startActivity(info);
                finish();
                break;

            case R.id.nav_sync:
                Intent sync = new Intent(TraderNewActivity.this, SynchronizationActivity.class);
                startActivity(sync);
                finish();
                break;

            case R.id.nav_logout:
                Logout logout = new Logout(TraderNewActivity.this, this);
                logout.logout();
                break;

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //TODO komentáře
    public void submitTraderForm(View view) {
        if(!validateCompanyName()){
            String message = getString(R.string.company_name_is_empty);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            inputLayoutCompanyName.setError(getString(R.string.company_name_is_empty));
            return;
        }

        if(!validatePhoneNumber()){
            String message = getString(R.string.telephone_has_wrong_format);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            inputLayoutTelephoneNumber.setError(getString(R.string.telephone_has_wrong_format));
            return;
        }

        if(!validateIdentificationNumber()){
            String message = getString(R.string.wrong_id_number);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            inputLayoutIdentificationNumber.setError(getString(R.string.wrong_id_number));
            return;
        }

        if(!validateTaxIdentificationNumber()){
            String message = getString(R.string.wrong_format_of_tid);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            inputLayoutTaxIdentificationNumber.setError(getString(R.string.wrong_format_of_tid));
            return;
        }

        trader = new Trader();
        trader.setTraderName(inputCompanyName.getText().toString());
        trader.setTraderContactPerson(inputContactPerson.getText().toString());
        trader.setTraderPhoneNumber(inputTelephoneNumber.getText().toString());
        trader.setTraderIN(inputIdentificationNumber.getText().toString());
        trader.setTraderTIN(inputTaxIdentificationNumber.getText().toString());
        trader.setTraderCity(inputCity.getText().toString());
        trader.setTraderStreet(inputStreet.getText().toString());
        trader.setTraderHouseNumber(inputHouseNumber.getText().toString());

        databaseHelper.addTrader(trader);

        String message = getString(R.string.trader_is_created);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Intent income = new Intent(TraderNewActivity.this, TraderActivity.class);
        startActivity(income);
        finish();

    }



    /**
     * Metoda, která validuje DIČ.
     * @return
     */
    private boolean validateTaxIdentificationNumber() {
        String taxIdentificationNumber = inputTaxIdentificationNumber.getText().toString();
        int inputLength = taxIdentificationNumber.length();
        if (taxIdentificationNumber.isEmpty()){
            return true;
        }else {
            if(inputLength >= 10 && inputLength <= 12){
                if(Character.isLetter(taxIdentificationNumber.charAt(0)) && Character.isLetter(taxIdentificationNumber.charAt(1))){
                    for (int i = 2; i<inputLength ; i++){
                        if(!Character.isDigit(taxIdentificationNumber.charAt(i))){
                            return false;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Metoda, která validuje IČO. Kontroluje, zda obsahuje první dva znaky a po té 8
     * číslic. Následně ověří validitu iča podel známeého algoritmu pro ověření iča.
     * Pokud je pole prázdné vrací true protože je to nepovinný údaj.
     * @return
     */
    private boolean validateIdentificationNumber() {
        String identificationNumber= inputIdentificationNumber.getText().toString();
        int temp = 0;
        int a, c;
        if(identificationNumber.isEmpty()){
            return true;
        }else {
            if(identificationNumber.length() == 10){
                if(Character.isLetter(identificationNumber.charAt(0)) && Character.isLetter(identificationNumber.charAt(1))){
                    for(int i = 2; i<identificationNumber.length()-1; i++){
                        if(Character.isDigit(identificationNumber.charAt(i))){
                            temp = temp + (Character.getNumericValue(identificationNumber.charAt(i)) * (10-i));
                        }else {
                            return false;
                        }
                    }
                    a = temp % 11;
                    if(a == 0){
                        c = 1;
                    }else if (a == 1){
                        c = 0;
                    }else {
                        c = 11 - temp;
                    }
                    if(Character.getNumericValue(identificationNumber.charAt(9)) == c){
                        return true;
                    }else {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Metoda, která validuje zadaný telefon od uživatele.
     * Telefonní čísloo je nepovinné. POkud je tedy prázdé, tak
     * metoda vrací true. Metoda akceptuje formát 123456789
     * a formát s předvolbou +420 123456789.
     * @return boolean
     */
    private boolean validatePhoneNumber() {
        String telephoneNumber = inputTelephoneNumber.getText().toString();
        if(telephoneNumber.isEmpty()){
            return true;
        }else {
            if(telephoneNumber.startsWith("+")){
                if(telephoneNumber.length() == 13){
                    return true;
                }else {
                    return false;
                }
            }
            if(Character.isDigit(telephoneNumber.charAt(0))){
                if(telephoneNumber.length() == 9){
                    return true;
                }else {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * Metoda, která validuje jediný povinný údaj ve formuláři.
     * Pokud je prázdný, metoda vrací false.
     * @return
     */
    private boolean validateCompanyName(){
        if(inputCompanyName.getText().toString().isEmpty()){
            return false;
        }
        return true;
    }

}
