package com.example.ondrejvane.zivnostnicek.activities.bill;

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
import com.example.ondrejvane.zivnostnicek.adapters.ListViewBillAdapter;
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
public class BillActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private boolean isExpense;      //proměnná, která udáváa, zda jsou zobrazovány příjmy neb výdaje

    private Spinner spinnerBillYear;
    private Spinner spinnerBillMonth;
    private ListView listViewBill;
    private ListViewBillAdapter listViewBillAdapter;

    //proměnné, které obsahují všechny příjmy načtené z databáze
    private String[] billName;
    private String[] billDate;
    private String[] billAmount;
    private int[] ID;
    private int[] holderId;

    //pomocné proměnné pro vyhledávání
    int[] tempIdYear;
    String[] tempBillNameYear;
    String[] tempBillDateYear;
    String[] tempBillAmountYear;


    /**
     * Metoda, která se provede při spuštění akctivity a provede nezbytné
     * úkony ke správnému fungování aktivity.
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BillActivity.this, BillNewActivity.class);
                intent.putExtra("IS_EXPENSE", isExpense);
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

        //click listener, který spustí aktivitu pro náhled faktury po kliknutí na položku v listu
        listViewBill.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int incomeId = ID[position];
                if(incomeId != -1){
                    Intent intent = new Intent(BillActivity.this, BillShowActivity.class);
                    intent.putExtra("BILL_ID", incomeId);
                    intent.putExtra("IS_EXPENSE", isExpense);
                    startActivity(intent);
                    finish();
                }
            }
        });


        spinnerBillYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0){
                    String findingYear= spinnerBillYear.getSelectedItem().toString();
                    if(billName.length == 0){
                        tempBillNameYear = new String[1];
                        tempBillDateYear = new String[1];
                        tempBillAmountYear = new String[1];
                        tempIdYear = new int[1];
                        tempBillNameYear[0] = getString(R.string.no_result);
                        tempBillDateYear[0] = getString(R.string.no_result);
                        tempBillAmountYear[0] = "";
                        tempIdYear[0] = -1;
                        ID = tempIdYear;
                        setAdapterToList(tempBillNameYear, tempBillDateYear, tempBillAmountYear);
                        return;
                    }else {
                        int foundIncomes = 0;
                        for (int i = 0; i < billName.length; i++){
                            if(FormatUtility.getYearFromDate(billDate[i]).equals(findingYear)){
                                foundIncomes++;
                            }
                        }
                        tempBillNameYear = new String[foundIncomes];
                        tempBillDateYear = new String[foundIncomes];
                        tempBillAmountYear = new String[foundIncomes];
                        tempIdYear = new int[foundIncomes];
                    }

                    int tempI=0;
                    boolean found = false;

                    for (int i = 0; i < billName.length; i++){
                        if(FormatUtility.getYearFromDate(billDate[i]).equals(findingYear)){
                            tempBillNameYear[tempI] = billName[i];
                            tempBillDateYear[tempI] = billDate[i];
                            tempBillAmountYear[tempI] = billAmount[i];
                            tempIdYear[tempI] = holderId[i];
                            tempI++;
                            found = true;
                        }
                    }

                    if(found){
                        ID = holderId;
                        setAdapterToList(tempBillNameYear, tempBillDateYear, tempBillAmountYear);
                    }else {
                        tempBillNameYear = new String[1];
                        tempBillDateYear = new String[1];
                        tempBillAmountYear = new String[1];
                        tempIdYear = new int[1];
                        tempBillNameYear[0] = getString(R.string.no_result);
                        tempBillDateYear[0] = getString(R.string.no_result);
                        tempBillAmountYear[0] = "";
                        tempIdYear[0] = -1;
                        ID = tempIdYear;
                        setAdapterToList(tempBillNameYear, tempBillDateYear, tempBillAmountYear);
                    }
                }else {
                    tempIdYear = holderId;
                    tempBillNameYear = billName;
                    tempBillDateYear = billDate;
                    tempBillAmountYear = billAmount;
                    //nastavení dat do adapteru pro zobrazení
                    setAdapterToList(billName, billDate, billAmount);
                    ID = holderId;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerBillMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int[] tempId;
                String[] tempBillNameYearAndMonth;
                String[] tempBillDateYearAndMonth;
                String[] tempVillAmountYearAndMonth;
                if(position != 0) {
                    String findingMonth = Integer.toString(position);
                    String findingYear = spinnerBillYear.getSelectedItem().toString();
                    if(billName.length == 0){
                        tempBillNameYearAndMonth = new String[1];
                        tempBillDateYearAndMonth = new String[1];
                        tempVillAmountYearAndMonth = new String[1];
                        tempId = new int[1];
                        tempBillNameYearAndMonth[0] = getString(R.string.no_result);
                        tempBillDateYearAndMonth[0] = getString(R.string.no_result);
                        tempVillAmountYearAndMonth[0] = "";
                        tempId[0] = -1;
                        ID = tempId;
                        setAdapterToList(tempBillNameYearAndMonth, tempBillDateYearAndMonth, tempVillAmountYearAndMonth);
                        return;
                    }else {
                        int foundIncomes = 0;
                        for (int i = 0; i < billName.length; i++){
                            if(FormatUtility.getYearFromDate(billDate[i]).equals(findingYear) && FormatUtility.getMonthFromDate(billDate[i]).equals(findingMonth)){
                                foundIncomes++;
                            }
                        }
                        tempBillNameYearAndMonth = new String[foundIncomes];
                        tempBillDateYearAndMonth = new String[foundIncomes];
                        tempVillAmountYearAndMonth = new String[foundIncomes];
                        tempId = new int[foundIncomes];
                    }

                    int tempI=0;
                    boolean found = false;

                    for (int i = 0; i < billName.length; i++){
                        if(FormatUtility.getYearFromDate(billDate[i]).equals(findingYear) && FormatUtility.getMonthFromDate(billDate[i]).equals(findingMonth)){
                            tempBillNameYearAndMonth[tempI] = billName[i];
                            tempBillDateYearAndMonth[tempI] = billDate[i];
                            tempVillAmountYearAndMonth[tempI] = billAmount[i];
                            tempId[tempI] = holderId[i];
                            tempI++;
                            found = true;
                        }
                    }

                    if(found){
                        ID = holderId;
                        setAdapterToList(tempBillNameYearAndMonth, tempBillDateYearAndMonth, tempVillAmountYearAndMonth);
                    }else {
                        tempBillNameYearAndMonth = new String[1];
                        tempBillDateYearAndMonth = new String[1];
                        tempVillAmountYearAndMonth = new String[1];
                        tempId = new int[1];
                        tempBillNameYearAndMonth[0] = getString(R.string.no_result);
                        tempBillDateYearAndMonth[0] = getString(R.string.no_result);
                        tempVillAmountYearAndMonth[0] = "";
                        tempId[0] = -1;
                        ID = tempId;
                        setAdapterToList(tempBillNameYearAndMonth, tempBillDateYearAndMonth, tempVillAmountYearAndMonth);
                    }
                }else {
                    ID = tempIdYear;
                    setAdapterToList(tempBillNameYear, tempBillDateYear, tempBillAmountYear);
                }

                }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private void initActivity() {

        isExpense = getIntent().getExtras().getBoolean("IS_EXPENSE", false);
        //pokud se jedná o výdaj, tak nastavím title
        setTitle();

        String[][] temp;
        spinnerBillYear = findViewById(R.id.spinnerIncomeYear);
        spinnerBillMonth = findViewById(R.id.spinnerIncomeMonth);
        listViewBill = findViewById(R.id.listViewIncome);


        //pokud je v nastavení vybrán pouze jeden rok, tak zobrazím do spinneru a zablokuji ho
        if(Settings.getInstance().isIsPickedOneYear()){
            spinnerBillYear.setEnabled(false);
            spinnerBillYear.setSelection(Settings.getInstance().getArrayYearId());
        }

        //inicializace databáze
        BillDatabaseHelper billDatabaseHelper = new BillDatabaseHelper(BillActivity.this);
        //načtení příslušných faktur z databáze
        if(isExpense){
            temp = billDatabaseHelper.getBillData(UserInformation.getInstance().getUserId(), 1); // 1 = hledám pouze výdaje
        }else {
            temp = billDatabaseHelper.getBillData(UserInformation.getInstance().getUserId(), 0); // 0 = hledám pouze příjmy
        }
        ID = ArrayUtility.arrayStringToInteger(temp[0]);
        holderId = ArrayUtility.arrayStringToInteger(temp[0]);
        billName = temp[1];
        billDate = temp[2];
        billAmount = temp[3];

        //nastavení dat do adapteru pro zobrazení
        setAdapterToList(billName, billDate, billAmount);

    }

    private void setTitle(){
        if(isExpense){
            setTitle(getString(R.string.title_activity_expense));
        }else {
            setTitle(getString(R.string.title_activity_income));
        }
    }

    private void setAdapterToList(String[] billName, String[] billDate, String[] billAmount){
        listViewBillAdapter = new ListViewBillAdapter(BillActivity.this, billName, billDate, billAmount);
        if(isExpense){
            listViewBillAdapter.isExpense(true);
        }
        listViewBill.setAdapter(listViewBillAdapter);
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

        BillActivity thisActivity = BillActivity.this;
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
