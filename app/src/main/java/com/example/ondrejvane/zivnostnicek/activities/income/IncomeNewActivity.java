package com.example.ondrejvane.zivnostnicek.activities.income;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ondrejvane.zivnostnicek.menu.Menu;
import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.adapters.ListViewBillItemAdapter;
import com.example.ondrejvane.zivnostnicek.database.BillDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.database.ItemQuantityDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.database.StorageItemDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.database.TraderDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.helper.Header;
import com.example.ondrejvane.zivnostnicek.helper.InputValidation;
import com.example.ondrejvane.zivnostnicek.helper.Logout;
import com.example.ondrejvane.zivnostnicek.helper.StorageItemBox;
import com.example.ondrejvane.zivnostnicek.helper.UserInformation;
import com.example.ondrejvane.zivnostnicek.model.Bill;
import com.example.ondrejvane.zivnostnicek.model.ItemQuantity;
import com.example.ondrejvane.zivnostnicek.model.StorageItem;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class IncomeNewActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView mDisplayDate;  //Datum
    private ImageView photoView;    //foto
    private Spinner spinnerTrader;  //obchodník
    private EditText incomeName;    //název / číslo faktury
    private EditText incomeAmount;  //částka
    private Spinner spinnerIncomeVat;   //DPH
    private TextInputLayout layoutIncomeName;
    private TextInputLayout layoutIncomeAmount;
    private Calendar cal;
    private String[][] traders;
    private String[][] storageItems;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private ArrayList<StorageItemBox> listOfItems;
    private ExpandableHeightListView expandableListView;
    private StorageItemDatabaseHelper storageItemDatabaseHelper;
    private TraderDatabaseHelper traderDatabaseHelper;
    private BillDatabaseHelper billDatabaseHelper;
    private ItemQuantityDatabaseHelper itemQuantityDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_new);

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

        //po stisknutí na položku faktury dojde ke smazání položky
        expandableListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listOfItems.remove(position);
                setDataToList();

            }
        });
    }

    /**
     * Metoda, která inicializuje všechny prvky této aktivity
     */
    private void initActivity() {
        mDisplayDate = findViewById(R.id.editTextDate);
        photoView = findViewById(R.id.photoView);
        spinnerTrader = findViewById(R.id.spinnerTrader);
        expandableListView = findViewById(R.id.listViewIncomeStorageItem);
        incomeName = findViewById(R.id.textInputEditName);
        incomeAmount = findViewById(R.id.textInputEditTextAmount);
        spinnerIncomeVat = findViewById(R.id.spinnerVATincome);
        layoutIncomeName = findViewById(R.id.textInputLayoutIncomeNumber);
        layoutIncomeAmount = findViewById(R.id.textInputLayoutIncomeAmount);

        //inicializace array listu pro položky faktury
        listOfItems = new ArrayList<>();

        //inicializace databáze
        traderDatabaseHelper = new TraderDatabaseHelper(IncomeNewActivity.this);
        storageItemDatabaseHelper = new StorageItemDatabaseHelper(IncomeNewActivity.this);
        billDatabaseHelper = new BillDatabaseHelper(IncomeNewActivity.this);
        itemQuantityDatabaseHelper = new ItemQuantityDatabaseHelper(IncomeNewActivity.this);

        //načtení obchodníků z databáze do globální proměnné
        traders = traderDatabaseHelper.getTradersData(UserInformation.getInstance().getUserId());
        //načtení skladových položek z databáze do globální proměnné
        storageItems = storageItemDatabaseHelper.getStorageItemData(UserInformation.getInstance().getUserId());

        //zjištění aktuálního data
        cal = Calendar.getInstance();
        String stringBuilder = String.valueOf(cal.get(Calendar.DAY_OF_MONTH)) +
                "." +
                (cal.get(Calendar.MONTH) + 1) +
                "." +
                cal.get(Calendar.YEAR);
        //nastavení aktuálního data
        mDisplayDate.setText(stringBuilder);

        //nastavení obchodníků do spinneru
        setDataToTraderSpinner();

    }

    /**
     * Procedura, která zobrazí date picker a nechá uživatele vybrat
     * datum. Následně datum vloží do pole datum.
     * @param view
     */
    public void showDateDialog(View view) {
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(IncomeNewActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener,
                year,month,day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = day + "." + month + "." + year;
                mDisplayDate.setText(date);
            }
        };
    }

    private void setDataToTraderSpinner(){
        List<String> userList=new ArrayList<>();

        userList.add(getString(R.string.select_trader));

        for (int i = 0; i<traders[0].length; i++){
            userList.add(traders[1][i]);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(IncomeNewActivity.this, android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, userList);

        spinnerTrader.setAdapter(adapter);

    }

    private void setDataToStorageItemSpinner(Spinner spinnerStorageItem){
        List<String> storageItemList=new ArrayList<>();

        storageItemList.add(getString(R.string.select_storage_item));

        for (int i = 0; i<storageItems[0].length; i++){
            storageItemList.add(storageItems[1][i]);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(IncomeNewActivity.this, android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, storageItemList);

        spinnerStorageItem.setAdapter(adapter);
    }

    /**
     * Metoda, která vyvolá dialogové okno a táže
     * se uživatele, zda chce pořídit novou fotku nebo
     * vybrat fotku z galerie. Po té volá odpovídající
     * metody pro vyvolání aktivity.
     * @param view
     */
    public void getPicture(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(IncomeNewActivity.this);
        builder.setTitle(R.string.take_picture_from)
                .setItems(R.array.photo, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto , 1);
                        }else {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, 0);
                        }
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public void getStorageItem(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(IncomeNewActivity.this);
        builder.setTitle(R.string.add)
                .setItems(R.array.income_dialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            //zobrazení dialogového okna pro vytvoření skladové položky
                            showNewItemDialog();
                        }else {
                            //zobrazení dialogového okna pro exitující položku = pouze navýšení množství
                            showExistsItemDialog();
                        }
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK){
                        Bitmap bitmap = (Bitmap)data.getExtras().get("data");
                        photoView.setImageBitmap(bitmap);
                    }
                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        //získání Uri z intentu
                        Uri pickedImage = data.getData();
                        //získání bitmapy z Uri
                        Bitmap bitmap = getBitmapFromUri(pickedImage);
                        //nastavení bitmapy do image view
                        photoView.setImageBitmap(bitmap);
                    }
            }
        }catch (NullPointerException e){
            System.out.println("Picture is empty");
        }
    }

    /**
     * Metoda, která převede obrázek z Uri do
     * bitmapy
     * @param pickedImage Uri vybraného obrázku
     * @return  Bitmap bitmapa odpovídajícího Uri
     */
    private Bitmap getBitmapFromUri(Uri pickedImage){
        String[] filePath = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
        cursor.moveToFirst();
        String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
        cursor.close();
        return bitmap;
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
        Intent home = new Intent(IncomeNewActivity.this, IncomeActivity.class);
        startActivity(home);
        finish();
    }

    private void setDataToList(){

        String[] itemName = new String[listOfItems.size()];
        float[] itemQuantity = new float[listOfItems.size()];
        String[] itemUnit = new String[listOfItems.size()];
         //projdu list a získám informace o položkách, které potřebuju
        for (int i=0; i<listOfItems.size(); i++){
            itemName[i] = listOfItems.get(i).getStorageItem().getName();
            itemQuantity[i] = listOfItems.get(i).getItemQuantity().getQuantity();
            itemUnit[i] = listOfItems.get(i).getStorageItem().getUnit();
        }

        //nastavení adapteru do list view pro zobrazení
        ListViewBillItemAdapter listViewItemAdapter = new ListViewBillItemAdapter(this, itemName, itemQuantity, itemUnit);
        expandableListView.setAdapter(listViewItemAdapter);

        //nastaví list view pro zobrazení více řádků issue => https://stackoverflow.com/questions/18367522/android-list-view-inside-a-scroll-view
        //vyřešeno použitím knihovny =>https://github.com/PaoloRotolo/ExpandableHeightListView
        expandableListView.setExpanded(true);
    }

    /**
     * Tato metoda zobrazí dialogové okno pro přidání
     * nové položky faktury. (Vytvoření nové skladové položky)
     */
    private void showNewItemDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(IncomeNewActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.income_item_new_dialog, null);
        //inicializace textových polí a tlačítka v dialogovém okně
        final EditText itemName = mView.findViewById(R.id.itemDailogName);
        final EditText itemQuantity = mView.findViewById(R.id.itemDailogaQuantity);
        final TextInputLayout itemNameLayout = mView.findViewById(R.id.itemLayoutDailogName);
        final TextInputLayout itemQuantityLayout = mView.findViewById(R.id.itemLayoutDailogQuantity);
        final Spinner spinnerUnit = mView.findViewById(R.id.spinnerUnit);
        Button addItem = mView.findViewById(R.id.buttonAddItem);

        //zobrazení dialogovéhookna
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = itemName.getText().toString();
                String quantity = itemQuantity.getText().toString();
                String unit = spinnerUnit.getSelectedItem().toString();

                if (!InputValidation.validateIsEmpty(name)){
                    itemNameLayout.setError(getString(R.string.item_name_is_empty));
                    return;
                }

                if (!InputValidation.validateIsEmpty(quantity)){
                    itemQuantityLayout.setError(getString(R.string.quantity_is_empty));
                    return;
                }

                //vytvoření nové skladové položky
                StorageItem storageItem = new StorageItem(UserInformation.getInstance().getUserId(), name, unit);

                //vytvoření množství ke skladové položce
                ItemQuantity itemQuantity = new ItemQuantity();
                itemQuantity.setQuantity(Float.parseFloat(quantity));

                //vytvoření pomocné třídy pro udržení skaldové položky a množství
                StorageItemBox storageItemBox = new StorageItemBox(storageItem, itemQuantity,true);

                //přidání položky faktury do listu
                listOfItems.add(storageItemBox);

                //zobrazení dat do listu
                setDataToList();

                //zavření dialogu
                dialog.dismiss();

            }
        });

    }

    /**
     * Metoda, která zobrazí dialog přidáním nového
     * množství skladové položky. Pro položku která již
     * existuje ve skladu.
     */
    private void showExistsItemDialog(){
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(IncomeNewActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.income_item_exists_dialog, null);
        //inicializace polí v dialogovém okně
        final Spinner spinnerStorageItem = mView.findViewById(R.id.spinnerStorageItemExists);
        final EditText itemQuantity = mView.findViewById(R.id.itemDailogExistsQuantity);
        final TextInputLayout layoutItemQuantity = mView.findViewById(R.id.layoutItemDailogExistsQuantity);
        final Button btnAddItem = mView.findViewById(R.id.buttonAddItemExists);

        //nastavení skladových položek do spinneru v dialogévém okně
        setDataToStorageItemSpinner(spinnerStorageItem);

        //zobrazení dialogového okna
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantity = itemQuantity.getText().toString();
                int indexInSpinner = spinnerStorageItem.getSelectedItemPosition();

                if(!InputValidation.validateIsEmpty(quantity)){
                    layoutItemQuantity.setError(getString(R.string.quantity_is_empty));
                    return;
                }

                if( indexInSpinner == 0){
                    //výpis o úspěšném uložení skladové položky
                    String message = getString(R.string.select_storage_item);
                    Toast.makeText(IncomeNewActivity.this, message, Toast.LENGTH_SHORT).show();
                    return;
                }

                int storageItemId = Integer.parseInt(storageItems[0][indexInSpinner-1]);

                //vytvoření skladové položky a vložení do listu položek
                StorageItem storageItem = storageItemDatabaseHelper.getStorageItemById(storageItemId);

                //vytvoření množství skladové položky
                ItemQuantity itemQuantity = new ItemQuantity();
                itemQuantity.setQuantity(Float.parseFloat(quantity));
                itemQuantity.setStorageItemId(storageItemId);
                StorageItemBox storageItemBox = new StorageItemBox(storageItem, itemQuantity,  false);

                //přidání položky do seznamu
                listOfItems.add(storageItemBox);

                //zobrazení položky v listu
                setDataToList();

                //zavření dialogu
                dialog.dismiss();
                
            }
        });

    }


    /**
     * Metoda vloží záznam nového příjmu do databáze.
     * @param view
     */
    public void submitNewIncomeForm(View view) {

        //načtení dat ze vstupních polí
        String name = incomeName.getText().toString();
        String amount = incomeAmount.getText().toString();
        String VAT = spinnerIncomeVat.getSelectedItem().toString();
        String date = mDisplayDate.getText().toString();
        Bitmap photo = ((BitmapDrawable)photoView.getDrawable()).getBitmap();
        int traderId;

        //obchodník vybrán
        if(spinnerTrader.getSelectedItemId() != 0){
            traderId = Integer.parseInt(traders[0][(int) spinnerTrader.getSelectedItemId()-1]);
        }else {
            //obchodník nevybrán
            traderId = -1;
        }

        //validate povinných polí => částka a název faktury
        if(!InputValidation.validateIsEmpty(name)){
            String message = getString(R.string.name_of_bill_is_empty);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            layoutIncomeName.setError(getString(R.string.name_of_bill_is_empty));
            return;
        }

        if(!InputValidation.validateIsEmpty(amount)){
            String message = getString(R.string.amount_is_empty);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            layoutIncomeAmount.setError(getString(R.string.amount_is_empty));
            return;
        }

        //vytvoření nové faktury a nastavení dat z aktivity
        Bill bill = new Bill();
        bill.setName(name);
        bill.setUserId(UserInformation.getInstance().getUserId());
        bill.setAmount(Float.parseFloat(amount));
        bill.setVAT(Integer.parseInt(VAT));
        bill.setDate(date);
        bill.setTraderId(traderId);
        bill.setPhoto(photo);
        //jedná se o příjem 0 = příjem  x 1 = výdaj
        bill.setIsExpense(0);

        //vložení příjmu do databáze
        long billId = billDatabaseHelper.addBill(bill);
        long storageItemId;

        //projdu list všech položek faktury
        for (int i=0; i<listOfItems.size(); i++){

            //jedná se o položku, která není ve skladu
            if(listOfItems.get(i).isNew()){
                storageItemId = storageItemDatabaseHelper.addStorageItem(listOfItems.get(i).getStorageItem());
                listOfItems.get(i).getItemQuantity().setStorageItemId(storageItemId);
                listOfItems.get(i).getItemQuantity().setBillId(billId);
                itemQuantityDatabaseHelper.addItemQuantity(listOfItems.get(i).getItemQuantity());
            }else {
                //položka je již evidivoaná ve skladu
                listOfItems.get(i).getItemQuantity().setBillId(billId);
                itemQuantityDatabaseHelper.addItemQuantity(listOfItems.get(i).getItemQuantity());
            }
        }


        //vypis uživateli o úspěšném vložení faktruy do databáze

        String message = getString(R.string.income_has_been_added);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Intent income = new Intent(IncomeNewActivity.this, IncomeActivity.class);
        startActivity(income);
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

        IncomeNewActivity thisActivity = IncomeNewActivity.this;
        Intent newIntent;

        //inicializace třídy menu, kde jsou definovány jednotlivé aktivity
        Menu menu = new Menu(thisActivity);
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
