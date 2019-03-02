package com.example.ondrejvane.zivnostnicek.activities.settings;

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

public class SettingsActivityTest {

    @Rule
    public ActivityTestRule<SettingsActivity> mActivityRule = new ActivityTestRule<>(SettingsActivity.class);

    private SettingsActivity settingsActivity = null;

    @Before
    public void setUp() {
        settingsActivity = mActivityRule.getActivity();
    }

    @Test
    public void testContext() {
        Context context = settingsActivity.getBaseContext();
        assertEquals("com.example.ondrejvane.zivnostnicek", context.getPackageName());
    }

    @Test
    public void testLaunch() {
        View view;

        view = settingsActivity.findViewById(R.id.checkBoxSettingsIN);
        assertNotNull(view);

        view = settingsActivity.findViewById(R.id.checkBoxSettingsTIN);
        assertNotNull(view);

        view = settingsActivity.findViewById(R.id.checkBoxSettingsYear);
        assertNotNull(view);

        view = settingsActivity.findViewById(R.id.spinnerSettingsYear);
        assertNotNull(view);

        view = settingsActivity.findViewById(R.id.checkBoxSettingsMonth);
        assertNotNull(view);

        view = settingsActivity.findViewById(R.id.spinnerSettingsMonth);
        assertNotNull(view);

    }

    @After
    public void tearDown() {
        settingsActivity = null;
    }
}