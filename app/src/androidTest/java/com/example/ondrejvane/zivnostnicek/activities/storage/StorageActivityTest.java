package com.example.ondrejvane.zivnostnicek.activities.storage;

import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.example.ondrejvane.zivnostnicek.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class StorageActivityTest {

    @Rule
    public ActivityTestRule<StorageActivity> mActivityRule = new ActivityTestRule<>(StorageActivity.class);

    private StorageActivity storageActivity = null;

    @Before
    public void setUp() {
        storageActivity = mActivityRule.getActivity();
    }

    @Test
    public void testContext() {
        Context context = storageActivity.getBaseContext();
        assertEquals("com.example.ondrejvane.zivnostnicek", context.getPackageName());
    }

    @Test
    public void testLaunch() {
        View view;

        view = storageActivity.findViewById(R.id.listViewStorage);
        assertNotNull(view);

        view = storageActivity.findViewById(R.id.editTextSearchStorage);
        assertNotNull(view);


    }

    @After
    public void tearDown() {
        storageActivity = null;
    }
}