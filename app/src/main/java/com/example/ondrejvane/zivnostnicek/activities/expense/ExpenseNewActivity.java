package com.example.ondrejvane.zivnostnicek.activities.expense;

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
import android.support.annotation.NonNull;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.activities.HomeActivity;
import com.example.ondrejvane.zivnostnicek.activities.info.InfoActivity;
import com.example.ondrejvane.zivnostnicek.activities.storage.StorageActivity;
import com.example.ondrejvane.zivnostnicek.activities.SynchronizationActivity;
import com.example.ondrejvane.zivnostnicek.activities.income.IncomeActivity;
import com.example.ondrejvane.zivnostnicek.activities.trader.TraderActivity;
import com.example.ondrejvane.zivnostnicek.database.BillDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.database.TraderDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.helper.Header;
import com.example.ondrejvane.zivnostnicek.helper.InputValidation;
import com.example.ondrejvane.zivnostnicek.helper.Logout;
import com.example.ondrejvane.zivnostnicek.helper.UserInformation;
import com.example.ondrejvane.zivnostnicek.model.Bill;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Aktivita, která se stará o vytvoření nového výdaje
 */
public class ExpenseNewActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private TextView mDisplayDate;
    private ImageView photoView;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private Calendar cal;
    private Spinner spinnerTrader;
    private Spinner spinnerVAT;
    private String[][] traders;

    private int traderId;
    private EditText inputExpenseName, inputExpenseAmount;
    private TextInputLayout inputLayoutExpenseName, inputLayoutExpenseAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_new);

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

        //načtení příslušného obchodníka ze spinneru
        spinnerTrader.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //pokud je není vybrá žádný záznam, tak není přidělený žádný obchodník
                if(position > 0){
                    //uložení id obchodníka do glob. proměnné
                    traderId = Integer.parseInt(traders[0][position-1]);
                }else {
                    traderId = -1;
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Procedura, která inicializuje potřebné prvky aktivity.
     */
    private void initActivity() {
        mDisplayDate = findViewById(R.id.editTextDate);
        photoView = findViewById(R.id.photoView);
        spinnerTrader = findViewById(R.id.spinnerTrader);
        spinnerVAT = findViewById(R.id.spinnerVAT);
        inputLayoutExpenseName = findViewById(R.id.textInputLayoutExpenseNumber);
        inputLayoutExpenseAmount = findViewById(R.id.textInputLayoutExpenseAmount);
        inputExpenseName = findViewById(R.id.textInputEditTextNumber);
        inputExpenseAmount = findViewById(R.id.textInputEditTextExpenseAmount);

        TraderDatabaseHelper traderDatabaseHelper = new TraderDatabaseHelper(ExpenseNewActivity.this);
        //načtení obchodníků z databáze
        traders = traderDatabaseHelper.getTradersData(UserInformation.getInstance().getUserId());
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

        DatePickerDialog dialog = new DatePickerDialog(ExpenseNewActivity.this,
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

    /**
     * Funcke, která změní kativitu po stisknutí
     * tlačítka zpět.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        Intent home = new Intent(ExpenseNewActivity.this, ExpenseActivity.class);
        startActivity(home);
        finish();
    }


    /**
     * Metoda, která vyvolá dialogové okno a táže
     * se uživatele, zda chce pořídit novou fotku nebo
     * vybrat fotku z galerie. Po té volá odpovídající
     * metody pro vyvolání aktivity.
     * @param view
     */
    public void getPicture(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(ExpenseNewActivity.this);
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

    private void setDataToTraderSpinner(){
        List<String> userList=new ArrayList<>();

        userList.add(getString(R.string.select_trader));

        for (int i = 0; i<traders[0].length; i++){
            userList.add(traders[1][i]);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(ExpenseNewActivity.this, android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, userList);

        spinnerTrader.setAdapter(adapter);

    }


    //TODO Tady jsem skončil.....=> tady musím ještě udělat načítání obchodníků z databáze
    //TODO a uložení celého výdaje do databáze

    /**
     * Metoda, která se stará o hlavní navigační menu aplikace
     * a přechod mezi hlavními aktivitami.
     * @param item  vybraná položka v menu
     * @return      boolean
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        ExpenseNewActivity thisActivity = ExpenseNewActivity.this;

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

    /**
     * Validace všech vstupních polí formuláře a
     * následné uložení výdaje do databáze.
     * @param view
     */
    public void addExpense(View view) {
        if(!InputValidation.validateIsEmpty(inputExpenseName.getText().toString())){
            String message = getString(R.string.company_name_is_empty);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            inputLayoutExpenseName.setError(getString(R.string.company_name_is_empty));
            return;
        }

        if(!InputValidation.validateIsEmpty(inputExpenseAmount.getText().toString())){
            String message = getString(R.string.company_name_is_empty);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            inputLayoutExpenseAmount.setError(getString(R.string.company_name_is_empty));
            return;
        }

        //naplnění třídy bill načtenými daty
        Bill bill = new Bill();
        bill.setBillNumber(inputExpenseName.getText().toString());
        bill.setBillAmount(Float.parseFloat(inputExpenseAmount.getText().toString()));
        bill.setBillDate(mDisplayDate.getText().toString());
        bill.setBillIsExpense(1);
        bill.setBillPhoto(((BitmapDrawable)photoView.getDrawable()).getBitmap());
        bill.setBillVAT(Integer.parseInt(spinnerVAT.getSelectedItem().toString()));
        bill.setBillUserId(UserInformation.getInstance().getUserId());

        if(traderId != -1){
            bill.setBillTraderId(traderId);
        }

        //přidání záznamu do databáze
        BillDatabaseHelper billDatabaseHelper = new BillDatabaseHelper(ExpenseNewActivity.this);
        billDatabaseHelper.addBill(bill);

        String message = getString(R.string.expense_is_created);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Intent income = new Intent(ExpenseNewActivity.this, ExpenseActivity.class);
        startActivity(income);
        finish();

    }


}
