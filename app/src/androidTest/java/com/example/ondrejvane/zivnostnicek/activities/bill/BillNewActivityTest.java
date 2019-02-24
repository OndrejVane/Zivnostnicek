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

public class BillNewActivityTest {

    @Rule
    public ActivityTestRule<BillNewActivity> mActivityRule = new ActivityTestRule<>(BillNewActivity.class);

    private BillNewActivity billNewActivity = null;

    @Before
    public void setUp() {
        billNewActivity = mActivityRule.getActivity();
    }

    @Test
    public void testContext(){
        Context context = billNewActivity.getBaseContext();
        assertEquals("com.example.ondrejvane.zivnostnicek", context.getPackageName());
    }

    @Test
    public void testLaunch(){
        View view;

        view = billNewActivity.findViewById(R.id.editTextDate);
        assertNotNull(view);

        view = billNewActivity.findViewById(R.id.photoView);
        assertNotNull(view);

        view = billNewActivity.findViewById(R.id.spinnerTrader);
        assertNotNull(view);

        view = billNewActivity.findViewById(R.id.spinnerBillType);
        assertNotNull(view);

        view = billNewActivity.findViewById(R.id.listViewIncomeStorageItem);
        assertNotNull(view);

        view = billNewActivity.findViewById(R.id.textInputEditName);
        assertNotNull(view);

        view = billNewActivity.findViewById(R.id.textInputEditTextAmount);
        assertNotNull(view);

        view = billNewActivity.findViewById(R.id.spinnerVATincome);
        assertNotNull(view);

        view = billNewActivity.findViewById(R.id.textInputLayoutIncomeNumber);
        assertNotNull(view);

        view = billNewActivity.findViewById(R.id.textInputLayoutIncomeAmount);
        assertNotNull(view);

        view = billNewActivity.findViewById(R.id.buttonAddBill);
        assertNotNull(view);

        view = billNewActivity.findViewById(R.id.textViewBillFill);
        assertNotNull(view);

    }

    @After
    public void tearDown() {
        billNewActivity = null;
    }
}