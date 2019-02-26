package com.example.ondrejvane.zivnostnicek.activities.note;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.ondrejvane.zivnostnicek.database.NoteDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.helper.Header;
import com.example.ondrejvane.zivnostnicek.helper.InputValidation;
import com.example.ondrejvane.zivnostnicek.helper.Logout;
import com.example.ondrejvane.zivnostnicek.model.Note;

import java.text.DateFormat;
import java.util.Date;

/**
 * Aktivita, která edituje poznámku obchodníka.
 */
public class NoteEditActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private int traderID;
    private int noteID;
    private TextInputLayout inputLayoutNoteTitleEdit, inputLayoutNoteEdit;
    private EditText inputNoteTitleEdit, inputNoteEdit;
    private NoteDatabaseHelper noteDatabaseHelper;
    private RatingBar noteRatingBarEdit;

    /**
     * Metoda, která se provede při spuštění akctivity a provede nezbytné
     * úkony ke správnému fungování aktivity.
     * @param savedInstanceState    savedInstanceState
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

        Header header = new Header( navigationView);
        header.setTextToHeader();

        initActivity();

        setTextToActivity();

        //skryje klávesnici při startu aplikace
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    /**
     * Procerdura, která nastaví text do všech políček
     * activity, které se načetli z databáze.
     */
    private void setTextToActivity() {
        Note note = noteDatabaseHelper.getNoteById(noteID);
        System.out.println("Note rating: "+ note.getRating());
        noteRatingBarEdit.setRating(note.getRating());
        inputNoteTitleEdit.setText(note.getTitle());
        inputNoteEdit.setText(note.getNote());
    }

    /**
     * Procedura, která inicializuje všechny potřebné prvky
     * aktivity.
     */
    private void initActivity() {
        noteDatabaseHelper = new NoteDatabaseHelper(NoteEditActivity.this);
        traderID = Integer.parseInt(getIntent().getExtras().get("TRADER_ID").toString());
        noteID = Integer.parseInt(getIntent().getExtras().get("NOTE_ID").toString());
        inputLayoutNoteTitleEdit = findViewById(R.id.textInputLayoutNoteTitleEdit);
        inputLayoutNoteEdit = findViewById(R.id.textInputLayoutNoteEdit);
        inputNoteTitleEdit = findViewById(R.id.textInputEditTextNoteTitleEdit);
        inputNoteEdit = findViewById(R.id.textInputEditTextNoteEdit);
        noteRatingBarEdit = findViewById(R.id.traderRatingBarEdit);
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
     * @param view
     */
    public void updateNoteForm(View view) {
        if(!InputValidation.validateNote(inputNoteTitleEdit.getText().toString())){
            String message = getString(R.string.note_title_is_empty);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            inputLayoutNoteTitleEdit.setError(message);
            return;
        }

        if(!InputValidation.validateNote(inputNoteEdit.getText().toString())){
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
        note.setIsDirty(1);
        note.setIsDeleted(0);

        noteDatabaseHelper.updateNoteById(note);

        Toast.makeText(this, R.string.note_update_message, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(NoteEditActivity.this, TraderShowActivity.class);
        intent.putExtra("TRADER_ID", traderID);
        intent.putExtra("NOTE_ID", noteID);
        startActivity(intent);
        finish();
    }

    /**
     * Metoda, která se stará o hlavní navigační menu aplikace.
     * @param item  vybraná položka v menu
     * @return      boolean
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //id vybrané položky v menu
        int id = item.getItemId();

        NoteEditActivity thisActivity = NoteEditActivity.this;
        Intent newIntent;

        //inicializace třídy menu, kde jsou definovány jednotlivé aktivity
        com.example.ondrejvane.zivnostnicek.menu.Menu menu = new com.example.ondrejvane.zivnostnicek.menu.Menu(thisActivity);
        newIntent = menu.getMenu(id);

        //pokud jedná o nějakou aktivitu, tak se spustí
        if(newIntent != null){
            startActivity(menu.getMenu(id));
            finish();
        }else {
            //pokud byla stisknuta položka odhlášení
            Logout logout = new Logout(thisActivity, this);
            logout.logout();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
