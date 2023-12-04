package com.example.hoard;

import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.UiObject;
import androidx.test.platform.app.InstrumentationRegistry;
import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.actionWithAssertions;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;

import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.GeneralLocation;
import androidx.test.espresso.action.GeneralSwipeAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Swipe;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class AppTest {

    private void signIn() {
        try {
            onView(withId(R.id.emailInput)).perform(ViewActions.typeText("test@gmail.com"));
            onView(withId(R.id.passwordInput)).perform(ViewActions.typeText("123456"));
            closeSoftKeyboard();
            onView(withId(R.id.signInButton)).perform(click());
            wait(2000);
        } catch (Exception e) {
        }
    }

    public void wait(int milliseconds) {
        // program pauses for milliseconds
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

    public static ViewAction swipeUp() {
        return actionWithAssertions(new GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER,
                GeneralLocation.TOP_CENTER, Press.FINGER));
    }

    public void allowPermissionIfNeeded(String permissionText) {
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        UiObject allowButton = device.findObject(new UiSelector()
                .textContains(permissionText)
                .className("android.widget.Button"));

        try {
            if (allowButton.exists() && allowButton.isEnabled()) {
                allowButton.click();
            }
        } catch (UiObjectNotFoundException e) {
        }
    }

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

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() throws Exception {
        signIn();
    }

    // This part of the code test add, edit and delete item.
    // Run testAddTag() and testAddItem() before running testEditItem() and testDeleteItem().
    @Test
    public void testAddTag() {
        onView(withId(R.id.addItemButton)).perform(click());
        wait(2000);
        onView(withId(R.id.AddTagButton)).perform(click());
        wait(2000);
        onView(withId(R.id.tagNameInput)).perform(ViewActions.typeText("Test Tag 2"));
        onView(withId(R.id.tagColorInput)).perform(ViewActions.typeText("#FF0000"));
        closeSoftKeyboard();
        onView(withId(R.id.submitButton)).perform(click());

        // Check if tag is present
        wait(2000);
        onView(isAssignableFrom(HorizontalScrollView.class)).perform(scrollToChip("Test Tag 2"));
        onView(withText("Test Tag 2")).check(matches(isDisplayed()));
    }

    @Test
    public void testAddItem() {
        try {
            onView(withId(R.id.emailInput)).perform(ViewActions.typeText("test@gmail.com"));
            onView(withId(R.id.passwordInput)).perform(ViewActions.typeText("123456"));
            closeSoftKeyboard();
            onView(withId(R.id.signInButton)).perform(click());
            wait(2000);
        } catch (Exception e) {
        }

        // Add item
        onView(withId(R.id.addItemButton)).perform(click());
        wait(2000);
        onView(withId(R.id.dateInput)).perform(ViewActions.typeText("01/01/2023"));
        onView(withId(R.id.descriptionInput)).perform(ViewActions.typeText("Test Item"));
        onView(withId(R.id.makeInput)).perform(ViewActions.typeText("Make"));
        onView(withId(R.id.modelInput)).perform(ViewActions.typeText("Model"));
        onView(withId(R.id.serialNumberInput)).perform(ViewActions.typeText("ASDF1234"));
        onView(withId(R.id.valueInput)).perform(ViewActions.typeText("100"));
        closeSoftKeyboard();
        onView(withId(R.id.commentInput)).perform(ViewActions.typeText("Comment"));
        closeSoftKeyboard();

        // Add tag
        onView(withId(R.id.AddTagButton)).perform(click());
        wait(1000);
        onView(withId(R.id.tagNameInput)).perform(ViewActions.typeText("Test Tag 1"));
        onView(withId(R.id.tagColorInput)).perform(ViewActions.typeText("#FF0000"));
        closeSoftKeyboard();
        onView(withId(R.id.submitButton)).perform(click());

        onView(isRoot()).perform(swipeUp());

        // Add image
        onView(withId(R.id.AddImageButton)).perform(click());
        onView(withId(R.id.btnCapture)).perform(click());
        wait(2000);
        try {
            // Change to the correct permission text
            allowPermissionIfNeeded("While using the app");
            onView(withId(R.id.btnCapture)).perform(click());
        } catch (Exception e) {
        }

        wait(2000);
        onView(withId(R.id.captureButton)).perform(click());
        wait(2000);
        onView(withId(R.id.submitButton)).perform(click());

        // Submit item
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
    }

    @Test
    public void testDeleteItem() {
    }

    // #	   Date	       Description	    Make	  Model	    Serial Number	    Value	    Comment         Tags
    // 1	01/01/2023	Initial Test Item	Alpha	  A1-1	    ALPH123	            500	        Initial Entry   Blue, White
    // 2	15/02/2023	Mid-Quarter Sample	Beta	  B2-1	    BETA456	            750	        Mid-Quarter     Blue
    // 3	30/03/2023	End of Quarter Test	Alpha	  G2000	    ALPH789	            1000	    Quarter End     Green
    // 4	10/04/2023	Spring Check Item	Delta	  D-700	    DELT012	            300	        Spring Check    Yellow, Green
    // 5	20/05/2023	Pre-Summer Review	Beta	  B-AD	    BETA345	            600	        Pre-Summer      White

    // This part of the code test sorting, filtering and bulk delete.
    // Run createItems() before running testSort(), testFilter() and testBulkDelete().
    @Test
    public void createItems() {
        // Add item 1
        onView(withId(R.id.addItemButton)).perform(click());
        wait(2000);
        onView(withId(R.id.dateInput)).perform(ViewActions.typeText("01/01/2023"));
        onView(withId(R.id.descriptionInput)).perform(ViewActions.typeText("Initial Test Item"));
        onView(withId(R.id.makeInput)).perform(ViewActions.typeText("Alpha"));
        onView(withId(R.id.modelInput)).perform(ViewActions.typeText("A1-1"));
        onView(withId(R.id.serialNumberInput)).perform(ViewActions.typeText("ALPH123"));
        onView(withId(R.id.valueInput)).perform(ViewActions.typeText("500"));
        closeSoftKeyboard();
        onView(withId(R.id.commentInput)).perform(ViewActions.typeText("Initial Entry"));
        closeSoftKeyboard();

        // Add tag
        onView(withId(R.id.AddTagButton)).perform(click());
        wait(1000);
        onView(withId(R.id.tagNameInput)).perform(ViewActions.typeText("Blue"));
        onView(withId(R.id.tagColorInput)).perform(ViewActions.typeText("#0000FF"));
        closeSoftKeyboard();
        onView(withId(R.id.submitButton)).perform(click());

        onView(isRoot()).perform(swipeUp());

        // Add tag
        onView(withId(R.id.AddTagButton)).perform(click());
        wait(1000);
        onView(withId(R.id.tagNameInput)).perform(ViewActions.typeText("White"));
        onView(withId(R.id.tagColorInput)).perform(ViewActions.typeText("#FFFFFF"));
        closeSoftKeyboard();
        onView(withId(R.id.submitButton)).perform(click());

        onView(isRoot()).perform(swipeUp());

        // Submit item
        wait(2000);
        onView(withId(R.id.submitButton)).perform(click());

        // Add item 2
        onView(withId(R.id.addItemButton)).perform(click());
        wait(2000);
        onView(withId(R.id.dateInput)).perform(ViewActions.typeText("15/02/2023"));
        onView(withId(R.id.descriptionInput)).perform(ViewActions.typeText("Mid-Quarter Sample"));
        onView(withId(R.id.makeInput)).perform(ViewActions.typeText("Beta"));
        onView(withId(R.id.modelInput)).perform(ViewActions.typeText("B2-1"));
        onView(withId(R.id.serialNumberInput)).perform(ViewActions.typeText("BETA456"));
        onView(withId(R.id.valueInput)).perform(ViewActions.typeText("750"));
        closeSoftKeyboard();
        onView(withId(R.id.commentInput)).perform(ViewActions.typeText("Mid-Quarter"));
        closeSoftKeyboard();

        // Add tag
        onView(withId(R.id.tagChipGroup)).perform(selectChipWithTag("Blue"));

        onView(isRoot()).perform(swipeUp());

        // Submit item
        wait(2000);
        onView(withId(R.id.submitButton)).perform(click());

        // Add item 3
        onView(withId(R.id.addItemButton)).perform(click());
        wait(2000);
        onView(withId(R.id.dateInput)).perform(ViewActions.typeText("30/03/2023"));
        onView(withId(R.id.descriptionInput)).perform(ViewActions.typeText("End of Quarter Test"));
        onView(withId(R.id.makeInput)).perform(ViewActions.typeText("Alpha"));
        onView(withId(R.id.modelInput)).perform(ViewActions.typeText("G2000"));
        onView(withId(R.id.serialNumberInput)).perform(ViewActions.typeText("ALPH789"));
        onView(withId(R.id.valueInput)).perform(ViewActions.typeText("1000"));
        closeSoftKeyboard();
        onView(withId(R.id.commentInput)).perform(ViewActions.typeText("Quarter End"));
        closeSoftKeyboard();

        // Add tag
        onView(withId(R.id.AddTagButton)).perform(click());
        wait(1000);
        onView(withId(R.id.tagNameInput)).perform(ViewActions.typeText("Green"));
        onView(withId(R.id.tagColorInput)).perform(ViewActions.typeText("#00FF00"));
        closeSoftKeyboard();
        onView(withId(R.id.submitButton)).perform(click());

        onView(isRoot()).perform(swipeUp());

        // Submit item
        wait(2000);
        onView(withId(R.id.submitButton)).perform(click());

        // Add item 4
        onView(withId(R.id.addItemButton)).perform(click());
        wait(2000);
        onView(withId(R.id.dateInput)).perform(ViewActions.typeText("10/04/2023"));
        onView(withId(R.id.descriptionInput)).perform(ViewActions.typeText("Spring Check Item"));
        onView(withId(R.id.makeInput)).perform(ViewActions.typeText("Delta"));
        onView(withId(R.id.modelInput)).perform(ViewActions.typeText("D-700"));
        onView(withId(R.id.serialNumberInput)).perform(ViewActions.typeText("DELT012"));
        onView(withId(R.id.valueInput)).perform(ViewActions.typeText("300"));
        closeSoftKeyboard();
        onView(withId(R.id.commentInput)).perform(ViewActions.typeText("Spring Check"));
        closeSoftKeyboard();

        // Add tag
        onView(withId(R.id.AddTagButton)).perform(click());
        wait(1000);
        onView(withId(R.id.tagNameInput)).perform(ViewActions.typeText("Yellow"));
        onView(withId(R.id.tagColorInput)).perform(ViewActions.typeText("#FFFF00"));
        closeSoftKeyboard();
        onView(withId(R.id.submitButton)).perform(click());
        onView(withId(R.id.tagChipGroup)).perform(selectChipWithTag("Green"));

        onView(isRoot()).perform(swipeUp());

        // Submit item
        wait(2000);
        onView(withId(R.id.submitButton)).perform(click());

        // Add item 5
        onView(withId(R.id.addItemButton)).perform(click());
        wait(2000);
        onView(withId(R.id.dateInput)).perform(ViewActions.typeText("20/05/2023"));
        onView(withId(R.id.descriptionInput)).perform(ViewActions.typeText("Pre-Summer Review"));
        onView(withId(R.id.makeInput)).perform(ViewActions.typeText("Beta"));
        onView(withId(R.id.modelInput)).perform(ViewActions.typeText("B-AD"));
        onView(withId(R.id.serialNumberInput)).perform(ViewActions.typeText("BETA345"));
        onView(withId(R.id.valueInput)).perform(ViewActions.typeText("600"));
        closeSoftKeyboard();
        onView(withId(R.id.commentInput)).perform(ViewActions.typeText("Pre-Summer"));
        closeSoftKeyboard();

        // Add tag
        onView(withId(R.id.tagChipGroup)).perform(selectChipWithTag("White"));

        onView(isRoot()).perform(swipeUp());

        // Submit item
        wait(2000);
        onView(withId(R.id.submitButton)).perform(click());
    }

    @Test
    public void testSort() {
        onView(withId(R.id.nav_sort)).perform(click());
        wait(2000);

        // Te
    }
}