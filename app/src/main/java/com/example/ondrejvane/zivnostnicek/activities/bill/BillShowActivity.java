package com.example.ondrejvane.zivnostnicek.activities.bill;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.adapters.ListViewBillItemAdapter;
import com.example.ondrejvane.zivnostnicek.database.BillDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.database.ItemQuantityDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.database.StorageItemDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.database.TraderDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.database.TypeBillDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.helper.Header;
import com.example.ondrejvane.zivnostnicek.session.Logout;
import com.example.ondrejvane.zivnostnicek.server.Push;
import com.example.ondrejvane.zivnostnicek.utilities.PictureUtility;
import com.example.ondrejvane.zivnostnicek.model.Bill;
import com.example.ondrejvane.zivnostnicek.model.ItemQuantity;
import com.example.ondrejvane.zivnostnicek.model.StorageItem;
import com.example.ondrejvane.zivnostnicek.model.Trader;
import com.example.ondrejvane.zivnostnicek.model.TypeBill;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;

import java.util.ArrayList;

/**
 * Aktivity pro zobrazení faktury.
 */
public class BillShowActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "BillShowActivity";

    //ptvky aktivity
    private EditText textViewBillName;
    private EditText textViewBillAmount;
    private EditText textViewBillVAT;
    private EditText textViewBillDate;
    private EditText textViewBillTrader;
    private EditText textViewBillType;
    private TextView textViewBillPhoto;
    private ImageView photoViewBillPhoto;

    //pomocné globální proměnné
    private ExpandableHeightListView expandableListView;
    private BillDatabaseHelper billDatabaseHelper;
    private TraderDatabaseHelper traderDatabaseHelper;
    private ItemQuantityDatabaseHelper quantityDatabaseHelper;
    private StorageItemDatabaseHelper storageItemDatabaseHelper;
    private TypeBillDatabaseHelper typeBillDatabaseHelper;
    private boolean isPictureFound;
    private Bill bill;
    private boolean isExpense;
    private int billId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_show);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.d(TAG, "Activity is starting");

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Header header = new Header(navigationView);
        header.setTextToHeader();

        //skryje klávesnici při startu aplikace
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        initActivity();

        Log.d(TAG, "Activity is successfully started");
    }

    /**
     * Inicializace aktivity
     */
    private void initActivity() {

        //kontrola a získání dat z extra
        if (getIntent().hasExtra("BILL_ID")) {
            billId = Integer.parseInt(getIntent().getExtras().get("BILL_ID").toString());
        } else {
            billId = 1;
        }

        //kontrola a získání dat z extra
        if (getIntent().hasExtra("IS_EXPENSE")) {
            isExpense = getIntent().getExtras().getBoolean("IS_EXPENSE", false);
        } else {
            isExpense = false;
        }


        //nastaví příslušné texty, podle toho, jestli se jedná o příjem nebo výdaj
        setTextToActivity();

        //inicializace textových polí
        textViewBillName = findViewById(R.id.textIncomeShowName);
        textViewBillAmount = findViewById(R.id.textIncomeShowAmount);
        textViewBillVAT = findViewById(R.id.textIncomeShowVAT);
        textViewBillDate = findViewById(R.id.textIncomeShowDate);
        textViewBillTrader = findViewById(R.id.textIncomeShowTrader);
        photoViewBillPhoto = findViewById(R.id.photoViewIncomeShow);
        textViewBillPhoto = findViewById(R.id.textViewIncomeShow);
        expandableListView = findViewById(R.id.listViewIncomeStorageItemShow);
        textViewBillType = findViewById(R.id.textIncomeShowBillType);


        //inicializace databáze
        billDatabaseHelper = new BillDatabaseHelper(BillShowActivity.this);
        traderDatabaseHelper = new TraderDatabaseHelper(BillShowActivity.this);
        quantityDatabaseHelper = new ItemQuantityDatabaseHelper(BillShowActivity.this);
        storageItemDatabaseHelper = new StorageItemDatabaseHelper(BillShowActivity.this);
        typeBillDatabaseHelper = new TypeBillDatabaseHelper(BillShowActivity.this);

        //načtení potřebných údajů z databáze z databáze a zobrazení v aktivitě
        bill = billDatabaseHelper.getBillById(billId);
        if (bill.getTraderId() != -1) {
            Trader trader = traderDatabaseHelper.getTraderById(bill.getTraderId());
            textViewBillTrader.setText(trader.getName());
        } else {
            textViewBillTrader.setText(getString(R.string.not_selected));
        }

        //pokud je přířazen obchodník, tak ho zobrazím
        if (bill.getTypeId() != -1) {
            TypeBill typeBill = typeBillDatabaseHelper.getTypeBillById(bill.getTypeId());
            textViewBillType.setText(typeBill.getName());
        } else {
            textViewBillType.setText(getString(R.string.not_selected));
        }

        textViewBillName.setText(bill.getName());
        textViewBillAmount.setText(Float.toString(bill.getAmount()));
        textViewBillVAT.setText(Integer.toString(bill.getVAT()));
        textViewBillDate.setText(bill.getDate());

        if (bill.getPhoto() != null && PictureUtility.tryLoadImageFromStorage(bill.getPhoto())) {
            //načtení obrázku z uložiště zařízení
            photoViewBillPhoto.setImageBitmap(PictureUtility.loadImageFromStorage(bill.getPhoto()));
            isPictureFound = true;

        } else {
            textViewBillPhoto.setText(getText(R.string.picture_is_not_available));
            isPictureFound = false;
        }

        showBillItemsToActivity(bill.getId());
    }

    /**
     * Nastavení titulku do aktivity podle příjmu nebo výdaje.
     */
    private void setTextToActivity() {
        if (isExpense) {
            setTitle(getString(R.string.title_activity_expense_show));
        }
    }

    /**
     * Procedura, která zobrazí všechny položky faktruy do listu.
     *
     * @param billId id faktury
     */
    private void showBillItemsToActivity(int billId) {
        ArrayList<ItemQuantity> arrayList = quantityDatabaseHelper.getItemQuantityByBillId(billId);
        String[] billItemName;
        float[] billItemQuantity;
        String[] billItemUnit;
        StorageItem storageItemTemp;
        //pokud obsahuje faktura nějaké položky
        if (arrayList.size() != 0) {
            billItemName = new String[arrayList.size()];
            billItemQuantity = new float[arrayList.size()];
            billItemUnit = new String[arrayList.size()];
            for (int i = 0; i < arrayList.size(); i++) {
                storageItemTemp = storageItemDatabaseHelper.getStorageItemById((int) arrayList.get(i).getStorageItemId());
                billItemName[i] = storageItemTemp.getName();
                billItemQuantity[i] = arrayList.get(i).getQuantity();
                billItemUnit[i] = storageItemTemp.getUnit();
            }
        } else {
            //pokud neobsahuje žádné položky
            billItemName = new String[]{getString(R.string.no_bill_items)};
            billItemQuantity = new float[1];
            billItemUnit = new String[]{""};
        }

        //nastavení adapteru do list view pro zobrazení
        ListViewBillItemAdapter listViewItemAdapter = new ListViewBillItemAdapter(this, billItemName, billItemQuantity, billItemUnit);
        //skryje obrázek koše
        listViewItemAdapter.isTrashHidden(true);
        expandableListView.setAdapter(listViewItemAdapter);
        expandableListView.setExpanded(true);
    }

    /**
     * Metoda, která po stisknutí text view nastartuje novou
     * aktivittu pro zobrazení obrázku na celou obrazovku.
     *
     * @param view view aktivity
     */
    public void showPicture(View view) {
        if (isPictureFound) {
            Intent intent = new Intent(BillShowActivity.this, ShowPictureActivity.class);
            Log.d(TAG, "Start activity Show PictureActivity");
            Log.d(TAG, "URI: " + bill.getPhoto());
            intent.putExtra("BITMAP_PATH", bill.getPhoto());
            startActivity(intent);
        } else {
            String message = getString(R.string.picture_is_not_available);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Metoda, která se zavolá pokáždé, když je stisknuto tlačítko zpět.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        Intent intent = new Intent(BillShowActivity.this, BillActivity.class);
        intent.putExtra("IS_EXPENSE", isExpense);
        //startActivity(intent);
        finish();
    }

    /**
     * Metoda, která vykresluje boční navigační menu.
     *
     * @param menu menu
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.income_show_menu, menu);
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
        switch (item.getItemId()) {
            case R.id.option_menu_income_show_edit:

                Intent intent = new Intent(BillShowActivity.this, BillEditActivity.class);
                intent.putExtra("BILL_ID", billId);
                intent.putExtra("IS_EXPENSE", isExpense);
                startActivity(intent);
                finish();
                return true;
            case R.id.option_menu_income_show_delete:
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
        AlertDialog.Builder alert = new AlertDialog.Builder(BillShowActivity.this);
        alert.setMessage(R.string.bill_delete_question).setCancelable(false)
                .setPositiveButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteBill();
                    }
                });
        AlertDialog alertDialog = alert.create();
        alert.setTitle(R.string.logout);
        alertDialog.show();

    }

    /**
     * Procedura, která odstraní fakturu z databáze.
     */
    private void deleteBill() {
        if (billDatabaseHelper.deleteBillWithId(bill.getId())) {

            //vypis uživateli o úspěšném smazání záznamu
            String message = getString(R.string.bill_has_been_deleted);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(BillShowActivity.this, BillActivity.class);

            //pokus o automatickou synchronizaci
            Push push = new Push(BillShowActivity.this);
            push.push();

            intent.putExtra("IS_EXPENSE", isExpense);
            startActivity(intent);
            finish();
        } else {
            //vypis uživateli o neúspěšném smazání záznamu
            String message = getString(R.string.bill_has_not_been_deleted);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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

        BillShowActivity thisActivity = BillShowActivity.this;
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
