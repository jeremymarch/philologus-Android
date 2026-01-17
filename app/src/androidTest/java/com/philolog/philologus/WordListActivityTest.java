package com.philolog.philologus;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static org.hamcrest.Matchers.anything;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class WordListActivityTest {

    @Rule
    public ActivityScenarioRule<WordListActivity> activityRule =
            new ActivityScenarioRule<>(WordListActivity.class);

    @Test
    public void testSelectWord() throws InterruptedException {
        // Wait for the database loading to finish and the list to be displayed.
        // We use a simple sleep here if idling resources aren't implemented, 
        // but checking for isDisplayed() on the list is usually enough for Espresso to wait.
        // However, given the focus error, let's give it a moment to settle.
        Thread.sleep(8000);

        onView(withId(android.R.id.list)).check(matches(isDisplayed()));

        // Click on the first item in the list
        onData(anything())
                .inAdapterView(withId(android.R.id.list))
                .atPosition(0)
                .perform(click());

        Thread.sleep(5000);

        // In Two-Pane mode, the detail container should be visible.
        // In Single-Pane mode, a new Activity (WordDetailActivity) is launched.
        // We check for the detail container with a more flexible matcher.
        onView(withId(R.id.word_detail_container)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }
}
