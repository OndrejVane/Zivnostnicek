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
import com.example.ondrejvane.zivnostnicek.utilities.FormatUtility;
import com.example.ondrejvane.zivnostnicek.helper.Header;
import com.example.ondrejvane.zivnostnicek.helper.Logout;
import com.example.ondrejvane.zivnostnicek.helper.Settings;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;

public class HomeYearSummaryActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomeYearSummaryActivity";

    //prvky aktivity
    private Spinner yearSpinner;
    private LineChart lineChart;
    private TextView expenseTextView;
    private TextView incomeTextView;
    private TextView bilancTextView;

    //globální proměnné
    private int pickedYear;
    private BillDatabaseHelper billDatabaseHelper;
    private float expenseYear;
    private float incomeYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "Starting activity");

        setContentView(R.layout.activity_home_year_summary);
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

        //nastavení dat do grafu
        setDataToGraph();

        //nastavení dat do text view
        setDataToTextView();

        Log.d(TAG, "Activity successfully init");

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    pickedYear = Integer.parseInt(yearSpinner.getSelectedItem().toString());
                } else {
                    //pokud není vybrán žádný rok, tak se zobrazuje aktuální rok
                    pickedYear = Calendar.getInstance().get(Calendar.YEAR);
                }

                //aktualizace grafu
                setDataToGraph();

                //aktualizace text view
                setDataToTextView();

                Log.d(TAG, "Picked year: " + pickedYear);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }



    private void initActivity() {
        yearSpinner = findViewById(R.id.spinnerHomeYearSummary);
        lineChart = findViewById(R.id.lineGraphYearSummary);
        Settings settings = Settings.getInstance();
        billDatabaseHelper = new BillDatabaseHelper(this);
        incomeTextView = findViewById(R.id.textViewHomeYearSummaryIncome);
        expenseTextView = findViewById(R.id.textViewHomeYearSummaryExpense);
        bilancTextView = findViewById(R.id.textViewHomeYearSummaryBalance);
        pickedYear = Calendar.getInstance().get(Calendar.YEAR);

        //pokud je vybrán jeden rok
        if(settings.isIsPickedOneYear()){
            yearSpinner.setEnabled(false);
            yearSpinner.setSelection(settings.getArrayYearId());
            pickedYear = Integer.parseInt(settings.getYear());
        }
        

    }

    private void setDataToTextView() {

        //zjištění výnosu
        float balance = incomeYear - expenseYear;

        //formátování hodnot
        String formattedIncome = FormatUtility.formatIncomeAmount(Float.toString(incomeYear));
        String formattedExpense = FormatUtility.formatExpenseAmount(Float.toString(expenseYear));
        String formattedBalance;

        //nastavení textu do aktivity
        incomeTextView.setText(formattedIncome);
        expenseTextView.setText(formattedExpense);

        if(balance > 0){
            formattedBalance = FormatUtility.formatBalanceAmount(balance);
            bilancTextView.setTextColor(getResources().getColor(R.color.income));
            bilancTextView.setText(formattedBalance);
            return;
        }

        if(balance < 0){
            formattedBalance = FormatUtility.formatBalanceAmount(balance);
            bilancTextView.setTextColor(getResources().getColor(R.color.expense));
            bilancTextView.setText(formattedBalance);
            return;
        }

        if (balance == 0) {
            bilancTextView.setTextColor(getResources().getColor(R.color.zero));
            bilancTextView.setText(getString(R.string.zero));
        }





    }

    private void setDataToGraph() {
        ArrayList<Entry> expenseValues = new ArrayList<>();
        ArrayList<Entry> incomeValues = new ArrayList<>();
        final ArrayList<String> labels = new ArrayList<>();
        float temp;
        expenseYear = 0;
        incomeYear = 0;
        String[] months = getResources().getStringArray(R.array.months);
        for(int i = 0; i < 12; i++){
            //načtení výdajů z databáze
            temp = billDatabaseHelper.getBillDataByDate(pickedYear, i+1, 1);
            expenseValues.add(new Entry(i, temp));
            expenseYear = expenseYear + temp;

            //načtení příjmů z databáze
            temp = billDatabaseHelper.getBillDataByDate(pickedYear, i+1, 0);
            incomeValues.add(new Entry(i, temp));
            incomeYear = incomeYear + temp;

            //načtení měsíců pro zobrazení do osy
            labels.add(months[i+1]);

        }

        LineDataSet setExpense, setIncome;

        setExpense = new LineDataSet(expenseValues, "Expense");
        setExpense.setColor(getResources().getColor(R.color.expense));
        setExpense.setCircleColor(getResources().getColor(R.color.expense));
        setExpense.setFillColor(getResources().getColor(R.color.expense));
        setExpense.setDrawFilled(true);


        setIncome = new LineDataSet(incomeValues, "Income");
        setIncome.setColor(getResources().getColor(R.color.income));
        setIncome.setCircleColor(getResources().getColor(R.color.income));
        setIncome.setFillColor(getResources().getColor(R.color.income));
        setIncome.setDrawFilled(true);

        LineData lineData = new LineData(setExpense, setIncome);

        lineChart.setData(lineData);
        lineChart.getAxisLeft().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.animateXY(1000, 1000);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getAxisRight().setAxisMinimum(0f);
        lineChart.getAxisLeft().setAxisMinimum(0f);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter(){
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            if(value%1 == 0){
                return labels.get((int) value);
            }else{
                return "";
            }
        }
    });

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
     * Metoda pro zobrazení postraního menu.
     *
     * @param menu
     * @return
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
     * @param item  vybraný item z menu
     * @return      boolean
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

        HomeYearSummaryActivity thisActivity = HomeYearSummaryActivity.this;
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
