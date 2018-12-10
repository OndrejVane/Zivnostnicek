package com.example.ondrejvane.zivnostnicek.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
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
import com.example.ondrejvane.zivnostnicek.activities.trader.TraderShowActivity;
import com.example.ondrejvane.zivnostnicek.database.DatabaseHelper;
import com.example.ondrejvane.zivnostnicek.helper.Header;
import com.example.ondrejvane.zivnostnicek.helper.Logout;
import com.example.ondrejvane.zivnostnicek.model.Note;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TraderEditNoteActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private int traderID;
    private int noteID;
    private TextInputLayout inputLayoutNoteTitleEdit, inputLayoutNoteEdit;
    private EditText inputNoteTitleEdit, inputNoteEdit;
    private DatabaseHelper databaseHelper;
    private RatingBar noteRatingBarEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trader_edit_note);
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

        //skryje klávesnici při startu aplikace
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    private void setTextToActivity() {
        Note note = databaseHelper.getNoteById(noteID);
        System.out.println("Note rating: "+ note.getRating());
        noteRatingBarEdit.setRating(note.getRating());
        inputNoteTitleEdit.setText(note.getTitle());
        inputNoteEdit.setText(note.getNote());
    }

    private void initActivity() {
        databaseHelper = new DatabaseHelper(TraderEditNoteActivity.this);
        traderID = Integer.parseInt(getIntent().getExtras().get("TRADER_ID").toString());
        noteID = Integer.parseInt(getIntent().getExtras().get("NOTE_ID").toString());
        inputLayoutNoteTitleEdit = findViewById(R.id.textInputLayoutNoteTitleEdit);
        inputLayoutNoteEdit = findViewById(R.id.textInputLayoutNoteEdit);
        inputNoteTitleEdit = findViewById(R.id.textInputEditTextNoteTitleEdit);
        inputNoteEdit = findViewById(R.id.textInputEditTextNoteEdit);
        noteRatingBarEdit = findViewById(R.id.traderRatingBarEdit);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        Intent intent = new Intent(TraderEditNoteActivity.this, TraderNoteActivity.class);
        intent.putExtra("TRADER_ID", traderID);
        intent.putExtra("NOTE_ID", noteID);
        startActivity(intent);
        finish();
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){

            case R.id.nav_home:
                Intent home = new Intent(TraderEditNoteActivity.this, HomeActivity.class);
                startActivity(home);
                finish();
                break;

            case R.id.nav_income:
                Intent income = new Intent(TraderEditNoteActivity.this, IncomeActivity.class);
                startActivity(income);
                finish();
                break;

            case R.id.nav_expense:
                Intent expense = new Intent(TraderEditNoteActivity.this, ExpenseActivity.class);
                startActivity(expense);
                finish();
                break;

            case R.id.nav_traders:
                Intent traders = new Intent(TraderEditNoteActivity.this, TraderActivity.class);
                startActivity(traders);
                finish();
                break;

            case R.id.nav_storage:
                Intent storage = new Intent(TraderEditNoteActivity.this, StorageActivity.class);
                startActivity(storage);
                finish();
                break;

            case R.id.nav_info:
                Intent info = new Intent(TraderEditNoteActivity.this, InfoActivity.class);
                startActivity(info);
                finish();
                break;

            case R.id.nav_sync:
                Intent sync = new Intent(TraderEditNoteActivity.this, SynchronizationActivity.class);
                startActivity(sync);
                finish();
                break;

            case R.id.nav_logout:
                Logout logout = new Logout(TraderEditNoteActivity.this, this);
                logout.logout();
                break;

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean validateNoteTitle(){
        if(inputNoteTitleEdit.getText().toString().isEmpty()){
            return false;
        }
        return true;
    }

    public boolean validateNote(){
        if(inputNoteEdit.getText().toString().isEmpty()){
            return false;
        }
        return true;
    }

    public void updateNoteForm(View view) {
        if(!validateNoteTitle()){
            String message = getString(R.string.note_title_is_empty);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            inputLayoutNoteTitleEdit.setError(message);
            return;
        }

        if(!validateNote()){
            String message = getString(R.string.note_is_empty);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            inputLayoutNoteEdit.setError(message);
            return;
        }

        Note note = new Note();
        DateFormat dateFormat1 = DateFormat.getDateInstance();
        Date date = new Date();

        note.setTitle(inputNoteTitleEdit.getText().toString());
        note.setId(noteID);
        note.setNote(inputNoteEdit.getText().toString());
        note.setTrader_id(traderID);
        note.setRating(Math.round(noteRatingBarEdit.getRating()));
        note.setDate(dateFormat1.format(date));

        databaseHelper.updateNoteById(note);

        Toast.makeText(this, R.string.note_update_message, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(TraderEditNoteActivity.this, TraderNoteActivity.class);
        intent.putExtra("TRADER_ID", traderID);
        intent.putExtra("NOTE_ID", noteID);
        startActivity(intent);
        finish();
    }
}
