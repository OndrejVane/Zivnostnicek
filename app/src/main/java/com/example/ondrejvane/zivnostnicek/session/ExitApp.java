package com.example.ondrejvane.zivnostnicek.session;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.example.ondrejvane.zivnostnicek.R;

public class ExitApp {

    private Activity activity;
    private Context context;

    /**
     * Kontruktor třídy log out
     *
     * @param activity příslušná aktivita
     * @param context  kontext aktivity
     */
    public ExitApp(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
    }

    /**
     * Procedura, která zobrazí dialogové okno a zeptá
     * se uživatele, zda si přeje být odhlášen.
     */
    public void alertExitApp() {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setMessage(R.string.exit_question).setCancelable(false)
                .setPositiveButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.moveTaskToBack(true);
                    }
                });
        AlertDialog alertDialog = alert.create();
        alert.setTitle(R.string.logout);
        alertDialog.show();

    }
}
