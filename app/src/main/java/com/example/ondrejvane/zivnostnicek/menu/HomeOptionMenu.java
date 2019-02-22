package com.example.ondrejvane.zivnostnicek.menu;

import android.content.Context;
import android.content.Intent;

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.activities.home.HomeActivity;
import com.example.ondrejvane.zivnostnicek.activities.home.HomeExpenseOrIncomeActivity;
import com.example.ondrejvane.zivnostnicek.activities.home.HomeTraderActivity;
import com.example.ondrejvane.zivnostnicek.activities.home.HomeVATActivity;
import com.example.ondrejvane.zivnostnicek.activities.home.HomeYearSummaryActivity;

public class HomeOptionMenu {

    private Context context;
    private static final String EXPENSE_KEY = "IS_EXPENSE";

    public HomeOptionMenu(Context context){
        this.context = context;
    }

    public Intent getMenu(int id) {
        Intent intent;

        switch (id) {
            case R.id.option_menu_home_income_and_expense:
                intent = new Intent(context, HomeActivity.class);
                return intent;
            case R.id.option_menu_home_year_summary:
                intent = new Intent(context, HomeYearSummaryActivity.class);
                return intent;
            case R.id.option_menu_home_income:
                intent = new Intent(context, HomeExpenseOrIncomeActivity.class);
                intent.putExtra(EXPENSE_KEY, false);
                return intent;
            case R.id.option_menu_home_expense:
                intent = new Intent(context, HomeExpenseOrIncomeActivity.class);
                intent.putExtra(EXPENSE_KEY, true);
                return intent;
            case R.id.option_menu_home_vat:
                intent = new Intent(context, HomeVATActivity.class);
                return intent;
            case R.id.option_menu_home_traders:
                intent = new Intent(context, HomeTraderActivity.class);
                return intent;
            default:
                intent = new Intent(context, HomeActivity.class);
                return intent;
        }
    }
}
