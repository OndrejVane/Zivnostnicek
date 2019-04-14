package com.example.ondrejvane.zivnostnicek.activities.info;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.helper.Header;
import com.example.ondrejvane.zivnostnicek.model.model_helpers.Event;
import com.example.ondrejvane.zivnostnicek.server.HttpsTrustManager;
import com.example.ondrejvane.zivnostnicek.server.Server;
import com.example.ondrejvane.zivnostnicek.session.Logout;
import com.example.ondrejvane.zivnostnicek.utilities.FormatUtility;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Aktivita, která zobrazuje důležité termíny pro podnikatele a malé živnostníky.
 * Důležité termíny jsou načítány přes API ze serveru.
 */
public class InfoDateActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, EasyPermissions.PermissionCallbacks {

    private static final String TAG = "InfoDateActivity";

    //grafické prvky aktivity
    private TextView textViewDate1;
    private TextView textViewDate11;
    private TextView textViewDate2;
    private TextView textViewDate22;
    private TextView textViewDate3;
    private TextView textViewDate33;
    private TextView textViewDate4;
    private TextView textViewDate44;
    private TextView textViewDate5;
    private TextView textViewDate55;
    private TextView textViewDate6;
    private TextView textViewDate66;


    //kod požadavku přístupu
    private static final int PERMISSION_REQUEST_CODE = 345;

    //název uloženého souboru s daty
    private static final String FILE_NAME = "dates.txt";

    //pomocná proměnná pro návratovou hodnotu
    private int returnValue;

    //text ze souboru
    private String stringFromFile;


    /**
     * Metoda, která se provede při spuštění aktivity a provede nezbytné
     * úkony ke správnému fungování aktivity.
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_date);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadDataAsync();
                setTextToActivity();
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

        //zavolání metody pro nastavení textu do aktivity přes metodu, která se zeptá uživatele o povolení práv
        trySetTextToActivity();


        //nastavení naslouchače pro text view po stisknutí
        textViewDate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String date = textViewDate1.getText().toString().replace("*", "");
                String title = textViewDate11.getText().toString();
                Event event = new Event(title, date);
                addEventToTheCalendar(event);
            }
        });

        //nastavení naslouchače pro text view po stisknutí
        textViewDate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String date = textViewDate2.getText().toString().replace("*", "");
                String title = textViewDate22.getText().toString();
                Event event = new Event(title, date);
                addEventToTheCalendar(event);
            }
        });

        //nastavení naslouchače pro text view po stisknutí
        textViewDate3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String date = textViewDate3.getText().toString().replace("*", "");
                String title = textViewDate33.getText().toString();
                Event event = new Event(title, date);
                addEventToTheCalendar(event);
            }
        });

        //nastavení naslouchače pro text view po stisknutí
        textViewDate4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String date = textViewDate4.getText().toString().replace("*", "");
                String title = textViewDate44.getText().toString();
                Event event = new Event(title, date);
                addEventToTheCalendar(event);
            }
        });

        //nastavení naslouchače pro text view po stisknutí
        textViewDate5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String date = textViewDate5.getText().toString().replace("*", "");
                String title = textViewDate55.getText().toString();
                Event event = new Event(title, date);
                addEventToTheCalendar(event);
            }
        });

        //nastavení naslouchače pro text view po stisknutí
        textViewDate6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String date = textViewDate6.getText().toString().replace("*", "");
                String title = textViewDate66.getText().toString();
                Event event = new Event(title, date);
                addEventToTheCalendar(event);
            }
        });

    }

    /**
     * Procedura, která inicializuje grafické
     * prvky aktivity.
     */
    private void initActivity() {
        textViewDate1 = findViewById(R.id.textViewDate1);
        textViewDate11 = findViewById(R.id.textViewDate11);
        textViewDate2 = findViewById(R.id.textViewDate2);
        textViewDate22 = findViewById(R.id.textViewDate22);
        textViewDate3 = findViewById(R.id.textViewDate3);
        textViewDate33 = findViewById(R.id.textViewDate33);
        textViewDate4 = findViewById(R.id.textViewDate4);
        textViewDate44 = findViewById(R.id.textViewDate44);
        textViewDate5 = findViewById(R.id.textViewDate5);
        textViewDate55 = findViewById(R.id.textViewDate55);
        textViewDate6 = findViewById(R.id.textViewDate6);
        textViewDate66 = findViewById(R.id.textViewDate66);
    }

