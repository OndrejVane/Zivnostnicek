package com.example.ondrejvane.zivnostnicek.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.ondrejvane.zivnostnicek.activities.trader.TraderActivity;
import com.example.ondrejvane.zivnostnicek.activities.trader.TraderEditActivity;
import com.example.ondrejvane.zivnostnicek.activities.trader.TraderShowActivity;
import com.example.ondrejvane.zivnostnicek.database.DatabaseHelper;
import com.example.ondrejvane.zivnostnicek.helper.Header;
import com.example.ondrejvane.zivnostnicek.helper.ListViewAdapter;
import com.example.ondrejvane.zivnostnicek.helper.Logout;

public class TraderNoteActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView listViewNote;
    private ListViewAdapter listViewAdapter;
    private int traderID;
    private DatabaseHelper databaseHelper;
    private RatingBar ratingBar;
    private TextView averageRating;
    private String[] noteTitle;
    private String[] noteRating;
    private int[] ID;
    private int globalPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trader_note);
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

        listViewNote.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                globalPosition = position;
                Intent intent = new Intent(TraderNoteActivity.this, TraderShowNoteActivity.class);
                intent.putExtra("NOTE_ID", ID[globalPosition]);
                intent.putExtra("TRADER_ID", traderID);
                startActivity(intent);
                finish();
            }
        });

    }

    private void initActivity() {
        traderID = Integer.parseInt(getIntent().getExtras().get("TRADER_ID").toString());
        databaseHelper = new DatabaseHelper(TraderNoteActivity.this);
        ratingBar = findViewById(R.id.noteShowRatingBar);
        averageRating = findViewById(R.id.textViewNoteAvarageRating);
        String temp[][];
        temp = databaseHelper.getNotesData(traderID);
        ID = arrayStringToInteger(temp[0]);
        noteTitle = temp[1];
        noteRating = temp[2];

        listViewNote = findViewById(R.id.listViewTrader);
        listViewAdapter = new ListViewAdapter(this, noteTitle, noteRating);
        listViewNote.setAdapter(listViewAdapter);

        ratingBar.setRating(countAverageRating());
        averageRating.append(Float.toString(countAverageRating()));
    }

    private float countAverageRating() {
        float sum = 0;
        float avarageRating;
        for (int i = 0; i<noteRating.length; i++){
            sum = sum + Float.parseFloat(noteRating[i]);
        }
        avarageRating = sum/noteRating.length;
        return (float) (Math.round(avarageRating * Math.pow(10, 2)) / Math.pow(10, 2));
    }


    private int[] arrayStringToInteger(String[] strings) {
        int[] integers = new int[strings.length];
        for (int i = 0; i<strings.length; i++){
            integers[i] = Integer.parseInt(strings[i]);
        }
        return integers;

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        Intent intent = new Intent(TraderNoteActivity.this, TraderActivity.class);
        intent.putExtra("TRADER_ID", traderID);
        startActivity(intent);
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
                intent = new Intent(TraderNoteActivity.this, TraderEditActivity.class);
                intent.putExtra("TRADER_ID", traderID);
                startActivity(intent);
                finish();
                return true;
            case R.id.option_menu_trader_show_add_note:
                intent = new Intent(TraderNoteActivity.this, TraderNewNoteActivity.class);
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
                Intent home = new Intent(TraderNoteActivity.this, HomeActivity.class);
                startActivity(home);
                finish();
                break;

            case R.id.nav_income:
                Intent income = new Intent(TraderNoteActivity.this, IncomeActivity.class);
                startActivity(income);
                finish();
                break;

            case R.id.nav_expense:
                Intent expense = new Intent(TraderNoteActivity.this, ExpenseActivity.class);
                startActivity(expense);
                finish();
                break;

            case R.id.nav_traders:
                Intent traders = new Intent(TraderNoteActivity.this, TraderActivity.class);
                startActivity(traders);
                finish();
                break;

            case R.id.nav_storage:
                Intent storage = new Intent(TraderNoteActivity.this, StorageActivity.class);
                startActivity(storage);
                finish();
                break;

            case R.id.nav_info:
                Intent info = new Intent(TraderNoteActivity.this, InfoActivity.class);
                startActivity(info);
                finish();
                break;

            case R.id.nav_sync:
                Intent sync = new Intent(TraderNoteActivity.this, SynchronizationActivity.class);
                startActivity(sync);
                finish();
                break;

            case R.id.nav_logout:
                Logout logout = new Logout(TraderNoteActivity.this, this);
                logout.logout();
                break;

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void goToTraderShowActivity(View view) {
        Intent intent = new Intent(TraderNoteActivity.this, TraderShowActivity.class);
        intent.putExtra("TRADER_ID", traderID);
        startActivity(intent);
        finish();
    }

    public void alertDelete(){
        AlertDialog.Builder alert = new AlertDialog.Builder(TraderNoteActivity.this);
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
        Intent intent = new Intent(TraderNoteActivity.this, TraderActivity.class);
        startActivity(intent);
        finish();
    }
}
