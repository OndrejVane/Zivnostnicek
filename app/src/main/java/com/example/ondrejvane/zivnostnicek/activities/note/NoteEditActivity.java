package com.example.ondrejvane.zivnostnicek.activities.note;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
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
import com.example.ondrejvane.zivnostnicek.model.database.NoteDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.helper.Header;
import com.example.ondrejvane.zivnostnicek.helper.TextInputLength;
import com.example.ondrejvane.zivnostnicek.session.Logout;
import com.example.ondrejvane.zivnostnicek.model.model_helpers.Note;
import com.example.ondrejvane.zivnostnicek.server.Push;

import java.text.DateFormat;
import java.util.Date;

/**
 * Aktivita, která edituje poznámku obchodníka.
 */
public class NoteEditActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "NoteEdit";

    //grafické prvky aktivity
    private TextInputLayout inputLayoutNoteTitleEdit, inputLayoutNoteEdit;
    private EditText inputNoteTitleEdit, inputNoteEdit;
    private RatingBar noteRatingBarEdit;

    //pomocné globální proměnné
    private NoteDatabaseHelper noteDatabaseHelper;
    private int traderID;
    private int noteID;


    /**
     * Metoda, která se provede při spuštění akctivity a provede nezbytné
     * úkony ke správnému fungování aktivity.
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);
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

        //nstavení načteného textu z db do aktivity
        setTextToActivity();

        //skryje klávesnici při startu aplikace
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }


    /**
     * Procedura, která inicializuje všechny potřebné prvky
     * aktivity.
     */
    private void initActivity() {
        noteDatabaseHelper = new NoteDatabaseHelper(NoteEditActivity.this);
        //načtení id obchodníka poslané z předchozí aktivity
        if (getIntent().hasExtra("TRADER_ID")) {
            traderID = Integer.parseInt(getIntent().getExtras().get("TRADER_ID").toString());
        } else {
            traderID = 1;
        }

        //načtení id poznnámky poslané z předchozí aktivity
        if (getIntent().hasExtra("NOTE_ID")) {
            noteID = Integer.parseInt(getIntent().getExtras().get("NOTE_ID").toString());
        } else {
            noteID = 1;
        }

        inputLayoutNoteTitleEdit = findViewById(R.id.textInputLayoutNoteTitleEdit);
        inputLayoutNoteEdit = findViewById(R.id.textInputLayoutNoteEdit);
        inputNoteTitleEdit = findViewById(R.id.textInputEditTextNoteTitleEdit);
        inputNoteEdit = findViewById(R.id.textInputEditTextNoteEdit);
        noteRatingBarEdit = findViewById(R.id.traderRatingBarEdit);
    }

    /**
     * Procerdura, která nastaví text do všech políček
     * activity, které se načetli z databáze.
     */
    private void setTextToActivity() {
        Note note = noteDatabaseHelper.getNoteById(noteID);
        Log.d(TAG, "Note rating: " + note.getRating());
        noteRatingBarEdit.setRating(note.getRating());
        inputNoteTitleEdit.setText(note.getTitle());
        inputNoteEdit.setText(note.getNote());
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
        Intent intent = new Intent(NoteEditActivity.this, TraderShowActivity.class);
        intent.putExtra("TRADER_ID", traderID);
        intent.putExtra("NOTE_ID", noteID);
        startActivity(intent);
        finish();
    }

    /**
     * Metoda, která načte data od uživatele a zjistí zda jsou validní.
     * Po té edituje poznámku a uloží jí do databáze.
     *
     * @param view view aktivity
     */
    public void updateNoteForm(View view) {

        //kontrola vstupních hodnot
        if (!inputValidation()) {
            return;
        }


        Note note = new Note();
        DateFormat dateFormat1 = DateFormat.getDateInstance();
        Date date = new Date();

        note.setTitle(inputNoteTitleEdit.getText().toString());
        note.setId(noteID);
        note.setNote(inputNoteEdit.getText().toString());
        note.setTraderId(traderID);
        note.setRating(Math.round(noteRatingBarEdit.getRating()));
        note.setDate(dateFormat1.format(date));
        note.setIsDirty(1);
        note.setIsDeleted(0);

        noteDatabaseHelper.updateNoteById(note);

        //pokus o automatickou synchronizaci
        Push push = new Push(NoteEditActivity.this);
        push.push();

        Toast.makeText(this, R.string.note_update_message, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(NoteEditActivity.this, TraderShowActivity.class);
        intent.putExtra("TRADER_ID", traderID);
        intent.putExtra("NOTE_ID", noteID);
        startActivity(intent);
        finish();
    }

    /**
     * Metoda, která kontroluje vstupní hodnoty.
     *
     * @return logická hodnota, která označuje zda validace probělha vpořádku
     */
    private boolean inputValidation() {
        String noteTitle = inputNoteTitleEdit.getText().toString();
        String note = inputNoteTitleEdit.getText().toString();

        if (noteTitle.isEmpty()) {
            String message = getString(R.string.note_title_is_empty);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            inputLayoutNoteTitleEdit.setError(message);
            return false;
        } else if (noteTitle.length() > TextInputLength.NOTE_TITLE_LENGTH) {
            String message = getString(R.string.input_is_too_long);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            inputLayoutNoteTitleEdit.setError(message);
            return false;
        }

        if (note.isEmpty()) {
            String message = getString(R.string.note_is_empty);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            inputLayoutNoteEdit.setError(message);
            return false;
        } else if (note.length() > TextInputLength.NOTE_TEXT_LENGTH) {
            String message = getString(R.string.input_is_too_long);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            inputLayoutNoteEdit.setError(message);
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

        NoteEditActivity thisActivity = NoteEditActivity.this;
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
