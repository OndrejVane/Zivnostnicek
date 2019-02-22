package com.example.ondrejvane.zivnostnicek.activities.home;

import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.example.ondrejvane.zivnostnicek.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class HomeExpenseOrIncomeActivityTest {

    @Rule
    public ActivityTestRule<HomeExpenseOrIncomeActivity> mActivityRule = new ActivityTestRule<>(HomeExpenseOrIncomeActivity.class);

    private HomeExpenseOrIncomeActivity homeExpenseOrIncomeActivity = null;

    @Before
    public void setUp() {
        homeExpenseOrIncomeActivity = mActivityRule.getActivity();
    }

    @Test
    public void testContext(){
        Context context = homeExpenseOrIncomeActivity.getBaseContext();
        assertEquals("com.example.ondrejvane.zivnostnicek", context.getPackageName());
    }

    @Test
    public void testLaunch(){
        View view;

        view = homeExpenseOrIncomeActivity.findViewById(R.id.spinnerHomeYearExpense);
        assertNotNull(view);

        view = homeExpenseOrIncomeActivity.findViewById(R.id.spinnerHomeMonthExpense);
        assertNotNull(view);

        view = homeExpenseOrIncomeActivity.findViewById(R.id.pieGraphHomeExpense);
        assertNotNull(view);

        view = homeExpenseOrIncomeActivity.findViewById(R.id.listViewHomeExpense);
        assertNotNull(view);
    }

    @After
    public void tearDown() {
        homeExpenseOrIncomeActivity = null;
    }
}