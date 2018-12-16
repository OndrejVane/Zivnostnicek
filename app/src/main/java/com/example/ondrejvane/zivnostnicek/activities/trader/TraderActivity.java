package com.example.ondrejvane.zivnostnicek.activities.trader;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.ondrejvane.zivnostnicek.activities.ExpenseActivity;
import com.example.ondrejvane.zivnostnicek.activities.HomeActivity;
import com.example.ondrejvane.zivnostnicek.activities.IncomeActivity;
import com.example.ondrejvane.zivnostnicek.activities.InfoActivity;
import com.example.ondrejvane.zivnostnicek.activities.StorageActivity;
import com.example.ondrejvane.zivnostnicek.activities.SynchronizationActivity;
import com.example.ondrejvane.zivnostnicek.database.TraderDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.helper.Header;
import com.example.ondrejvane.zivnostnicek.helper.ListViewAdapter;
import com.example.ondrejvane.zivnostnicek.helper.Logout;
import com.example.ondrejvane.zivnostnicek.helper.UserInformation;

/**
 * Aktivita, která zobrazí všechny obchodníky příslušného uživatele.
 *
 */
public class TraderActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView listViewTrader;
    private ListViewAdapter listViewAdapter;
    private EditText inputSearch;
    private TraderDatabaseHelper traderDatabaseHelper;
    private String[] traderName;
    private String[] traderContactPerson;
    private int[] ID;
    private int globalPosition;

    /**
     * Metoda, která se provede při spuštění akctivity a porovede nezbytné
     * úkony ke správnému fungování aktivity.
     * @param savedInstanceState    savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trader);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //stará se o přechod do nové aplikace po stisknutí tlačítk přidat
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

        initActivity();

        //skryje klávesnici při startu aktivity
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //po stisknutí objektu v listview překne do activity, která zobrazí info o obchodníkovi
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
                String tempTraderName[];
                String tempTraderContactPerson[];
                if (traderName.length == 0){
                    tempTraderName = new String [1];
                    tempTraderContactPerson = new String [1];
                    tempTraderName[0]=getString(R.string.no_result);
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

    /**
     * Procedura, která inicializuje všechny potřebné prvky
     * aktivity
     */
    private void initActivity() {
        String temp[][];
        UserInformation userInformation = UserInformation.getInstance();
        traderDatabaseHelper = new TraderDatabaseHelper(TraderActivity.this);
        temp = traderDatabaseHelper.getTradersData(userInformation.getUserId());
        ID = arrayStringToInteger(temp[0]);
        traderName = temp[1];
        traderContactPerson = temp[2];

        listViewTrader = findViewById(R.id.listViewTrader);
        listViewAdapter = new ListViewAdapter(this, traderName, traderContactPerson);
        inputSearch = findViewById(R.id.editTextSearch);
        listViewTrader.setAdapter(listViewAdapter);
    }

    /**
     * Metoda, která převede pole strngu(čísel) na pole integeru.
     * @param strings   String[]    pole stringu(čísel)
     * @return          int[]       pole integeru
     */
    private int[] arrayStringToInteger(String[] strings) {
        int[] integers = new int[strings.length];
        for (int i = 0; i<strings.length; i++){
            integers[i] = Integer.parseInt(strings[i]);
        }
        return integers;

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
        TraderActivity thisActivity = TraderActivity.this;

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
