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
public class LoginActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<>(LoginActivity.class);

    private LoginActivity loginActivity = null;

    @Before
    public void setUp() {
        loginActivity = mActivityRule.getActivity();
    }

    @Test
    public void testContext(){
        Context context = loginActivity.getBaseContext();
        assertEquals("com.example.ondrejvane.zivnostnicek", context.getPackageName());
    }

    @Test
    public void testLaunch(){
        View view;

        view = loginActivity.findViewById(R.id.imageViewLoginLogo);
        assertNotNull(view);

        view = loginActivity.findViewById(R.id.userAddress);
        assertNotNull(view);

        view = loginActivity.findViewById(R.id.userPassword);
        assertNotNull(view);

        view = loginActivity.findViewById(R.id.checkBox);
        assertNotNull(view);
    }

    @After
    public void tearDown() {
        loginActivity = null;
    }
}