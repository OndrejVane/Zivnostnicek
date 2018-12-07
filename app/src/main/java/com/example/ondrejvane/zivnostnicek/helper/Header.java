package com.example.ondrejvane.zivnostnicek.helper;

import android.content.Context;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.ondrejvane.zivnostnicek.R;


/**
 * Pomocná třída, která nastavuje obsah headeru
 */
public class Header extends AppCompatActivity {

    private Context context;
    private NavigationView navigationView;
    private UserInformation userInformation;

    /**
     * Konstruktor třídy Header.
     * @param navigationView
     * @param context
     */
    public Header(NavigationView navigationView, Context context){
        this.context = context;
        this.navigationView = navigationView;
        this.userInformation = UserInformation.getInstance();
    }

    /**
     * Metoda, která nastaví do headeru jméno uživatele a jeho email.
     * Údáje o uživateli jsou získány z třídy UserInformation.
     */
    public void setTextToHeader() {

        View headerView = navigationView.getHeaderView(0);
        TextView headerFullName = headerView.findViewById(R.id.header_user_full_name);
        TextView headerEmailAddress = headerView.findViewById(R.id.header_email_address);
        headerFullName.setText(userInformation.getFullName());
        headerEmailAddress.setText(userInformation.getMail());

    }
}
