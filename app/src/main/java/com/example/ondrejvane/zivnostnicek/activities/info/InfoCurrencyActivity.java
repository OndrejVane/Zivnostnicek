package com.example.ondrejvane.zivnostnicek.activities.info;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.helper.Header;
import com.example.ondrejvane.zivnostnicek.server.HttpsTrustManager;
import com.example.ondrejvane.zivnostnicek.server.RequestQueue;
import com.example.ondrejvane.zivnostnicek.session.Logout;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Aktivita pro zobrazení jednotlivých světový měn. Měny jsou načtené
 * z API čnb.
 */
public class InfoCurrencyActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, EasyPermissions.PermissionCallbacks {

    private static final String TAG = "InfoCurrency";

    //grafické prvky aktivity
    private TextView euroCurrency;
    private TextView gbCurrency;
    private TextView usaCurrency;
    private TextView canadaCurrency;
    private TextView swedenCurrency;
    private TextView date;
    private ProgressDialog pDialog;

    //pomocné gloální proměnné
    private static final String CNB_URL = "https://www.cnb.cz/cs/financni_trhy/devizovy_trh/kurzy_devizoveho_trhu/denni_kurz.txt";
    private static final String FILE_NAME = "denni_kurz.txt";
    private String stringFromFile;

    //kod požadavku přístupu
    private static final int PERMISSION_REQUEST_CODE = 345;

    /**
     * Metoda, která se provede při spuštění aktivity a provede nezbytné
     * úkony ke správnému fungování aktivity.
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_currency);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryDownloadFileFromServer(view);
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

        //pokus o stáhnutí souboru z CNB api
        tryDownloadFileFromServer(findViewById(android.R.id.content));

    }

    /**
     * Procedura, která inicializuje příslušné
     * grafické prvky.
     */
    private void initActivity() {
        euroCurrency = findViewById(R.id.textEuropeCurrency);
        gbCurrency = findViewById(R.id.textGBCurrency);
        usaCurrency = findViewById(R.id.textUSACurrency);
        canadaCurrency = findViewById(R.id.textCanadaCurrency);
        swedenCurrency = findViewById(R.id.textSwedenCurrency);
        date = findViewById(R.id.textDateInput);
    }

    /**
     * Pokud jsou v lokálním uložišti nějaká data, tak jsou načtena
     * a zobrazena do aktivity.
     */
    private void setTextToActivity() {
        //načtení dat z lokálního souboru
        stringFromFile = readFromFile();
        if (stringFromFile != null) {
            //parsování souboru a získání potřebných dat
            parseFileAndSetToTextField();
        } else {
            Toast.makeText(this, R.string.data_not_found, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Progress dialog pro v průběhu stahování souboru.
     */
    private void displayLoader() {
        pDialog = new ProgressDialog(InfoCurrencyActivity.this);
        pDialog.setMessage(getString(R.string.downloading_in_progress));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

    }


    /**
     * Metoda, která se pokusí o stáhnutí souboru z
     * api ČNB. Poté dojde k nastavení stažených dat do aktivity.
     *
     * @param view view aktivity
     */
    public void downloadFileFromServer(View view) {

        //zobrazení dialogového okna
        displayLoader();

        //povolení nedůvěryhodných certifikátu pro zabezpečené spojení
        HttpsTrustManager.allowAllSSL();

        //inicializace nového požadavku
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                CNB_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismiss();

                        //uložení řetězce do souboru pro pozdější použití
                        saveFile(response);

                        //zobrazení kurzů do aktivity
                        setTextToActivity();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //zkusit alespoň zobrazit starší data
                        setTextToActivity();

                        pDialog.dismiss();

                        //zobrazení informace uživateli, pokud došlo k chybě
                        Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.can_not_connect_to_the_server), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Error message: " + error.getMessage());
                    }
                }
        );

        RequestQueue.getInstance(this).addToRequestQueue(stringRequest);


    }


    /**
     * Metoda, pro parsování dat a zobrazení do aktivity
     */
    private void parseFileAndSetToTextField() {
        int index = stringFromFile.indexOf(" ");
        String[] splited = stringFromFile.split("\\|");
        for (int i = 0; i < splited.length; i++) {

            //měna EU
            if (splited[i].equals("EMU")) {
                euroCurrency.setText(splited[i + 4]);
            }

            //mena velké británíe
            if (splited[i].equals("Velká Británie")) {
                gbCurrency.setText(splited[i + 4]);
            }

            //mena USA
            if (splited[i].equals("USA")) {
                usaCurrency.setText(splited[i + 4]);
            }

            //mena kanady
            if (splited[i].equals("Kanada")) {
                canadaCurrency.setText(splited[i + 4]);
            }

            //mena švédska
            if (splited[i].equals("Švédsko")) {
                swedenCurrency.setText(splited[i + 4]);
            }

            //nastavení data aktualizace
            if (stringFromFile.length() > 0) {
                date.setText(stringFromFile.substring(0, index));
            }
        }
    }


    /**
     * Procedura pro stáhnutí textu z url čnb a uložení
     * do lokálního textového souboru.
     */
    private void saveFile(String file) {

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(InfoCurrencyActivity.this.openFileOutput(FILE_NAME, Context.MODE_PRIVATE));
            outputStreamWriter.write(file);
            outputStreamWriter.close();

        } catch (MalformedURLException e) {
            Log.d(TAG, "Malformed URL: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "I/O Error: " + e.getMessage());
        }
    }

    /**
     * Metoda, která se pokusí načíst data z lokálního
     * souboru a vrátí je jak řetězec.
     *
     * @return načtený řetězec
     */
    private String readFromFile() {

        Context context = InfoCurrencyActivity.this;
        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(FILE_NAME);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;


                while ((receiveString = bufferedReader.readLine()) != null) {
                    ret = ret + receiveString + "|";

                }
                inputStream.close();

            }
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found");
            return null;
        } catch (IOException e) {
            Log.d(TAG, "Can not read file");
            return null;
        }
        return ret;
    }

    /**
     * Procedura, která je volána po stisku tlačítka zpět.
     * Ukončí tuto aktivitu a spustí odpovídající.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        Intent intent = new Intent(InfoCurrencyActivity.this, InfoActivity.class);
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

        InfoCurrencyActivity thisActivity = InfoCurrencyActivity.this;
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

    /*
    Část kodu, která se stará o přidělení přistupu aplikace k uložišti.
     */

    @AfterPermissionGranted(PERMISSION_REQUEST_CODE)
    public void tryDownloadFileFromServer(View view) {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (EasyPermissions.hasPermissions(this, perms)) {
            downloadFileFromServer(view);
        } else {
            EasyPermissions.requestPermissions(this, getResources().getString(R.string.permission_info_currency), PERMISSION_REQUEST_CODE, perms);
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
