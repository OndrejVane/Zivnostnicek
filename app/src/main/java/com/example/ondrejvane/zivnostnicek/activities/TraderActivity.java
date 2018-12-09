package com.example.ondrejvane.zivnostnicek.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.database.DatabaseHelper;
import com.example.ondrejvane.zivnostnicek.helper.Header;
import com.example.ondrejvane.zivnostnicek.helper.ListViewAdapter;
import com.example.ondrejvane.zivnostnicek.helper.Logout;
import com.example.ondrejvane.zivnostnicek.helper.UserInformation;

public class TraderActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView listViewTrader;
    private ListViewAdapter listViewAdapter;
    private EditText inputSearch;
    private DatabaseHelper databaseHelper;
    String[] traderName;
    String[] traderContactPerson;

    int[] ID;
    int globalPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trader);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent income = new Intent(TraderActivity.this, TraderNewActivity.class);
                startActivity(income);
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

        initView();

        listViewTrader.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                globalPosition = position;
                Intent intent = new Intent(TraderActivity.this, TraderShowActivity.class);
                intent.putExtra("TRADER_ID", ID[globalPosition]);
                startActivity(intent);
                finish();
            }
        });

        /**
         * Ojektu editText přidáme addTextChangedListener
         */
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            /**
             * Při změně textu se vytvoří pole stringu, pomocí for cyklu se projede každý řádek a hledá se shoda.
             * Při shodě se zapíše obsah porovnávaného prvku v původním poli do nově vytvořeného pole.
             * Pokud dojde alespoň k jedné shodě, změní se obsah listView.
             * Pokud ke shodě nedojde, listView se vykreslí pouze s jedním zápisem a to nenalezeno nenalezeno
             * @param cs
             * @param arg1
             * @param arg2
             * @param arg3
             */
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                String tempTraderName[];
                String tempTraderContactPerson[];
                if (traderName.length == 0){
                    tempTraderName = new String [1];
                    tempTraderContactPerson = new String [1];
                    tempTraderName[0]=getString(R.string.no_result);;
                    tempTraderContactPerson[0]=getString(R.string.no_result);
                    listViewTrader = findViewById(R.id.listViewTrader);
                    listViewAdapter = new ListViewAdapter(TraderActivity.this,tempTraderName,tempTraderContactPerson);
                    listViewTrader.setAdapter(listViewAdapter);
                    return;

                }else {
                    tempTraderName = new String [traderName.length];
                    tempTraderContactPerson = new String [traderContactPerson.length];
                }
                int tempI=0;
                boolean found = false;
                String findingString = inputSearch.getText().toString().toLowerCase();

                for (int i = 0; i<traderName.length;i++){
                    if (traderName[i].toLowerCase().contains(findingString) ||  traderContactPerson[i].toLowerCase().contains(findingString)){
                        tempTraderName[tempI] = traderName[i];
                        tempTraderContactPerson[tempI] = traderContactPerson[i];
                        tempI++;
                        found = true;
                    }
                }
                if (found){
                    listViewTrader = findViewById(R.id.listViewTrader);
                    listViewAdapter = new ListViewAdapter(TraderActivity.this,tempTraderName,tempTraderContactPerson);
                    listViewTrader.setAdapter(listViewAdapter);
                }
                else {
                    tempTraderName[0]=getString(R.string.no_result);
                    tempTraderContactPerson[0] = getString(R.string.no_result);
                    listViewTrader = findViewById(R.id.listViewTrader);
                    listViewAdapter = new ListViewAdapter(TraderActivity.this,tempTraderName,tempTraderContactPerson);
                    listViewTrader.setAdapter(listViewAdapter);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initView() {
        String temp[][];
        UserInformation userInformation = UserInformation.getInstance();
        databaseHelper = new DatabaseHelper(TraderActivity.this);
        temp = databaseHelper.getTradersData(userInformation.getUserId());
        ID = arrayStringToInteger(temp[0]);
        traderName = temp[1];
        traderContactPerson = temp[2];

        listViewTrader = findViewById(R.id.listViewTrader);
        listViewAdapter = new ListViewAdapter(this, traderName, traderContactPerson);
        inputSearch = findViewById(R.id.editTextSearch);
        listViewTrader.setAdapter(listViewAdapter);
    }

    private int[] arrayStringToInteger(String[] strings) {
        int[] integers = new int[strings.length];
        for (int i = 0; i<strings.length; i++){
            integers[i] = Integer.parseInt(strings[i]);
        }
        return integers;

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){

            case R.id.nav_home:
                Intent home = new Intent(TraderActivity.this, HomeActivity.class);
                startActivity(home);
                finish();
                break;

            case R.id.nav_income:
                Intent income = new Intent(TraderActivity.this, IncomeActivity.class);
                startActivity(income);
                finish();
                break;

            case R.id.nav_expense:
                Intent expense = new Intent(TraderActivity.this, ExpenseActivity.class);
                startActivity(expense);
                finish();
                break;

            case R.id.nav_traders:
                Intent traders = new Intent(TraderActivity.this, TraderActivity.class);
                startActivity(traders);
                finish();
                break;

            case R.id.nav_storage:
                Intent storage = new Intent(TraderActivity.this, StorageActivity.class);
                startActivity(storage);
                finish();
                break;

            case R.id.nav_info:
                Intent info = new Intent(TraderActivity.this, InfoActivity.class);
                startActivity(info);
                finish();
                break;

            case R.id.nav_sync:
                Intent sync = new Intent(TraderActivity.this, SynchronizationActivity.class);
                startActivity(sync);
                finish();
                break;

            case R.id.nav_logout:
                Logout logout = new Logout(TraderActivity.this, this);
                logout.logout();
                break;

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
