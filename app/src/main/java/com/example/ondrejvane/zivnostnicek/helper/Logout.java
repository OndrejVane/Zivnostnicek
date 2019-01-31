package com.example.ondrejvane.zivnostnicek.helper;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.activities.LoginActivity;

public class Logout extends AppCompatActivity {

    private Activity activity;
    private Context context;

    /**
     * Kontruktor třídy log out
     *
     * @param activity příslušná aktivita
     * @param context  kontext aktivity
     */
    public Logout(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
    }

    /**
     * Pomocná procedura, která obaluje funkci pro zobrazení
     * dialogového okna a pro odhlášení.
     */
    public void logout() {
        alertLogOut();
    }

    /**
     * Procedura, která zobrazí dialogové okno a zeptá
     * se uživatele, zda si přeje být odhlášen.
     */
    private void alertLogOut() {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setMessage(R.string.log_out_question).setCancelable(false)
                .setPositiveButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logOutActivity();
                    }
                });
        AlertDialog alertDialog = alert.create();
        alert.setTitle(R.string.logout);
        alertDialog.show();

    }

    /**
     * Porcesura, která provede odhlášení uživtele
     * a spustí aktivitu pro přihlášení do aplikace.
     */
    private void logOutActivity() {

        //resetování instací pro user data a pro user nastavení
        Settings.getInstance().resetInstance();
        UserInformation.getInstance().resetInstace();

        //aktualizace dat v shared preferences o přihlášeném uživateli
        SharedPreferences sp = context.getSharedPreferences("USER", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("IS_LOGEDIN", false);
        editor.putString("USER", null);
        editor.apply();
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
        ((Activity) context).finish();
    }
}
