package com.example.ondrejvane.zivnostnicek.activities.home;


import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.database.NoteDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.database.TraderDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.helper.UserInformation;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeTradersFragment extends Fragment {

    //prvky aktivity
    private HorizontalBarChart barChart;

    //globální proměnné
    private String[] tradersName;
    private float[] tradersEvaluation;
    private NoteDatabaseHelper noteDatabaseHelper;
    private TraderDatabaseHelper traderDatabaseHelper;


    public HomeTradersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_traders, container, false);

        //inicializace aktivity
        initActivity(view);


        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //získání dat z databáze
                getDataFromDatabase();

                //nastavení dat do grafu
                setDataToGraph(tradersName, tradersEvaluation);
            }
        });



        return view;
    }

    private void initActivity(View view) {

        barChart = view.findViewById(R.id.barChartTraders);
        noteDatabaseHelper = new NoteDatabaseHelper(getContext());
        traderDatabaseHelper = new TraderDatabaseHelper(getContext());
    }

    private void getDataFromDatabase() {
        String[][] tempTraders;
        tempTraders = traderDatabaseHelper.getTradersData(UserInformation.getInstance().getUserId());

        tradersName = tempTraders[1];
        tradersEvaluation = new float[tradersName.length];

        for (int i = 0; i < tradersName.length; i++) {
            int tempTraderId = Integer.parseInt(tempTraders[0][i]);
            tradersEvaluation[i] = noteDatabaseHelper.getAvarageTatingByTraderId(tempTraderId);
        }
    }

    private void setDataToGraph(String[] tradersName, float[] tradersEvaluation) {
        // Create bars
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        // Create explanation labels for each bar
        final ArrayList<String> barLabels = new ArrayList<>();

        for (int i = 0; i < tradersEvaluation.length; i++) {
            barEntries.add(new BarEntry((float) i, tradersEvaluation[i]));
            barLabels.add(tradersName[i]);
        }

        // Create a data set from the entry values
        BarDataSet dataSet = new BarDataSet(barEntries, "Traders");
        // Set data set values to be visible on the graph
        dataSet.setDrawValues(true);

        // Create a data object from the data set
        BarData data = new BarData(dataSet);

        // Make the chart use the acquired data
        barChart.setData(data);

        // Display explanation labels
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(barLabels));
        barChart.getXAxis().setGranularity(1f);
        // Set the minimum and maximum bar values
        barChart.getAxisLeft().setAxisMaximum((float) 5.7);
        barChart.getAxisLeft().setAxisMinimum(0);


        // Set a color for each bar in the chart based on its value

        // Animate chart so that bars are sliding from left to right
        //barChart.animateXY(1000, 1000);

        // Hide grid lines
        barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        // Hide graph description
        barChart.getDescription().setEnabled(false);
        // Hide graph legend
        barChart.getLegend().setEnabled(false);

        // Set colors and font style
        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        data.setValueTextSize(13f);
        data.setValueTextColor(Color.DKGRAY);
        barChart.setScaleEnabled(false);
    }

}
