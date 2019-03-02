package com.example.ondrejvane.zivnostnicek.activities.note;

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

public class NoteShowActivityTest {

    @Rule
    public ActivityTestRule<NoteShowActivity> mActivityRule = new ActivityTestRule<>(NoteShowActivity.class);

    private NoteShowActivity noteShowActivity = null;

    @Before
    public void setUp() {
        noteShowActivity = mActivityRule.getActivity();
    }

    @Test
    public void testContext() {
        Context context = noteShowActivity.getBaseContext();
        assertEquals("com.example.ondrejvane.zivnostnicek", context.getPackageName());
    }

    @Test
    public void testLaunch() {
        View view;

        view = noteShowActivity.findViewById(R.id.textInputEditTextShowNoteTitle);
        assertNotNull(view);

        view = noteShowActivity.findViewById(R.id.textInputEditTextShowNoteDate);
        assertNotNull(view);

        view = noteShowActivity.findViewById(R.id.textInputEditTextShowNote);
        assertNotNull(view);

        view = noteShowActivity.findViewById(R.id.traderShowNoteRatingBar);
        assertNotNull(view);
    }

    @After
    public void tearDown() {
        noteShowActivity = null;
    }
}