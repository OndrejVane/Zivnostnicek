package com.example.ondrejvane.zivnostnicek.activities.storage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.activities.home.HomeActivity;
import com.example.ondrejvane.zivnostnicek.database.ModelHelpers.ItemQuantityDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.database.ModelHelpers.StorageItemDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.helper.Header;
import com.example.ondrejvane.zivnostnicek.adapters.ListViewStorageAdapter;
import com.example.ondrejvane.zivnostnicek.session.Logout;
import com.example.ondrejvane.zivnostnicek.session.UserInformation;
import com.example.ondrejvane.zivnostnicek.model.StorageItem;

import java.util.ArrayList;

/**
 * Aktivity, která zobrazuje skladové položky a jejich dostupné
 * množství v listu.
 */
public class StorageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //grafické prvky aktivity
    private ListViewStorageAdapter listViewStorageAdapter;
    private ListView listViewStorageItem;

    //pomocné globální proměnné
    private EditText inputSearch;
    private String[] storageItemName;
    private float[] storageItemQuantity;
    private String[] storageItemUnit;
    private int[] ID;
    private int[] holderId;

    /**
     * Meotoda, která je volána při vytvoření aktivity
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //nastavení akce pro floating button (spuštění aktivtity new storage item)
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StorageActivity.this, StorageNewActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //nastavení textu do headeru
        Header header = new Header(navigationView);
        header.setTextToHeader();

        //inicializace aktivity
        initActivity();

        //skryje klávesnici při startu aktivity
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //po stisknutí objektu v listview překne do activity, která zobrazí info o skladové položce
        listViewStorageItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int storageItemId = ID[position];
                if (storageItemId != -1) {
                    Intent intent = new Intent(StorageActivity.this, StorageShowActivity.class);
                    intent.putExtra("STORAGE_ITEM_ID", storageItemId);
                    startActivity(intent);
                }

            }
        });

        //listenr, který je volán vždy po změně textu  a vyhledává příslušné položky
        //které odpovídají tomuto prefixu
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            /**
             * Při změně textu se vytvoří pole stringu, pomocí for cyklu se projede každý řádek a hledá se shoda.
             * Při shodě se zapíše obsah porovnávaného prvku v původním poli do nově vytvořeného pole.
             * Pokud dojde alespoň k jedné shodě, změní se obsah listView.
             * Pokud ke shodě nedojde, listView se vykreslí pouze s jedním zápisem a to nenalezeno nenalezeno
             *
             * @param cs    CharSequence
             * @param arg1  int
             * @param arg2  int
             * @param arg3  int
             */
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                String tempStorageItemName[];
                float tempStorageItemQuantity[];
                String tempStorageItemUnit[];
                int tempId[];
                String findingString = inputSearch.getText().toString().toLowerCase();

                if (storageItemName.length == 0) {
                    tempStorageItemName = new String[1];
                    tempStorageItemQuantity = new float[1];
                    tempStorageItemUnit = new String[1];
                    tempId = new int[1];
                    tempStorageItemName[0] = getString(R.string.no_result);
                    tempStorageItemQuantity[0] = (float) 0.0;
                    tempStorageItemUnit[0] = "";
                    tempId[0] = -1;
                    ID = tempId;
                    listViewStorageAdapter = new ListViewStorageAdapter(StorageActivity.this, tempStorageItemName, tempStorageItemQuantity, tempStorageItemUnit);
                    listViewStorageItem.setAdapter(listViewStorageAdapter);
                    return;

                } else {
                    //zjištění počtu nalezených položek a inicializace polí
                    int foundStorageItems = 0;
                    for (int i = 0; i < storageItemName.length; i++) {
                        if (storageItemName[i].toLowerCase().contains(findingString)) {
                            foundStorageItems++;
                        }
                    }
                    tempStorageItemName = new String[foundStorageItems];
                    tempStorageItemQuantity = new float[foundStorageItems];
                    tempStorageItemUnit = new String[foundStorageItems];
                    tempId = new int[foundStorageItems];
                }

                int tempI = 0;
                boolean found = false;


                for (int i = 0; i < storageItemName.length; i++) {
                    if (storageItemName[i].toLowerCase().contains(findingString)) {
                        tempStorageItemName[tempI] = storageItemName[i];
                        tempStorageItemQuantity[tempI] = storageItemQuantity[i];
                        tempStorageItemUnit[tempI] = storageItemUnit[i];
                        tempId[tempI] = holderId[i];
                        tempI++;
                        found = true;
                    }
                }
                if (found) {
                    listViewStorageAdapter = new ListViewStorageAdapter(StorageActivity.this, tempStorageItemName, tempStorageItemQuantity, tempStorageItemUnit);
                    listViewStorageItem.setAdapter(listViewStorageAdapter);
                    ID = tempId;
                } else {
                    tempStorageItemName = new String[1];
                    tempStorageItemQuantity = new float[1];
                    tempStorageItemUnit = new String[1];
                    tempId = new int[1];
                    tempStorageItemName[0] = getString(R.string.no_result);
                    tempStorageItemQuantity[0] = (float) 0.0;
                    tempStorageItemUnit[0] = "";
                    tempId[0] = -1;
                    ID = tempId;
                    listViewStorageAdapter = new ListViewStorageAdapter(StorageActivity.this, tempStorageItemName, tempStorageItemQuantity, tempStorageItemUnit);
                    listViewStorageItem.setAdapter(listViewStorageAdapter);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * Inicializace prvků v aktivitě
     */
    private void initActivity() {
        ArrayList<StorageItem> listStorageItem;
        listViewStorageItem = findViewById(R.id.listViewStorage);
        StorageItemDatabaseHelper storageItemDatabaseHelper = new StorageItemDatabaseHelper(StorageActivity.this);
        ItemQuantityDatabaseHelper itemQuantityDatabaseHelper = new ItemQuantityDatabaseHelper(StorageActivity.this);
        listStorageItem = storageItemDatabaseHelper.getStorageItemByUserId(UserInformation.getInstance().getUserId());
        listViewStorageItem = findViewById(R.id.listViewStorage);
        inputSearch = findViewById(R.id.editTextSearchStorage);

        //inicializace polí pro uložení dat
        ID = new int[listStorageItem.size()];
        holderId = new int[listStorageItem.size()];
        storageItemName = new String[listStorageItem.size()];
        storageItemQuantity = new float[listStorageItem.size()];
        storageItemUnit = new String[listStorageItem.size()];


        //projdu list a získám informace o položkách, které potřebuju
        for (int i = 0; i < listStorageItem.size(); i++) {
            ID[i] = listStorageItem.get(i).getId();
            holderId[i] = listStorageItem.get(i).getId();
            storageItemName[i] = listStorageItem.get(i).getName();
            storageItemQuantity[i] = itemQuantityDatabaseHelper.getQuantityWithStorageItemId(listStorageItem.get(i).getId());
            storageItemUnit[i] = listStorageItem.get(i).getUnit();
        }

        //nastavení adapteru do list view pro zobrazení
        listViewStorageAdapter = new ListViewStorageAdapter(this, storageItemName, storageItemQuantity, storageItemUnit);
        listViewStorageItem.setAdapter(listViewStorageAdapter);


    }

    /**
     * Metoda, která je volána po stisknutí tlačítka zpět.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent homeActivity = new Intent(this, HomeActivity.class);
            startActivity(homeActivity);
        }
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

        StorageActivity thisActivity = StorageActivity.this;
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
