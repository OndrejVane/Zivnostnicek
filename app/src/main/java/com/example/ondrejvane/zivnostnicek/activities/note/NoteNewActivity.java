package com.example.ondrejvane.zivnostnicek.activities.note;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.example.ondrejvane.zivnostnicek.activities.trader.TraderShowActivity;
import com.example.ondrejvane.zivnostnicek.database.ModelHelpers.NoteDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.helper.Header;
import com.example.ondrejvane.zivnostnicek.helper.TextInputLength;
import com.example.ondrejvane.zivnostnicek.session.Logout;
import com.example.ondrejvane.zivnostnicek.model.Note;
import com.example.ondrejvane.zivnostnicek.server.Push;

import java.text.DateFormat;
import java.util.Date;

/**
 * Aktivita, která přidává poznámku k vybranému obchodníkovi.
 */
public class NoteNewActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //grafické prvky aktivity
    private TextInputLayout inputLayoutNoteTitle, inputLayoutNote;
    private EditText inputNoteTitle, inputNote;
    private NoteDatabaseHelper noteDatabaseHelper;
    private RatingBar ratingBar;

    //pomocné globální proměnné
    private int traderID;

    /**
     * Metoda, která se provede při spuštění akctivity a provede nezbytné
     * úkony ke správnému fungování aktivity.
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_new);
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

        //incializace aktivity
        initActivity();

        //skryje klávesnici při startu aplikace
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    /**
     * Procedura, která inicializuje všechny potřebné prvky
     * aktivity.
     */
    private void initActivity() {
        noteDatabaseHelper = new NoteDatabaseHelper(NoteNewActivity.this);
        //načtení id obchodnía z předchozí aktivity
        if (getIntent().hasExtra("TRADER_ID")) {
            traderID = Integer.parseInt(getIntent().getExtras().get("TRADER_ID").toString());
        } else {
            traderID = 1;
        }

        inputLayoutNoteTitle = findViewById(R.id.textInputLayoutNoteTitle);
        inputLayoutNote = findViewById(R.id.textInputLayoutNote);
        inputNoteTitle = findViewById(R.id.textInputEditTextNoteTitle);
        inputNote = findViewById(R.id.textInputEditTextNote);
        ratingBar = findViewById(R.id.traderRatingBar);
    }

    /**
     * Procedura, která po stisknutí tlačítka zpět nastartuje příslušnou
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
        Intent intent = new Intent(NoteNewActivity.this, TraderShowActivity.class);
        intent.putExtra("TRADER_ID", traderID);
        startActivity(intent);
        finish();
    }

    /**
     * Procedura, která načte vložená data od uživatele. Zkontroluje, zda jsou
     * data validní a následně je vloží do databáze.
     *
     * @param view view aktivity
     */
    public void submitNoteForm(View view) {

        //kontrola vstupních hodnot
        if(!inputValidation()){
            return;
        }


        Note note = new Note();
        DateFormat dateFormat1 = DateFormat.getDateInstance();
        Date date = new Date();

        note.setTitle(inputNoteTitle.getText().toString());
        note.setNote(inputNote.getText().toString());
        note.setTraderId(traderID);
        note.setRating(Math.round(ratingBar.getRating()));
        note.setDate(dateFormat1.format(date));
        note.setIsDirty(1);
        note.setIsDeleted(0);

        //uložení záznamu do databáze
        noteDatabaseHelper.addNote(note, false);

        //pokus o automatickou synchronizaci
        Push push = new Push(NoteNewActivity.this);
        push.push();

        Toast.makeText(this, R.string.note_has_been_added, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(NoteNewActivity.this, TraderShowActivity.class);
        intent.putExtra("TRADER_ID", traderID);
        startActivity(intent);
        finish();
    }

    /**
     * Metoda, která kontroluje vstupní hodnoty.
     *
     * @return logická hodnota, která označuje zda validace probělha vpořádku
     */
    private boolean inputValidation() {
        String noteTitle = inputNoteTitle.getText().toString();
        String note = inputNoteTitle.getText().toString();

        if (noteTitle.isEmpty()) {
            String message = getString(R.string.note_title_is_empty);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            inputLayoutNoteTitle.setError(message);
            return false;
        } else if (noteTitle.length() > TextInputLength.NOTE_TITLE_LENGTH) {
            String message = getString(R.string.input_is_too_long);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            inputLayoutNoteTitle.setError(message);
            return false;
        }

        if (note.isEmpty()) {
            String message = getString(R.string.note_is_empty);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            inputLayoutNote.setError(message);
            return false;
        } else if (note.length() > TextInputLength.NOTE_TEXT_LENGTH) {
            String message = getString(R.string.input_is_too_long);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            inputLayoutNote.setError(message);
            return false;
        }
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

        NoteNewActivity thisActivity = NoteNewActivity.this;
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
