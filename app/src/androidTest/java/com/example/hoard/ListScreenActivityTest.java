package com.example.hoard;


import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withChild;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.google.common.base.Predicates.instanceOf;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.contrib.RecyclerViewActions;
//import androidx.test.espresso.contrib.RecyclerViewActions;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.JMock1Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;

import static java.util.EnumSet.allOf;

import android.view.View;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ListScreenActivityTest {

    @Rule
    public ActivityScenarioRule<ListScreen> scenario = new
            ActivityScenarioRule<ListScreen>(ListScreen.class);

    public void wait(int mili){

        try {
            Thread.sleep(mili); // Adjust the delay as needed
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static ViewAssertion withItemCount(final int expectedCount) {
        return new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                if (!(view instanceof RecyclerView)) {
                    throw noViewFoundException;
                }

                RecyclerView recyclerView = (RecyclerView) view;
                RecyclerView.Adapter adapter = recyclerView.getAdapter();
                assertThat(adapter.getItemCount(), is(expectedCount));
            }
        };
    }

    @Test
    public void testAddItem(){
        onView(withId(R.id.addItemButton)).perform(click());
        // Fill out the "Date of Purchase" field
        onView(withId(R.id.dateInput)).perform(ViewActions.typeText("09/09/2023"));
        // Fill out the "Brief Description" field
        onView(withId(R.id.descriptionInput)).perform(ViewActions.typeText("Unit Test Add Item"));

        // Fill out the "Make" and "Model" fields
        onView(withId(R.id.makeInput)).perform(ViewActions.typeText("Brand"));
        onView(withId(R.id.modelInput)).perform(ViewActions.typeText("Model XYZ"));

        // Fill out the "Serial Number" fields
        onView(withId(R.id.serialNumberInput)).perform(ViewActions.typeText("123456"));

        // Fill out the "Estimated Value" field
        onView(withId(R.id.valueInput)).perform(ViewActions.typeText("500"));

        // Fill out the "Comments" field
        onView(withId(R.id.commentInput)).perform(ViewActions.typeText("This is a comment."));
        closeSoftKeyboard();
        onView(withId(R.id.submitButton)).perform(click());

        wait(2000);

        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));

        // Check if the added item is present in the list
        String addedItemDescription = "Unit Test Add Item"; // Adjust with the actual description
        onView(withText(addedItemDescription)).check(matches(isDisplayed()));
    }

    @Test
    public void testBulkDelete() {
        // Assuming the RecyclerView is identified by R.id.recyclerView
        testAddItem();
        String targetDescription = "Unit Test Add Item";
        wait(2000);
        onView(withText(targetDescription)).perform(longClick());


        // Click the button that removes the item (adjust the button ID accordingly)
        onView(withId(R.id.bulk_delete)).perform(click());

        // Assuming the positive button has an ID, replace R.id.positiveButton with the actual ID
        onView(withText("Yes")).perform(click());

        // Verify that the item is no longer in the RecyclerView
        wait(2000);
        String removedItemDescription = "Unit Test Add Item"; // Replace with the actual description
        onView(withText(removedItemDescription)).check(doesNotExist());
    }

    @Test
    public void testBulkTag() {
        // Assuming the RecyclerView is identified by R.id.recyclerView

        // Long press on the first item in the RecyclerView
        wait(2000);
        onView(withId(R.id.recyclerView))
                .perform(RecyclerViewActions.scrollToPosition(0))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, longClick()));

        String targetTag = "Dont Delete";
        wait(2000);

        // Click the button that removes the item (adjust the button ID accordingly)
        onView(withId(R.id.bulk_tag)).perform(click());

        onView(withId(R.id.tagChipGroup))
                .perform(scrollTo())
                .check(matches(hasDescendant(withText(targetTag)))).perform(click());


        // Verify that the item is no longer in the RecyclerView
        wait(2000);

    }
}
