package com.example.ondrejvane.zivnostnicek.activities.sync;

import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.activities.home.HomeExpenseOrIncomeActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class SynchronizationActivityTest {

    @Rule
    public ActivityTestRule<SynchronizationActivity> mActivityRule = new ActivityTestRule<>(SynchronizationActivity.class);

    private SynchronizationActivity synchronizationActivity= null;

    @Before
    public void setUp() {
        synchronizationActivity  = mActivityRule.getActivity();
    }

    @Test
    public void testContext(){
        Context context = synchronizationActivity.getBaseContext();
        assertEquals("com.example.ondrejvane.zivnostnicek", context.getPackageName());
    }

    @Test
    public void testLaunch(){
        View view;

        view = synchronizationActivity.findViewById(R.id.checkBoxSyncOn);
        assertNotNull(view);

        view = synchronizationActivity.findViewById(R.id.checkBoxSyncWiFi);
        assertNotNull(view);

        view = synchronizationActivity.findViewById(R.id.textViewSyncInfo1);
        assertNotNull(view);

    }

    @After
    public void tearDown() {
        synchronizationActivity = null;
    }
}