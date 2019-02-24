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

public class BillEditActivityTest {

    @Rule
    public ActivityTestRule<BillEditActivity> mActivityRule = new ActivityTestRule<>(BillEditActivity.class);

    private BillEditActivity billEditActivity = null;

    @Before
    public void setUp() {
        billEditActivity = mActivityRule.getActivity();
    }

    @Test
    public void testContext(){
        Context context = billEditActivity.getBaseContext();
        assertEquals("com.example.ondrejvane.zivnostnicek", context.getPackageName());
    }

    @Test
    public void testLaunch(){
        View view;

        view = billEditActivity.findViewById(R.id.textViewBillEditInfo);
        assertNotNull(view);

        view = billEditActivity.findViewById(R.id.textLayoutInputBillEditName);
        assertNotNull(view);

        view = billEditActivity.findViewById(R.id.textInputBillEditName);
        assertNotNull(view);

        view = billEditActivity.findViewById(R.id.textInputBillEditAmount);
        assertNotNull(view);

        view = billEditActivity.findViewById(R.id.textLayoutInputBillEditAmount);
        assertNotNull(view);

        view = billEditActivity.findViewById(R.id.spinnerVATBillEdit);
        assertNotNull(view);

        view = billEditActivity.findViewById(R.id.editTextBillEditDate);
        assertNotNull(view);

        view = billEditActivity.findViewById(R.id.spinnerTraderBillEdit);
        assertNotNull(view);

        view = billEditActivity.findViewById(R.id.spinnerEditBillType);
        assertNotNull(view);

        view = billEditActivity.findViewById(R.id.photoViewBillEdit);
        assertNotNull(view);

        view = billEditActivity.findViewById(R.id.listViewEditBillStorageItems);
        assertNotNull(view);

        view = billEditActivity.findViewById(R.id.buttonSaveEditBill);
        assertNotNull(view);

    }

    @After
    public void tearDown() {
        billEditActivity = null;
    }
}