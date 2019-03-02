package com.example.ondrejvane.zivnostnicek.activities.note;

import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.example.ondrejvane.zivnostnicek.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class NoteNewActivityTest {

    @Rule
    public ActivityTestRule<NoteNewActivity> mActivityRule = new ActivityTestRule<>(NoteNewActivity.class);

    private NoteNewActivity noteNewActivity = null;

    @Before
    public void setUp() {
        noteNewActivity = mActivityRule.getActivity();
    }

    @Test
    public void testContext() {
        Context context = noteNewActivity.getBaseContext();
        assertEquals("com.example.ondrejvane.zivnostnicek", context.getPackageName());
    }

    @Test
    public void testLaunch() {
        View view;

        view = noteNewActivity.findViewById(R.id.textInputLayoutNoteTitle);
        assertNotNull(view);

        view = noteNewActivity.findViewById(R.id.textInputLayoutNote);
        assertNotNull(view);

        view = noteNewActivity.findViewById(R.id.textInputEditTextNoteTitle);
        assertNotNull(view);

        view = noteNewActivity.findViewById(R.id.textInputEditTextNote);
        assertNotNull(view);

        view = noteNewActivity.findViewById(R.id.traderRatingBar);
        assertNotNull(view);

    }

    @After
    public void tearDown() {
        noteNewActivity = null;
    }
}