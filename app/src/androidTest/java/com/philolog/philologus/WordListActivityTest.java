package com.philolog.philologus;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;

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
    public void testSelectWord() {
        // Wait for the list to be displayed
        onView(withId(android.R.id.list)).check(matches(isDisplayed()));

        // Click on the first item in the list
        onData(anything())
                .inAdapterView(withId(android.R.id.list))
                .atPosition(0)
                .perform(click());

        // Verify that the detail container is displayed (in two-pane mode) 
        // or that the WordDetailActivity is launched.
        // For simplicity, we just check if something in the detail view is now visible.
        // If it's a single pane, it will launch WordDetailActivity.
        // If it's two-pane, it will show WordDetailFragment in word_detail_container.
        
        onView(withId(R.id.word_detail_container)).check(matches(isDisplayed()));
    }
}
