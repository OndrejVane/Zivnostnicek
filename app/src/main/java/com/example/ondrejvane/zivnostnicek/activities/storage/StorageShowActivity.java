package com.example.ondrejvane.zivnostnicek.activities.storage;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.database.ModelHelpers.ItemQuantityDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.database.ModelHelpers.StorageItemDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.helper.Header;
import com.example.ondrejvane.zivnostnicek.session.Logout;
import com.example.ondrejvane.zivnostnicek.model.StorageItem;
import com.example.ondrejvane.zivnostnicek.server.Push;

/**
 * Aktivita, která zobrazuje náhled skladové položky.
 */
public class StorageShowActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //grafické prvky aktivity
    private TextView storageItemName;
    private TextView storageItemQuantity;
    private TextView storageItemNote;

    //pomocné globální proměnné
    private int storageItemID;
    private StorageItemDatabaseHelper storageItemDatabaseHelper;
    private ItemQuantityDatabaseHelper itemQuantityDatabaseHelper;

    /**
     * Meotda, která je volána při startu aktivity a
     * provede inicializaci aktivity.
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_show);
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

        //skryje klávesnici při startu aktivity
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //inicializace aktvity
        initActivity();
    }

    /**
     * Procedura, která inicializuje aktivitu.
     */
    @SuppressLint("SetTextI18n")
    private void initActivity() {

        //pokus o načtení dat předaných z předchozí aktivity
        if (getIntent().hasExtra("STORAGE_ITEM_ID")) {
            storageItemID = Integer.parseInt(getIntent().getExtras().get("STORAGE_ITEM_ID").toString());
        } else {
            storageItemID = 1;
        }

        //inicializace databáze
        storageItemDatabaseHelper = new StorageItemDatabaseHelper(StorageShowActivity.this);
        itemQuantityDatabaseHelper = new ItemQuantityDatabaseHelper(StorageShowActivity.this);

        //inicializace grafických prvků aktivity
        storageItemName = findViewById(R.id.showStorageItemName);
        storageItemQuantity = findViewById(R.id.showStorageItemQuantity);
        storageItemNote = findViewById(R.id.showInputEditTextNote);

        //načtení dat z databáze
        StorageItem storageItem = storageItemDatabaseHelper.getStorageItemById(storageItemID);

        //pokud je záznam v databazi nalezen, tak ho zobrazím
        if (storageItem != null) {
            storageItemName.setText(storageItem.getName());
            storageItemQuantity.setText(itemQuantityDatabaseHelper.getQuantityWithStorageItemId(storageItem.getId()) + " " + storageItem.getUnit());
            storageItemNote.setText(storageItem.getNote());
        }

    }

    /**
     * Procedura, která je volána při stisknutí
     * zpět. Po stisknutí tlačítka je ukočnena tato aktivity
     * a nastartována předchozí aktivity.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.storage_show_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.option_menu_storage_show_edit) {
            Intent intent = new Intent(StorageShowActivity.this, StorageEditActivity.class);
            intent.putExtra("STORAGE_ITEM_ID", storageItemID);
            startActivity(intent);
            finish();
            return true;
        }

        if (id == R.id.option_menu_storage_show_edit_delete) {
            alertDelete();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Procedura, která vykreslí upozornění. Dotáže se
     * uživatele, zda si je opravdu jistý smazáním obchodníka.
     * Pokud ano zavolá proceduru deteleTrader. Pokdu ne, upozorněné se zavře
     * a nic se nestane.
     */
    public void alertDelete() {
        AlertDialog.Builder alert = new AlertDialog.Builder(StorageShowActivity.this);
        alert.setMessage(R.string.delete_storage_item_question).setCancelable(false)
                .setPositiveButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteStorageItem();
                    }
                });
        AlertDialog alertDialog = alert.create();
        alert.setTitle(R.string.logout);
        alertDialog.show();

    }

    /**
     * Procedura, která odstraní záznam z databáze. Po té ukončí aktivitu
     * a nastartuje kativitu pro zobrazení skladových položek.
     */
    private void deleteStorageItem() {
        if (storageItemDatabaseHelper.deleteStorageItemById(storageItemID)) {

            //záloha dat
            Push push = new Push(this);
            push.push();

            Toast.makeText(StorageShowActivity.this, R.string.storage_item_has_been_deleted, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(StorageShowActivity.this, StorageActivity.class);
            startActivity(intent);
            finish();
        }
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

        StorageShowActivity thisActivity = StorageShowActivity.this;
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
