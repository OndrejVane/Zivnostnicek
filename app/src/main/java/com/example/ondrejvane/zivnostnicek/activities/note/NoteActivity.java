package com.example.ondrejvane.zivnostnicek.activities.note;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.activities.expense.ExpenseActivity;
import com.example.ondrejvane.zivnostnicek.activities.HomeActivity;
import com.example.ondrejvane.zivnostnicek.activities.income.IncomeActivity;
import com.example.ondrejvane.zivnostnicek.activities.info.InfoActivity;
import com.example.ondrejvane.zivnostnicek.activities.storage.StorageActivity;
import com.example.ondrejvane.zivnostnicek.activities.SynchronizationActivity;
import com.example.ondrejvane.zivnostnicek.activities.trader.TraderActivity;
import com.example.ondrejvane.zivnostnicek.activities.trader.TraderEditActivity;
import com.example.ondrejvane.zivnostnicek.activities.trader.TraderShowActivity;
import com.example.ondrejvane.zivnostnicek.database.NoteDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.database.TraderDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.helper.Header;
import com.example.ondrejvane.zivnostnicek.helper.ListViewTraderAdapter;
import com.example.ondrejvane.zivnostnicek.helper.Logout;

/**
 * Aktivita, která se stará o zobrazení všech poznámek k vybranému obchodníkovi.
 */
public class NoteActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView listViewNote;
    private ListViewTraderAdapter listViewTraderAdapter;
    private int traderID;
    private TraderDatabaseHelper traderDatabaseHelper;
    private NoteDatabaseHelper noteDatabaseHelper;
    private RatingBar ratingBar;
    private TextView averageRating;
    private String[] noteTitle;
    private String[] noteRating;
    private int[] ID;
    private int globalPosition;

    /**
     * Metoda, která se provede při spuštění akctivity a provede nezbytné
     * úkony ke správnému fungování aktivity.
     * @param savedInstanceState    savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //přidání nové poznámky
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NoteActivity.this, NoteNewActivity.class);
                intent.putExtra("TRADER_ID", traderID);
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

        //po stisknutí vybraného listview překne do aktivity pro zobrazní poznámky
        listViewNote.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                globalPosition = position;
                Intent intent = new Intent(NoteActivity.this, NoteShowActivity.class);
                intent.putExtra("NOTE_ID", ID[globalPosition]);
                intent.putExtra("TRADER_ID", traderID);
                startActivity(intent);
                finish();
            }
        });

    }

    /**
     * Procedura, která inicializuje všechny potřebné prvky
     * aktivity.
     */
    private void initActivity() {
        traderID = Integer.parseInt(getIntent().getExtras().get("TRADER_ID").toString());
        noteDatabaseHelper = new NoteDatabaseHelper(NoteActivity.this);
        traderDatabaseHelper = new TraderDatabaseHelper(NoteActivity.this);
        ratingBar = findViewById(R.id.noteShowRatingBar);
        averageRating = findViewById(R.id.textViewNoteAvarageRating);
        String temp[][];
        temp = noteDatabaseHelper.getNotesData(traderID);
        ID = arrayStringToInteger(temp[0]);
        noteTitle = temp[1];
        noteRating = temp[2];

        listViewNote = findViewById(R.id.listViewTrader);
        listViewTraderAdapter = new ListViewTraderAdapter(this, noteTitle, noteRating);
        listViewNote.setAdapter(listViewTraderAdapter);

        ratingBar.setRating(countAverageRating());
        averageRating.append(Float.toString(countAverageRating()));
    }

    /**
     * Metoda, která sočte průměrné hodnocení obchodníka.
     * @return  float průměrné hodnocení obchodníka
     */
    private float countAverageRating() {
        float sum = 0;
        float averageRating;
        for (int i = 0; i<noteRating.length; i++){
            sum = sum + Float.parseFloat(noteRating[i]);
        }
        averageRating = sum/noteRating.length;
        return (float) (Math.round(averageRating * Math.pow(10, 2)) / Math.pow(10, 2));
    }


    /**
     * Metoda, která převede pole strngu(čísel) na pole integeru.
     * @param strings   String[]    pole stringu(čísel)
     * @return          int[]       pole integeru
     */
    private int[] arrayStringToInteger(String[] strings) {
        int[] integers = new int[strings.length];
        for (int i = 0; i<strings.length; i++){
            integers[i] = Integer.parseInt(strings[i]);
        }
        return integers;

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
        Intent intent = new Intent(NoteActivity.this, TraderActivity.class);
        intent.putExtra("TRADER_ID", traderID);
        startActivity(intent);
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
     * mezi aktivitami.
     * @param item  vybraný item z menu
     * @return      boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.option_menu_trader_show_edit:
                intent = new Intent(NoteActivity.this, TraderEditActivity.class);
                intent.putExtra("TRADER_ID", traderID);
                startActivity(intent);
                finish();
                return true;
            case R.id.option_menu_trader_show_add_note:
                intent = new Intent(NoteActivity.this, NoteNewActivity.class);
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
     * Metoda, která změní aktivitu na Trader Show Activity. Přiloží
     * id obchodníka, aby bylo možné zobrazit příslušného obchodníka.
     * @param view  view příslušné aktivity
     */
    public void goToTraderShowActivity(View view) {
        Intent intent = new Intent(NoteActivity.this, TraderShowActivity.class);
        intent.putExtra("TRADER_ID", traderID);
        startActivity(intent);
        finish();
    }

    /**
     * Procedura, která vykreslí upozornění. Dotáže se
     * uživatele, zda si je opravdu jistý smazáním obchodníka.
     * Pokud ano zavolá proceduru deteleTrader. Pokdu ne, upozorněné se zavře
     * a nic se nestane.
     */
    private void alertDelete(){
        AlertDialog.Builder alert = new AlertDialog.Builder(NoteActivity.this);
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
        Intent intent = new Intent(NoteActivity.this, TraderActivity.class);
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
        NoteActivity thisActivity = NoteActivity.this;

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
