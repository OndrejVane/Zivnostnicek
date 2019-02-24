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

public class HomeVATActivityTest {

    @Rule
    public ActivityTestRule<HomeVATActivity> mActivityRule = new ActivityTestRule<>(HomeVATActivity.class);

    private HomeVATActivity homeVATActivity = null;

    @Before
    public void setUp() {
        homeVATActivity = mActivityRule.getActivity();
    }

    @Test
    public void testContext(){
        Context context = homeVATActivity.getBaseContext();
        assertEquals("com.example.ondrejvane.zivnostnicek", context.getPackageName());
    }

    @Test
    public void testLaunch(){
        View view;

        view = homeVATActivity.findViewById(R.id.spinnerHomeVATYear);
        assertNotNull(view);

        view = homeVATActivity.findViewById(R.id.spinnerHomeVATMonth);
        assertNotNull(view);

        view = homeVATActivity.findViewById(R.id.pieGraphHomeVAT);
        assertNotNull(view);

        view = homeVATActivity.findViewById(R.id.textViewHomeInputVAT);
        assertNotNull(view);

        view = homeVATActivity.findViewById(R.id.textViewHomeOutputVAT);
        assertNotNull(view);

        view = homeVATActivity.findViewById(R.id.textViewHomeVATLabel);
        assertNotNull(view);

        view = homeVATActivity.findViewById(R.id.textViewHomeBalanceVAT);
        assertNotNull(view);



    }

    @After
    public void tearDown() {
        homeVATActivity = null;
    }

}