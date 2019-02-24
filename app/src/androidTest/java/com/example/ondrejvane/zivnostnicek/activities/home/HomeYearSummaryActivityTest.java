package com.example.ondrejvane.zivnostnicek.activities.home;

import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.activities.bill.BillActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class HomeYearSummaryActivityTest {

    @Rule
    public ActivityTestRule<HomeYearSummaryActivity> mActivityRule = new ActivityTestRule<>(HomeYearSummaryActivity.class);

    private HomeYearSummaryActivity homeYearSummaryActivity = null;

    @Before
    public void setUp() {
        homeYearSummaryActivity = mActivityRule.getActivity();
    }

    @Test
    public void testContext(){
        Context context = homeYearSummaryActivity.getBaseContext();
        assertEquals("com.example.ondrejvane.zivnostnicek", context.getPackageName());
    }

    @Test
    public void testLaunch(){
        View view;

        view = homeYearSummaryActivity.findViewById(R.id.spinnerHomeYearSummary);
        assertNotNull(view);

        view = homeYearSummaryActivity.findViewById(R.id.lineGraphYearSummary);
        assertNotNull(view);

        view = homeYearSummaryActivity.findViewById(R.id.textViewHomeYearSummaryIncome);
        assertNotNull(view);

        view = homeYearSummaryActivity.findViewById(R.id.textViewHomeYearSummaryExpense);
        assertNotNull(view);

        view = homeYearSummaryActivity.findViewById(R.id.textViewHomeYearSummaryBalance);
        assertNotNull(view);

    }

    @After
    public void tearDown() {
        homeYearSummaryActivity = null;
    }

}