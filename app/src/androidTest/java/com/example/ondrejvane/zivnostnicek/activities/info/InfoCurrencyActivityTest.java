package com.example.ondrejvane.zivnostnicek.activities.info;

import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.activities.home.HomeActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class InfoCurrencyActivityTest {

    @Rule
    public ActivityTestRule<InfoCurrencyActivity> mActivityRule = new ActivityTestRule<>(InfoCurrencyActivity.class);

    private InfoCurrencyActivity infoCurrencyActivity = null;

    @Before
    public void setUp() {
        infoCurrencyActivity = mActivityRule.getActivity();
    }

    @Test
    public void testContext() {
        Context context = infoCurrencyActivity.getBaseContext();
        assertEquals("com.example.ondrejvane.zivnostnicek", context.getPackageName());
    }

    @Test
    public void testLaunch() {
        View view;

        view = infoCurrencyActivity.findViewById(R.id.textEuropeCurrency);
        assertNotNull(view);

        view = infoCurrencyActivity.findViewById(R.id.textGBCurrency);
        assertNotNull(view);

        view = infoCurrencyActivity.findViewById(R.id.textUSACurrency);
        assertNotNull(view);

        view = infoCurrencyActivity.findViewById(R.id.textCanadaCurrency);
        assertNotNull(view);

        view = infoCurrencyActivity.findViewById(R.id.textSwedenCurrency);
        assertNotNull(view);

        view = infoCurrencyActivity.findViewById(R.id.textDateInput);
        assertNotNull(view);

    }

    @After
    public void tearDown() {
        infoCurrencyActivity = null;
    }
}