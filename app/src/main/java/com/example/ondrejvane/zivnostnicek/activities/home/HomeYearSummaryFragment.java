package com.example.ondrejvane.zivnostnicek.activities.home;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeYearSummaryFragment extends Fragment {

    private static final String TAG = "HomeYearSummary";

    //prvky aktivity
    private Spinner yearSpinner;
    private LineChart lineChart;
    private TextView expenseTextView;
    private TextView incomeTextView;
    private TextView bilancTextView;

    //globální proměnné
    private int pickedYear;
    private BillDatabaseHelper billDatabaseHelper;
    private float expenseYear;
    private float incomeYear;


    public HomeYearSummaryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_year_summary, container, false);

        //inicializace aktivity
        initActivity(view);

        //inicializace nastavení
        setSettings();

        //nastavení dat do text view
        setDataToTextView();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //nastavení dat do grafu
                setDataToGraph();


            }
        });






        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    pickedYear = Integer.parseInt(yearSpinner.getSelectedItem().toString());
                } else {
                    //pokud není vybrán žádný rok, tak se zobrazuje aktuální rok
                    pickedYear = Calendar.getInstance().get(Calendar.YEAR);
                }

                Log.d(TAG, "Spinner");
                //aktualizace grafu
                setDataToGraph();

                //aktualizace text view
                setDataToTextView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return view;
    }

    private void initActivity(View view) {
        yearSpinner = view.findViewById(R.id.spinnerHomeYearSummary);
        lineChart = view.findViewById(R.id.lineGraphYearSummary);
        billDatabaseHelper = new BillDatabaseHelper(getContext());
        incomeTextView = view.findViewById(R.id.textViewHomeYearSummaryIncome);
        expenseTextView = view.findViewById(R.id.textViewHomeYearSummaryExpense);
        bilancTextView = view.findViewById(R.id.textViewHomeYearSummaryBalance);
        pickedYear = Calendar.getInstance().get(Calendar.YEAR);
    }

    private void setSettings() {
        Settings settings = Settings.getInstance();
        //pokud je vybrán jeden rok
        if(settings.isIsPickedOneYear()){
            yearSpinner.setEnabled(false);
            yearSpinner.setSelection(settings.getArrayYearId());
            pickedYear = Integer.parseInt(settings.getYear());
        }
    }


    private void setDataToTextView() {
        //zjištění výnosu
        float balance = incomeYear - expenseYear;

        //formátování hodnot
        String formattedIncome = FormatUtility.formatIncomeAmount(Float.toString(incomeYear));
        String formattedExpense = FormatUtility.formatExpenseAmount(Float.toString(expenseYear));
        String formattedBalance;

        //nastavení textu do aktivity
        incomeTextView.setText(formattedIncome);
        expenseTextView.setText(formattedExpense);

        if(balance > 0){
            formattedBalance = FormatUtility.formatBalanceAmount(balance);
            bilancTextView.setTextColor(getResources().getColor(R.color.income));
            bilancTextView.setText(formattedBalance);
            return;
        }

        if(balance < 0){
            formattedBalance = FormatUtility.formatBalanceAmount(balance);
            bilancTextView.setTextColor(getResources().getColor(R.color.expense));
            bilancTextView.setText(formattedBalance);
            return;
        }

        if (balance == 0) {
            bilancTextView.setTextColor(getResources().getColor(R.color.zero));
            bilancTextView.setText(getString(R.string.zero));
        }
    }

    private void setDataToGraph() {
        ArrayList<Entry> expenseValues = new ArrayList<>();
        ArrayList<Entry> incomeValues = new ArrayList<>();
        final ArrayList<String> labels = new ArrayList<>();
        float temp;
        expenseYear = 0;
        incomeYear = 0;
        String[] months = getResources().getStringArray(R.array.months);
        for(int i = 0; i < 12; i++){
            //načtení výdajů z databáze
            temp = billDatabaseHelper.getBillDataByDate(pickedYear, i+1, 1);
            expenseValues.add(new Entry(i, temp));
            expenseYear = expenseYear + temp;

            //načtení příjmů z databáze
            temp = billDatabaseHelper.getBillDataByDate(pickedYear, i+1, 0);
            incomeValues.add(new Entry(i, temp));
            incomeYear = incomeYear + temp;

            //načtení měsíců pro zobrazení do osy
            labels.add(months[i+1]);

        }

        LineDataSet setExpense, setIncome;

        setExpense = new LineDataSet(expenseValues, "Expense");
        setExpense.setColor(getResources().getColor(R.color.expense));
        setExpense.setCircleColor(getResources().getColor(R.color.expense));
        setExpense.setFillColor(getResources().getColor(R.color.expense));
        setExpense.setDrawFilled(true);


        setIncome = new LineDataSet(incomeValues, "Income");
        setIncome.setColor(getResources().getColor(R.color.income));
        setIncome.setCircleColor(getResources().getColor(R.color.income));
        setIncome.setFillColor(getResources().getColor(R.color.income));
        setIncome.setDrawFilled(true);

        LineData lineData = new LineData(setExpense, setIncome);

        lineChart.setData(lineData);
        lineChart.getAxisLeft().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.getDescription().setEnabled(false);
        //lineChart.animateXY(1000, 1000);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getAxisRight().setAxisMinimum(0f);
        lineChart.getAxisLeft().setAxisMinimum(0f);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter(){
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if(value%1 == 0){
                    return labels.get((int) value);
                }else{
                    return "";
                }
            }
        });

    }



}
