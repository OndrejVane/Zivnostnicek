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
import android.widget.Spinner;
import android.widget.TextView;

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.database.BillDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.menu.HomeOptionMenu;
import com.example.ondrejvane.zivnostnicek.utilities.FormatUtility;
import com.example.ondrejvane.zivnostnicek.helper.Header;
import com.example.ondrejvane.zivnostnicek.helper.Logout;
import com.example.ondrejvane.zivnostnicek.helper.Settings;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomeActivity";

    //prvky fragmentu
    private Spinner spinnerPickedYear;
    private Spinner spinnerPickedMonth;
    private PieChart pieChart;
    private TextView textViewIncome;
    private TextView textViewExpense;
    private TextView textViewBalance;

    //pomocné globální proměnné
    private BillDatabaseHelper billDatabaseHelper;
    private int pickedYear = -1;
    private int pickedMonth = -1;
    float incomes;
    float expense;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "Starting activity HomeActivity");

        setContentView(R.layout.activity_home);
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

        //implementace nastavení
        setSettings();

        //načtení dat z databáze
        incomes = billDatabaseHelper.getBillDataByDate(pickedYear, pickedMonth, 0);
        expense = billDatabaseHelper.getBillDataByDate(pickedYear, pickedMonth, 1);


        //zobrazení dat do grafu
        addDataToChart(incomes, expense);



        //akce při výběru roku ze spinner
        spinnerPickedYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    pickedYear = Integer.parseInt(spinnerPickedYear.getSelectedItem().toString());
                } else {
                    pickedYear = -1;
                }

                getDataAndSetToActivity();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //akce při výběru mesíce ze spinneru
        spinnerPickedMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    pickedMonth = position;
                } else {
                    pickedMonth = -1;
                }
                getDataAndSetToActivity();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private void initActivity() {
        //inicializace prvků v aktivitě
        spinnerPickedYear = findViewById(R.id.spinnerHomeYear);
        spinnerPickedMonth = findViewById(R.id.spinnerHomeMonth);
        pieChart = findViewById(R.id.pieGraphHome);
        textViewIncome = findViewById(R.id.textViewHomeIncome);
        textViewExpense = findViewById(R.id.textViewHomeExpense);
        textViewBalance = findViewById(R.id.textViewHomeBalance);

        //inicializace databáze
        billDatabaseHelper = new BillDatabaseHelper(this);
    }

    private void addDataToChart(float incomes, float expense) {
        //inicializace grafu
        pieChart.setRotationEnabled(true);
        pieChart.setCenterText(getString(R.string.income_and_expense_space));
        pieChart.setCenterTextSize(20);
        pieChart.setCenterTextRadiusPercent(80);

        ArrayList<PieEntry> arrayData = new ArrayList<>();
        ArrayList<String> arrayDataStrings = new ArrayList<>();

        arrayData.add(new PieEntry(incomes, 0));
        arrayData.add(new PieEntry(expense, 1));

        arrayDataStrings.add(getString(R.string.income));
        arrayDataStrings.add(getString(R.string.expense));

        PieDataSet pieDataSet = new PieDataSet(arrayData, null);
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(15);

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(getResources().getColor(R.color.income));
        colors.add(getResources().getColor(R.color.expense));

        pieDataSet.setColors(colors);


        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
        pieChart.getLegend().setEnabled(false);
        pieChart.animateXY(700, 700);
        pieChart.getDescription().setEnabled(false);

    }

    private void setSettings() {
        Settings settings = Settings.getInstance();
        //pokud je vybraný jeden rok
        if (settings.isIsPickedOneYear()) {
            spinnerPickedYear.setEnabled(false);
            spinnerPickedYear.setSelection(settings.getArrayYearId());
            pickedYear = Integer.parseInt(settings.getYear());
        }

        if (settings.isPickedOneMonth()) {
            spinnerPickedMonth.setEnabled(false);
            spinnerPickedMonth.setSelection(settings.getArrayMonthId());
            pickedMonth = settings.getArrayMonthId();
        }
    }

    private void getDataAndSetToActivity() {
        //načtení dat z databáze
        float incomes = billDatabaseHelper.getBillDataByDate(pickedYear, pickedMonth, 0);
        float expense = billDatabaseHelper.getBillDataByDate(pickedYear, pickedMonth, 1);
        float balance = incomes - expense;

        //nastavení dat do grafu
        addDataToChart(incomes, expense);

        //nastavení dat do text view
        String formattedIncomes = FormatUtility.formatIncomeAmount(Float.toString(incomes));
        String formattedExpense = FormatUtility.formatExpenseAmount(Float.toString(expense));
        String formattedBalance;
        textViewIncome.setText(formattedIncomes);
        textViewExpense.setText(formattedExpense);

        if(balance > 0){
            formattedBalance = FormatUtility.formatBalanceAmount(balance);
            textViewBalance.setTextColor(getResources().getColor(R.color.income));
            textViewBalance.setText(formattedBalance);
            return;
        }

        if(balance < 0){
            formattedBalance = FormatUtility.formatBalanceAmount(balance);
            textViewBalance.setTextColor(getResources().getColor(R.color.expense));
            textViewBalance.setText(formattedBalance);
            return;
        }

        if (balance == 0) {
            textViewBalance.setTextColor(getResources().getColor(R.color.zero));
            textViewBalance.setText(getString(R.string.zero));
        }
    }

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
     *
     * @param item vybraná položka v menu
     * @return boolean
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //id vybrané položky v menu
        int id = item.getItemId();

        HomeActivity thisActivity = HomeActivity.this;
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
