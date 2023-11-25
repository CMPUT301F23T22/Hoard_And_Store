package com.example.hoard;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;

import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textview.MaterialTextView;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;

public class ListScreenActivity {
    @Rule
    public ActivityScenarioRule<ListScreen> scenario = new ActivityScenarioRule<>(ListScreen.class);

    public static ViewAction selectChipWithTag(final String tagName) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(ChipGroup.class);
            }

            @Override
            public String getDescription() {
                return "Select chip with tag name: " + tagName;
            }

            @Override
            public void perform(UiController uiController, View view) {
                ChipGroup chipGroup = (ChipGroup) view;
                for (int i = 0; i < chipGroup.getChildCount(); i++) {
                    Chip chip = (Chip) chipGroup.getChildAt(i);
                    if (chip.getTag() != null && chip.getTag() instanceof Tag) {
                        Tag tag = (Tag) chip.getTag();
                        if (tagName.equals(tag.getTagName())) {
                            chip.performClick();
                            break;
                        }
                    }
                }
            }
        };
    }

    public static ViewAction clickOnViewChildWithId(final int viewId) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return allOf(isDisplayed(), isAssignableFrom(ViewGroup.class));
            }

            @Override
            public String getDescription() {
                return "Click on a child view with specified ID.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                View childView = view.findViewById(viewId);
                if (childView != null && childView.isShown()) {
                    childView.performClick();
                }
            }
        };
    }

    public static ViewAction scrollToChip(final String tagName) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return allOf(isDisplayed(), isAssignableFrom(HorizontalScrollView.class));
            }

            @Override
            public String getDescription() {
                return "Scroll HorizontalScrollView to display: " + tagName;
            }

            @Override
            public void perform(UiController uiController, View view) {
                HorizontalScrollView scrollView = (HorizontalScrollView) view;
                ChipGroup chipGroup = (ChipGroup) scrollView.getChildAt(0);
                for (int i = 0; i < chipGroup.getChildCount(); i++) {
                    Chip chip = (Chip) chipGroup.getChildAt(i);
                    if (chip.getText().equals(tagName)) {
                        scrollView.scrollTo((int) chip.getX(), (int) chip.getY());
                        break;
                    }
                }
            }
        };
    }

    public void wait(int mili){

        try {
            Thread.sleep(mili);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAddTag() {
        onView(withId(R.id.addItemButton)).perform(click());
        onView(withId(R.id.AddTagButton)).perform(click());
        onView(withId(R.id.tagNameInput)).perform(ViewActions.typeText("Test Tag 2"));
        onView(withId(R.id.tagColorInput)).perform(ViewActions.typeText("#FF0000"));
        closeSoftKeyboard();
        onView(withId(R.id.submitButton)).perform(click());
        wait(2000);
        onView(isAssignableFrom(HorizontalScrollView.class)).perform(scrollToChip("Test Tag 2"));
        onView(withText("Test Tag 2")).check(matches(isDisplayed()));
    }

    @Test
    public void testAddItem() {
        onView(withId(R.id.addItemButton)).perform(click());
        onView(withId(R.id.dateInput)).perform(ViewActions.typeText("01/01/2023"));
        onView(withId(R.id.descriptionInput)).perform(ViewActions.typeText("Test Item"));
        onView(withId(R.id.makeInput)).perform(ViewActions.typeText("Make"));
        onView(withId(R.id.modelInput)).perform(ViewActions.typeText("Model"));
        onView(withId(R.id.serialNumberInput)).perform(ViewActions.typeText("ASDF1234"));
        onView(withId(R.id.valueInput)).perform(ViewActions.typeText("100"));
        onView(withId(R.id.commentInput)).perform(ViewActions.typeText("Comment"));
        closeSoftKeyboard();
        onView(withId(R.id.AddTagButton)).perform(click());
        onView(withId(R.id.tagNameInput)).perform(ViewActions.typeText("Test Tag 1"));
        onView(withId(R.id.tagColorInput)).perform(ViewActions.typeText("#FF0000"));
        closeSoftKeyboard();
        onView(withId(R.id.submitButton)).perform(click());
        wait(2000);
        onView(withId(R.id.submitButton)).perform(click());
        wait(2000);
        onView(withId(R.id.recyclerView))
                .perform(RecyclerViewActions.scrollTo(hasDescendant(withText("Test Item"))))
                .perform(RecyclerViewActions.actionOnItem(hasDescendant(withText("Test Item")), clickOnViewChildWithId(R.id.detailsArrow)));
        onView(withId(R.id.dateOfAcquisitionTextView)).check(matches(withText("Date Of Purchase: 2023-01-01")));
        onView(withId(R.id.briefDescriptionTextView)).check(matches(withText("Description: Test Item")));
        onView(withId(R.id.makeTextView)).check(matches(withText("Make: Make")));
        onView(withId(R.id.modelTextView)).check(matches(withText("Model: Model")));
        onView(withId(R.id.serialNumberTextView)).check(matches(withText("Serial Number: ASDF1234")));
        onView(withId(R.id.estimatedValueTextView)).check(matches(withText("Estimated value: 100.00")));
        onView(withId(R.id.commentTextView)).check(matches(withText("Comment: Comment")));
        onView(withId(R.id.chipGroup)).check(matches(hasDescendant(withText("Test Tag 1"))));
    }

    @Test
    public void testEditItem() {
        wait(2000);
        onView(withId(R.id.recyclerView))
                .perform(RecyclerViewActions.scrollTo(hasDescendant(withText("Test Item"))))
                .perform(RecyclerViewActions.actionOnItem(hasDescendant(withText("Test Item")), clickOnViewChildWithId(R.id.detailsArrow)));
        wait(2000);
        onView(withId(R.id.editButton)).perform(click());
        onView(withId(R.id.dateInput)).perform(replaceText("31/12/2023"));
        onView(withId(R.id.descriptionInput)).perform(replaceText("Test Item Edited"));
        onView(withId(R.id.makeInput)).perform(replaceText("Make Edited"));
        onView(withId(R.id.modelInput)).perform(replaceText("Model Edited"));
        onView(withId(R.id.serialNumberInput)).perform(replaceText("ASD123"));
        onView(withId(R.id.valueInput)).perform(replaceText("1000"));
        onView(withId(R.id.commentInput)).perform(replaceText("Comment Edited"));
        onView(withId(R.id.tagChipGroup)).perform(selectChipWithTag("Test Tag 2"));
        closeSoftKeyboard();
        onView(withId(R.id.submitButton)).perform(click());
        wait(2000);
        onView(withId(R.id.dateOfAcquisitionTextView)).check(matches(withText("Date Of Purchase: 2023-12-31")));
        onView(withId(R.id.briefDescriptionTextView)).check(matches(withText("Description: Test Item Edited")));
        onView(withId(R.id.makeTextView)).check(matches(withText("Make: Make Edited")));
        onView(withId(R.id.modelTextView)).check(matches(withText("Model: Model Edited")));
        onView(withId(R.id.serialNumberTextView)).check(matches(withText("Serial Number: ASD123")));
        onView(withId(R.id.estimatedValueTextView)).check(matches(withText("Estimated value: 1000.00")));
        onView(withId(R.id.commentTextView)).check(matches(withText("Comment: Comment Edited")));
        onView(withId(R.id.chipGroup)).check(matches(hasDescendant(withText("Test Tag 1"))));
        onView(withId(R.id.chipGroup)).check(matches(hasDescendant(withText("Test Tag 2"))));
    }

    @Test
    public void testDeleteItem() {
        wait(2000);
        Espresso.onView(ViewMatchers.withId(R.id.recyclerView))
                .perform(RecyclerViewActions.actionOnItem(
                        ViewMatchers.hasDescendant(ViewMatchers.withText("Test Item Edited")),
                        ViewActions.swipeLeft()));
        wait(4000);
        onView(withText("Test Item Edited")).check(doesNotExist());
    }

    @Test
    public void testBulkDelete() {
        onView(withId(R.id.addItemButton)).perform(click());
        onView(withId(R.id.dateInput)).perform(ViewActions.typeText("01/01/2023"));
        onView(withId(R.id.descriptionInput)).perform(ViewActions.typeText("Test Bulk Delete Item 1"));
        onView(withId(R.id.makeInput)).perform(ViewActions.typeText("Make"));
        onView(withId(R.id.modelInput)).perform(ViewActions.typeText("Model"));
        onView(withId(R.id.serialNumberInput)).perform(ViewActions.typeText("ASDF1234"));
        onView(withId(R.id.valueInput)).perform(ViewActions.typeText("100"));
        onView(withId(R.id.commentInput)).perform(ViewActions.typeText("Comment"));
        onView(withId(R.id.tagChipGroup)).perform(selectChipWithTag("Test Tag 1"));
        closeSoftKeyboard();
        onView(withId(R.id.submitButton)).perform(click());
        wait(2000);
        onView(withId(R.id.addItemButton)).perform(click());
        onView(withId(R.id.dateInput)).perform(ViewActions.typeText("01/01/2023"));
        onView(withId(R.id.descriptionInput)).perform(ViewActions.typeText("Test Bulk Delete Item 2"));
        onView(withId(R.id.makeInput)).perform(ViewActions.typeText("Make"));
        onView(withId(R.id.modelInput)).perform(ViewActions.typeText("Model"));
        onView(withId(R.id.serialNumberInput)).perform(ViewActions.typeText("ASDF1234"));
        onView(withId(R.id.valueInput)).perform(ViewActions.typeText("100"));
        onView(withId(R.id.commentInput)).perform(ViewActions.typeText("Comment"));
        onView(withId(R.id.tagChipGroup)).perform(selectChipWithTag("Test Tag 2"));
        closeSoftKeyboard();
        onView(withId(R.id.submitButton)).perform(click());
        wait(2000);
        onView(withText("Test Bulk Delete Item 1")).perform(longClick());
        onView(withText("Test Bulk Delete Item 2")).perform(click());
        onView(withId(R.id.bulk_delete)).perform(click());
        onView(withText("Yes")).perform(click());
        wait(2000);
        onView(withText("Test Bulk Delete Item 1")).check(doesNotExist());
        onView(withText("Test Bulk Delete Item 2")).check(doesNotExist());
    }
}