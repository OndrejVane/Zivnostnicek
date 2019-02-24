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

public class ShowPictureActivityTest {

    @Rule
    public ActivityTestRule<ShowPictureActivity> mActivityRule = new ActivityTestRule<>(ShowPictureActivity.class);

    private ShowPictureActivity showPictureActivity = null;

    @Before
    public void setUp() {
        showPictureActivity = mActivityRule.getActivity();
    }

    @Test
    public void testContext() {
        Context context = showPictureActivity.getBaseContext();
        assertEquals("com.example.ondrejvane.zivnostnicek", context.getPackageName());
    }

    @Test
    public void testLaunch() {
        View view;

        view = showPictureActivity.findViewById(R.id.touchImageView);
        assertNotNull(view);

    }

    @After
    public void tearDown() {
        showPictureActivity = null;
    }

}