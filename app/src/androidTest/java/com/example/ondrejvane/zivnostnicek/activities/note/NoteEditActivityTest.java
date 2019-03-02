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

public class NoteEditActivityTest {

    @Rule
    public ActivityTestRule<NoteEditActivity> mActivityRule = new ActivityTestRule<>(NoteEditActivity.class);

    private NoteEditActivity noteEditActivity = null;

    @Before
    public void setUp() {
        noteEditActivity = mActivityRule.getActivity();
    }

    @Test
    public void testContext() {
        Context context = noteEditActivity.getBaseContext();
        assertEquals("com.example.ondrejvane.zivnostnicek", context.getPackageName());
    }

    @Test
    public void testLaunch() {
        View view;

        view = noteEditActivity.findViewById(R.id.textInputLayoutNoteTitleEdit);
        assertNotNull(view);

        view = noteEditActivity.findViewById(R.id.textInputLayoutNoteEdit);
        assertNotNull(view);

        view = noteEditActivity.findViewById(R.id.textInputEditTextNoteTitleEdit);
        assertNotNull(view);

        view = noteEditActivity.findViewById(R.id.textInputEditTextNoteEdit);
        assertNotNull(view);

        view = noteEditActivity.findViewById(R.id.traderRatingBarEdit);
        assertNotNull(view);

    }

    @After
    public void tearDown() {
        noteEditActivity = null;
    }
}