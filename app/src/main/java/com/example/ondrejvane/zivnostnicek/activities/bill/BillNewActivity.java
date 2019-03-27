package com.example.ondrejvane.zivnostnicek.activities.bill;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ondrejvane.zivnostnicek.database.ModelHelpers.TypeBillDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.helper.TextInputLength;
import com.example.ondrejvane.zivnostnicek.server.Push;
import com.example.ondrejvane.zivnostnicek.utilities.PictureUtility;
import com.example.ondrejvane.zivnostnicek.menu.Menu;
import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.adapters.ListViewBillItemAdapter;
import com.example.ondrejvane.zivnostnicek.database.ModelHelpers.BillDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.database.ModelHelpers.ItemQuantityDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.database.ModelHelpers.StorageItemDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.database.ModelHelpers.TraderDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.helper.Header;
import com.example.ondrejvane.zivnostnicek.helper.InputValidation;
import com.example.ondrejvane.zivnostnicek.session.Logout;
import com.example.ondrejvane.zivnostnicek.model.StorageItemBox;
import com.example.ondrejvane.zivnostnicek.session.UserInformation;
import com.example.ondrejvane.zivnostnicek.model.Bill;
import com.example.ondrejvane.zivnostnicek.model.ItemQuantity;
import com.example.ondrejvane.zivnostnicek.model.StorageItem;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Kativity pro přidávání nové faktury
 */
public class BillNewActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, EasyPermissions.PermissionCallbacks {

    private final String TAG = "BillNewActivity";

    //prvky aktivity
    private TextView mDisplayDate;  //Datum
    private ImageView photoView;    //foto
    private Spinner spinnerTrader;  //obchodník
    private Spinner spinnerBillType;    //spinner typ faktury
    private EditText billName;    //název / číslo faktury
    private EditText billAmount;  //částka
    private Spinner spinnerBillVat;   //DPH
    private Button addBill;
    private TextInputLayout layoutBillName;
    private TextInputLayout layoutBillAmount;
    private TextView fillLabel;
    private CheckBox checkBoxGeneratePDF;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    //pomocné globální proměnné
    private boolean isExpense;
    private Calendar cal;
    private String[][] traders;
    private String[][] storageItems;
    private String[][] billTypes;
    private ArrayList<StorageItemBox> listOfItems;
    private ExpandableHeightListView expandableListView;
    private StorageItemDatabaseHelper storageItemDatabaseHelper;
    private TraderDatabaseHelper traderDatabaseHelper;
    private BillDatabaseHelper billDatabaseHelper;
    private ItemQuantityDatabaseHelper itemQuantityDatabaseHelper;
    private TypeBillDatabaseHelper typeBillDatabaseHelper;
    private String picturePath = null;

    //kod oprávnění přístupu ke kameře a uložišti
    private final int PERMISSION_REQUEST_CODE_CAMERA = 123;
    private final int PERMISSION_REQUEST_CODE_GALLERY = 321;

