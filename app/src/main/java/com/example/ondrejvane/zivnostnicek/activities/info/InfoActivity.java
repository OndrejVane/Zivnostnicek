package com.example.ondrejvane.zivnostnicek.activities.info;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.activities.home.HomeActivity;
import com.example.ondrejvane.zivnostnicek.helper.Header;
import com.example.ondrejvane.zivnostnicek.session.ExitApp;
import com.example.ondrejvane.zivnostnicek.session.Logout;

/**
 * Aktivita, která zobrazí menu ze 4 výběry. Po
 * Stisknutí na vybranou položku spustí dannou aktivitu.
 */
public class InfoActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_info);

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
    }

    /**
     * Metoda, která nastartuje příslušnou aktivitu.
     *
     * @param view view aktivity
     */
    public void goToInfoCurrencyActivity(View view) {
        Intent intent = new Intent(InfoActivity.this, InfoCurrencyActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Metoda, která nastartuje příslušnou aktivitu.
     *
     * @param view view aktivity
     */
    public void goToInfoAboutAppActivity(View view) {
        Intent intent = new Intent(InfoActivity.this, InfoAboutAppActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Metoda, která nastartuje příslušnou aktivitu.
     *
     * @param view view aktivity
     */
    public void goToInfoLinksActivity(View view) {
        Intent intent = new Intent(InfoActivity.this, InfoLinksActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Metoda, která nastartuje příslušnou aktivitu.
     *
     * @param view view aktivity
     */
    public void goToInfoDateActivity(View view) {
        Intent intent = new Intent(InfoActivity.this, InfoDateActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Metoda, která je volána po stisknutí tlačítka zpět.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent homeActivity = new Intent(this, HomeActivity.class);
            startActivity(homeActivity);
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

        InfoActivity thisActivity = InfoActivity.this;
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
