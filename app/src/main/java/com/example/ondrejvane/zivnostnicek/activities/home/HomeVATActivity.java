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
import com.example.ondrejvane.zivnostnicek.helper.Header;
import com.example.ondrejvane.zivnostnicek.helper.Logout;
import com.example.ondrejvane.zivnostnicek.helper.Settings;
import com.example.ondrejvane.zivnostnicek.utilities.FormatUtility;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

public class HomeVATActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomeVATActivity";

    //prvky aktivity
    private Spinner spinnerYear;
    private Spinner spinnerMonth;
    private PieChart pieChart;
    private TextView textViewInputVAT;
    private TextView textViewOutputVAT;
    private TextView textViewBalancVATLabel;
    private TextView textViewBalancVATAmount;

    //globální proměnné
    private BillDatabaseHelper billDatabaseHelper;
    private int pickedYear = -1;
    private int pickedMonth = -1;
    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "Starting activity");

        setContentView(R.layout.activity_home_vat);
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

        //inicializace nastavení
        setSettings();

        Log.d(TAG, "Activity successfully init");

        //akce při výběru roku ze spinner
        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    pickedYear = Integer.parseInt(spinnerYear.getSelectedItem().toString());
                } else {
                    pickedYear = -1;
                }

                getDataAndSetToActivity();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //akce při výběru roku ze spinner
        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        spinnerYear = findViewById(R.id.spinnerHomeVATYear);
        spinnerMonth = findViewById(R.id.spinnerHomeVATMonth);
        pieChart = findViewById(R.id.pieGraphHomeVAT);
        textViewInputVAT = findViewById(R.id.textViewHomeInputVAT);
        textViewOutputVAT = findViewById(R.id.textViewHomeOutputVAT);
        textViewBalancVATLabel = findViewById(R.id.textViewHomeVATLabel);
        textViewBalancVATAmount = findViewById(R.id.textViewHomeBalanceVAT);

        settings = Settings.getInstance();
        billDatabaseHelper = new BillDatabaseHelper(this);
    }

    private void setSettings() {

        //pokud je vybraný jeden rok
        if (settings.isIsPickedOneYear()) {
            spinnerYear.setEnabled(false);
            spinnerYear.setSelection(settings.getArrayYearId());
            pickedYear = Integer.parseInt(settings.getYear());
        }

        if (settings.isPickedOneMonth()) {
            spinnerMonth.setEnabled(false);
            spinnerMonth.setSelection(settings.getArrayMonthId());
            pickedMonth = settings.getArrayMonthId();
        }

    }

    private void getDataAndSetToActivity() {
        double inputAmountVAT = billDatabaseHelper.getBillVatByDate(pickedYear, pickedMonth, 1);
        double outputAmountVAT = billDatabaseHelper.getBillVatByDate(pickedYear, pickedMonth, 0);
        double balanceVAT = inputAmountVAT - outputAmountVAT;

        //nastavení dat do grafu
        addDataToChart(inputAmountVAT, outputAmountVAT);

        //nastavení dat do text view
        String formattedIncomes = FormatUtility.formatIncomeAmount(Double.toString(inputAmountVAT)).substring(1);
        String formattedExpense = FormatUtility.formatExpenseAmount(Double.toString(outputAmountVAT)).substring(1);
        String formattedBalance;
        textViewInputVAT.setText(formattedIncomes);
        textViewOutputVAT.setText(formattedExpense);

        if (balanceVAT > 0) {
            formattedBalance = FormatUtility.formatBalanceAmount((float) balanceVAT).substring(1);
            textViewBalancVATAmount.setTextColor(getResources().getColor(R.color.income));
            textViewBalancVATAmount.setText(formattedBalance);
            textViewBalancVATLabel.setText(getString(R.string.claim));
            return;
        }

        if (balanceVAT < 0) {
            formattedBalance = FormatUtility.formatBalanceAmount((float) balanceVAT).substring(1);
            textViewBalancVATAmount.setTextColor(getResources().getColor(R.color.expense));
            textViewBalancVATAmount.setText(formattedBalance);
            textViewBalancVATLabel.setText(getString(R.string.obligation));
            return;
        }

        if (balanceVAT == 0) {
            textViewBalancVATAmount.setTextColor(getResources().getColor(R.color.zero));
            textViewBalancVATAmount.setText(getString(R.string.zero));
        }
    }

    private void addDataToChart(double inputAmountVAT, double outputAmountVAT) {
        //inicializace grafu
        pieChart.setRotationEnabled(true);
        pieChart.setCenterText(getString(R.string.VAT_label));
        pieChart.setCenterTextSize(20);
        pieChart.setCenterTextRadiusPercent(80);

        ArrayList<PieEntry> arrayData = new ArrayList<>();
        ArrayList<String> arrayDataStrings = new ArrayList<>();

        arrayData.add(new PieEntry((float) inputAmountVAT, 0));
        arrayData.add(new PieEntry((float) outputAmountVAT, 1));

        arrayDataStrings.add(getString(R.string.received));
        arrayDataStrings.add(getString(R.string.paid));

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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
        Intent intent;
        switch (item.getItemId()) {
            case R.id.option_menu_home_income_and_expense:
                intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.option_menu_home_year_summary:
                intent = new Intent(this, HomeYearSummaryActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.option_menu_home_income:
                intent = new Intent(this, HomeIncomeActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.option_menu_home_expense:
                intent = new Intent(this, HomeExpenseActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.option_menu_home_vat:
                intent = new Intent(this, HomeVATActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.option_menu_home_traders:
                intent = new Intent(this, HomeTraderActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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

        HomeVATActivity thisActivity = HomeVATActivity.this;
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
