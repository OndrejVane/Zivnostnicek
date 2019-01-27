package com.example.ondrejvane.zivnostnicek.activities.income;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.adapters.ListViewIncomeAdapter;
import com.example.ondrejvane.zivnostnicek.database.BillDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.helper.ArrayUtility;
import com.example.ondrejvane.zivnostnicek.helper.FormatUtility;
import com.example.ondrejvane.zivnostnicek.helper.Header;
import com.example.ondrejvane.zivnostnicek.helper.Logout;
import com.example.ondrejvane.zivnostnicek.helper.Settings;
import com.example.ondrejvane.zivnostnicek.helper.UserInformation;

/**
 * Aktivity pro zobrazení všech příjmu.
 */
public class IncomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Spinner spinnerIncomeYear;
    private Spinner spinnerIncomeMonth;
    private ListView listViewIncome;
    private ListViewIncomeAdapter listViewIncomeAdapter;

    //proměnné, které obsahují všechny příjmy načtené z databáze
    private String[] incomeName;
    private String[] incomeDate;
    private String[] incomeAmount;
    private int[] ID;
    private int[] holderId;

    //pomocné proměnné pro vyhledávání
    int[] tempIdYear;
    String[] tempIncomeNameYear;
    String[] tempIncomeDateYear;
    String[] tempIncomeAmountYear;


    /**
     * Metoda, která se provede při spuštění akctivity a provede nezbytné
     * úkony ke správnému fungování aktivity.
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IncomeActivity.this, IncomeNewActivity.class);
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

        listViewIncome.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int incomeId = ID[position];
                /*
                if(incomeId != -1){
                    Intent intent = new Intent(IncomeActivity.this, IncomeShowActivity.class);
                    intent.putExtra("BILL_ID", incomeId);
                    startActivity(intent);
                    finish();
                }
                */
            }
        });


        spinnerIncomeYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0){
                    String findingYear= spinnerIncomeYear.getSelectedItem().toString();
                    if(incomeName.length == 0){
                        tempIncomeNameYear = new String[1];
                        tempIncomeDateYear = new String[1];
                        tempIncomeAmountYear = new String[1];
                        tempIdYear = new int[1];
                        tempIncomeNameYear[0] = getString(R.string.no_result);
                        tempIncomeDateYear[0] = getString(R.string.no_result);
                        tempIncomeAmountYear[0] = "";
                        tempIdYear[0] = -1;
                        ID = tempIdYear;
                        listViewIncomeAdapter = new ListViewIncomeAdapter(IncomeActivity.this, tempIncomeNameYear, tempIncomeDateYear, tempIncomeAmountYear);
                        listViewIncome.setAdapter(listViewIncomeAdapter);
                        return;
                    }else {
                        int foundIncomes = 0;
                        for (int i = 0; i < incomeName.length; i++){
                            if(FormatUtility.getYearFromDate(incomeDate[i]).equals(findingYear)){
                                foundIncomes++;
                            }
                        }
                        tempIncomeNameYear = new String[foundIncomes];
                        tempIncomeDateYear = new String[foundIncomes];
                        tempIncomeAmountYear = new String[foundIncomes];
                        tempIdYear = new int[foundIncomes];
                    }

                    int tempI=0;
                    boolean found = false;

                    for (int i = 0; i < incomeName.length; i++){
                        if(FormatUtility.getYearFromDate(incomeDate[i]).equals(findingYear)){
                            tempIncomeNameYear[tempI] = incomeName[i];
                            tempIncomeDateYear[tempI] = incomeDate[i];
                            tempIncomeAmountYear[tempI] = incomeAmount[i];
                            tempIdYear[tempI] = holderId[i];
                            tempI++;
                            found = true;
                        }
                    }

                    if(found){
                        ID = holderId;
                        listViewIncomeAdapter = new ListViewIncomeAdapter(IncomeActivity.this, tempIncomeNameYear, tempIncomeDateYear, tempIncomeAmountYear);
                        listViewIncome.setAdapter(listViewIncomeAdapter);
                    }else {
                        tempIncomeNameYear = new String[1];
                        tempIncomeDateYear = new String[1];
                        tempIncomeAmountYear = new String[1];
                        tempIdYear = new int[1];
                        tempIncomeNameYear[0] = getString(R.string.no_result);
                        tempIncomeDateYear[0] = getString(R.string.no_result);
                        tempIncomeAmountYear[0] = "";
                        tempIdYear[0] = -1;
                        ID = tempIdYear;
                        listViewIncomeAdapter = new ListViewIncomeAdapter(IncomeActivity.this, tempIncomeNameYear, tempIncomeDateYear, tempIncomeAmountYear);
                        listViewIncome.setAdapter(listViewIncomeAdapter);
                    }
                }else {
                    tempIdYear = holderId;
                    tempIncomeNameYear = incomeName;
                    tempIncomeDateYear = incomeDate;
                    tempIncomeAmountYear = incomeAmount;
                    //nastavení dat do adapteru pro zobrazení
                    listViewIncomeAdapter = new ListViewIncomeAdapter(IncomeActivity.this, incomeName, incomeDate, incomeAmount);
                    listViewIncome.setAdapter(listViewIncomeAdapter);
                    ID = holderId;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerIncomeMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int[] tempId;
                String[] tempIncomeNameYearAndMonth;
                String[] tempIncomeDateYearAndMonth;
                String[] tempIncomeAmountYearAndMonth;
                if(position != 0) {
                    String findingMonth = Integer.toString(position);
                    String findingYear = spinnerIncomeYear.getSelectedItem().toString();
                    if(incomeName.length == 0){
                        tempIncomeNameYearAndMonth = new String[1];
                        tempIncomeDateYearAndMonth = new String[1];
                        tempIncomeAmountYearAndMonth = new String[1];
                        tempId = new int[1];
                        tempIncomeNameYearAndMonth[0] = getString(R.string.no_result);
                        tempIncomeDateYearAndMonth[0] = getString(R.string.no_result);
                        tempIncomeAmountYearAndMonth[0] = "";
                        tempId[0] = -1;
                        ID = tempId;
                        listViewIncomeAdapter = new ListViewIncomeAdapter(IncomeActivity.this, tempIncomeNameYearAndMonth, tempIncomeDateYearAndMonth, tempIncomeAmountYearAndMonth);
                        listViewIncome.setAdapter(listViewIncomeAdapter);
                        return;
                    }else {
                        int foundIncomes = 0;
                        for (int i = 0; i < incomeName.length; i++){
                            if(FormatUtility.getYearFromDate(incomeDate[i]).equals(findingYear) && FormatUtility.getMonthFromDate(incomeDate[i]).equals(findingMonth)){
                                foundIncomes++;
                            }
                        }
                        tempIncomeNameYearAndMonth = new String[foundIncomes];
                        tempIncomeDateYearAndMonth = new String[foundIncomes];
                        tempIncomeAmountYearAndMonth = new String[foundIncomes];
                        tempId = new int[foundIncomes];
                    }

                    int tempI=0;
                    boolean found = false;

                    for (int i = 0; i < incomeName.length; i++){
                        if(FormatUtility.getYearFromDate(incomeDate[i]).equals(findingYear) && FormatUtility.getMonthFromDate(incomeDate[i]).equals(findingMonth)){
                            tempIncomeNameYearAndMonth[tempI] = incomeName[i];
                            tempIncomeDateYearAndMonth[tempI] = incomeDate[i];
                            tempIncomeAmountYearAndMonth[tempI] = incomeAmount[i];
                            tempId[tempI] = holderId[i];
                            tempI++;
                            found = true;
                        }
                    }

                    if(found){
                        ID = holderId;
                        listViewIncomeAdapter = new ListViewIncomeAdapter(IncomeActivity.this, tempIncomeNameYearAndMonth, tempIncomeDateYearAndMonth, tempIncomeAmountYearAndMonth);
                        listViewIncome.setAdapter(listViewIncomeAdapter);
                    }else {
                        tempIncomeNameYearAndMonth = new String[1];
                        tempIncomeDateYearAndMonth = new String[1];
                        tempIncomeAmountYearAndMonth = new String[1];
                        tempId = new int[1];
                        tempIncomeNameYearAndMonth[0] = getString(R.string.no_result);
                        tempIncomeDateYearAndMonth[0] = getString(R.string.no_result);
                        tempIncomeAmountYearAndMonth[0] = "";
                        tempId[0] = -1;
                        ID = tempId;
                        listViewIncomeAdapter = new ListViewIncomeAdapter(IncomeActivity.this, tempIncomeNameYearAndMonth, tempIncomeDateYearAndMonth, tempIncomeAmountYearAndMonth);
                        listViewIncome.setAdapter(listViewIncomeAdapter);
                    }
                }else {
                    ID = tempIdYear;
                    listViewIncomeAdapter = new ListViewIncomeAdapter(IncomeActivity.this, tempIncomeNameYear, tempIncomeDateYear, tempIncomeAmountYear);
                    listViewIncome.setAdapter(listViewIncomeAdapter);
                }

                }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private void initActivity() {
        String[][] temp;
        spinnerIncomeYear = findViewById(R.id.spinnerIncomeYear);
        spinnerIncomeMonth = findViewById(R.id.spinnerIncomeMonth);
        listViewIncome = findViewById(R.id.listViewIncome);


        //pokud je v nastavení vybrán pouze jeden rok, tak zobrazím do spinneru a zablokuji ho
        if(Settings.getInstance().isIsPickedOneYear()){
            spinnerIncomeYear.setEnabled(false);
            spinnerIncomeYear.setSelection(Settings.getInstance().getArrayYearId());
        }

        //inicializace databáze
        BillDatabaseHelper billDatabaseHelper = new BillDatabaseHelper(IncomeActivity.this);
        //načtení příslušných dat z databáze
        temp = billDatabaseHelper.getBillData(UserInformation.getInstance().getUserId(), 0); // 0 = hledám pouze příjmy
        ID = ArrayUtility.arrayStringToInteger(temp[0]);
        holderId = ArrayUtility.arrayStringToInteger(temp[0]);
        incomeName = temp[1];
        incomeDate = temp[2];
        incomeAmount = temp[3];

        //nastavení dat do adapteru pro zobrazení
        listViewIncomeAdapter = new ListViewIncomeAdapter(this, incomeName, incomeDate, incomeAmount);
        listViewIncome.setAdapter(listViewIncomeAdapter);

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
     * Metoda, která se stará o hlavní navigační menu aplikace.
     * @param item  vybraná položka v menu
     * @return      boolean
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //id vybrané položky v menu
        int id = item.getItemId();

        IncomeActivity thisActivity = IncomeActivity.this;
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
