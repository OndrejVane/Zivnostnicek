package com.example.ondrejvane.zivnostnicek.activities.storage;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.ondrejvane.zivnostnicek.activities.HomeActivity;
import com.example.ondrejvane.zivnostnicek.activities.SynchronizationActivity;
import com.example.ondrejvane.zivnostnicek.activities.expense.ExpenseActivity;
import com.example.ondrejvane.zivnostnicek.activities.income.IncomeActivity;
import com.example.ondrejvane.zivnostnicek.activities.info.InfoActivity;
import com.example.ondrejvane.zivnostnicek.activities.trader.TraderActivity;
import com.example.ondrejvane.zivnostnicek.activities.trader.TraderShowActivity;
import com.example.ondrejvane.zivnostnicek.database.StorageItemDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.helper.Header;
import com.example.ondrejvane.zivnostnicek.helper.Logout;
import com.example.ondrejvane.zivnostnicek.model.StorageItem;

public class StorageShowActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private int storageItemID;
    private StorageItemDatabaseHelper storageItemDatabaseHelper;
    private TextView storageItemName;
    private TextView storageItemQuantity;
    private TextView storageItemNote;

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

        Header header = new Header( navigationView, this);
        header.setTextToHeader();

        //skryje klávesnici při startu aktivity
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        initActivity();
    }

    private void initActivity() {
        storageItemID = Integer.parseInt(getIntent().getExtras().get("STORAGE_ITEM_ID").toString());
        storageItemDatabaseHelper = new StorageItemDatabaseHelper(StorageShowActivity.this);
        storageItemName = findViewById(R.id.showStorageItemName);
        storageItemQuantity = findViewById(R.id.showStorageItemQuantity);
        storageItemNote = findViewById(R.id.showInputEditTextNote);
        StorageItem storageItem = storageItemDatabaseHelper.getStorageItemById(storageItemID);
        //pokud je záznam v databazi nalezen, tak ho zobrazím
        if(storageItem != null){
            storageItemName.setText(storageItem.getName());
            storageItemQuantity.setText(Float.toString(storageItem.getQuantity()) + " "+ storageItem.getUnit());
            storageItemNote.setText(storageItem.getNote());
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

        Intent intent = new Intent(StorageShowActivity.this, StorageActivity.class);
        startActivity(intent);
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
            //TODO přepnout do aktivity k editaci záznamu
            return true;
        }

        if(id == R.id.option_menu_storage_show_edit_delete){
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
    public void alertDelete(){
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

    private void deleteStorageItem(){
        if(storageItemDatabaseHelper.deleteStorageItembyId(storageItemID)){
            Toast.makeText(StorageShowActivity.this, R.string.storage_item_has_been_deleted, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(StorageShowActivity.this, StorageActivity.class);
            startActivity(intent);
            finish();
        }
    }
    /**
     * Metoda, která se stará o hlavní navigační menu aplikace
     * a přechod mezi hlavními aktivitami.
     * @param item  vybraná položka v menu
     * @return  boolean
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        StorageShowActivity thisActivity = StorageShowActivity.this;

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
