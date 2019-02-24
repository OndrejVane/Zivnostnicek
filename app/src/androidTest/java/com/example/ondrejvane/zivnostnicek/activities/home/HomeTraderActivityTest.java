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

public class HomeTraderActivityTest {

    @Rule
    public ActivityTestRule<HomeTraderActivity> mActivityRule = new ActivityTestRule<>(HomeTraderActivity.class);

    private HomeTraderActivity homeTraderActivity = null;

    @Before
    public void setUp() {
        homeTraderActivity = mActivityRule.getActivity();
    }

    @Test
    public void testContext(){
        Context context = homeTraderActivity.getBaseContext();
        assertEquals("com.example.ondrejvane.zivnostnicek", context.getPackageName());
    }

    @Test
    public void testLaunch(){
        View view;

        view = homeTraderActivity.findViewById(R.id.barChartTraders);
        assertNotNull(view);


    }

    @After
    public void tearDown() {
        homeTraderActivity = null;
    }

}