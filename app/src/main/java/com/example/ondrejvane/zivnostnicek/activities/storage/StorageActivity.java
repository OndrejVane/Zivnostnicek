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
import android.widget.Toast;

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.activities.HomeActivity;
import com.example.ondrejvane.zivnostnicek.activities.SynchronizationActivity;
import com.example.ondrejvane.zivnostnicek.activities.expense.ExpenseActivity;
import com.example.ondrejvane.zivnostnicek.activities.income.IncomeActivity;
import com.example.ondrejvane.zivnostnicek.activities.info.InfoActivity;
import com.example.ondrejvane.zivnostnicek.activities.trader.TraderActivity;
import com.example.ondrejvane.zivnostnicek.activities.trader.TraderShowActivity;
import com.example.ondrejvane.zivnostnicek.database.StorageItemDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.helper.Header;
import com.example.ondrejvane.zivnostnicek.helper.ListViewStorageAdapter;
import com.example.ondrejvane.zivnostnicek.helper.ListViewTraderAdapter;
import com.example.ondrejvane.zivnostnicek.helper.Logout;
import com.example.ondrejvane.zivnostnicek.helper.UserInformation;
import com.example.ondrejvane.zivnostnicek.model.StorageItem;

import java.util.ArrayList;

import static android.widget.Toast.LENGTH_SHORT;

public class StorageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView listViewStorageItem;
    private ListViewStorageAdapter listViewStorageAdapter;
    private EditText inputSearch;
    private String[] storageItemName;
    private float[] storageItemQuantity;
    private String[] storageItemUnit;
    private int[] ID;
    private int globalPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StorageActivity.this, StorageNewActivity.class);
                startActivity(intent);
                finish();
            }
        });

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

        //skryje klávesnici při startu aktivity
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //po stisknutí objektu v listview překne do activity, která zobrazí info o skladové položce
        listViewStorageItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                globalPosition = position;
                Intent intent = new Intent(StorageActivity.this, StorageShowActivity.class);
                intent.putExtra("STORAGE_ITEM_ID", ID[globalPosition]);
                startActivity(intent);
                finish();

            }
        });

        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            /**
             * Při změně textu se vytvoří pole stringu, pomocí for cyklu se projede každý řádek a hledá se shoda.
             * Při shodě se zapíše obsah porovnávaného prvku v původním poli do nově vytvořeného pole.
             * Pokud dojde alespoň k jedné shodě, změní se obsah listView.
             * Pokud ke shodě nedojde, listView se vykreslí pouze s jedním zápisem a to nenalezeno nenalezeno
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
                if (storageItemName.length == 0){
                    tempStorageItemName = new String [1];
                    tempStorageItemQuantity = new float[1];
                    tempStorageItemUnit = new String[1];
                    tempStorageItemName[0]=getString(R.string.no_result);
                    tempStorageItemQuantity[0] = (float) 0.0;
                    tempStorageItemUnit[0] = "";
                    listViewStorageAdapter = new ListViewStorageAdapter(StorageActivity.this, tempStorageItemName, tempStorageItemQuantity, tempStorageItemUnit);
                    listViewStorageItem.setAdapter(listViewStorageAdapter);
                    return;

                }else {
                    tempStorageItemName = new String [storageItemName.length];
                    tempStorageItemQuantity = new float [storageItemQuantity.length];
                    tempStorageItemUnit = new String[storageItemUnit.length];
                }
                int tempI=0;
                boolean found = false;
                String findingString = inputSearch.getText().toString().toLowerCase();

                for (int i = 0; i<storageItemName.length;i++){
                    if (storageItemName[i].toLowerCase().contains(findingString)){
                        tempStorageItemName[tempI] = storageItemName[i];
                        tempStorageItemQuantity[tempI] = storageItemQuantity[i];
                        tempStorageItemUnit[tempI] = storageItemUnit[i];
                        tempI++;
                        found = true;
                    }
                }
                if (found){
                    listViewStorageAdapter = new ListViewStorageAdapter(StorageActivity.this,tempStorageItemName,tempStorageItemQuantity, tempStorageItemUnit);
                    listViewStorageItem.setAdapter(listViewStorageAdapter);
                }
                else {
                    tempStorageItemName[0]=getString(R.string.no_result);
                    tempStorageItemQuantity[0] = (float) 0.0;
                    tempStorageItemUnit[0] = "";
                    listViewStorageAdapter = new ListViewStorageAdapter(StorageActivity.this,tempStorageItemName,tempStorageItemQuantity, tempStorageItemUnit);
                    listViewStorageItem.setAdapter(listViewStorageAdapter);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initActivity() {
        ArrayList<StorageItem> listStorageItem;
        listViewStorageItem = findViewById(R.id.listViewStorage);
        StorageItemDatabaseHelper storageItemDatabaseHelper = new StorageItemDatabaseHelper(StorageActivity.this);
        listStorageItem = storageItemDatabaseHelper.getStorageItemByUserId(UserInformation.getInstance().getUserId());
        listViewStorageItem = findViewById(R.id.listViewStorage);
        inputSearch = findViewById(R.id.editTextSearchStorage);

        //inicializace polí pro uložení dat
        ID = new int[listStorageItem.size()];
        storageItemName = new String[listStorageItem.size()];
        storageItemQuantity = new float[listStorageItem.size()];
        storageItemUnit = new String[listStorageItem.size()];


        //projdu list a získám informace o položkách, které potřebuju
        for (int i=0; i<listStorageItem.size(); i++){
            ID[i] = listStorageItem.get(i).getId();
            storageItemName[i] = listStorageItem.get(i).getName();
            storageItemQuantity[i] = listStorageItem.get(i).getQuantity();
            storageItemUnit[i] = listStorageItem.get(i).getUnit();
        }

        //nastavení adapteru do list view pro zobrazení
        listViewStorageAdapter = new ListViewStorageAdapter(this, storageItemName, storageItemQuantity, storageItemUnit);
        listViewStorageItem.setAdapter(listViewStorageAdapter);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){

            case R.id.nav_home:
                Intent home = new Intent(StorageActivity.this, HomeActivity.class);
                startActivity(home);
                finish();
                break;

            case R.id.nav_income:
                Intent income = new Intent(StorageActivity.this, IncomeActivity.class);
                startActivity(income);
                finish();
                break;

            case R.id.nav_expense:
                Intent expense = new Intent(StorageActivity.this, ExpenseActivity.class);
                startActivity(expense);
                finish();
                break;

            case R.id.nav_traders:
                Intent traders = new Intent(StorageActivity.this, TraderActivity.class);
                startActivity(traders);
                finish();
                break;

            case R.id.nav_storage:
                Intent storage = new Intent(StorageActivity.this, StorageActivity.class);
                startActivity(storage);
                finish();
                break;

            case R.id.nav_info:
                Intent info = new Intent(StorageActivity.this, InfoActivity.class);
                startActivity(info);
                finish();
                break;

            case R.id.nav_sync:
                Intent sync = new Intent(StorageActivity.this, SynchronizationActivity.class);
                startActivity(sync);
                finish();
                break;

            case R.id.nav_logout:
                Logout logout = new Logout(StorageActivity.this, this);
                logout.logout();
                break;

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