    /**
     * Procedura, která zkusí načíst data z lokálního souboru.
     * Pokud toto nepodaří informuje o tom uživatele.
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
     * Procedura, která rozdělí vstupní data na požadované informace
     * a zobrazí text do aktivity.
     */
    @SuppressLint("SetTextI18n")
    private void parseFileAndSetToTextField() {
        String[] splitted = stringFromFile.split(";");

        String year = splitted[0];

        textViewDate1.setText(FormatUtility.formatDateToShow(splitted[1]));
        textViewDate11.setText(splitted[2]);
        textViewDate2.setText(FormatUtility.formatDateToShow(splitted[3]));
        textViewDate22.setText(splitted[4]);
        textViewDate3.setText(FormatUtility.formatDateToShow(splitted[5]));
        textViewDate33.setText(splitted[6]);
        textViewDate4.setText(FormatUtility.formatDateToShow(splitted[7]));
        textViewDate44.setText(splitted[8]);
        textViewDate5.setText(FormatUtility.formatDateToShow(splitted[9]));
        textViewDate55.setText(splitted[10]);
        textViewDate6.setText(FormatUtility.formatDateToShow(splitted[11]));
        textViewDate66.setText(splitted[12]);

    }

    /**
     * Metoda, která je spuštěna na pozadí(v jiném vlákně
     * nez GUI). Metoda volá další metodu pro stažení dat
     * z internetu. Následně infomruje uživatele o výsledku akce.
     */
    public synchronized void downloadDataAsync() {
        //nesmí být ve stejném vlákně jako GUI
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                //stáhnutí souboru z url cnb
                downloadFile();
            }
        });
        //instarnet není dostupný
        if (returnValue == -1) {
            Toast.makeText(this, R.string.connect_to_internet, Toast.LENGTH_SHORT).show();
            //cnb není dostuoné
        } else if (returnValue == -2) {
            Toast.makeText(this, R.string.can_not_connect_to_the_server, Toast.LENGTH_SHORT).show();
            //úspěch
        } else if (returnValue == 1) {
            Toast.makeText(this, R.string.data_sucessfully_updated, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Procedura pro stáhnutí textu z url serveru a uložení
     * do lokálního textového souboru.
     */
    private void downloadFile() {
        String file = "";

        try {

            //povolení nedůvěryhodných certifikátu pro zabezpečené spojení
            HttpsTrustManager.allowAllSSL();

            URL url = new URL(Server.getInstance().getDatesUrl());
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            while ((line = in.readLine()) != null) {
                file = file + line + "\n";
            }
            in.close();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(InfoDateActivity.this.openFileOutput(FILE_NAME, Context.MODE_PRIVATE));
            outputStreamWriter.write(file);
            outputStreamWriter.close();
            returnValue = 1;

        } catch (MalformedURLException e) {
            returnValue = -2;
            Log.d(TAG, "Malformed URL: " + e.getMessage());
        } catch (IOException e) {
            returnValue = -1;
            Log.d(TAG, "I/O Error: " + e.getMessage());
        }
    }

    /**
     * Metoda, která se posí načíst data z lokálního
     * souboru a vrátí je jak řetězec.
     *
     * @return načtený řetězec
     */
    private String readFromFile() {

        Context context = InfoDateActivity.this;
        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(FILE_NAME);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;


                while ((receiveString = bufferedReader.readLine()) != null) {
                    ret = ret + receiveString + "\n";

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
     * Meotda, která vloží nouvou událost do
     * kalendáře.
     *
     * @param event Třída události, která obsahuje informace.
     */
    private void addEventToTheCalendar(Event event) {

        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType("vnd.android.cursor.item/event");


        long startTime = event.getCalendar().getTimeInMillis();
        long endTime = event.getCalendar().getTimeInMillis() + 60 * 60 * 1000;

        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime);
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime);
        intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);

        intent.putExtra(CalendarContract.Events.TITLE, event.getTitle());

        startActivity(intent);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

        Intent intent = new Intent(InfoDateActivity.this, InfoActivity.class);
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

        InfoDateActivity thisActivity = InfoDateActivity.this;
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
    public void trySetTextToActivity() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_CALENDAR};

        if (EasyPermissions.hasPermissions(this, perms)) {
            setTextToActivity();
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
