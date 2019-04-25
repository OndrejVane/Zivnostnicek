package com.example.ondrejvane.zivnostnicek.activities.bill;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.activities.home.HomeActivity;
import com.example.ondrejvane.zivnostnicek.adapters.ListViewBillAdapter;
import com.example.ondrejvane.zivnostnicek.model.database.BillDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.model.database.TypeBillDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.helper.TextInputLength;
import com.example.ondrejvane.zivnostnicek.model.model_helpers.BillBox;
import com.example.ondrejvane.zivnostnicek.helper.Header;
import com.example.ondrejvane.zivnostnicek.session.Logout;
import com.example.ondrejvane.zivnostnicek.helper.Settings;
import com.example.ondrejvane.zivnostnicek.session.UserInformation;
import com.example.ondrejvane.zivnostnicek.model.model_helpers.TypeBill;
import com.example.ondrejvane.zivnostnicek.server.Push;

import java.util.ArrayList;

import top.defaults.colorpicker.ColorPickerPopup;

/**
 * Aktivita pro zobrazení všech příjmu nebo výdajů.
 */
public class BillActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "BillActivity";

    //proměnná, která udává, zda jsou zobrazovány příjmy neb výdaje
    private boolean isExpense;

    //prvky aktivity
    private Spinner spinnerBillYear;
    private Spinner spinnerBillMonth;
    private ListView listViewBill;


    //pomocné globální proměnné
    private int[] ID;
    private int pickedYear = -1;
    private int pickedMonth = -1;
    private boolean firstPickMonth = true;

    /**
     * Metoda, která se provede při spuštění akctivity a provede nezbytné
     * úkony ke správnému fungování aktivity.
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "Starting activity");

        setContentView(R.layout.activity_bill);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //po stisknutí na floating button spuštění aktivity pro přídání nové faktury
        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BillActivity.this, BillNewActivity.class);
                intent.putExtra("IS_EXPENSE", isExpense);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //nastavení headeru do aktivity
        Header header = new Header(navigationView);
        header.setTextToHeader();

        //inicializace aktivity
        initActivity();

        Log.d(TAG, "Successfully initialized");

        //click listener, který spustí aktivitu pro náhled faktury po kliknutí na položku v listu
        listViewBill.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int incomeId = ID[position];
                if (incomeId != -1) {
                    Log.d(TAG, "Picked bill " + position);
                    Intent intent = new Intent(BillActivity.this, BillShowActivity.class);
                    intent.putExtra("BILL_ID", incomeId);
                    intent.putExtra("IS_EXPENSE", isExpense);
                    startActivity(intent);
                    finish();
                }
            }
        });


        //po vybrání roku ze spinneru se provede následující kod (vyhledání po)
        spinnerBillYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                    pickedYear = Integer.parseInt(spinnerBillYear.getSelectedItem().toString());
                } else {
                    pickedYear = -1;
                }

                readDataFromDatabase();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //po vybrání měsíce ze spinneru se provede následující kod (vyhledání po)
        spinnerBillMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //zabránění načítání dat z databáze vícekrát při inicializaci aktivity
                if (firstPickMonth) {
                    firstPickMonth = false;
                    return;
                }

                if (position != 0) {
                    pickedMonth = position;
                } else {
                    pickedMonth = -1;
                }

                readDataFromDatabase();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    /**
     * Funkce, která inicializuje aktivitu.
     */
    private void initActivity() {

        //zjištění, jestli je přiložené nějaké extra
        if (getIntent().hasExtra("IS_EXPENSE")) {
            isExpense = getIntent().getExtras().getBoolean("IS_EXPENSE", false);
        } else {
            isExpense = false;
        }
        //pokud se jedná o výdaj, tak nastavím title
        setTitle();

        //inicializace prvků v aktivitě
        spinnerBillYear = findViewById(R.id.spinnerIncomeYear);
        spinnerBillMonth = findViewById(R.id.spinnerIncomeMonth);
        listViewBill = findViewById(R.id.listViewIncome);

        //implementace nastavení do aktivity
        setSettings();

        //načtení dat z databáze
        readDataFromDatabase();

    }

    /**
     * Funkce, která načte data z databáze a uloží je do
     * globálních proměnných. Tato akce je prováděna v jiném
     * vlákně něž vlákno GUI, aby nezpomalovala odezvu UI.
     */
    private void readDataFromDatabase() {
        Log.d(TAG, "Reading data from db");

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                BillDatabaseHelper billDatabaseHelper = new BillDatabaseHelper(BillActivity.this);

                ArrayList<BillBox> billBox;

                Log.d(TAG, "Picked year: " + pickedYear);
                Log.d(TAG, "Picked month: " + pickedMonth);

                if (isExpense) {
                    billBox = billDatabaseHelper.getAllBillsWithTypesByDate(pickedYear, pickedMonth, 1);
                } else {
                    billBox = billDatabaseHelper.getAllBillsWithTypesByDate(pickedYear, pickedMonth, 0);
                }

                Log.d(TAG, "Number of bills: " + billBox.size());

                ID = new int[billBox.size()];

                for (int i = 0; i < billBox.size(); i++) {
                    ID[i] = billBox.get(i).getBill().getId();
                }

                setAdapterToList(billBox);
            }
        });
    }

    /**
     * Načtení nastavení aktuálního uživatele do aktivity.
     */
    private void setSettings() {
        Settings settings = Settings.getInstance();
        //pokud je v nastavení vybrán pouze jeden rok, tak zobrazím do spinneru a zablokuji ho
        if (settings.isIsPickedOneYear()) {
            spinnerBillYear.setEnabled(false);
            spinnerBillYear.setSelection(settings.getArrayYearId());
            pickedYear = Integer.parseInt(settings.getYear());
            Log.d(TAG, "Picked year " + pickedYear);
        }

        //pokud je v nastavení vybrán pouze jeden měsíc, tak zobrazím do spinneru a zablokuji ho
        if (settings.isPickedOneMonth()) {
            spinnerBillMonth.setEnabled(false);
            spinnerBillMonth.setSelection(settings.getArrayMonthId());
            pickedMonth = settings.getArrayMonthId();
            Log.d(TAG, "Picked month " + pickedMonth);
        }
    }

    /**
     * Nastavení nadpisu do aktivity, podle toho jestli se jedná o
     * příjem nebo výdaj.
     */
    private void setTitle() {
        if (isExpense) {
            setTitle(getString(R.string.title_activity_expense));
        } else {
            setTitle(getString(R.string.title_activity_income));
        }
    }

    /**
     * Nastavení adapteru do listu pro zobrazení.
     *
     * @param billBox array list (faktury + typy faktur)
     */
    private void setAdapterToList(ArrayList<BillBox> billBox) {
        final ListViewBillAdapter listViewBillAdapter = new ListViewBillAdapter(BillActivity.this, billBox);
        if (isExpense) {
            listViewBillAdapter.isExpense(true);
        }
        listViewBill.post(new Runnable() {
            @Override
            public void run() {
                listViewBill.setAdapter(listViewBillAdapter);
            }
        });
    }

    /**
     * Funkce, která zobrazí dialogové okno pro přidání typu
     * faktury. Po stisknutí tlačítka přidá záznam do databáze.
     */
    private void addTypeBillDialogShow() {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(BillActivity.this);
        @SuppressLint("InflateParams")
        View mView = getLayoutInflater().inflate(R.layout.type_bill_add_dialog, null);
        //inicializace polí v dialogovém okně
        final Button btnPickColor = mView.findViewById(R.id.buttonPickColor);
        final Button btnAddBillType = mView.findViewById(R.id.buttonAddBillType);
        final EditText editTextBillTypeName = mView.findViewById(R.id.editTextBillTypeName);
        final TextInputLayout layouBillTypeName = mView.findViewById(R.id.layoutBillTypeName);

        //zobrazení dialogového okna
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        //proměnná pro vybranou barvu
        final int[] pickedColor = new int[1];
        pickedColor[0] = getResources().getColor(R.color.colorPrimary);

        //zobrazneí color pickeru
        btnPickColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new ColorPickerPopup.Builder(BillActivity.this)
                        .initialColor(getResources().getColor(R.color.colorPrimary))    //nastavení init barvy
                        .enableBrightness(false)
                        .enableAlpha(false)
                        .okTitle(getString(R.string.select))
                        .cancelTitle(getString(R.string.close))
                        .showIndicator(true)
                        .showValue(false)
                        .build()
                        .show(v, new ColorPickerPopup.ColorPickerObserver() {
                            @Override
                            public void onColorPicked(int color) {
                                v.setBackgroundColor(color);
                                pickedColor[0] = color;
                            }

                        });
            }


        });
        //přidání typu faktury do databáze
        btnAddBillType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String billTypeName = editTextBillTypeName.getText().toString();

                //kontrola vstupních polí
                if (billTypeName.isEmpty()) {
                    layouBillTypeName.setError(getString(R.string.bill_type_name_is_empty));
                    return;
                } else if (billTypeName.length() > TextInputLength.BILL_TYPE_NAME_LENGTH) {
                    layouBillTypeName.setError(getString(R.string.input_is_too_long));
                    return;
                }

                //vložení typu do databáze
                TypeBill typeBill = new TypeBill(UserInformation.getInstance().getUserId(), billTypeName, pickedColor[0]);
                typeBill.setIsDirty(1);
                typeBill.setIsDeleted(0);
                TypeBillDatabaseHelper typeBillDatabaseHelper = new TypeBillDatabaseHelper(BillActivity.this);
                typeBillDatabaseHelper.addTypeBill(typeBill, false);

                //záloha dat
                Push push = new Push(BillActivity.this);
                push.push();

                //vypis uživateli o úspěšném vložení typu do databáze

                String message = getString(R.string.bill_type_has_been_added);
                Toast.makeText(BillActivity.this, message, Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });
    }

    /**
     * Metoda, která se stará o vykreslení menu v pravém rohu.
     *
     * @param menu menu
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bill_type_menu, menu);
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
            case R.id.option_menu_bill_type_add:
                addTypeBillDialogShow();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * Metoda, která po stisknutí tlačítka zpět nastartuje příslušnou
     * aktivitu a přiloží potřebné informace.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent homeActivity = new Intent(this, HomeActivity.class);
            startActivity(homeActivity);
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

        BillActivity thisActivity = BillActivity.this;
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
