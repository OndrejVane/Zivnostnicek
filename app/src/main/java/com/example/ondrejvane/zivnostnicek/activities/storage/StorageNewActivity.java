package com.example.ondrejvane.zivnostnicek.activities.storage;

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
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.database.ItemQuantityDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.database.StorageItemDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.helper.Header;
import com.example.ondrejvane.zivnostnicek.helper.InputValidation;
import com.example.ondrejvane.zivnostnicek.helper.Logout;
import com.example.ondrejvane.zivnostnicek.helper.UserInformation;
import com.example.ondrejvane.zivnostnicek.model.ItemQuantity;
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

        Header header = new Header( navigationView);
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
     * Metoda, která načte vstupní data od uživatele.
     * Zkontroluje validitu dat a po té uloží do databáze.
     * Po úspěšném uložení skladové položky do databáze přepne
     * do aktivity skladu.
     * @param view
     */
    public void submitStorageItemForm(View view) {
        String name, quantity, units, note;
        long storageItemId;
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

        //inicializace databáze
        StorageItemDatabaseHelper storageItemDatabaseHelper = new StorageItemDatabaseHelper(StorageNewActivity.this);
        ItemQuantityDatabaseHelper itemQuantityDatabaseHelper = new ItemQuantityDatabaseHelper(StorageNewActivity.this);

        //přidání nové skladové položky do databáze
        //StorageItem storageItem = new StorageItem(UserInformation.getInstance().getUserId(), name, Float.parseFloat(quantity), units, note);
        StorageItem storageItem = new StorageItem();
        storageItem.setUserId(UserInformation.getInstance().getUserId());
        storageItem.setName(name);
        storageItem.setUnit(units);
        storageItem.setNote(note);
        storageItem.setIsDirty(1);
        storageItem.setIsDeleted(0);
        storageItemId = storageItemDatabaseHelper.addStorageItem(storageItem, false);

        //přidání záznamu do tabulky množství položky
        ItemQuantity itemQuantity = new ItemQuantity();
        itemQuantity.setStorageItemId(storageItemId);
        itemQuantity.setQuantity(Float.parseFloat(quantity));
        itemQuantity.setBillId(-1);                                 //tato skladové položka nepatří k žádné faktuře
        itemQuantity.setIsDirty(1);
        itemQuantity.setIsDeleted(0);
        itemQuantityDatabaseHelper.addItemQuantity(itemQuantity, false);

        //výpis o úspěšném uložení skladové položky
        String message = getString(R.string.storage_item_has_been_added);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        //Přepnutí aktivity do skladu
        Intent intent = new Intent(StorageNewActivity.this, StorageActivity.class);
        startActivity(intent);
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

        StorageNewActivity thisActivity = StorageNewActivity.this;
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
