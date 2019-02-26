package com.example.ondrejvane.zivnostnicek.activities.bill;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.example.ondrejvane.zivnostnicek.helper.InputValidation;
import com.example.ondrejvane.zivnostnicek.helper.Logout;
import com.example.ondrejvane.zivnostnicek.utilities.PictureUtility;
import com.example.ondrejvane.zivnostnicek.model.StorageItemBox;
import com.example.ondrejvane.zivnostnicek.helper.UserInformation;
import com.example.ondrejvane.zivnostnicek.model.Bill;
import com.example.ondrejvane.zivnostnicek.model.ItemQuantity;
import com.example.ondrejvane.zivnostnicek.model.StorageItem;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Aktivity pro editaci faktury
 */
public class BillEditActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "BillEditActivity";

    //prvky aktivity
    private TextView textViewBillEditInfo;
    private TextInputLayout textLayoutInputBillEditName;
    private EditText textInputBillEditName;
    private TextInputLayout textLayoutInputBillEditAmount;
    private EditText textInputBillEditAmount;
    private Spinner spinnerVATBillEdit;
    private EditText editTextBillEditDate;
    private Spinner spinnerTraderBillEdit;
    private Spinner spinnerEditBillType;
    private ImageView photoViewBillEdit;
    private ExpandableHeightListView listViewEditBillStorageItems;
    private Button buttonSaveEditBill;

    //pomocné globální proměnné
    private int billId;
    private boolean isExpense;
    private Bill bill;
    private Calendar cal;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private Uri pictureUri = null;
    private String[][] traders;
    private String[][] billTypes;
    private String[][] storageItems;
    private ArrayList<StorageItemBox> listBillItems;
    private ArrayList<StorageItemBox> listBillItemsDeleted;
    private StorageItemDatabaseHelper storageItemDatabaseHelper;
    private BillDatabaseHelper billDatabaseHelper;
    private ItemQuantityDatabaseHelper itemQuantityDatabaseHelper;
    private TypeBillDatabaseHelper typeBillDatabaseHelper;

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
        setContentView(R.layout.activity_bill_edit);
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

        initActivity();
        Log.d(TAG, "Activity successfully init");


        //po kliknutí na položku faktury se smaže
        listViewEditBillStorageItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listBillItemsDeleted.add(listBillItems.remove(position));
                showBillItemsToActivity(listBillItems);
            }
        });

    }

    /**
     * Procedura, která porvádí inicializaci aktivity
     */
    private void initActivity() {
        //načtení dat z shared preferences
        //načtení bill id
        if (getIntent().hasExtra("BILL_ID")) {
            billId = Integer.parseInt(getIntent().getExtras().get("BILL_ID").toString());
        } else {
            billId = 1;
        }

        //načtení, zda se jedná o příjem nebo výdaj
        if (getIntent().hasExtra("IS_EXPENSE")) {
            isExpense = getIntent().getExtras().getBoolean("IS_EXPENSE", false);
        } else {
            isExpense = false;
        }


        //inicializace prvků aktivity
        textViewBillEditInfo = findViewById(R.id.textViewBillEditInfo);
        textLayoutInputBillEditName = findViewById(R.id.textLayoutInputBillEditName);
        textInputBillEditName = findViewById(R.id.textInputBillEditName);
        textInputBillEditAmount = findViewById(R.id.textInputBillEditAmount);
        textLayoutInputBillEditAmount = findViewById(R.id.textLayoutInputBillEditAmount);
        spinnerVATBillEdit = findViewById(R.id.spinnerVATBillEdit);
        editTextBillEditDate = findViewById(R.id.editTextBillEditDate);
        spinnerTraderBillEdit = findViewById(R.id.spinnerTraderBillEdit);
        spinnerEditBillType = findViewById(R.id.spinnerEditBillType);
        photoViewBillEdit = findViewById(R.id.photoViewBillEdit);
        listViewEditBillStorageItems = findViewById(R.id.listViewEditBillStorageItems);
        buttonSaveEditBill = findViewById(R.id.buttonSaveEditBill);

        //nastavení textu do aktivity
        seTextToActivity();

        //inicializace databáze
        storageItemDatabaseHelper = new StorageItemDatabaseHelper(this);
        TraderDatabaseHelper traderDatabaseHelper = new TraderDatabaseHelper(this);
        billDatabaseHelper = new BillDatabaseHelper(this);
        itemQuantityDatabaseHelper = new ItemQuantityDatabaseHelper(this);
        typeBillDatabaseHelper = new TypeBillDatabaseHelper(this);

        //získání dat z databáze
        bill = billDatabaseHelper.getBillById(billId);
        traders = traderDatabaseHelper.getTradersData(UserInformation.getInstance().getUserId());
        billTypes = typeBillDatabaseHelper.getTypeBillData(UserInformation.getInstance().getUserId());
        storageItems = storageItemDatabaseHelper.getStorageItemData(UserInformation.getInstance().getUserId());

        //inicializace kalendáře
        cal = Calendar.getInstance();

        //nastavení dat do aktivity
        textInputBillEditName.setText(bill.getName());
        textInputBillEditAmount.setText(Float.toString(bill.getAmount()));
        editTextBillEditDate.setText(bill.getDate());

        //nastavení obrázku do image view
        if (bill.getPhoto() != null && PictureUtility.tryReadPicture(Uri.parse(bill.getPhoto()), this)) {

            PictureUtility.setBitmap(Uri.parse(bill.getPhoto()), this, photoViewBillEdit);
        }

        //načtení položek faktury a uložení do listu
        listBillItems = itemQuantityDatabaseHelper.getItemQuantityAndStorageItemByBillId(billId);

        //zobrazení položek faktury
        showBillItemsToActivity(listBillItems);
        listBillItemsDeleted = new ArrayList<>();

        //zobrazení obchodníků do spinneru
        setDataToTraderSpinner(spinnerTraderBillEdit);

        //zobrazení typu faktur do spinneru
        setDataToBillTypeSpinner(spinnerEditBillType);
    }

    /**
     * Zobrazení položek faktury do aktivity.
     *
     * @param arrayList list s položkam faktury
     */
    private void showBillItemsToActivity(ArrayList<StorageItemBox> arrayList) {

        String[] billItemName;
        float[] billItemQuantity;
        String[] billItemUnit;
        boolean isEmpty = false;
        //pokud obsahuje faktura nějaké položky
        if (arrayList.size() != 0) {
            billItemName = new String[arrayList.size()];
            billItemQuantity = new float[arrayList.size()];
            billItemUnit = new String[arrayList.size()];
            for (int i = 0; i < arrayList.size(); i++) {
                billItemName[i] = arrayList.get(i).getStorageItem().getName();
                billItemQuantity[i] = arrayList.get(i).getItemQuantity().getQuantity();
                billItemUnit[i] = arrayList.get(i).getStorageItem().getUnit();
            }
        } else {
            //pokud neobsahuje žádné položky
            billItemName = new String[]{getString(R.string.no_bill_items)};
            billItemQuantity = new float[1];
            billItemUnit = new String[]{""};
            isEmpty = true;
        }

        //nastavení adapteru do list view pro zobrazení
        ListViewBillItemAdapter listViewItemAdapter = new ListViewBillItemAdapter(this, billItemName, billItemQuantity, billItemUnit);
        //skryje obrázek koše
        if (isEmpty) {
            listViewItemAdapter.isTrashHidden(true);
        }
        listViewEditBillStorageItems.setAdapter(listViewItemAdapter);
        listViewEditBillStorageItems.setExpanded(true);
    }

    /**
     * Pokud se jendá o výdaj, tak tato procedura
     * nastaví příslušný text do aktivity
     */
    private void seTextToActivity() {
        if (isExpense) {
            setTitle(getString(R.string.title_activity_expense_edit));
            buttonSaveEditBill.setText(R.string.save_expense);
            textViewBillEditInfo.setText(R.string.edit_information_about_expense);
        }
    }

    /**
     * Procedure, která nastaví všechny obchodníky do spinneru.
     *
     * @param spinnerTrader spinner s obchodníky
     */
    private void setDataToTraderSpinner(Spinner spinnerTrader) {
        List<String> traderList = new ArrayList<>();

        traderList.add(getString(R.string.select_trader));


        for (int i = 0; i < traders[0].length; i++) {
            traderList.add(traders[1][i]);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, traderList);

        spinnerTrader.setAdapter(adapter);

    }


    /**
     * Procedura, která nastaví všechny existující
     * typy faktur do spinneru.
     *
     * @param spinnerBillType spinner pro výběr typu faktury
     */
    private void setDataToBillTypeSpinner(Spinner spinnerBillType) {
        List<String> typeList = new ArrayList<>();

        typeList.add(getString(R.string.select_bill_type));

        for (int i = 0; i < billTypes[0].length; i++) {
            typeList.add(billTypes[1][i]);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, typeList);

        spinnerBillType.setAdapter(adapter);
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

        DatePickerDialog dialog = new DatePickerDialog(BillEditActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener,
                year, month, day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = day + "." + month + "." + year;
                editTextBillEditDate.setText(date);
            }
        };
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.take_picture_from)
                .setItems(R.array.photo, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto, 1);
                        } else {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, 0);
                        }
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    /**
     * Metoda, která vykreslí dialogové okno pro přidání položky
     * katury. NOV8 vs EXISTUJÍCÍ
     *
     * @param view view aktivity
     */
    public void getStorageItem(View view) {
        if (!isExpense) {
            showExistsItemDialog();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(BillEditActivity.this);
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
     * Tato metoda zobrazí dialogové okno pro přidání
     * nové položky faktury. (Vytvoření nové skladové položky)
     */
    private void showNewItemDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(BillEditActivity.this);
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
                itemQuantity.setId(-1);

                //vytvoření pomocné třídy pro udržení skaldové položky a množství
                StorageItemBox storageItemBox = new StorageItemBox(storageItem, itemQuantity, true);

                //přidání položky faktury do listu
                listBillItems.add(storageItemBox);

                //zobrazení dat do listu
                showBillItemsToActivity(listBillItems);

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
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(BillEditActivity.this);
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
                    Toast.makeText(BillEditActivity.this, message, Toast.LENGTH_SHORT).show();
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

                //přidání položky faktury do listu
                listBillItems.add(storageItemBox);

                //zobrazení dat do listu
                showBillItemsToActivity(listBillItems);

                //zavření dialogu
                dialog.dismiss();

            }
        });

    }

    /**
     * Metoda, která zobrazí všechny skladové položky do spinneru.
     *
     * @param spinnerStorageItem spinner pro skladové položky
     */
    private void setDataToStorageItemSpinner(Spinner spinnerStorageItem) {
        List<String> storageItemList = new ArrayList<>();

        storageItemList.add(getString(R.string.select_storage_item));

        for (int i = 0; i < storageItems[0].length; i++) {
            storageItemList.add(storageItems[1][i]);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(BillEditActivity.this, android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, storageItemList);

        spinnerStorageItem.setAdapter(adapter);
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
                case 0:
                    if (resultCode == RESULT_OK) {
                        //uložení odkazu na ubrázek do globální proměnné
                        pictureUri = data.getData();
                        //převedení uri na bitmapu pro zobrazení
                        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                        //zobrazení bitmapy
                        photoViewBillEdit.setImageBitmap(bitmap);
                    }
                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        //získání Uri z intentu
                        Uri pickedImage = data.getData();
                        //uložení odkazu na obrázek do globální proměnné
                        pictureUri = pickedImage;
                        //získání bitmapy z Uri
                        Bitmap bitmap = PictureUtility.getBitmapFromUri(pickedImage, this);
                        //nastavení bitmapy do image view
                        photoViewBillEdit.setImageBitmap(bitmap);
                    }
            }
        } catch (NullPointerException e) {
            pictureUri = null;
        }
    }


    /**
     * Po stisknutí tlačítka zpět ukončí tuto
     * aktivitu a přepne do aktivity pro zobrazení
     * faktury.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        Intent intent = new Intent(BillEditActivity.this, BillShowActivity.class);
        intent.putExtra("BILL_ID", billId);
        intent.putExtra("IS_EXPENSE", isExpense);
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

        BillEditActivity thisActivity = BillEditActivity.this;
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

    /**
     * Metoda, která zpracuje vstupní formulář a aktualizuje
     * data v databázi.
     *
     * @param view
     */
    public void updateBillForm(View view) {

        //načtení vstupních polí
        String name = textInputBillEditName.getText().toString();
        String amount = textInputBillEditAmount.getText().toString();
        String VAT = spinnerVATBillEdit.getSelectedItem().toString();
        String date = editTextBillEditDate.getText().toString();
        int traderId;
        int billTypeId;


        //validate povinných polí => částka a název faktury
        if (!InputValidation.validateIsEmpty(name)) {
            String message = getString(R.string.name_of_bill_is_empty);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            textLayoutInputBillEditName.setError(getString(R.string.name_of_bill_is_empty));
            return;
        }

        if (!InputValidation.validateIsEmpty(amount)) {
            String message = getString(R.string.amount_is_empty);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            textLayoutInputBillEditAmount.setError(getString(R.string.amount_is_empty));
            return;
        }

        //obchodník vybrán
        if (spinnerTraderBillEdit.getSelectedItemId() != 0) {
            traderId = Integer.parseInt(traders[0][(int) spinnerTraderBillEdit.getSelectedItemId() - 1]);
        } else {
            //obchodník nevybrán
            traderId = -1;
        }

        //druh faktury vybrán
        if (spinnerEditBillType.getSelectedItemId() != 0) {
            billTypeId = Integer.parseInt(billTypes[0][(int) spinnerEditBillType.getSelectedItemId() - 1]);
        } else {
            billTypeId = -1;
        }

        bill.setId(billId);
        bill.setUserId(UserInformation.getInstance().getUserId());
        bill.setName(name);
        bill.setAmount(Float.parseFloat(amount));
        bill.setVAT(Integer.parseInt(VAT));
        bill.setDate(date);
        bill.setTraderId(traderId);
        bill.setTypeId(billTypeId);
        bill.setIsDirty(1);
        bill.setIsDeleted(0);

        //pokud je pořízena fotka, tak uložit do db
        if (pictureUri != null) {
            bill.setPhoto(pictureUri.toString());
        }

        //zda se jedná o příjem nebo výdaje
        if (isExpense) {
            bill.setIsExpense(1);
        } else {
            bill.setIsExpense(0);
        }

        //uložení aktualizovaného záznamu do databáze
        billDatabaseHelper.updateBill(bill);
        long storageItemId;

        //smazání všech smazaných položek z faktury
        for (int i = 0; i < listBillItemsDeleted.size(); i++) {
            itemQuantityDatabaseHelper.deleteItemQuantityById(listBillItemsDeleted.get(i).getItemQuantity().getId());
        }

        //přídání všech nových položek faktruy do databáze
        for (int i = 0; i < listBillItems.size(); i++) {
            //pokud se jedná o novou
            if (listBillItems.get(i).isNew()) {
                //nově přidaná skladová položka
                if (listBillItems.get(i).getItemQuantity().getId() == -1) {
                    listBillItems.get(i).getStorageItem().setIsDirty(1);
                    listBillItems.get(i).getStorageItem().setIsDeleted(0);
                    storageItemId = storageItemDatabaseHelper.addStorageItem(listBillItems.get(i).getStorageItem());
                    listBillItems.get(i).getItemQuantity().setStorageItemId(storageItemId);
                    listBillItems.get(i).getItemQuantity().setBillId(billId);
                    listBillItems.get(i).getItemQuantity().setIsDirty(1);
                    listBillItems.get(i).getItemQuantity().setIsDirty(0);
                    itemQuantityDatabaseHelper.addItemQuantity(listBillItems.get(i).getItemQuantity());
                } else {
                    //položka je již evidivoaná ve skladu, přidám pouze nové množství
                    if (!isExpense) {
                        //pokud se jedná o výdaj, přidávám se zápornou hodnotou => jako vyskladění
                        listBillItems.get(i).getItemQuantity().setBillId(billId);
                        listBillItems.get(i).getItemQuantity().setIsDirty(1);
                        listBillItems.get(i).getItemQuantity().setIsDirty(0);
                        listBillItems.get(i).getItemQuantity().setQuantity(listBillItems.get(i).getItemQuantity().getQuantity() * (-1));
                        itemQuantityDatabaseHelper.addItemQuantity(listBillItems.get(i).getItemQuantity());
                    } else {
                        listBillItems.get(i).getItemQuantity().setBillId(billId);
                        listBillItems.get(i).getItemQuantity().setIsDirty(1);
                        listBillItems.get(i).getItemQuantity().setIsDirty(0);
                        itemQuantityDatabaseHelper.addItemQuantity(listBillItems.get(i).getItemQuantity());
                    }
                }

            }
        }

        //vypis uživateli o úspěšné editaci faktury a uložení do databáze

        String message = getString(R.string.income_has_been_saved);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(BillEditActivity.this, BillShowActivity.class);
        intent.putExtra("IS_EXPENSE", isExpense);
        intent.putExtra("BILL_ID", billId);
        startActivity(intent);
        finish();
    }


}
