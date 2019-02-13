package com.example.ondrejvane.zivnostnicek.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.activities.home.HomeActivity;
import com.example.ondrejvane.zivnostnicek.helper.Settings;
import com.example.ondrejvane.zivnostnicek.helper.UserInformation;
import com.example.ondrejvane.zivnostnicek.model.User;
import com.example.ondrejvane.zivnostnicek.session.SessionHandler;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private SessionHandler sessionHandler;
    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //skryje horní panel
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();

        //zákaz orientace na šířku
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_main);

        sessionHandler = new SessionHandler(getApplicationContext());
        ImageView logo = findViewById(R.id.imageViewLogoMain);
        settings = Settings.getInstance();

        //zobrazit logo do aktivity => musí se nastavit takto pomocí knihovny, jinak vytvoří memory leak
        Glide.with(this).load(R.drawable.logo1).into(logo);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent;
                if(sessionHandler.isLoggedIn()){
                    Log.d(TAG, "User is logged in");
                    intent = new Intent(MainActivity.this, HomeActivity.class);

                    //načtení potřebných údajů z session handleru(shared preferences)
                    UserInformation userInformation = UserInformation.getInstance();
                    User user = sessionHandler.getUserDetails();
                    userInformation.setUserId(user.getId());
                    userInformation.setMail(user.getEmail());
                    userInformation.setFullName(user.getFullName());

                    //načtení dat o nstavení aplikace ze shared preferences
                    settings.readSettingsFromSharedPreferences(MainActivity.this);
                    startActivity(intent);
                    finish();
                }else {
                    Log.d(TAG, "User is not logged in");
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        }, 1000);
    }
}
