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

public class HomeActivityTest {

    @Rule
    public ActivityTestRule<HomeActivity> mActivityRule = new ActivityTestRule<>(HomeActivity.class);

    private HomeActivity homeActivity = null;

    @Before
    public void setUp() {
        homeActivity = mActivityRule.getActivity();
    }

    @Test
    public void testContext() {
        Context context = homeActivity.getBaseContext();
        assertEquals("com.example.ondrejvane.zivnostnicek", context.getPackageName());
    }

    @Test
    public void testLaunch() {
        View view;

        view = homeActivity.findViewById(R.id.spinnerHomeYear);
        assertNotNull(view);

        view = homeActivity.findViewById(R.id.spinnerHomeMonth);
        assertNotNull(view);

        view = homeActivity.findViewById(R.id.pieGraphHome);
        assertNotNull(view);

        view = homeActivity.findViewById(R.id.textViewHomeIncome);
        assertNotNull(view);

        view = homeActivity.findViewById(R.id.textViewHomeExpense);
        assertNotNull(view);

        view = homeActivity.findViewById(R.id.textViewHomeBalance);
        assertNotNull(view);

    }

    @After
    public void tearDown() {
        homeActivity = null;
    }

}