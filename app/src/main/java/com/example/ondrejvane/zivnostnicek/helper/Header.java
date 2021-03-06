package com.example.ondrejvane.zivnostnicek.helper;

import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.session.UserInformation;


/**
 * Pomocná třída, která nastavuje obsah headeru
 */
public class Header extends AppCompatActivity {


    private NavigationView navigationView;
    private UserInformation userInformation;

    /**
     * Konstruktor třídy Header.
     *
     * @param navigationView navigační zobrazení
     */
    public Header(NavigationView navigationView) {
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
