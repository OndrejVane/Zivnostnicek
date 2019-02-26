package com.example.ondrejvane.zivnostnicek.activities.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.adapters.ListViewHomeAdapter;
import com.example.ondrejvane.zivnostnicek.database.BillDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.database.TypeBillDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.helper.Header;
import com.example.ondrejvane.zivnostnicek.helper.Logout;
import com.example.ondrejvane.zivnostnicek.helper.Settings;
import com.example.ondrejvane.zivnostnicek.helper.UserInformation;
import com.example.ondrejvane.zivnostnicek.menu.HomeOptionMenu;
import com.example.ondrejvane.zivnostnicek.model.TypeBill;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

/**
 * Aktivity, která zobrazuje příjmy nebo výdaje členěné podle druhů.
 */
public class HomeExpenseOrIncomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomeEOrIActivity";

    //prvky aktivity
    private Spinner spinnerYear;
    private Spinner spinnerMonth;
    private PieChart pieChart;
    private ListView listViewExpense;


    //pomocné gloální proměnné
    private TypeBillDatabaseHelper typeBillDatabaseHelper;
    private BillDatabaseHelper billDatabaseHelper;
    private String[] typeName;
    private float[] typeAmount;
    private int[] typeColor;
    private int pickedYear = -1;
    private int pickedMonth = -1;
    private boolean isExpense;
    private boolean isFirstPickYear = true;
    private boolean isFirstPickMonth = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_expense_or_income);

        Log.d(TAG, "Activity is starting");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        //nastavení textu do aktivity
        setTitle();

        //implementace nastavení
        setSettings();

        //nastavení dat do aktivity
        setDataToActivity();

        Log.d(TAG, "Activity is successfully started");

        //nastavení (aktualizace dat) akce po vybrání roku ze spinneru
        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //kontrola, zda se jjedná o první výber
                if (isFirstPickYear) {
                    isFirstPickYear = false;
                    return;
                }

                if (position != 0) {
                    pickedYear = Integer.parseInt(spinnerYear.getSelectedItem().toString());
                } else {
                    pickedYear = -1;
                }

                setDataToActivity();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //nastavení (aktualizace dat) akce po vybrání roku ze spinneru
        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //kontrola, zda se jjedná o první výber
                if (isFirstPickMonth) {
                    isFirstPickMonth = false;
                    return;
                }

                if (position != 0) {
                    pickedMonth = position;
                } else {
                    pickedMonth = -1;
                }

                setDataToActivity();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private void setTitle() {
        if(isExpense){
            setTitle(getResources().getString(R.string.title_activity_home_expense));
        }
    }

    /**
     * Inicializace všech potřebných prvků v aktivitě.
     */
    private void initActivity() {
        //zjištění, zda se jedná o náhled příjmu
        if (getIntent().hasExtra("IS_EXPENSE")) {
            isExpense = getIntent().getExtras().getBoolean("IS_EXPENSE", false);
        } else {
            isExpense = false;
        }

        //inicializace graf prvků aktivity
        spinnerYear = findViewById(R.id.spinnerHomeYearExpense);
        spinnerMonth = findViewById(R.id.spinnerHomeMonthExpense);
        pieChart = findViewById(R.id.pieGraphHomeExpense);
        listViewExpense = findViewById(R.id.listViewHomeExpense);

        //inicializace databáze
        typeBillDatabaseHelper = new TypeBillDatabaseHelper(this);
        billDatabaseHelper = new BillDatabaseHelper(this);
    }

    /**
     * Načtení data z databáze a zobrazení do aktivity
     */
    private void setDataToActivity() {
        ArrayList<TypeBill> tempTypeBill = typeBillDatabaseHelper.getAllTypeByUserId(UserInformation.getInstance().getUserId());
        tempTypeBill.get(0).setColor(getResources().getColor(R.color.colorPrimary));
        tempTypeBill.get(0).setName(getResources().getString(R.string.not_assigned));

        typeName = new String[tempTypeBill.size()];
        typeColor = new int[tempTypeBill.size()];
        typeAmount = new float[tempTypeBill.size()];

        for (int i = 0; i < tempTypeBill.size(); i++) {
            typeName[i] = tempTypeBill.get(i).getName();
            typeColor[i] = tempTypeBill.get(i).getColor();
            typeAmount[i] = billDatabaseHelper.getTotalAmountByTypeId(pickedYear, pickedMonth, tempTypeBill.get(i).getId(), isExpense);
        }

        //nastavení dat do lsitu
        setAdapterToList(typeName, typeColor, typeAmount);

        //nastavení dat do grafu
        setDataToGraph(typeName, typeColor, typeAmount);

    }

    /**
     * Metoda, která nastavuje načtená data z databáze do grafu.
     * @param typeName  pole s názvem typu
     * @param typeColor pole s barvou typu
     * @param typeAmount    pole se součtem všech P/V v daném typu
     */
    private void setDataToGraph(String[] typeName, int[] typeColor, float[] typeAmount) {
        //inicializace grafu
        pieChart.setRotationEnabled(true);
        if (isExpense) {
            pieChart.setCenterText(getString(R.string.expense));
        } else {
            pieChart.setCenterText(getString(R.string.income));
        }
        pieChart.setCenterTextSize(20);
        pieChart.setCenterTextRadiusPercent(80);

        //inciilaizace potřebných listů (vstupy do grafu)
        ArrayList<PieEntry> arrayData = new ArrayList<>();
        ArrayList<String> arrayDataStrings = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        //přidání prvků do listů
        for (int i = 0; i < typeAmount.length; i++) {
            if (typeAmount[i] != 0.0) {
                arrayData.add(new PieEntry(typeAmount[i], i));
                arrayDataStrings.add(typeName[i]);
                colors.add(typeColor[i]);
            }
        }

        PieDataSet pieDataSet = new PieDataSet(arrayData, null);
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(15);

        pieDataSet.setColors(colors);


        //upraga zobrazení grafu
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
        pieChart.getLegend().setEnabled(false);
        pieChart.animateXY(700, 700);
        pieChart.getDescription().setEnabled(false);
    }


    /**
     * Nastavení dat do do listu pomocí adapteru
     * @param typeName  pole s názvem typu
     * @param typeColor pole s barvou typu
     * @param typeAmount    pole se součtem všech P/V v daném typu
     */
    private void setAdapterToList(String[] typeName, int[] typeColor, float[] typeAmount) {
        ListViewHomeAdapter listViewHomeAdapter = new ListViewHomeAdapter(this, typeColor, typeName, typeAmount);
        listViewHomeAdapter.setExpense(isExpense);

        listViewExpense.setAdapter(listViewHomeAdapter);
    }

    private void setSettings() {
        Settings settings = Settings.getInstance();
        //pokud je vybraný jeden rok
        if (settings.isIsPickedOneYear()) {
            spinnerYear.setEnabled(false);
            spinnerYear.setSelection(settings.getArrayYearId());
            pickedYear = Integer.parseInt(settings.getYear());
        }

        //pokud je vybraný měsíc
        if (settings.isPickedOneMonth()) {
            spinnerMonth.setEnabled(false);
            spinnerMonth.setSelection(settings.getArrayMonthId());
            pickedMonth = settings.getArrayMonthId();
        }
    }

    /**
     * Metoda, která se porvede po stisknutí talčítka zpět
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
     * Metoda, která se stará o vykreslení bočního navigačního menu.
     * @param menu menu
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_side_menu, menu);
        return true;
    }

    /**
     * Metoda, která se stará o boční navigační menu a přechod
     * mezi aktivitami
     *
     * @param item vybraný item z menu
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        HomeOptionMenu homeOptionMenu = new HomeOptionMenu(this);
        Intent intent = homeOptionMenu.getMenu(item.getItemId());
        startActivity(intent);
        finish();
        return true;
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

        HomeExpenseOrIncomeActivity thisActivity = HomeExpenseOrIncomeActivity.this;
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
