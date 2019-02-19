package com.example.ondrejvane.zivnostnicek.activities.bill;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.adapters.ListViewBillAdapter;
import com.example.ondrejvane.zivnostnicek.database.BillDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.database.TypeBillDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.utilities.ArrayUtility;
import com.example.ondrejvane.zivnostnicek.utilities.FormatUtility;
import com.example.ondrejvane.zivnostnicek.helper.Header;
import com.example.ondrejvane.zivnostnicek.helper.InputValidation;
import com.example.ondrejvane.zivnostnicek.helper.Logout;
import com.example.ondrejvane.zivnostnicek.helper.Settings;
import com.example.ondrejvane.zivnostnicek.helper.UserInformation;
import com.example.ondrejvane.zivnostnicek.model.TypeBill;

import top.defaults.colorpicker.ColorPickerPopup;

/**
 * Aktivity pro zobrazení všech příjmu.
 */
public class BillActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private boolean isExpense;      //proměnná, která udáváa, zda jsou zobrazovány příjmy neb výdaje

    private Spinner spinnerBillYear;
    private Spinner spinnerBillMonth;
    private ListView listViewBill;

    //proměnné, které obsahují všechny příjmy načtené z databáze
    private String[] billName;
    private String[] billDate;
    private String[] billAmount;
    private String[] billTypeName;
    private String[] billTypeColor;
    private int[] ID;
    private int[] holderId;

    //pomocné proměnné pro vyhledávání
    private int[] tempIdYear;
    private String[] tempBillNameYear;
    private String[] tempBillDateYear;
    private String[] tempBillAmountYear;
    private String[] tempBillTypeName;
    private String[] tempBillTypeColor;


    /**
     * Metoda, která se provede při spuštění akctivity a provede nezbytné
     * úkony ke správnému fungování aktivity.
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BillActivity.this, BillNewActivity.class);
                intent.putExtra("IS_EXPENSE", isExpense);
                startActivity(intent);
                finish();
            }
        });

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

        //click listener, který spustí aktivitu pro náhled faktury po kliknutí na položku v listu
        listViewBill.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int incomeId = ID[position];
                if(incomeId != -1){
                    Intent intent = new Intent(BillActivity.this, BillShowActivity.class);
                    intent.putExtra("BILL_ID", incomeId);
                    intent.putExtra("IS_EXPENSE", isExpense);
                    startActivity(intent);
                    //finish();
                }
            }
        });


        spinnerBillYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0){
                    String findingYear= spinnerBillYear.getSelectedItem().toString();
                    if(billName.length == 0){
                        tempBillNameYear = new String[1];
                        tempBillDateYear = new String[1];
                        tempBillAmountYear = new String[1];
                        tempBillTypeName = new String[1];
                        tempBillTypeColor = new String[1];
                        tempIdYear = new int[1];
                        tempBillNameYear[0] = getString(R.string.no_result);
                        tempBillDateYear[0] = getString(R.string.no_result);
                        tempBillAmountYear[0] = "";
                        tempBillTypeName[0] = getString(R.string.no_result);
                        tempBillTypeColor[0] = Integer.toString(getResources().getColor(R.color.colorPrimary));
                        tempIdYear[0] = -1;
                        ID = tempIdYear;
                        setAdapterToList(tempBillNameYear, tempBillDateYear, tempBillAmountYear, tempBillTypeName, tempBillTypeColor);
                        return;
                    }else {
                        int foundIncomes = 0;
                        for (int i = 0; i < billName.length; i++){
                            if(FormatUtility.getYearFromDate(billDate[i]).equals(findingYear)){
                                foundIncomes++;
                            }
                        }
                        tempBillNameYear = new String[foundIncomes];
                        tempBillDateYear = new String[foundIncomes];
                        tempBillAmountYear = new String[foundIncomes];
                        tempBillTypeName = new String[foundIncomes];
                        tempBillTypeColor = new String[foundIncomes];
                        tempIdYear = new int[foundIncomes];
                    }

                    int tempI=0;
                    boolean found = false;

                    for (int i = 0; i < billName.length; i++){
                        if(FormatUtility.getYearFromDate(billDate[i]).equals(findingYear)){
                            tempBillNameYear[tempI] = billName[i];
                            tempBillDateYear[tempI] = billDate[i];
                            tempBillAmountYear[tempI] = billAmount[i];
                            tempBillTypeName[tempI] = billTypeName[i];
                            tempBillTypeColor[tempI] = billTypeColor[i];
                            tempIdYear[tempI] = holderId[i];
                            tempI++;
                            found = true;
                        }
                    }

                    if(found){
                        ID = holderId;
                        setAdapterToList(tempBillNameYear, tempBillDateYear, tempBillAmountYear, tempBillTypeName, tempBillTypeColor);
                    }else {
                        tempBillNameYear = new String[1];
                        tempBillDateYear = new String[1];
                        tempBillAmountYear = new String[1];
                        tempBillTypeName = new String[1];
                        tempBillTypeColor = new String[1];
                        tempIdYear = new int[1];
                        tempBillNameYear[0] = getString(R.string.no_result);
                        tempBillDateYear[0] = getString(R.string.no_result);
                        tempBillAmountYear[0] = "";
                        tempBillTypeName[0] = getString(R.string.no_result);
                        tempBillTypeColor[0] = Integer.toString(getResources().getColor(R.color.colorPrimary));
                        tempIdYear[0] = -1;
                        ID = tempIdYear;
                        setAdapterToList(tempBillNameYear, tempBillDateYear, tempBillAmountYear, tempBillTypeName, tempBillTypeColor);
                    }
                }else {
                    tempIdYear = holderId;
                    tempBillNameYear = billName;
                    tempBillDateYear = billDate;
                    tempBillAmountYear = billAmount;
                    tempBillTypeName = billTypeName;
                    tempBillTypeColor = billTypeColor;
                    //nastavení dat do adapteru pro zobrazení
                    setAdapterToList(billName, billDate, billAmount, tempBillTypeName, tempBillTypeColor);
                    ID = holderId;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerBillMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int[] tempId;
                String[] tempBillNameYearAndMonth;
                String[] tempBillDateYearAndMonth;
                String[] tempBillAmountYearAndMonth;
                String[] tempBillTypeNameYearAndMonth;
                String[] tempBillTypeColorYearAndMonth;
                if(position != 0) {
                    String findingMonth = Integer.toString(position);
                    String findingYear = spinnerBillYear.getSelectedItem().toString();
                    if(billName.length == 0){
                        tempBillNameYearAndMonth = new String[1];
                        tempBillDateYearAndMonth = new String[1];
                        tempBillAmountYearAndMonth = new String[1];
                        tempBillTypeNameYearAndMonth = new String[1];
                        tempBillTypeColorYearAndMonth = new String[1];
                        tempId = new int[1];
                        tempBillNameYearAndMonth[0] = getString(R.string.no_result);
                        tempBillDateYearAndMonth[0] = getString(R.string.no_result);
                        tempBillTypeNameYearAndMonth[0] = getString(R.string.no_result);
                        tempBillTypeColorYearAndMonth[0] = Integer.toString(getResources().getColor(R.color.colorPrimary));
                        tempBillAmountYearAndMonth[0] = "";
                        tempId[0] = -1;
                        ID = tempId;
                        setAdapterToList(tempBillNameYearAndMonth, tempBillDateYearAndMonth, tempBillAmountYearAndMonth, tempBillTypeNameYearAndMonth, tempBillTypeColorYearAndMonth);
                        return;
                    }else {
                        int foundIncomes = 0;
                        for (int i = 0; i < billName.length; i++){
                            if(FormatUtility.getYearFromDate(billDate[i]).equals(findingYear) && FormatUtility.getMonthFromDate(billDate[i]).equals(findingMonth)){
                                foundIncomes++;
                            }
                        }
                        tempBillNameYearAndMonth = new String[foundIncomes];
                        tempBillDateYearAndMonth = new String[foundIncomes];
                        tempBillAmountYearAndMonth = new String[foundIncomes];
                        tempBillTypeNameYearAndMonth = new String[foundIncomes];
                        tempBillTypeColorYearAndMonth = new String[foundIncomes];
                        tempId = new int[foundIncomes];
                    }

                    int tempI=0;
                    boolean found = false;

                    for (int i = 0; i < billName.length; i++){
                        if(FormatUtility.getYearFromDate(billDate[i]).equals(findingYear) && FormatUtility.getMonthFromDate(billDate[i]).equals(findingMonth)){
                            tempBillNameYearAndMonth[tempI] = billName[i];
                            tempBillDateYearAndMonth[tempI] = billDate[i];
                            tempBillAmountYearAndMonth[tempI] = billAmount[i];
                            tempBillTypeNameYearAndMonth[tempI] = billTypeName[i];
                            tempBillTypeColorYearAndMonth[tempI] = billTypeColor[i];
                            tempId[tempI] = holderId[i];
                            tempI++;
                            found = true;
                        }
                    }

                    if(found){
                        ID = holderId;
                        setAdapterToList(tempBillNameYearAndMonth, tempBillDateYearAndMonth, tempBillAmountYearAndMonth, tempBillTypeNameYearAndMonth, tempBillTypeColorYearAndMonth);
                    }else {
                        tempBillNameYearAndMonth = new String[1];
                        tempBillDateYearAndMonth = new String[1];
                        tempBillAmountYearAndMonth = new String[1];
                        tempBillTypeNameYearAndMonth = new String[1];
                        tempBillTypeColorYearAndMonth = new String[1];
                        tempId = new int[1];
                        tempBillNameYearAndMonth[0] = getString(R.string.no_result);
                        tempBillDateYearAndMonth[0] = getString(R.string.no_result);
                        tempBillTypeNameYearAndMonth[0] = getString(R.string.no_result);
                        tempBillTypeColorYearAndMonth[0] = Integer.toString(getResources().getColor(R.color.colorPrimary));
                        tempBillAmountYearAndMonth[0] = "";
                        tempId[0] = -1;
                        ID = tempId;
                        setAdapterToList(tempBillNameYearAndMonth, tempBillDateYearAndMonth, tempBillAmountYearAndMonth, tempBillTypeNameYearAndMonth, tempBillTypeColorYearAndMonth);
                    }
                }else {
                    ID = tempIdYear;
                    setAdapterToList(tempBillNameYear, tempBillDateYear, tempBillAmountYear, tempBillTypeName, tempBillTypeColor);
                }

                }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private void initActivity() {

        isExpense = getIntent().getExtras().getBoolean("IS_EXPENSE", false);
        //pokud se jedná o výdaj, tak nastavím title
        setTitle();

        String[][] temp;
        spinnerBillYear = findViewById(R.id.spinnerIncomeYear);
        spinnerBillMonth = findViewById(R.id.spinnerIncomeMonth);
        listViewBill = findViewById(R.id.listViewIncome);


        //pokud je v nastavení vybrán pouze jeden rok, tak zobrazím do spinneru a zablokuji ho
        if(Settings.getInstance().isIsPickedOneYear()){
            spinnerBillYear.setEnabled(false);
            spinnerBillYear.setSelection(Settings.getInstance().getArrayYearId());
        }

        //pokud je v nastavení vybrán pouze jeden měsíc, tak zobrazím do spinneru a zablokuji ho
        if(Settings.getInstance().isPickedOneMonth()){
            spinnerBillMonth.setEnabled(false);
            spinnerBillMonth.setSelection(Settings.getInstance().getArrayMonthId());
        }

        //inicializace databáze
        BillDatabaseHelper billDatabaseHelper = new BillDatabaseHelper(BillActivity.this);
        //načtení příslušných faktur z databáze
        if(isExpense){
            temp = billDatabaseHelper.getBillData(UserInformation.getInstance().getUserId(), 1); // 1 = hledám pouze výdaje
        }else {
            temp = billDatabaseHelper.getBillData(UserInformation.getInstance().getUserId(), 0); // 0 = hledám pouze příjmy
        }
        ID = ArrayUtility.arrayStringToInteger(temp[0]);
        holderId = ArrayUtility.arrayStringToInteger(temp[0]);
        billName = temp[1];
        billDate = temp[2];
        billAmount = temp[3];
        billTypeName = temp[4];
        billTypeColor = temp[5];

        //nastavení dat do adapteru pro zobrazení
        setAdapterToList(billName, billDate, billAmount, billTypeName, billTypeColor);

    }

    private void setTitle(){
        if(isExpense){
            setTitle(getString(R.string.title_activity_expense));
        }else {
            setTitle(getString(R.string.title_activity_income));
        }
    }

    private void setAdapterToList(String[] billName, String[] billDate, String[] billAmount, String[] typeBillName, String[] typeBillColor){
        ListViewBillAdapter listViewBillAdapter = new ListViewBillAdapter(BillActivity.this, billName, billDate, billAmount, typeBillName, typeBillColor);
        if(isExpense){
            listViewBillAdapter.isExpense(true);
        }
        listViewBill.setAdapter(listViewBillAdapter);
    }

    private void addTypeBillDialogShow() {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(BillActivity.this);
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

                if(!InputValidation.validateIsEmpty(billTypeName)){
                    layouBillTypeName.setError(getString(R.string.bill_type_name_is_empty));
                    return;
                }

                //vložení typu do databáze
                TypeBill typeBill = new TypeBill(UserInformation.getInstance().getUserId(), billTypeName, pickedColor[0]);
                TypeBillDatabaseHelper typeBillDatabaseHelper = new TypeBillDatabaseHelper(BillActivity.this);
                typeBillDatabaseHelper.addTypeBill(typeBill);

                //vypis uživateli o úspěšném vložení typu do databáze

                String message = getString(R.string.bill_type_has_been_added);
                Toast.makeText(BillActivity.this, message, Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bill_type_menu, menu);
        return true;
    }

    /**
     * Metoda, která se stará o boční navigační menu a přechod
     * mezi aktivitami
     * @param item  vybraný item z menu
     * @return      boolean
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
            super.onBackPressed();
        }
    }


    /**
     * Metoda, která se stará o hlavní navigační menu aplikace.
     * @param item  vybraná položka v menu
     * @return      boolean
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
