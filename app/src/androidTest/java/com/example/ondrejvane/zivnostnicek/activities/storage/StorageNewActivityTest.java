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

public class StorageNewActivityTest {


    @Rule
    public ActivityTestRule<StorageNewActivity> mActivityRule = new ActivityTestRule<>(StorageNewActivity.class);

    private StorageNewActivity storageNewActivity = null;

    @Before
    public void setUp() {
        storageNewActivity = mActivityRule.getActivity();
    }

    @Test
    public void testContext() {
        Context context = storageNewActivity.getBaseContext();
        assertEquals("com.example.ondrejvane.zivnostnicek", context.getPackageName());
    }

    @Test
    public void testLaunch() {
        View view;

        view = storageNewActivity.findViewById(R.id.inputTextStorageItemName);
        assertNotNull(view);

        view = storageNewActivity.findViewById(R.id.layoutStorageItemName);
        assertNotNull(view);

        view = storageNewActivity.findViewById(R.id.inputTextStorageItemQuantity);
        assertNotNull(view);

        view = storageNewActivity.findViewById(R.id.layoutStorageItemQuantity);
        assertNotNull(view);

        view = storageNewActivity.findViewById(R.id.spinnerUnit);
        assertNotNull(view);

        view = storageNewActivity.findViewById(R.id.textInputEditTextNote);
        assertNotNull(view);

    }

    @After
    public void tearDown() {
        storageNewActivity = null;
    }

}