package com.example.ondrejvane.zivnostnicek.activities.trader;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.activities.ExpenseActivity;
import com.example.ondrejvane.zivnostnicek.activities.HomeActivity;
import com.example.ondrejvane.zivnostnicek.activities.IncomeActivity;
import com.example.ondrejvane.zivnostnicek.activities.InfoActivity;
import com.example.ondrejvane.zivnostnicek.activities.StorageActivity;
import com.example.ondrejvane.zivnostnicek.activities.SynchronizationActivity;
import com.example.ondrejvane.zivnostnicek.activities.TraderNewNoteActivity;
import com.example.ondrejvane.zivnostnicek.activities.TraderNoteActivity;
import com.example.ondrejvane.zivnostnicek.database.DatabaseHelper;
import com.example.ondrejvane.zivnostnicek.helper.Header;
import com.example.ondrejvane.zivnostnicek.helper.Logout;
import com.example.ondrejvane.zivnostnicek.model.Trader;

public class TraderShowActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseHelper databaseHelper;
    private int traderID;
    private Trader trader;

    private EditText inputCompanyNameShow, inputContactPersonShow, inputTelephoneNumberShow;
    private EditText inputIdentificationNumberShow, inputTaxIdentificationNumberShow;
    private EditText inputCityShow, inputStreetShow, inputHouseNumberShow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trader_show);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        setTextToActivity();


    }

    private void initActivity() {
        databaseHelper = new DatabaseHelper(TraderShowActivity.this);
        traderID = Integer.parseInt(getIntent().getExtras().get("TRADER_ID").toString());
        trader = databaseHelper.getTraderById(traderID);
        inputCompanyNameShow = findViewById(R.id.textInputEditTextCompanyNameShow);
        inputContactPersonShow = findViewById(R.id.textInputEditTextContactPersonShow);
        inputTelephoneNumberShow = findViewById(R.id.textInputEditTextTelephoneNumberShow);
        inputIdentificationNumberShow = findViewById(R.id.textInputEditTextIdentificationNumberShow);
        inputTaxIdentificationNumberShow = findViewById(R.id.textInputEditTextTaxIdentificationNumberShow);
        inputCityShow = findViewById(R.id.textInputEditTextCityShow);
        inputStreetShow = findViewById(R.id.textInputEditTextStreetShow);
        inputHouseNumberShow = findViewById(R.id.textInputEditTextHouseNumberShow);
    }

    private void setTextToActivity(){
        inputCompanyNameShow.setText(trader.getTraderName());
        inputContactPersonShow.setText(trader.getTraderContactPerson());
        inputTelephoneNumberShow.setText(trader.getTraderPhoneNumber());
        inputIdentificationNumberShow.setText(trader.getTraderIN());
        inputTaxIdentificationNumberShow.setText(trader.getTraderTIN());
        inputCityShow.setText(trader.getTraderCity());
        inputStreetShow.setText(trader.getTraderStreet());
        inputHouseNumberShow.setText(trader.getTraderHouseNumber());
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        Intent home = new Intent(TraderShowActivity.this, TraderActivity.class);
        startActivity(home);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.trader_show_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.option_menu_trader_show_edit:
                intent = new Intent(TraderShowActivity.this, TraderEditActivity.class);
                intent.putExtra("TRADER_ID", traderID);
                startActivity(intent);
                finish();
                return true;
            case R.id.option_menu_trader_show_add_note:
                intent = new Intent(TraderShowActivity.this, TraderNewNoteActivity.class);
                intent.putExtra("TRADER_ID", traderID);
                startActivity(intent);
                finish();
                return true;
            case R.id.option_menu_trader_show_edit_delete:
                alertDelete();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){

            case R.id.nav_home:
                Intent home = new Intent(TraderShowActivity.this, HomeActivity.class);
                startActivity(home);
                finish();
                break;

            case R.id.nav_income:
                Intent income = new Intent(TraderShowActivity.this, IncomeActivity.class);
                startActivity(income);
                finish();
                break;

            case R.id.nav_expense:
                Intent expense = new Intent(TraderShowActivity.this, ExpenseActivity.class);
                startActivity(expense);
                finish();
                break;

            case R.id.nav_traders:
                Intent traders = new Intent(TraderShowActivity.this, TraderActivity.class);
                startActivity(traders);
                finish();
                break;

            case R.id.nav_storage:
                Intent storage = new Intent(TraderShowActivity.this, StorageActivity.class);
                startActivity(storage);
                finish();
                break;

            case R.id.nav_info:
                Intent info = new Intent(TraderShowActivity.this, InfoActivity.class);
                startActivity(info);
                finish();
                break;

            case R.id.nav_sync:
                Intent sync = new Intent(TraderShowActivity.this, SynchronizationActivity.class);
                startActivity(sync);
                finish();
                break;

            case R.id.nav_logout:
                Logout logout = new Logout(TraderShowActivity.this, this);
                logout.logout();
                break;

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void alertDelete(){
        AlertDialog.Builder alert = new AlertDialog.Builder(TraderShowActivity.this);
        alert.setMessage(R.string.delete_trader_question).setCancelable(false)
                .setPositiveButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteTrader();
                    }
                });
        AlertDialog alertDialog = alert.create();
        alert.setTitle(R.string.logout);
        alertDialog.show();

    }

    private void deleteTrader(){
        boolean result = databaseHelper.deleteTraderById(traderID);
        if(result){
            Toast.makeText(this, R.string.trader_deleted_message, Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, R.string.trader_not_deleted_message, Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(TraderShowActivity.this, TraderActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToTraderNoteActivity(View view) {
        Intent intent = new Intent(TraderShowActivity.this, TraderNoteActivity.class);
        intent.putExtra("TRADER_ID", traderID);
        startActivity(intent);
        finish();
    }
}
