package com.example.ondrejvane.zivnostnicek.activities;

import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.example.ondrejvane.zivnostnicek.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class RegisterActivityTest {

    @Rule
    public ActivityTestRule<RegisterActivity> mActivityRule = new ActivityTestRule<>(RegisterActivity.class);

    private RegisterActivity registerActivity = null;

    @Before
    public void setUp() {
        registerActivity = mActivityRule.getActivity();
    }

    @Test
    public void testContext(){
        Context context = registerActivity.getBaseContext();
        assertEquals("com.example.ondrejvane.zivnostnicek", context.getPackageName());
    }

    @Test
    public void testLaunch() {
        View view;

        view = registerActivity.findViewById(R.id.name);
        assertNotNull(view);

        view = registerActivity.findViewById(R.id.userAddress);
        assertNotNull(view);

        view = registerActivity.findViewById(R.id.userPassword);
        assertNotNull(view);

        view = registerActivity.findViewById(R.id.userConfirmPassword);
        assertNotNull(view);
    }

    @After
    public void tearDown() {
        registerActivity = null;
    }
}