package com.example.ondrejvane.zivnostnicek.activities.info;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.helper.Header;
import com.example.ondrejvane.zivnostnicek.helper.Logout;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;

public class InfoCurrencyActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView euroCurrency;
    private TextView gbCurrency;
    private TextView usaCurrency;
    private TextView canadaCurrency;
    private TextView swedenCurrency;
    private TextView date;
    private String  cnbURL = "http://www.cnb.cz/cs/financni_trhy/devizovy_trh/kurzy_devizoveho_trhu/denni_kurz.txt";
    private String stringFromFile;
    //globální proměnná pro předání hodnoty z vlákna -1=není připojen k internetu -2=web není dostupný 1=OK
    private int returnValue;

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

        Header header = new Header( navigationView, this);
        header.setTextToHeader();

        initActivity();

        setTextToActivity();
    }

    private void initActivity() {
        euroCurrency = findViewById(R.id.textEuropeCurrency);
        gbCurrency = findViewById(R.id.textGBCurrency);
        usaCurrency = findViewById(R.id.textUSACurrency);
        canadaCurrency = findViewById(R.id.textCanadaCurrency);
        swedenCurrency = findViewById(R.id.textSwedenCurrency);
        date = findViewById(R.id.textDateInput);
    }

    private void setTextToActivity() {
        stringFromFile = readFromFile();
        if(stringFromFile != null){
            parseFileAndSetToTextField();
        }else {
            Toast.makeText(this, "Data not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void parseFileAndSetToTextField() {
        int index = stringFromFile.indexOf(" ");
        String[] splited = stringFromFile.split("\\|");
        for (int i = 0; i<splited.length; i++){

            if(splited[i].equals("EMU")){
                euroCurrency.setText(splited[i+4]);
            }

            if(splited[i].equals("Velká Británie")){
                gbCurrency.setText(splited[i+4]);
            }

            if(splited[i].equals("USA")){
                usaCurrency.setText(splited[i+4]);
            }

            if(splited[i].equals("Kanada")){
                canadaCurrency.setText(splited[i+4]);
            }

            if(splited[i].equals("Švédsko")){
                swedenCurrency.setText(splited[i+4]);
            }
        }
        date.setText(stringFromFile.substring(0, index));
    }

    public synchronized void downloadDataAsync(){
        //nesmí být ve stejném vlákně jako GUI
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                downloadFile();
            }
        });
        if(returnValue == -1){
            Toast.makeText(this, "Connect to internet", Toast.LENGTH_SHORT).show();
        }else if(returnValue == -2){
            Toast.makeText(this, "Website is not reachable", Toast.LENGTH_SHORT).show();
        }else if(returnValue == 1){
            Toast.makeText(this, "Data successfully updated", Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadFile() {
        String file = "";

        try {

            URL url = new URL(cnbURL);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            while ((line = in.readLine()) != null) {
                file = file + line + "\n";
            }
            in.close();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(InfoCurrencyActivity.this.openFileOutput("denni_kurz.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(file);
            outputStreamWriter.close();
            returnValue = 1;

        }
        catch (MalformedURLException e) {
            returnValue = -2;
            System.out.println("Malformed URL: " + e.getMessage());
        }
        catch (IOException e) {
            returnValue = -1;
            System.out.println("I/O Error: " + e.getMessage());
        }
    }

    private String readFromFile() {

        Context context = InfoCurrencyActivity.this;
        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("denni_kurz.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;


                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    ret = ret + receiveString + "|";

                }
                inputStream.close();

            }
        }
        catch (FileNotFoundException e) {
            System.out.println("Can not read file");
            return null;
        } catch (IOException e) {
            System.out.println("File not found");
            return null;
        }
        return ret;
    }

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
     * @param item  vybraná položka v menu
     * @return      boolean
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //id vybrané položky v menu
        int id = item.getItemId();

        InfoCurrencyActivity thisActivity = InfoCurrencyActivity.this;
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
