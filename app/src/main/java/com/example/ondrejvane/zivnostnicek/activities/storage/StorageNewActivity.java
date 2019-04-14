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
import com.example.ondrejvane.zivnostnicek.model.database.ItemQuantityDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.model.database.StorageItemDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.helper.Header;
import com.example.ondrejvane.zivnostnicek.helper.TextInputLength;
import com.example.ondrejvane.zivnostnicek.session.Logout;
import com.example.ondrejvane.zivnostnicek.session.UserInformation;
import com.example.ondrejvane.zivnostnicek.model.model_helpers.ItemQuantity;
import com.example.ondrejvane.zivnostnicek.model.model_helpers.StorageItem;
import com.example.ondrejvane.zivnostnicek.server.Push;

/**
 * Aktivity, která vytvoří novou skladovou položku
 * a uloží jí do databáze
 */
public class StorageNewActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //grafické prvky aktivity
    private EditText storageItemName;
    private EditText storageItemQuantity;
    private Spinner storageItemUnit;
    private EditText storageItemNote;
    private TextInputLayout layoutStorageItemName;
    private TextInputLayout layoutStorageItemQuantity;

    /**
     * Meotda, která je volána při spuštění aktivity a
     * inicializuje jí.
     *
     * @param savedInstanceState saved InstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_new);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //nastavení textu do headur
        Header header = new Header(navigationView);
        header.setTextToHeader();

        //inicializace aktivity
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

    /**
     * Metoda, která načte vstupní data od uživatele.
     * Zkontroluje validitu dat a po té uloží do databáze.
     * Po úspěšném uložení skladové položky do databáze přepne
     * do aktivity skladu.
     *
     * @param view view aktivity
     */
    public void submitStorageItemForm(View view) {
        String name, quantity, units, note;
        long storageItemId;

        //validace, zda jsou potřebné položky vyplněné
        if (!validateInputs()) {
            return;
        }

        //načtení položek
        name = storageItemName.getText().toString();
        quantity = storageItemQuantity.getText().toString();
        units = storageItemUnit.getSelectedItem().toString();
        note = storageItemNote.getText().toString();


        //inicializace databáze
        StorageItemDatabaseHelper storageItemDatabaseHelper = new StorageItemDatabaseHelper(StorageNewActivity.this);
        ItemQuantityDatabaseHelper itemQuantityDatabaseHelper = new ItemQuantityDatabaseHelper(StorageNewActivity.this);

        //přidání nové skladové položky do databáze
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

        //záloha dat
        Push push = new Push(this);
        push.push();

        //výpis o úspěšném uložení skladové položky
        String message = getString(R.string.storage_item_has_been_added);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        //Přepnutí aktivity do skladu
        Intent intent = new Intent(StorageNewActivity.this, StorageActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * validace vstupníhc polí při ukládání skladové položky.
     *
     * @return logická hodnota, která určuje zda je validace Ok
     */
    private boolean validateInputs() {
        String storageName = storageItemName.getText().toString();
        String storageQuantity = storageItemQuantity.getText().toString();
        String storageNote = storageItemNote.getText().toString();

        if (storageName.isEmpty()) {
            String message = getString(R.string.item_name_is_empty);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            layoutStorageItemName.setError(message);
            return false;
        } else if (storageName.length() > TextInputLength.STORAGE_ITEM_NAME_LENGTH) {
            String message = getString(R.string.input_is_too_long);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            layoutStorageItemName.setError(message);
            return false;
        }

        if (storageQuantity.isEmpty()) {
            String message = getString(R.string.quantity_is_empty);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            layoutStorageItemQuantity.setError(message);
            return false;
        }

        if (!storageNote.isEmpty() && storageNote.length() > TextInputLength.STORAGE_ITEM_NOTE_LENGTH) {
            String message = getString(R.string.input_is_too_long);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     * Metoda, která je volána při stisknutí tlačítka zpět.
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

        StorageNewActivity thisActivity = StorageNewActivity.this;
        Intent newIntent;

        //inicializace třídy menu, kde jsou definovány jednotlivé aktivity
        com.example.ondrejvane.zivnostnicek.menu.Menu menu = new com.example.ondrejvane.zivnostnicek.menu.Menu(thisActivity);
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