    /**
     * Metoda, která se provede při spuštění akctivity a provede nezbytné
     * úkony ke správnému fungování aktivity.
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_new);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Header header = new Header(navigationView);
        header.setTextToHeader();

        //inicaliazace aktivity
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
        //kontrola, zda je nějaké extra
        if (getIntent().hasExtra("IS_EXPENSE")) {
            //načtení extra
            isExpense = getIntent().getExtras().getBoolean("IS_EXPENSE", false);
        } else {
            isExpense = false;
        }


        mDisplayDate = findViewById(R.id.editTextDate);
        photoView = findViewById(R.id.photoView);
        spinnerTrader = findViewById(R.id.spinnerTrader);
        spinnerBillType = findViewById(R.id.spinnerBillType);
        expandableListView = findViewById(R.id.listViewIncomeStorageItem);
        billName = findViewById(R.id.textInputEditName);
        billAmount = findViewById(R.id.textInputEditTextAmount);
        spinnerBillVat = findViewById(R.id.spinnerVATincome);
        layoutBillName = findViewById(R.id.textInputLayoutIncomeNumber);
        layoutBillAmount = findViewById(R.id.textInputLayoutIncomeAmount);
        addBill = findViewById(R.id.buttonAddBill);
        fillLabel = findViewById(R.id.textViewBillFill);
        //checkBoxGeneratePDF = findViewById(R.id.checkBoxGeneratePDF);

        //zobrazení textu, podle toho, jestli jde o příjem nebo výdaj
        setTextToActivity();

        //inicializace array listu pro položky faktury
        listOfItems = new ArrayList<>();

        //inicializace databáze
        traderDatabaseHelper = new TraderDatabaseHelper(BillNewActivity.this);
        storageItemDatabaseHelper = new StorageItemDatabaseHelper(BillNewActivity.this);
        billDatabaseHelper = new BillDatabaseHelper(BillNewActivity.this);
        itemQuantityDatabaseHelper = new ItemQuantityDatabaseHelper(BillNewActivity.this);
        typeBillDatabaseHelper = new TypeBillDatabaseHelper(BillNewActivity.this);

        //načtení obchodníků z databáze do globální proměnné
        traders = traderDatabaseHelper.getTradersData(UserInformation.getInstance().getUserId());
        //načtení skladových položek z databáze do globální proměnné
        storageItems = storageItemDatabaseHelper.getStorageItemData(UserInformation.getInstance().getUserId());
        //načtení typ faktur z databáze do globální proměnné
        billTypes = typeBillDatabaseHelper.getTypeBillData(UserInformation.getInstance().getUserId());

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
        setDataToTraderSpinner(spinnerTrader);

        //nastavení druhu faktur do spinneru
        setDataToBillTypeSpinner();

    }

    /**
     * Funkce, která nastaví všechny dostupné typy faktur
     * uživatele do spinneru.
     */
    private void setDataToBillTypeSpinner() {
        List<String> typeList = new ArrayList<>();

        typeList.add(getString(R.string.select_bill_type));

        Log.d(TAG, "Number of types " + billTypes[0].length);

        for (int i = 0; i < billTypes[0].length; i++) {
            typeList.add(billTypes[1][i]);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(BillNewActivity.this, android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, typeList);

        spinnerBillType.setAdapter(adapter);
    }

    /**
     * Nastavení textu do aktivity, podle toho, jeslti se
     * jedná o příjem nebo výdaj.
     */
    private void setTextToActivity() {
        if (isExpense) {
            setTitle(getString(R.string.title_activity_expense_new));
            addBill.setText(getString(R.string.add_expense));
            fillLabel.setText(getString(R.string.fill_information_about_expense));
        } else {
            setTitle(getString(R.string.title_activity_income_new));
        }
    }

    /**
     * Procedura, která zobrazí date picker a nechá uživatele vybrat
     * datum. Následně datum vloží do pole datum.
     *
     * @param view view aktivity
     */
    public void showDateDialog(View view) {
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = day + "." + month + "." + year;
                Log.d(TAG, "Picked date " + date);
                mDisplayDate.setText(date);
            }
        };

        DatePickerDialog dialog = new DatePickerDialog(BillNewActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener,
                year, month, day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();


    }

