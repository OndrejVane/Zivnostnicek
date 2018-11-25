package com.example.ondrejvane.zivnostnicek.helper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.database.DatabaseHelper;
import com.example.ondrejvane.zivnostnicek.model.User;

/**
 * Pomocná třída, která nastavuje obsah headeru
 */
public class Header extends AppCompatActivity {

    private Context context;
    private NavigationView navigationView;
    private DatabaseHelper databaseHelper;

    /**
     * Konstruktor třídy Header.
     * @param navigationView
     * @param context
     */
    public Header(NavigationView navigationView, Context context){
        this.context = context;
        this.navigationView = navigationView;
        this.databaseHelper = new DatabaseHelper(context);
    }

    /**
     * Metoda, která nastaví do headeru jméno uživatele a jeho email.
     * Údáje o uživateli jsou získány z databáze
     */
    public void setTextToHeader() {

        View headerView = navigationView.getHeaderView(0);
        TextView headerFullName = headerView.findViewById(R.id.header_user_full_name);
        TextView headerEmailAddress = headerView.findViewById(R.id.header_email_address);
        SharedPreferences sp = context.getSharedPreferences("USER", MODE_PRIVATE);
        String emailAddress = sp.getString("USER", "NULL");
        User user = databaseHelper.getUserByEmailAddress(emailAddress);
        headerFullName.setText(user.getFullName());
        headerEmailAddress.setText(user.getEmail());

    }
}
