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

public class BillShowActivityTest {

    @Rule
    public ActivityTestRule<BillShowActivity> mActivityRule = new ActivityTestRule<>(BillShowActivity.class);

    private BillShowActivity billShowActivity = null;

    @Before
    public void setUp() {
        billShowActivity = mActivityRule.getActivity();
    }

    @Test
    public void testContext(){
        Context context = billShowActivity.getBaseContext();
        assertEquals("com.example.ondrejvane.zivnostnicek", context.getPackageName());
    }

    @Test
    public void testLaunch(){
        View view;

        view = billShowActivity.findViewById(R.id.textIncomeShowName);
        assertNotNull(view);

        view = billShowActivity.findViewById(R.id.textIncomeShowAmount);
        assertNotNull(view);

        view = billShowActivity.findViewById(R.id.textIncomeShowVAT);
        assertNotNull(view);

        view = billShowActivity.findViewById(R.id.textIncomeShowDate);
        assertNotNull(view);

        view = billShowActivity.findViewById(R.id.textIncomeShowTrader);
        assertNotNull(view);

        view = billShowActivity.findViewById(R.id.photoViewIncomeShow);
        assertNotNull(view);

        view = billShowActivity.findViewById(R.id.textViewIncomeShow);
        assertNotNull(view);

        view = billShowActivity.findViewById(R.id.listViewIncomeStorageItemShow);
        assertNotNull(view);

        view = billShowActivity.findViewById(R.id.textIncomeShowBillType);
        assertNotNull(view);


    }

    @After
    public void tearDown() {
        billShowActivity = null;
    }
}