    /**
     * Nastavení obchodníků do spinneru
     *
     * @param spinnerTrader spinner s obchodníky
     */
    private void setDataToTraderSpinner(Spinner spinnerTrader) {
        List<String> userList = new ArrayList<>();

        userList.add(getString(R.string.select_trader));

        for (int i = 0; i < traders[0].length; i++) {
            userList.add(traders[1][i]);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(BillNewActivity.this, android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, userList);

        spinnerTrader.setAdapter(adapter);

    }

    /**
     * Nastavení skladových položek do spinneru.
     *
     * @param spinnerStorageItem spinner se skl. polož.
     */
    private void setDataToStorageItemSpinner(Spinner spinnerStorageItem) {
        List<String> storageItemList = new ArrayList<>();

        storageItemList.add(getString(R.string.select_storage_item));

        for (int i = 0; i < storageItems[0].length; i++) {
            storageItemList.add(storageItems[1][i]);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(BillNewActivity.this, android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, storageItemList);

        spinnerStorageItem.setAdapter(adapter);
    }

    /**
     * Metoda, která vyvolá dialogové okno a táže
     * se uživatele, zda chce pořídit novou fotku nebo
     * vybrat fotku z galerie. Po té volá odpovídající
     * metody pro vyvolání aktivity.
     *
     * @param view view aktivity
     */
    public void getPicture(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(BillNewActivity.this);
        builder.setTitle(R.string.take_picture_from)
                .setItems(R.array.photo, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            callGallery();
                        } else {
                            callCamera();
                        }
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (cameraIntent.resolveActivity(getPackageManager()) != null) {

            File imageFile = getImageFile();

            if (imageFile != null) {
                Uri imageUri = FileProvider.getUriForFile(this, "com.example.provider.zivnostnicek", imageFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(cameraIntent, 0);
            }
        }
    }

    private void openGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 1);
    }

    /**
     * Metoda, která se spustí po načtení obrázku.
     *
     * @param requestCode kod požadavku
     * @param resultCode  kod výsledku
     * @param data        požadovaná data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {
                //fotoaparát
                case 0:
                    if (resultCode == RESULT_OK) {
                        //převedení uri na bitmapu pro zobrazení
                        //Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                        //zobrazení bitmapy
                        //photoView.setImageBitmap(bitmap);

                        //uložení obrázku do uložiště zařízení
                        //picturePath = PictureUtility.saveToInternalStorage(bitmap, this);
                        Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
                        photoView.setImageBitmap(bitmap);

                    }
                    break;
                //galerie
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        //získání Uri z intentu
                        Uri imageUri = data.getData();

                        //získání absolutní cesty k souboru
                        String imageFilePath = PictureUtility.getAbsolutePathFromUri(imageUri, getContentResolver());

                        //pokud je cesta správně, tak se zobrazí obrázek do aktivity
                        if (imageFilePath != null) {

                            //načtení obrazku z cesty
                            Bitmap bitmap = PictureUtility.getBitmap(imageFilePath);
                            photoView.setImageBitmap(bitmap);
                            picturePath = imageFilePath;
                        }

                        Log.d(TAG, "Picture URI " + picturePath);

                    }
            }
        } catch (NullPointerException e) {
            picturePath = null;
            Log.d(TAG, "On activity result null pointer exception");
        }
    }


    /**
     * Metoda, která vytvoří nový soubor pro fotku s
     * unikátním názvem podle data. Cestu uloží do globální proměnné
     * a jako návratovou hodnotu vrátí soubor
     *
     * @return vytvořený soubor
     */
    private File getImageFile() {
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = "jpg_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File imageFile = null;
        try {
            imageFile = File.createTempFile(imageName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Cannot create a file");
        }
        picturePath = imageFile.getAbsolutePath();
        return imageFile;
    }

    /**
     * Metoda, která zobrazí dialogové okno pro přídání
     * nové položky aktury.
     *
     * @param view view aktivity
     */
    public void getStorageItem(View view) {
        if (!isExpense) {
            showExistsItemDialog();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(BillNewActivity.this);
            builder.setTitle(R.string.add)
                    .setItems(R.array.income_dialog, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                //zobrazení dialogového okna pro vytvoření skladové položky
                                showNewItemDialog();
                            } else {
                                //zobrazení dialogového okna pro exitující položku = pouze navýšení množství
                                showExistsItemDialog();
                            }
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
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

        finish();
    }

    /**
     * Vykreslení lsitu položek faktury do aktivity.
     */
    private void setDataToList() {

        String[] itemName = new String[listOfItems.size()];
        float[] itemQuantity = new float[listOfItems.size()];
        String[] itemUnit = new String[listOfItems.size()];
        //projdu list a získám informace o položkách, které potřebuju
        for (int i = 0; i < listOfItems.size(); i++) {
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
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(BillNewActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.bill_item_new_dialog, null);
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

                //validace vstupů
                if (!InputValidation.validateIsEmpty(name)) {
                    itemNameLayout.setError(getString(R.string.item_name_is_empty));
                    return;
                }

                if (!InputValidation.validateIsEmpty(quantity)) {
                    itemQuantityLayout.setError(getString(R.string.quantity_is_empty));
                    return;
                }

                //vytvoření nové skladové položky
                StorageItem storageItem = new StorageItem(UserInformation.getInstance().getUserId(), name, unit);

                //vytvoření množství ke skladové položce
                ItemQuantity itemQuantity = new ItemQuantity();
                itemQuantity.setQuantity(Float.parseFloat(quantity));

                //vytvoření pomocné třídy pro udržení skaldové položky a množství
                StorageItemBox storageItemBox = new StorageItemBox(storageItem, itemQuantity, true);

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
    private void showExistsItemDialog() {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(BillNewActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.bill_item_exists_dialog, null);
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

                if (!InputValidation.validateIsEmpty(quantity)) {
                    layoutItemQuantity.setError(getString(R.string.quantity_is_empty));
                    return;
                }

                if (indexInSpinner == 0) {
                    //výpis o úspěšném uložení skladové položky
                    String message = getString(R.string.select_storage_item);
                    Toast.makeText(BillNewActivity.this, message, Toast.LENGTH_SHORT).show();
                    return;
                }

                int storageItemId = Integer.parseInt(storageItems[0][indexInSpinner - 1]);

                //pokud se jedná o příjem musím zkontrolovat, zda mám dostatečné množství ve skladu
                if (!isExpense) {
                    float storageQuantity = itemQuantityDatabaseHelper.getQuantityWithStorageItemId(storageItemId);
                    if (storageQuantity < Float.parseFloat(quantity)) {
                        layoutItemQuantity.setError(getString(R.string.no_quantity_required));
                        return;
                    }
                }

                //vytvoření skladové položky a vložení do listu položek
                StorageItem storageItem = storageItemDatabaseHelper.getStorageItemById(storageItemId);


                //vytvoření množství skladové položky
                ItemQuantity itemQuantity = new ItemQuantity();
                itemQuantity.setQuantity(Float.parseFloat(quantity));
                itemQuantity.setStorageItemId(storageItemId);
                StorageItemBox storageItemBox = new StorageItemBox(storageItem, itemQuantity, false);

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
     *
     * @param view view aktivity
     */
    public void submitNewBillForm(View view) {

        //validace povinných polí
        if (!inputValidation()) {
            return;
        }

        //načtení dat ze vstupních polí
        String name = billName.getText().toString();
        String amount = billAmount.getText().toString();
        String VAT = spinnerBillVat.getSelectedItem().toString();
        String date = mDisplayDate.getText().toString();
        int traderId;
        int billTypeId;


        //obchodník vybrán
        if (spinnerTrader.getSelectedItemId() != 0) {
            traderId = Integer.parseInt(traders[0][(int) spinnerTrader.getSelectedItemId() - 1]);
        } else {
            //obchodník nevybrán
            traderId = -1;
        }

        //druh faktury vybrán
        if (spinnerBillType.getSelectedItemId() != 0) {
            billTypeId = Integer.parseInt(billTypes[0][(int) spinnerBillType.getSelectedItemId() - 1]);
        } else {
            billTypeId = -1;
        }

        //vytvoření nové faktury a nastavení dat z aktivity
        Bill bill = new Bill();
        bill.setName(name);
        bill.setUserId(UserInformation.getInstance().getUserId());
        bill.setAmount(Float.parseFloat(amount));
        bill.setVAT(Integer.parseInt(VAT));
        bill.setDate(date);
        bill.setTraderId(traderId);
        bill.setTypeId(billTypeId);
        bill.setIsDirty(1);
        bill.setIsDeleted(0);
        //pokud je pořízen obrázek k faktuře
        if (picturePath != null) {
            bill.setPhoto(picturePath.toString());
        }
        //zda se jedná o příjem nebo výdaje
        if (isExpense) {
            bill.setIsExpense(1);
        } else {
            bill.setIsExpense(0);
        }

        //vložení příjmu do databáze
        long billId = billDatabaseHelper.addBill(bill, false);
        long storageItemId;

        //projdu list všech položek faktury
        for (int i = 0; i < listOfItems.size(); i++) {

            //jedná se o položku, která není ve skladu
            if (listOfItems.get(i).isNew()) {
                listOfItems.get(i).getStorageItem().setIsDirty(1);
                listOfItems.get(i).getStorageItem().setIsDeleted(0);
                storageItemId = storageItemDatabaseHelper.addStorageItem(listOfItems.get(i).getStorageItem(), false);
                listOfItems.get(i).getItemQuantity().setStorageItemId(storageItemId);
                listOfItems.get(i).getItemQuantity().setBillId(billId);
                listOfItems.get(i).getItemQuantity().setIsDirty(1);
                listOfItems.get(i).getItemQuantity().setIsDeleted(0);
                itemQuantityDatabaseHelper.addItemQuantity(listOfItems.get(i).getItemQuantity(), false);
            } else {
                //položka je již evidivoaná ve skladu
                if (!isExpense) {
                    //pokud se jedná o výdaj, přidávám se zápornou hodnotou => jako vyskladění
                    listOfItems.get(i).getItemQuantity().setBillId(billId);
                    listOfItems.get(i).getItemQuantity().setIsDirty(1);
                    listOfItems.get(i).getItemQuantity().setIsDeleted(0);
                    listOfItems.get(i).getItemQuantity().setQuantity(listOfItems.get(i).getItemQuantity().getQuantity() * (-1));
                    itemQuantityDatabaseHelper.addItemQuantity(listOfItems.get(i).getItemQuantity(), false);
                } else {
                    listOfItems.get(i).getItemQuantity().setBillId(billId);
                    listOfItems.get(i).getItemQuantity().setIsDirty(1);
                    listOfItems.get(i).getItemQuantity().setIsDeleted(0);
                    itemQuantityDatabaseHelper.addItemQuantity(listOfItems.get(i).getItemQuantity(), false);
                }
            }
        }

        //pokus o automatickou synchronizaci
        Push push = new Push(BillNewActivity.this);
        push.push();

        //vypis uživateli o úspěšném vložení faktruy do databáze
        String message = getString(R.string.income_has_been_added);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(BillNewActivity.this, BillActivity.class);
        intent.putExtra("IS_EXPENSE", isExpense);
        startActivity(intent);
        finish();
    }

    private boolean inputValidation() {
        String name = billName.getText().toString();
        String amount = billAmount.getText().toString();

        //validate povinných polí => částka a název faktury
        if (name.isEmpty()) {
            String message = getString(R.string.name_of_bill_is_empty);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            layoutBillName.setError(getString(R.string.name_of_bill_is_empty));
            return false;
        } else if (name.length() > TextInputLength.BILL_NAME_LENGTH) {
            String message = getString(R.string.input_is_too_long);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            layoutBillName.setError(getString(R.string.name_of_bill_is_empty));
            return false;
        }

        if (amount.isEmpty()) {
            String message = getString(R.string.amount_is_empty);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            layoutBillAmount.setError(getString(R.string.amount_is_empty));
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
    public boolean onNavigationItemSelected(MenuItem item) {
        //id vybrané položky v menu
        int id = item.getItemId();

        BillNewActivity thisActivity = BillNewActivity.this;
        Intent newIntent;

        //inicializace třídy menu, kde jsou definovány jednotlivé aktivity
        Menu menu = new Menu(thisActivity);
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



    /*
    Čast kodu, která se stará o přidělení přistupu aplikace ke kameře a uložišti
     */

    /**
     * Metoda, která nejprve zsjití zda byli přiděleny
     * potřebné oprávnění a potom zavolá metoda, která otevře fotoaparát.
     * Pokud nejsou oprávnění přidělena, je volána další metoda, která
     * se zeptá uživatele zda chce přidělit pověřní.
     */
    @AfterPermissionGranted(PERMISSION_REQUEST_CODE_CAMERA)
    public void callCamera() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (EasyPermissions.hasPermissions(this, perms)) {
            openCamera();
        } else {
            EasyPermissions.requestPermissions(this, getResources().getString(R.string.permission_info_camera), PERMISSION_REQUEST_CODE_CAMERA, perms);
        }
    }

    /**
     * Metoda, která nejprve zsjití zda byli přiděleny
     * potřebné oprávnění a potom zavolá metoda, která otevře galerii.
     * Pokud nejsou oprávnění přidělena, je volána další metoda, která
     * se zeptá uživatele zda chce přidělit pověřní.
     */
    @AfterPermissionGranted(PERMISSION_REQUEST_CODE_GALLERY)
    public void callGallery() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE};

        if (EasyPermissions.hasPermissions(this, perms)) {
            openGallery();
        } else {
            EasyPermissions.requestPermissions(this, getResources().getString(R.string.permission_info_gallery), PERMISSION_REQUEST_CODE_GALLERY, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }


}
