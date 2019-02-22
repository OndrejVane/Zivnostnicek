package com.example.ondrejvane.zivnostnicek.activities.home;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.database.BillDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.helper.Settings;
import com.example.ondrejvane.zivnostnicek.utilities.FormatUtility;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeIncomeExpenseFragment extends Fragment {

    //prvky fragmentu
    private Spinner spinnerPickedYear;
    private Spinner spinnerPickedMonth;
    private PieChart pieChart;
    private TextView textViewIncome;
    private TextView textViewExpense;
    private TextView textViewBalance;

    //pomocné globální proměnné
    private BillDatabaseHelper billDatabaseHelper;
    private int pickedYear = -1;
    private int pickedMonth = -1;


    public HomeIncomeExpenseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_income_expense, container, false);

        //inicializace fragmentu
        initFragment(view);

        //implementace nastavení
        setSettings();


        //načtení dat z databáze
        float incomes = billDatabaseHelper.getBillDataByDate(pickedYear, pickedMonth, 0);
        float expense = billDatabaseHelper.getBillDataByDate(pickedYear, pickedMonth, 1);

        //zobrazení dat do grafu
        addDataToChart(incomes, expense);

        //akce při výběru roku ze spinner
        spinnerPickedYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    pickedYear = Integer.parseInt(spinnerPickedYear.getSelectedItem().toString());
                } else {
                    pickedYear = -1;
                }

                getDataAndSetToActivity();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //akce při výběru mesíce ze spinneru
        spinnerPickedMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    pickedMonth = position;
                } else {
                    pickedMonth = -1;
                }
                getDataAndSetToActivity();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return view;
    }

    private void initFragment(View view) {
        //inicializace prvků v aktivitě
        spinnerPickedYear = view.findViewById(R.id.spinnerHomeYear);
        spinnerPickedMonth = view.findViewById(R.id.spinnerHomeMonth);
        pieChart = view.findViewById(R.id.pieGraphHome);
        textViewIncome = view.findViewById(R.id.textViewHomeIncome);
        textViewExpense = view.findViewById(R.id.textViewHomeExpense);
        textViewBalance = view.findViewById(R.id.textViewHomeBalance);

        //inicializace databáze
        billDatabaseHelper = new BillDatabaseHelper(getContext());
    }

    private void addDataToChart(float incomes, float expense) {
        //inicializace grafu
        pieChart.setRotationEnabled(true);
        pieChart.setCenterText(getString(R.string.income_and_expense_space));
        pieChart.setCenterTextSize(20);
        pieChart.setCenterTextRadiusPercent(80);

        ArrayList<PieEntry> arrayData = new ArrayList<>();
        ArrayList<String> arrayDataStrings = new ArrayList<>();

        arrayData.add(new PieEntry(incomes, 0));
        arrayData.add(new PieEntry(expense, 1));

        arrayDataStrings.add(getString(R.string.income));
        arrayDataStrings.add(getString(R.string.expense));

        PieDataSet pieDataSet = new PieDataSet(arrayData, null);
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(15);

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(getResources().getColor(R.color.income));
        colors.add(getResources().getColor(R.color.expense));

        pieDataSet.setColors(colors);


        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
        pieChart.getLegend().setEnabled(false);
        //pieChart.animateXY(700, 700);
        pieChart.getDescription().setEnabled(false);

    }

    private void setSettings() {
        Settings settings = Settings.getInstance();
        //pokud je vybraný jeden rok
        if (settings.isIsPickedOneYear()) {
            spinnerPickedYear.setEnabled(false);
            spinnerPickedYear.setSelection(settings.getArrayYearId());
            pickedYear = Integer.parseInt(settings.getYear());
        }

        if (settings.isPickedOneMonth()) {
            spinnerPickedMonth.setEnabled(false);
            spinnerPickedMonth.setSelection(settings.getArrayMonthId());
            pickedMonth = settings.getArrayMonthId();
        }
    }

    private void getDataAndSetToActivity() {
        //načtení dat z databáze
        float incomes = billDatabaseHelper.getBillDataByDate(pickedYear, pickedMonth, 0);
        float expense = billDatabaseHelper.getBillDataByDate(pickedYear, pickedMonth, 1);
        float balance = incomes - expense;

        //nastavení dat do grafu
        addDataToChart(incomes, expense);

        //nastavení dat do text view
        String formattedIncomes = FormatUtility.formatIncomeAmount(Float.toString(incomes));
        String formattedExpense = FormatUtility.formatExpenseAmount(Float.toString(expense));
        String formattedBalance;
        textViewIncome.setText(formattedIncomes);
        textViewExpense.setText(formattedExpense);

        if(balance > 0){
            formattedBalance = FormatUtility.formatBalanceAmount(balance);
            textViewBalance.setTextColor(getResources().getColor(R.color.income));
            textViewBalance.setText(formattedBalance);
            return;
        }

        if(balance < 0){
            formattedBalance = FormatUtility.formatBalanceAmount(balance);
            textViewBalance.setTextColor(getResources().getColor(R.color.expense));
            textViewBalance.setText(formattedBalance);
            return;
        }

        if (balance == 0) {
            textViewBalance.setTextColor(getResources().getColor(R.color.zero));
            textViewBalance.setText(getString(R.string.zero));
        }
    }
}
