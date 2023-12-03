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
        /**
         * This method selects the chip with the specified tag name.
         * @param tagName The name of the tag to select.
         * @return ViewAction
         */
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                /**
                 * This method returns the constraints for the view.
                 * @return Matcher<View>
                 */
                return isAssignableFrom(ChipGroup.class);
            }

            @Override
            public String getDescription() {
                /**
                 * This method returns a description of the view action.
                 * @return String
                 */
                return "Select chip with tag name: " + tagName;
            }

            @Override
            public void perform(UiController uiController, View view) {
                /**
                 * This method cycles through the chips in the chip group and selects the chip with the specified tag name.
                 * @param uiController The UiController instance.
                 * @param view The view to perform the action on.
                 */
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
        /**
         * This method clicks on a child view with the specified ID.
         * @param viewId The ID of the view to click on.
         * @return ViewAction
         */
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                /**
                 * This method returns the constraints for the view.
                 * @return Matcher<View>
                 */
                return allOf(isDisplayed(), isAssignableFrom(ViewGroup.class));
            }

            @Override
            public String getDescription() {
                /**
                 * This method returns a description of the view action.
                 * @return String
                 */
                return "Click on a child view with specified ID.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                /**
                 * This method finds the child view with the specified ID and clicks on it.
                 * @param uiController The UiController instance.
                 * @param view The view to perform the action on.
                 */
                View childView = view.findViewById(viewId);
                if (childView != null && childView.isShown()) {
                    childView.performClick();
                }
            }
        };
    }

    public static ViewAction scrollToChip(final String tagName) {
        /**
         * This method scrolls the HorizontalScrollView to display the chip with the specified tag name.
         * @param tagName The name of the tag to scroll to.
         * @return ViewAction
         */
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                /**
                 * This method returns the constraints for the view.
                 * @return Matcher<View>
                 */
                return allOf(isDisplayed(), isAssignableFrom(HorizontalScrollView.class));
            }

            @Override
            public String getDescription() {
                /**
                 * This method returns a description of the view action.
                 * @return String
                 */
                return "Scroll HorizontalScrollView to display: " + tagName;
            }

            @Override
            public void perform(UiController uiController, View view) {
                /**
                 * This method scrolls the HorizontalScrollView to display the chip with the specified tag name.
                 * @param uiController The UiController instance.
                 * @param view The view to perform the action on.
                 */
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

    public void wait(int milliseconds){
        // program pauses for milliseconds
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAddTag() {
        // Add tag
        onView(withId(R.id.addItemButton)).perform(click());
        onView(withId(R.id.AddTagButton)).perform(click());
        onView(withId(R.id.tagNameInput)).perform(ViewActions.typeText("Test Tag 2"));
        onView(withId(R.id.tagColorInput)).perform(ViewActions.typeText("#FF0000"));
        closeSoftKeyboard();
        onView(withId(R.id.submitButton)).perform(click());

        // Check if tag is present
        wait(2000);
        onView(isAssignableFrom(HorizontalScrollView.class)).perform(scrollToChip("Test Tag 2"));
        onView(withText("Test Tag 2")).check(matches(isDisplayed()));
    }

    // Run this test first before running testEditItem() and testDeleteItem()
    @Test
    public void testAddItem() {
        // Add item
        onView(withId(R.id.addItemButton)).perform(click());
        onView(withId(R.id.dateInput)).perform(ViewActions.typeText("01/01/2023"));
        onView(withId(R.id.descriptionInput)).perform(ViewActions.typeText("Test Item"));
        onView(withId(R.id.makeInput)).perform(ViewActions.typeText("Make"));
        onView(withId(R.id.modelInput)).perform(ViewActions.typeText("Model"));
        onView(withId(R.id.serialNumberInput)).perform(ViewActions.typeText("ASDF1234"));
        onView(withId(R.id.valueInput)).perform(ViewActions.typeText("100"));
        onView(withId(R.id.commentInput)).perform(ViewActions.typeText("Comment"));
        closeSoftKeyboard();

        // Add tag
        onView(withId(R.id.AddTagButton)).perform(click());
        onView(withId(R.id.tagNameInput)).perform(ViewActions.typeText("Test Tag 1"));
        onView(withId(R.id.tagColorInput)).perform(ViewActions.typeText("#FF0000"));
        closeSoftKeyboard();
        onView(withId(R.id.submitButton)).perform(click());
        wait(2000);
        onView(withId(R.id.submitButton)).perform(click());

        // Check if item is present
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
        // Edit item
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

        // Check if item is edited
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
        // Delete item
        wait(2000);
        Espresso.onView(ViewMatchers.withId(R.id.recyclerView))
                .perform(RecyclerViewActions.actionOnItem(
                        ViewMatchers.hasDescendant(ViewMatchers.withText("Test Item Edited")),
                        ViewActions.swipeLeft()));

        // Check if item is deleted
        wait(4000);
        onView(withText("Test Item Edited")).check(doesNotExist());
    }

    @Test
    public void testBulkDelete() {
        // Add 2 items
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

        //Perform bulk delete
        wait(2000);
        onView(withText("Test Bulk Delete Item 1")).perform(longClick());
        onView(withText("Test Bulk Delete Item 2")).perform(click());
        onView(withId(R.id.bulk_delete)).perform(click());
        onView(withText("Yes")).perform(click());

        // Check if items are deleted
        wait(2000);
        onView(withText("Test Bulk Delete Item 1")).check(doesNotExist());
        onView(withText("Test Bulk Delete Item 2")).check(doesNotExist());
    }

    // Sorting and Filtering tests
    // #	Date	Description	Make	Model	Serial Number	Value	Comment
    //1	01/01/2023	Initial Test Item	AlphaMake	AlphaModel	ALPH123	500	Initial Entry
    //2	15/02/2023	Mid-Quarter Sample	BetaMake	BetaModel	BETA456	750	Mid-Quarter
    //3	30/03/2023	End of Quarter Test	GammaMake	GammaModel	GAMM789	1000	Quarter End
    //4	10/04/2023	Spring Check Item	DeltaMake	DeltaModel	DELT012	300	Spring Check
    //5	20/05/2023	Pre-Summer Review	EpsilonMake	EpsModel	EPSI345	600	Pre-Summer

    //Create items to tests sorting and filtering
    @Test
    public void createItems() {
        // Add item number 1
        onView(withId(R.id.addItemButton)).perform(click());
        onView(withId(R.id.dateInput)).perform(ViewActions.typeText("01/01/2023"));
        onView(withId(R.id.descriptionInput)).perform(ViewActions.typeText("Initial Test Item"));
        onView(withId(R.id.makeInput)).perform(ViewActions.typeText("AlphaMake"));
        onView(withId(R.id.modelInput)).perform(ViewActions.typeText("AlphaModel"));
        onView(withId(R.id.serialNumberInput)).perform(ViewActions.typeText("ALPH123"));
        onView(withId(R.id.valueInput)).perform(ViewActions.typeText("500"));
        onView(withId(R.id.commentInput)).perform(ViewActions.typeText("Initial Entry"));
        closeSoftKeyboard();
        onView(withId(R.id.submitButton)).perform(click());
        wait(2000);

        // Add item number 2
        onView(withId(R.id.addItemButton)).perform(click());
        onView(withId(R.id.dateInput)).perform(ViewActions.typeText("15/02/2023"));
        onView(withId(R.id.descriptionInput)).perform(ViewActions.typeText("Mid-Quarter Sample"));
        onView(withId(R.id.makeInput)).perform(ViewActions.typeText("BetaMake"));
        onView(withId(R.id.modelInput)).perform(ViewActions.typeText("BetaModel"));
        onView(withId(R.id.serialNumberInput)).perform(ViewActions.typeText("BETA456"));
        onView(withId(R.id.valueInput)).perform(ViewActions.typeText("750"));
        onView(withId(R.id.commentInput)).perform(ViewActions.typeText("Mid-Quarter"));
        closeSoftKeyboard();
        onView(withId(R.id.submitButton)).perform(click());
        wait(2000);

        // Add item number 3
        onView(withId(R.id.addItemButton)).perform(click());
        onView(withId(R.id.dateInput)).perform(ViewActions.typeText("30/03/2023"));
        onView(withId(R.id.descriptionInput)).perform(ViewActions.typeText("End of Quarter Test"));
        onView(withId(R.id.makeInput)).perform(ViewActions.typeText("GammaMake"));
        onView(withId(R.id.modelInput)).perform(ViewActions.typeText("GammaModel"));
        onView(withId(R.id.serialNumberInput)).perform(ViewActions.typeText("GAMM789"));
        onView(withId(R.id.valueInput)).perform(ViewActions.typeText("1000"));
        onView(withId(R.id.commentInput)).perform(ViewActions.typeText("Quarter End"));
        closeSoftKeyboard();
        onView(withId(R.id.submitButton)).perform(click());
        wait(2000);

        // Add item number 4
        onView(withId(R.id.addItemButton)).perform(click());
        onView(withId(R.id.dateInput)).perform(ViewActions.typeText("10/04/2023"));
        onView(withId(R.id.descriptionInput)).perform(ViewActions.typeText("Spring Check Item"));
        onView(withId(R.id.makeInput)).perform(ViewActions.typeText("DeltaMake"));
        onView(withId(R.id.modelInput)).perform(ViewActions.typeText("DeltaModel"));
        onView(withId(R.id.serialNumberInput)).perform(ViewActions.typeText("DELT012"));
        onView(withId(R.id.valueInput)).perform(ViewActions.typeText("300"));
        onView(withId(R.id.commentInput)).perform(ViewActions.typeText("Spring Check"));
        closeSoftKeyboard();
        onView(withId(R.id.submitButton)).perform(click());
        wait(2000);

        // Add item number 5
        onView(withId(R.id.addItemButton)).perform(click());
        onView(withId(R.id.dateInput)).perform(ViewActions.typeText("20/05/2023"));
        onView(withId(R.id.descriptionInput)).perform(ViewActions.typeText("Pre-Summer Review"));
        onView(withId(R.id.makeInput)).perform(ViewActions.typeText("EpsilonMake"));
        onView(withId(R.id.modelInput)).perform(ViewActions.typeText("EpsModel"));
        onView(withId(R.id.serialNumberInput)).perform(ViewActions.typeText("EPSI345"));
        onView(withId(R.id.valueInput)).perform(ViewActions.typeText("600"));
        onView(withId(R.id.commentInput)).perform(ViewActions.typeText("Pre-Summer"));
        closeSoftKeyboard();
        onView(withId(R.id.submitButton)).perform(click());
        wait(2000);

    }
}