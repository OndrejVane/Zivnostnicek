package com.example.ondrejvane.zivnostnicek.activities.home;

import android.content.Intent;
import android.graphics.Color;
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

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.database.ModelHelpers.NoteDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.database.ModelHelpers.TraderDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.helper.Header;
import com.example.ondrejvane.zivnostnicek.session.Logout;
import com.example.ondrejvane.zivnostnicek.session.UserInformation;
import com.example.ondrejvane.zivnostnicek.menu.HomeOptionMenu;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

/**
 * Aktivita, která zobrazuje graf s obchodníky a jejich hodnocením
 */
public class HomeTraderActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomeTraderActivity";

    //prvky aktivity
    private HorizontalBarChart barChart;

    //globální proměnné
    private String[] tradersName;
    private float[] tradersEvaluation;
    private NoteDatabaseHelper noteDatabaseHelper;
    private TraderDatabaseHelper traderDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "Starting activity HomeActivity");

        setContentView(R.layout.activity_home_trader);
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

        //získání dat z databáze
        getDataFromDatabase();

        //nastavení dat do grafu
        setDataToGraph(tradersName, tradersEvaluation);

        Log.d(TAG, "Activity HomeActivity successfully started");
    }

    /**
     * Inicializace prvků v aktivitě
     */
    private void initActivity() {

        barChart = findViewById(R.id.barChartTraders);
        noteDatabaseHelper = new NoteDatabaseHelper(this);
        traderDatabaseHelper = new TraderDatabaseHelper(this);


    }

    /**
     * Načtení data za databáze.
     */
    private void getDataFromDatabase() {
        String[][] tempTraders;
        tempTraders = traderDatabaseHelper.getTradersData(UserInformation.getInstance().getUserId());

        tradersName = tempTraders[1];
        tradersEvaluation = new float[tradersName.length];

        for (int i = 0; i < tradersName.length; i++) {
            int tempTraderId = Integer.parseInt(tempTraders[0][i]);
            tradersEvaluation[i] = noteDatabaseHelper.getAverageRatingByTraderId(tempTraderId);
        }
    }

    /**
     * Nastavení data do grafu
     *
     * @param tradersName       pole s názvy obchodníků
     * @param tradersEvaluation pole s hodnocením obchodníků
     */
    private void setDataToGraph(String[] tradersName, float[] tradersEvaluation) {
        // vytvoření seznamu vstupů
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        // vytvoření sezanmu pro labely
        final ArrayList<String> barLabels = new ArrayList<>();

        //přidání data do listů
        for (int i = 0; i < tradersEvaluation.length; i++) {
            barEntries.add(new BarEntry((float) i, tradersEvaluation[i]));
            barLabels.add(tradersName[i]);
        }

        // Create a data set from the entry values
        BarDataSet dataSet = new BarDataSet(barEntries, "Traders");
        // Set data set values to be visible on the graph
        dataSet.setDrawValues(true);

        // Create a data object from the data set
        BarData data = new BarData(dataSet);

        //natavení data do grafu
        barChart.setData(data);

        // zobrazení popisků
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(barLabels));
        barChart.getXAxis().setGranularity(1f);
        barChart.getAxisLeft().setAxisMaximum((float) 5.7);
        barChart.getAxisLeft().setAxisMinimum(0);

        //nastavení animace grafu
        barChart.animateXY(1000, 1000);

        //schování gridu
        barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);

        // nastavení barevného schéma
        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        data.setValueTextSize(13f);
        data.setValueTextColor(Color.DKGRAY);
        barChart.setScaleEnabled(false);
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
     * Metoda, která se stará o boční navigační menu a přechod
     * mezi aktivitami
     *
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

        HomeTraderActivity thisActivity = HomeTraderActivity.this;
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
