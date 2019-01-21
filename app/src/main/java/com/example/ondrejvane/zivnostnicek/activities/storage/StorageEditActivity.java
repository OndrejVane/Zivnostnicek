package com.example.ondrejvane.zivnostnicek.activities.storage;

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

public class StorageEditActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private int storageItemID;
    private StorageItemDatabaseHelper storageItemDatabaseHelper;
    private EditText inputStorageItemNameEdit;
    private EditText inputStorageItemQuantityEdit;
    private TextInputLayout layoutStorageItemNameEdit;
    private TextInputLayout layoutStorageItemQuantityEdit;
    private Spinner spinnerUnit;
    private EditText inputStorageItemNote;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_edit);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Header header = new Header( navigationView, this);
        header.setTextToHeader();

        initActivity();
    }

    private void initActivity() {
        storageItemID = Integer.parseInt(getIntent().getExtras().get("STORAGE_ITEM_ID").toString());
        storageItemDatabaseHelper = new StorageItemDatabaseHelper(StorageEditActivity.this);
        inputStorageItemNameEdit = findViewById(R.id.inputTextStorageItemNameEdit);
        layoutStorageItemNameEdit = findViewById(R.id.layoutStorageItemNameEdit);
        inputStorageItemQuantityEdit = findViewById(R.id.inputTextStorageItemQuantityEdit);
        layoutStorageItemQuantityEdit = findViewById(R.id.layoutStorageItemQuantityEdit);
        spinnerUnit = findViewById(R.id.spinnerUnitEdit);
        inputStorageItemNote = findViewById(R.id.textInputEditTextNote);

        //načtení příslušného záznamu z databáze
        StorageItem storageItem = storageItemDatabaseHelper.getStorageItemById(storageItemID);
        //načtení dat do kativity
        inputStorageItemNameEdit.setText(storageItem.getName());
        inputStorageItemQuantityEdit.setText(Float.toString(storageItem.getQuantity()));
        inputStorageItemNote.setText(storageItem.getNote());

    }

    /**
     * Metoda, která po stisknutí tlačítku uloží změny skladové
     * položky do databáze.
     * @param view
     */
    public void editStorageItem(View view) {

        String name = inputStorageItemNameEdit.getText().toString();
        String quantity = inputStorageItemQuantityEdit.getText().toString();
        String units = spinnerUnit.getSelectedItem().toString();
        String note = inputStorageItemNote.getText().toString();

        if(!InputValidation.validateIsEmpty(name)){
            String message = getString(R.string.item_name_is_empty);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            layoutStorageItemNameEdit.setError(message);
            return;
        }

        if(!InputValidation.validateIsEmpty(quantity)){
            String message = getString(R.string.quantity_is_empty);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            layoutStorageItemQuantityEdit.setError(message);
            return;
        }

        StorageItem storageItem = new StorageItem(UserInformation.getInstance().getUserId(), name, Float.parseFloat(quantity), units, note);
        storageItem.setId(storageItemID);
        storageItemDatabaseHelper.updateStorageItemById(storageItem);

        //výpis o úspěšném uložení skladové položky
        String message = getString(R.string.storage_item_has_been_edited);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        //přepnutí do předchozí aktivity
        Intent intent = new Intent(StorageEditActivity.this, StorageShowActivity.class);
        intent.putExtra("STORAGE_ITEM_ID", storageItemID);
        startActivity(intent);
        finish();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        Intent intent = new Intent(StorageEditActivity.this, StorageShowActivity.class);
        intent.putExtra("STORAGE_ITEM_ID", storageItemID);
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        StorageEditActivity thisActivity = StorageEditActivity.this;

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
