package com.example.ondrejvane.zivnostnicek.activities.note;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
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
import com.example.ondrejvane.zivnostnicek.session.Logout;
import com.example.ondrejvane.zivnostnicek.model.Note;
import com.example.ondrejvane.zivnostnicek.server.Push;

/**
 * Aktivita, která zobrazí vybranou poznámku.
 */
public class NoteShowActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //grafické prvky aktivity
    private RatingBar noteRatingBar;
    private NoteDatabaseHelper noteDatabaseHelper;
    private EditText editTextNoteName, editTextNoteDate, editTextNote;

    //pomocné globální proměnné
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
        setContentView(R.layout.activity_note_show);
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

        //skryje klávesnici při startu aplikace
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //inicializace aktivity
        initActivity();

        //nastavení dat do aktivity
        setTextToActivity();
    }

    /**
     * Procedura, která inicializuje všechny potřebné prvky
     * aktivity.
     */
    private void initActivity() {
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

        noteDatabaseHelper = new NoteDatabaseHelper(NoteShowActivity.this);
        editTextNoteName = findViewById(R.id.textInputEditTextShowNoteTitle);
        editTextNoteDate = findViewById(R.id.textInputEditTextShowNoteDate);
        editTextNote = findViewById(R.id.textInputEditTextShowNote);
        noteRatingBar = findViewById(R.id.traderShowNoteRatingBar);

    }

    /**
     * Procerdura, která nastaví text do všech políček
     * activity, které se načetli z databáze.
     */
    private void setTextToActivity() {
        Note note = noteDatabaseHelper.getNoteById(noteID);
        noteRatingBar.setRating(note.getRating());
        editTextNoteName.setText(note.getTitle());
        editTextNoteDate.setText(note.getDate());
        editTextNote.setText(note.getNote());
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
        Intent intent = new Intent(NoteShowActivity.this, TraderShowActivity.class);
        intent.putExtra("TRADER_ID", traderID);
        startActivity(intent);
        finish();
    }

    /**
     * Metoda, která vytvoří boční navigační menu po
     * zahájení atcitivity.
     *
     * @param menu bočnínavigační menu
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_show_menu, menu);
        return true;
    }

    /**
     * Metoda, která se stará o boční navigační menu a přechod
     * mezi aktivitami
     *
     * @param item vybraný item z menu
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.option_menu_note_show_edit:
                intent = new Intent(NoteShowActivity.this, NoteEditActivity.class);
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


    /**
     * Procedura, která vykreslí upozornění. Dotáže se
     * uživatele, zda si je opravdu jistý smazáním poznámky.
     * Pokud ano zavolá proceduru deteleNote. Pokud ne, upozornění se zavře
     * a nic se nestane.
     */
    private void alertDelete() {
        AlertDialog.Builder alert = new AlertDialog.Builder(NoteShowActivity.this);
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

    /**
     * Procedura, která smaže záznam poznámky v databázi.
     */
    private void deleteNote() {
        boolean result = noteDatabaseHelper.deleteNoteById(noteID);
        if (result) {
            Toast.makeText(this, R.string.note_delete_message, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.note_not_deleted_message, Toast.LENGTH_SHORT).show();
        }

        //pokus o automatickou synchronizaci
        Push push = new Push(this);
        push.push();

        Intent intent = new Intent(NoteShowActivity.this, TraderShowActivity.class);
        intent.putExtra("TRADER_ID", traderID);
        startActivity(intent);
        finish();
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

        NoteShowActivity thisActivity = NoteShowActivity.this;
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
