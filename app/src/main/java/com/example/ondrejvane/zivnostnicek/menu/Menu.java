package com.example.ondrejvane.zivnostnicek.menu;

import android.content.Context;
import android.content.Intent;

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.activities.HomeActivity;
import com.example.ondrejvane.zivnostnicek.activities.SynchronizationActivity;
import com.example.ondrejvane.zivnostnicek.activities.expense.ExpenseActivity;
import com.example.ondrejvane.zivnostnicek.activities.income.IncomeActivity;
import com.example.ondrejvane.zivnostnicek.activities.info.InfoActivity;
import com.example.ondrejvane.zivnostnicek.activities.settings.SettingsActivity;
import com.example.ondrejvane.zivnostnicek.activities.storage.StorageActivity;
import com.example.ondrejvane.zivnostnicek.activities.trader.TraderActivity;

/**
 * Tato třída slouží k obsluze vybrané položky v menu.
 */
public class Menu {

    private Context context;

    /**
     * Konstruktor třídy Menu.
     * @param context
     */
    public Menu(Context context){
        this.context = context;
    }

    /**
     * Metoda zjistí podle id, která položka menu byla
     * stisknuta a vrátí záměr příslušné aktivity. Pokud se jedná
     * o odhlášení vrací v záměru hodnotu null.
     * @param id    id položky v menu
     * @return      záměr příslušné aktivity
     */
    public Intent getMenu(int id){
        Intent intent = null;
        switch (id){

            case R.id.nav_home:
                intent = new Intent(this.context, HomeActivity.class);
                break;

            case R.id.nav_income:
                intent = new Intent(this.context, IncomeActivity.class);
                break;

            case R.id.nav_expense:
                intent = new Intent(this.context, ExpenseActivity.class);
                break;

            case R.id.nav_traders:
                intent = new Intent(this.context, TraderActivity.class);
                break;

            case R.id.nav_storage:
                intent = new Intent(this.context, StorageActivity.class);
                break;

            case R.id.nav_info:
                intent = new Intent(this.context, InfoActivity.class);
                break;

            case R.id.nav_sync:
                intent = new Intent(this.context, SynchronizationActivity.class);
                break;

            case R.id.nav_settings:
                intent = new Intent(this.context, SettingsActivity.class);
                break;

            case R.id.nav_logout:
                intent = null;
                break;

        }

        return intent;
    }
}
