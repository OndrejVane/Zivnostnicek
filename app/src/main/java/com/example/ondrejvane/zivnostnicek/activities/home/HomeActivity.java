package com.example.ondrejvane.zivnostnicek.activities.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
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
import com.example.ondrejvane.zivnostnicek.adapters.TabPagerHomeAdapter;
import com.example.ondrejvane.zivnostnicek.adapters.TabPagerTraderAdapter;
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

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabPagerHomeAdapter tabPagerHomeAdapter;

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

    }

    /**
     * Inicializace všech potřebných prvků v aktivitě.
     */
    private void initActivity() {
        //inicializace prvků v aktivitě
        tabLayout = findViewById(R.id.tabLayoutHome);
        viewPager = findViewById(R.id.viewPagerHome);

        //načtení titulků pro jednotlivé fragmenty
        String[] titles = new String[4];
        titles[0] = getResources().getString(R.string.income_and_expense);
        titles[1] = getResources().getString(R.string.year_summary);
        titles[2] = getResources().getString(R.string.vat_summary);
        titles[3] = getResources().getString(R.string.traders_summary);

        //inicializace oken
        tabPagerHomeAdapter = new TabPagerHomeAdapter(getSupportFragmentManager(), titles, 4);
        viewPager.setAdapter(tabPagerHomeAdapter);
        tabLayout.setupWithViewPager(viewPager);

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
