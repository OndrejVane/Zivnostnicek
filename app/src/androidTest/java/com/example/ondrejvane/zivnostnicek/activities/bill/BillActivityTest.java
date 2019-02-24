package com.example.ondrejvane.zivnostnicek.activities.bill;

import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.example.ondrejvane.zivnostnicek.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class BillActivityTest {

    @Rule
    public ActivityTestRule<BillActivity> mActivityRule = new ActivityTestRule<>(BillActivity.class);

    private BillActivity billActivity = null;

    @Before
    public void setUp() {
        billActivity = mActivityRule.getActivity();
    }

    @Test
    public void testContext(){
        Context context = billActivity.getBaseContext();
        assertEquals("com.example.ondrejvane.zivnostnicek", context.getPackageName());
    }

    @Test
    public void testLaunch(){
        View view;

        view = billActivity.findViewById(R.id.spinnerIncomeYear);
        assertNotNull(view);

        view = billActivity.findViewById(R.id.spinnerIncomeMonth);
        assertNotNull(view);

        view = billActivity.findViewById(R.id.listViewIncome);
        assertNotNull(view);

    }

    @After
    public void tearDown() {
        billActivity = null;
    }
}