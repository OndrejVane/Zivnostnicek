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

public class StorageEditActivityTest {
    @Rule
    public ActivityTestRule<StorageEditActivity> mActivityRule = new ActivityTestRule<>(StorageEditActivity.class);

    private StorageEditActivity storageEditActivity = null;

    @Before
    public void setUp() {
        storageEditActivity = mActivityRule.getActivity();
    }

    @Test
    public void testContext(){
        Context context = storageEditActivity.getBaseContext();
        assertEquals("com.example.ondrejvane.zivnostnicek", context.getPackageName());
    }

    @Test
    public void testLaunch(){
        View view;

        view = storageEditActivity.findViewById(R.id.inputTextStorageItemNameEdit);
        assertNotNull(view);

        view = storageEditActivity.findViewById(R.id.layoutStorageItemNameEdit);
        assertNotNull(view);

        view = storageEditActivity.findViewById(R.id.inputTextStorageItemQuantityEdit);
        assertNotNull(view);

        view = storageEditActivity.findViewById(R.id.layoutStorageItemQuantityEdit);
        assertNotNull(view);

        view = storageEditActivity.findViewById(R.id.spinnerUnitEdit);
        assertNotNull(view);

        view = storageEditActivity.findViewById(R.id.textInputEditTextNote);
        assertNotNull(view);
    }

    @After
    public void tearDown() {
        storageEditActivity = null;
    }
}