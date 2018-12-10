package com.example.ondrejvane.zivnostnicek.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.activities.trader.TraderActivity;
import com.example.ondrejvane.zivnostnicek.activities.trader.TraderEditActivity;
import com.example.ondrejvane.zivnostnicek.activities.trader.TraderShowActivity;
import com.example.ondrejvane.zivnostnicek.database.DatabaseHelper;
import com.example.ondrejvane.zivnostnicek.helper.Header;
import com.example.ondrejvane.zivnostnicek.helper.Logout;
import com.example.ondrejvane.zivnostnicek.model.Note;

public class TraderShowNoteActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private int traderID;
    private int noteID;
    private RatingBar noteRatingBar;
    private DatabaseHelper databaseHelper;
    private EditText editTextNoteName, editTextNoteDate, editTextNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trader_show_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Header header = new Header( navigationView, this);
        header.setTextToHeader();

        //skryje klávesnici při startu aplikace
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        initActivity();

        setTextToActivity();
    }

    private void setTextToActivity() {
        Note note = databaseHelper.getNoteById(noteID);
        noteRatingBar.setRating(note.getRating());
        editTextNoteName.setText(note.getTitle());
        editTextNoteDate.setText(note.getDate());
        editTextNote.setText(note.getNote());
    }

    private void initActivity() {
        traderID = Integer.parseInt(getIntent().getExtras().get("TRADER_ID").toString());
        noteID = Integer.parseInt(getIntent().getExtras().get("NOTE_ID").toString());
        databaseHelper = new DatabaseHelper(TraderShowNoteActivity.this);
        editTextNoteName = findViewById(R.id.textInputEditTextShowNoteTitle);
        editTextNoteDate = findViewById(R.id.textInputEditTextShowNoteDate);
        editTextNote = findViewById(R.id.textInputEditTextShowNote);
        noteRatingBar = findViewById(R.id.traderShowNoteRatingBar);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        Intent intent = new Intent(TraderShowNoteActivity.this, TraderNoteActivity.class);
        intent.putExtra("TRADER_ID", traderID);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_show_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.option_menu_note_show_edit:
                intent = new Intent(TraderShowNoteActivity.this, TraderEditNoteActivity.class);
                intent.putExtra("TRADER_ID", traderID);
                intent.putExtra("NOTE_ID", noteID);
                startActivity(intent);
                finish();
                return true;
            case R.id.option_menu_note_show_delete:
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
                Intent home = new Intent(TraderShowNoteActivity.this, HomeActivity.class);
                startActivity(home);
                finish();
                break;

            case R.id.nav_income:
                Intent income = new Intent(TraderShowNoteActivity.this, IncomeActivity.class);
                startActivity(income);
                finish();
                break;

            case R.id.nav_expense:
                Intent expense = new Intent(TraderShowNoteActivity.this, ExpenseActivity.class);
                startActivity(expense);
                finish();
                break;

            case R.id.nav_traders:
                Intent traders = new Intent(TraderShowNoteActivity.this, TraderActivity.class);
                startActivity(traders);
                finish();
                break;

            case R.id.nav_storage:
                Intent storage = new Intent(TraderShowNoteActivity.this, StorageActivity.class);
                startActivity(storage);
                finish();
                break;

            case R.id.nav_info:
                Intent info = new Intent(TraderShowNoteActivity.this, InfoActivity.class);
                startActivity(info);
                finish();
                break;

            case R.id.nav_sync:
                Intent sync = new Intent(TraderShowNoteActivity.this, SynchronizationActivity.class);
                startActivity(sync);
                finish();
                break;

            case R.id.nav_logout:
                Logout logout = new Logout(TraderShowNoteActivity.this, this);
                logout.logout();
                break;

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void alertDelete(){
        AlertDialog.Builder alert = new AlertDialog.Builder(TraderShowNoteActivity.this);
        alert.setMessage(R.string.note_delete_question).setCancelable(false)
                .setPositiveButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteNote();
                    }
                });
        AlertDialog alertDialog = alert.create();
        alert.setTitle(R.string.logout);
        alertDialog.show();

    }

    private void deleteNote(){
        boolean result = databaseHelper.deleteNoteById(noteID);
        if(result){
            Toast.makeText(this, R.string.note_delete_message, Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, R.string.note_not_deleted_message, Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(TraderShowNoteActivity.this, TraderNoteActivity.class);
        intent.putExtra("TRADER_ID", traderID);
        startActivity(intent);
        finish();
    }
}
