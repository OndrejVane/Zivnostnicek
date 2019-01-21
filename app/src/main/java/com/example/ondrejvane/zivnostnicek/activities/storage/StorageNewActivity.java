package com.example.ondrejvane.zivnostnicek.activities.storage;

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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.activities.HomeActivity;
import com.example.ondrejvane.zivnostnicek.activities.SynchronizationActivity;
import com.example.ondrejvane.zivnostnicek.activities.expense.ExpenseActivity;
import com.example.ondrejvane.zivnostnicek.activities.income.IncomeActivity;
import com.example.ondrejvane.zivnostnicek.activities.info.InfoActivity;
import com.example.ondrejvane.zivnostnicek.activities.trader.TraderActivity;
import com.example.ondrejvane.zivnostnicek.database.StorageItemDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.helper.Header;
import com.example.ondrejvane.zivnostnicek.helper.InputValidation;
import com.example.ondrejvane.zivnostnicek.helper.Logout;
import com.example.ondrejvane.zivnostnicek.helper.UserInformation;
import com.example.ondrejvane.zivnostnicek.model.StorageItem;

/**
 * Aktivity, která vytvoří novou skladovou položku
 * a uloží jí do databáze
 */
public class StorageNewActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private EditText storageItemName;
    private EditText storageItemQuantity;
    private Spinner storageItemUnit;
    private EditText storageItemNote;
    private TextInputLayout layoutStorageItemName;
    private TextInputLayout layoutStorageItemQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_new);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Header header = new Header( navigationView, this);
        header.setTextToHeader();

        initActivity();
    }

    /**
     * Metoda, která inicializuje potřebné prvky
     * v této aktivitě.
     */
    private void initActivity() {
        storageItemName = findViewById(R.id.inputTextStorageItemName);
        layoutStorageItemName = findViewById(R.id.layoutStorageItemName);
        storageItemQuantity = findViewById(R.id.inputTextStorageItemQuantity);
        layoutStorageItemQuantity = findViewById(R.id.layoutStorageItemQuantity);
        storageItemUnit = findViewById(R.id.spinnerUnit);
        storageItemNote = findViewById(R.id.textInputEditTextNote);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        Intent home = new Intent(StorageNewActivity.this, StorageActivity.class);
        startActivity(home);
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        StorageNewActivity thisActivity = StorageNewActivity.this;

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

    /**
     * Metoda, která načte vstupní data od uživatele.
     * Zkontroluje validitu dat a po té uloží do databáze.
     * Po úspěšném uložení skladové položky do databáze přepne
     * do aktivity skladu.
     * @param view
     */
    public void submitStorageItemForm(View view) {
        String name, quantity, units, note;
        name = storageItemName.getText().toString();
        quantity = storageItemQuantity.getText().toString();
        units = storageItemUnit.getSelectedItem().toString();
        note = storageItemNote.getText().toString();

        if(!InputValidation.validateIsEmpty(name)){
            String message = getString(R.string.item_name_is_empty);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            layoutStorageItemName.setError(message);
            return;
        }

        if (!InputValidation.validateIsEmpty(quantity)){
            String message = getString(R.string.quantity_is_empty);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            layoutStorageItemQuantity.setError(message);
            return;
        }

        //přidání nové skladové položky do databáze
        StorageItem storageItem = new StorageItem(UserInformation.getInstance().getUserId(), name, Float.parseFloat(quantity), units, note);
        StorageItemDatabaseHelper storageItemDatabaseHelper = new StorageItemDatabaseHelper(StorageNewActivity.this);
        storageItemDatabaseHelper.addStorageItem(storageItem);

        //výpis o úspěšném uložení skladové položky
        String message = getString(R.string.storage_item_has_been_added);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        //Přepnutí aktivity do skladu
        Intent intent = new Intent(StorageNewActivity.this, StorageActivity.class);
        startActivity(intent);
        finish();
    }
}
