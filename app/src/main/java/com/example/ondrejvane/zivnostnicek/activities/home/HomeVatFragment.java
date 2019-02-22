package com.example.ondrejvane.zivnostnicek.activities.home;


import android.os.AsyncTask;
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
public class HomeVatFragment extends Fragment {

    //prvky aktivity
    private Spinner spinnerYear;
    private Spinner spinnerMonth;
    private PieChart pieChart;
    private TextView textViewInputVAT;
    private TextView textViewOutputVAT;
    private TextView textViewBalancVATLabel;
    private TextView textViewBalancVATAmount;

    //globální proměnné
    private BillDatabaseHelper billDatabaseHelper;
    private int pickedYear = -1;
    private int pickedMonth = -1;


    public HomeVatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_vat, container, false);
        //inicializace aktivity
        initActivity(view);

        //inicializace nastavení
        setSettings();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                //nastavení dat do aktivity
                getDataAndSetToActivity();
            }
        });




        //akce při výběru roku ze spinner
        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    pickedYear = Integer.parseInt(spinnerYear.getSelectedItem().toString());
                } else {
                    pickedYear = -1;
                }

                getDataAndSetToActivity();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //akce při výběru roku ze spinner
        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

    private void initActivity(View view) {
        spinnerYear = view.findViewById(R.id.spinnerHomeVATYear);
        spinnerMonth = view.findViewById(R.id.spinnerHomeVATMonth);
        pieChart = view.findViewById(R.id.pieGraphHomeVAT);
        textViewInputVAT = view.findViewById(R.id.textViewHomeInputVAT);
        textViewOutputVAT = view.findViewById(R.id.textViewHomeOutputVAT);
        textViewBalancVATLabel = view.findViewById(R.id.textViewHomeVATLabel);
        textViewBalancVATAmount = view.findViewById(R.id.textViewHomeBalanceVAT);

        billDatabaseHelper = new BillDatabaseHelper(getContext());
    }

    private void setSettings() {
        Settings settings = Settings.getInstance();
        //pokud je vybraný jeden rok
        if (settings.isIsPickedOneYear()) {
            spinnerYear.setEnabled(false);
            spinnerYear.setSelection(settings.getArrayYearId());
            pickedYear = Integer.parseInt(settings.getYear());
        }

        //pokud je vybraný jeden měsíc
        if (settings.isPickedOneMonth()) {
            spinnerMonth.setEnabled(false);
            spinnerMonth.setSelection(settings.getArrayMonthId());
            pickedMonth = settings.getArrayMonthId();
        }
    }

    private void getDataAndSetToActivity() {
        double inputAmountVAT = billDatabaseHelper.getBillVatByDate(pickedYear, pickedMonth, 1);
        double outputAmountVAT = billDatabaseHelper.getBillVatByDate(pickedYear, pickedMonth, 0);
        double balanceVAT = inputAmountVAT - outputAmountVAT;

        //nastavení dat do grafu
        addDataToChart(inputAmountVAT, outputAmountVAT);

        //nastavení dat do text view
        String formattedIncomes = FormatUtility.formatIncomeAmount(Double.toString(inputAmountVAT)).substring(1);
        String formattedExpense = FormatUtility.formatExpenseAmount(Double.toString(outputAmountVAT)).substring(1);
        String formattedBalance;
        textViewInputVAT.setText(formattedIncomes);
        textViewOutputVAT.setText(formattedExpense);

        if (balanceVAT > 0) {
            formattedBalance = FormatUtility.formatBalanceAmount((float) balanceVAT).substring(1);
            textViewBalancVATAmount.setTextColor(getResources().getColor(R.color.income));
            textViewBalancVATAmount.setText(formattedBalance);
            textViewBalancVATLabel.setText(getString(R.string.claim));
            return;
        }

        if (balanceVAT < 0) {
            formattedBalance = FormatUtility.formatBalanceAmount((float) balanceVAT).substring(1);
            textViewBalancVATAmount.setTextColor(getResources().getColor(R.color.expense));
            textViewBalancVATAmount.setText(formattedBalance);
            textViewBalancVATLabel.setText(getString(R.string.obligation));
            return;
        }

        if (balanceVAT == 0) {
            textViewBalancVATAmount.setTextColor(getResources().getColor(R.color.zero));
            textViewBalancVATAmount.setText(getString(R.string.zero));
        }
    }

    private void addDataToChart(double inputAmountVAT, double outputAmountVAT) {
        //inicializace grafu
        pieChart.setRotationEnabled(true);
        pieChart.setCenterText(getString(R.string.VAT_label));
        pieChart.setCenterTextSize(20);
        pieChart.setCenterTextRadiusPercent(80);

        ArrayList<PieEntry> arrayData = new ArrayList<>();
        ArrayList<String> arrayDataStrings = new ArrayList<>();

        arrayData.add(new PieEntry((float) inputAmountVAT, 0));
        arrayData.add(new PieEntry((float) outputAmountVAT, 1));

        arrayDataStrings.add(getString(R.string.received));
        arrayDataStrings.add(getString(R.string.paid));

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

}
