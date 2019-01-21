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
import com.example.ondrejvane.zivnostnicek.activities.expense.ExpenseActivity;
import com.example.ondrejvane.zivnostnicek.activities.HomeActivity;
import com.example.ondrejvane.zivnostnicek.activities.income.IncomeActivity;
import com.example.ondrejvane.zivnostnicek.activities.info.InfoActivity;
import com.example.ondrejvane.zivnostnicek.activities.storage.StorageActivity;
import com.example.ondrejvane.zivnostnicek.activities.SynchronizationActivity;
import com.example.ondrejvane.zivnostnicek.activities.note.NoteNewActivity;
import com.example.ondrejvane.zivnostnicek.activities.note.NoteActivity;
import com.example.ondrejvane.zivnostnicek.database.TraderDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.helper.Header;
import com.example.ondrejvane.zivnostnicek.helper.Logout;
import com.example.ondrejvane.zivnostnicek.model.Trader;

/**
 * Aktivita, která se stará o zobrazení vybraného obchodníka.
 */
public class TraderShowActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TraderDatabaseHelper traderDatabaseHelper;
    private int traderID;
    private Trader trader;

    private EditText inputCompanyNameShow, inputContactPersonShow, inputTelephoneNumberShow;
    private EditText inputIdentificationNumberShow, inputTaxIdentificationNumberShow;
    private EditText inputCityShow, inputStreetShow, inputHouseNumberShow;


    /**
     * Metoda, která se provede při spuštění akctivity a provede nezbytné
     * úkony ke správnému fungování aktivity.
     * @param savedInstanceState
     */
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

    /**
     * Procedura, která inicializuje všechny potřebné prvky
     * aktivity
     */
    private void initActivity() {
        traderDatabaseHelper = new TraderDatabaseHelper(TraderShowActivity.this);
        traderID = Integer.parseInt(getIntent().getExtras().get("TRADER_ID").toString());
        trader = traderDatabaseHelper.getTraderById(traderID);
        inputCompanyNameShow = findViewById(R.id.textInputEditTextCompanyNameShow);
        inputContactPersonShow = findViewById(R.id.textInputEditTextContactPersonShow);
        inputTelephoneNumberShow = findViewById(R.id.textInputEditTextTelephoneNumberShow);
        inputIdentificationNumberShow = findViewById(R.id.textInputEditTextIdentificationNumberShow);
        inputTaxIdentificationNumberShow = findViewById(R.id.textInputEditTextTaxIdentificationNumberShow);
        inputCityShow = findViewById(R.id.textInputEditTextCityShow);
        inputStreetShow = findViewById(R.id.textInputEditTextStreetShow);
        inputHouseNumberShow = findViewById(R.id.textInputEditTextHouseNumberShow);
    }

    /**
     * Procerdura, která nastaví text do všech políček
     * activity, které se načetli z databáze.
     */
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
        Intent home = new Intent(TraderShowActivity.this, TraderActivity.class);
        startActivity(home);
        finish();
    }


    /**
     * Metoda, která vytvoří boční navigační menu po
     * zahájení atcitivity.
     * @param menu  bočnínavigační menu
     * @return      boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.trader_show_menu, menu);
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
            case R.id.option_menu_trader_show_edit:
                intent = new Intent(TraderShowActivity.this, TraderEditActivity.class);
                intent.putExtra("TRADER_ID", traderID);
                startActivity(intent);
                finish();
                return true;
            case R.id.option_menu_trader_show_add_note:
                intent = new Intent(TraderShowActivity.this, NoteNewActivity.class);
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

    /**
     * Procedura, která vykreslí upozornění. Dotáže se
     * uživatele, zda si je opravdu jistý smazáním obchodníka.
     * Pokud ano zavolá proceduru deteleTrader. Pokdu ne, upozorněné se zavře
     * a nic se nestane.
     */
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

    /**
     * Procedura, která odstraní z databáze obchodníka podle ID
     * a přepne intent do předchozí aktivity.
     */
    private void deleteTrader(){
        boolean result = traderDatabaseHelper.deleteTraderById(traderID);
        if(result){
            Toast.makeText(this, R.string.trader_deleted_message, Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, R.string.trader_not_deleted_message, Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(TraderShowActivity.this, TraderActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Metoda, která změní aktivitu na Note Activity. Přiloží
     * id obchodníka, aby bylo možné zobrazit a následně přidávat
     * poznámky k příslušnému obchodníkovi.
     * @param view  view příslušné aktivity
     */
    public void goToTraderNoteActivity(View view) {
        Intent intent = new Intent(TraderShowActivity.this, NoteActivity.class);
        intent.putExtra("TRADER_ID", traderID);
        startActivity(intent);
        finish();
    }

    /**
     * Metoda, která se stará o hlavní navigační menu aplikace
     * a přechod mezi hlavními aktivitami.
     * @param item  vybraná položka v menu
     * @return      boolean
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        TraderShowActivity thisActivity = TraderShowActivity.this;

        switch (id){

            case R.id.nav_home:
                Intent home = new Intent(thisActivity, HomeActivity.class);
                startActivity(home);
                finish();
                break;

            case R.id.nav_income:
                Intent income = new Intent(thisActivity, IncomeActivity.class);
                startActivity(income);
                finish();
                break;

            case R.id.nav_expense:
                Intent expense = new Intent(thisActivity, ExpenseActivity.class);
                startActivity(expense);
                finish();
                break;

            case R.id.nav_traders:
                Intent traders = new Intent(thisActivity, TraderActivity.class);
                startActivity(traders);
                finish();
                break;

            case R.id.nav_storage:
                Intent storage = new Intent(thisActivity, StorageActivity.class);
                startActivity(storage);
                finish();
                break;

            case R.id.nav_info:
                Intent info = new Intent(thisActivity, InfoActivity.class);
                startActivity(info);
                finish();
                break;

            case R.id.nav_sync:
                Intent sync = new Intent(thisActivity, SynchronizationActivity.class);
                startActivity(sync);
                finish();
                break;

            case R.id.nav_logout:
                Logout logout = new Logout(thisActivity, this);
                logout.logout();
                break;

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
