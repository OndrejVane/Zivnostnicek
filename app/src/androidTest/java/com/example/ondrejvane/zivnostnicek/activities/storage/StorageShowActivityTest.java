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

public class StorageShowActivityTest {

    @Rule
    public ActivityTestRule<StorageShowActivity> mActivityRule = new ActivityTestRule<>(StorageShowActivity.class);

    private StorageShowActivity storageShowActivity = null;

    @Before
    public void setUp() {
        storageShowActivity = mActivityRule.getActivity();
    }

    @Test
    public void testContext() {
        Context context = storageShowActivity.getBaseContext();
        assertEquals("com.example.ondrejvane.zivnostnicek", context.getPackageName());
    }

    @Test
    public void testLaunch() {
        View view;

        view = storageShowActivity.findViewById(R.id.showStorageItemName);
        assertNotNull(view);

        view = storageShowActivity.findViewById(R.id.showStorageItemQuantity);
        assertNotNull(view);

        view = storageShowActivity.findViewById(R.id.showInputEditTextNote);
        assertNotNull(view);


    }

    @After
    public void tearDown() {
        storageShowActivity = null;
    }

}