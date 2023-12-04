package com.example.hoard;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.UiObject;
import androidx.test.platform.app.InstrumentationRegistry;
import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.actionWithAssertions;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.google.firebase.firestore.util.Preconditions.checkNotNull;
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

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class AppTest {

    private void signIn() {
        try {
            wait(3000);
            onView(withId(R.id.emailInput)).perform(ViewActions.typeText("test@gmail.com"));
            onView(withId(R.id.passwordInput)).perform(ViewActions.typeText("123456"));
            closeSoftKeyboard();
            onView(withId(R.id.signInButton)).perform(click());
            wait(3000);
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

    public static ViewAction swipeDown() {
        return actionWithAssertions(new GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER,
                GeneralLocation.BOTTOM_CENTER, Press.FINGER));
    }


    // StackOverFlow: https://stackoverflow.com/questions/52737309/espresso-check-recyclerview-items-are-ordered-correctly/52828004#52828004
    // Accessed Dec 3,2023
    public static Matcher<View> hasItemAtPosition(final Matcher<View> matcher, final int position) {
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                matcher.describeTo(description);
            }
            @Override
            protected boolean matchesSafely(RecyclerView recyclerView) {
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
                return matcher.matches(viewHolder.itemView);
            }
        };
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
    // 1	01/01/2023	Item One        	Alpha	  A1-1	    ALPH123	            500	        Initial Entry   Blue, White
    // 2	15/02/2023	Sample           	Beta	  B2-1	    BETA456	            750	        Mid-Quarter     Blue
    // 3	30/03/2023	Throwaway Item  	Alpha	  G2000	    ALPH789	            1000	    Quarter End     Green
    // 4	10/04/2023	Item 2          	Delta	  D-700	    DELT012	            300	        Spring Check    Yellow, Green
    // 5	20/05/2023	Random Sample     	Beta	  B-AD	    BETA345	            600	        Pre-Summer      White

    // This part of the code test sorting, filtering and bulk delete.
    // Run createItems() before running testSort(), testFilter() and testBulkDelete().
    @Test
    public void createItems() {
        // Add item 1
        onView(withId(R.id.addItemButton)).perform(click());
        wait(2000);
        onView(withId(R.id.dateInput)).perform(ViewActions.typeText("01/01/2023"));
        onView(withId(R.id.descriptionInput)).perform(ViewActions.typeText("Item One"));
        onView(withId(R.id.makeInput)).perform(ViewActions.typeText("Alpha"));
        onView(withId(R.id.modelInput)).perform(ViewActions.typeText("A1-1"));
        onView(withId(R.id.serialNumberInput)).perform(ViewActions.typeText("ALPH123"));
        onView(withId(R.id.valueInput)).perform(ViewActions.typeText("500"));
        closeSoftKeyboard();
        onView(withId(R.id.commentInput)).perform(ViewActions.typeText("Initial Entry"));
        closeSoftKeyboard();
        wait(2000);

        // Add tag
        onView(withId(R.id.AddTagButton)).perform(click());
        wait(1000);
        onView(withId(R.id.tagNameInput)).perform(ViewActions.typeText("Blue"));
        onView(withId(R.id.tagColorInput)).perform(ViewActions.typeText("#0000FF"));
        closeSoftKeyboard();
        onView(withId(R.id.submitButton)).perform(click());
        wait(2000);

        onView(withId(R.id.AddTagButton)).perform(click());
        wait(1000);
        onView(withId(R.id.tagNameInput)).perform(ViewActions.typeText("White"));
        onView(withId(R.id.tagColorInput)).perform(ViewActions.typeText("#FFFFFF"));
        closeSoftKeyboard();
        onView(withId(R.id.submitButton)).perform(click());
        wait(2000);

        onView(isRoot()).perform(swipeUp());

        // Submit item
        wait(2000);
        onView(withId(R.id.submitButton)).perform(click());

        // Add item 2
        onView(withId(R.id.addItemButton)).perform(click());
        wait(2000);
        onView(withId(R.id.dateInput)).perform(ViewActions.typeText("15/02/2023"));
        onView(withId(R.id.descriptionInput)).perform(ViewActions.typeText("Sample"));
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
        onView(withId(R.id.descriptionInput)).perform(ViewActions.typeText("Throwaway Item"));
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
        wait(2000);

        onView(isRoot()).perform(swipeUp());

        // Submit item
        wait(2000);
        onView(withId(R.id.submitButton)).perform(click());

        // Add item 4
        onView(withId(R.id.addItemButton)).perform(click());
        wait(2000);
        onView(withId(R.id.dateInput)).perform(ViewActions.typeText("10/04/2023"));
        onView(withId(R.id.descriptionInput)).perform(ViewActions.typeText("Item 2"));
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
        wait(2000);
        onView(withId(R.id.tagChipGroup)).perform(selectChipWithTag("Green"));

        onView(isRoot()).perform(swipeUp());

        // Submit item
        wait(2000);
        onView(withId(R.id.submitButton)).perform(click());

        // Add item 5
        onView(withId(R.id.addItemButton)).perform(click());
        wait(2000);
        onView(withId(R.id.dateInput)).perform(ViewActions.typeText("20/05/2023"));
        onView(withId(R.id.descriptionInput)).perform(ViewActions.typeText("Random Sample"));
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
        // #	   Date	       Description	    Make	  Model	    Serial Number	    Value	    Comment         Tags
        // 1	01/01/2023	Item One        	Alpha	  A1-1	    ALPH123	            500	        Initial Entry   Blue, White
        // 2	15/02/2023	Sample           	Beta	  B2-1	    BETA456	            750	        Mid-Quarter     Blue
        // 3	30/03/2023	Throwaway Item  	Alpha	  G2000	    ALPH789	            1000	    Quarter End     Green
        // 4	10/04/2023	Item 2          	Delta	  D-700	    DELT012	            300	        Spring Check    Yellow, Green
        // 5	20/05/2023	Random Sample     	Beta	  B-AD	    BETA345	            600	        Pre-Summer      White
        onView(withId(R.id.nav_sort)).perform(click());
        wait(2000);
        onView(withId(R.id.sorting))
                .perform(RecyclerViewActions.scrollTo(hasDescendant(withText("Date"))))
                .perform(RecyclerViewActions.actionOnItem(hasDescendant(withText("Date")), click()));
        onView(withId(R.id.sort_descending)).perform(click());
        onView(withId(R.id.action_apply)).perform(click());
        wait(2000);


        onView(hasItemAtPosition(hasDescendant(withText("Random Sample")), 0)).check(matches(isDisplayed()));
        onView(hasItemAtPosition(hasDescendant(withText("Item 2")), 1)).check(matches(isDisplayed()));
        onView(hasItemAtPosition(hasDescendant(withText("Throwaway Item")), 2)).check(matches(isDisplayed()));
        onView(hasItemAtPosition(hasDescendant(withText("Sample")), 3)).check(matches(isDisplayed()));
        onView(hasItemAtPosition(hasDescendant(withText("Item One")), 4)).check(matches(isDisplayed()));


        // Description
        onView(withId(R.id.nav_sort)).perform(click());
        wait(2000);
        onView(withId(R.id.sorting))
                .perform(RecyclerViewActions.scrollTo(hasDescendant(withText("Description"))))
                .perform(RecyclerViewActions.actionOnItem(hasDescendant(withText("Description")), click()));
        onView(withId(R.id.sort_ascedning)).perform(click());
        onView(withId(R.id.action_apply)).perform(click());
        wait(2000);

        onView(hasItemAtPosition(hasDescendant(withText("Item 2")), 0)).check(matches(isDisplayed()));
        onView(hasItemAtPosition(hasDescendant(withText("Item One")), 1)).check(matches(isDisplayed()));
        onView(hasItemAtPosition(hasDescendant(withText("Random Sample")), 2)).check(matches(isDisplayed()));
        onView(hasItemAtPosition(hasDescendant(withText("Sample")), 3)).check(matches(isDisplayed()));
        onView(hasItemAtPosition(hasDescendant(withText("Throwaway Item")), 4)).check(matches(isDisplayed()));

       // Tags
        onView(withId(R.id.nav_sort)).perform(click());
        wait(2000);
        onView(withId(R.id.sorting))
                .perform(RecyclerViewActions.scrollTo(hasDescendant(withText("Tags"))))
                .perform(RecyclerViewActions.actionOnItem(hasDescendant(withText("Tags")), click()));
        onView(withId(R.id.sort_descending)).perform(click());
        onView(withId(R.id.action_apply)).perform(click());
        wait(2000);

        onView(hasItemAtPosition(hasDescendant(withText("Random Sample")), 0)).check(matches(isDisplayed()));
        onView(hasItemAtPosition(hasDescendant(withText("Throwaway Item")), 1)).check(matches(isDisplayed()));
        onView(hasItemAtPosition(hasDescendant(withText("Item 2")), 2)).check(matches(isDisplayed()));
        onView(hasItemAtPosition(hasDescendant(withText("Sample")), 3)).check(matches(isDisplayed()));
        onView(hasItemAtPosition(hasDescendant(withText("Item One")), 4)).check(matches(isDisplayed()));


        // Estimated Value
        onView(withId(R.id.nav_sort)).perform(click());
        wait(2000);
        onView(withId(R.id.sorting))
                .perform(RecyclerViewActions.scrollTo(hasDescendant(withText("Estimated Value"))))
                .perform(RecyclerViewActions.actionOnItem(hasDescendant(withText("Estimated Value")), click()));
        onView(withId(R.id.sort_ascedning)).perform(click());
        onView(withId(R.id.action_apply)).perform(click());
        wait(2000);

        onView(hasItemAtPosition(hasDescendant(withText("Item 2")), 0)).check(matches(isDisplayed()));
        onView(hasItemAtPosition(hasDescendant(withText("Item One")), 1)).check(matches(isDisplayed()));
        onView(hasItemAtPosition(hasDescendant(withText("Random Sample")), 2)).check(matches(isDisplayed()));
        onView(hasItemAtPosition(hasDescendant(withText("Sample")), 3)).check(matches(isDisplayed()));
        onView(hasItemAtPosition(hasDescendant(withText("Throwaway Item")), 4)).check(matches(isDisplayed()));


        // Estimated Value
        onView(withId(R.id.nav_sort)).perform(click());
        wait(2000);
        onView(withId(R.id.sorting))
                .perform(RecyclerViewActions.scrollTo(hasDescendant(withText("Description"))))
                .perform(RecyclerViewActions.actionOnItem(hasDescendant(withText("Description")), click()));
        onView(withId(R.id.sort_descending)).perform(click());
        onView(withId(R.id.action_apply)).perform(click());
        wait(2000);

        onView(hasItemAtPosition(hasDescendant(withText("Item 2")), 4)).check(matches(isDisplayed()));
        onView(hasItemAtPosition(hasDescendant(withText("Item One")), 3)).check(matches(isDisplayed()));
        onView(hasItemAtPosition(hasDescendant(withText("Random Sample")), 2)).check(matches(isDisplayed()));
        onView(hasItemAtPosition(hasDescendant(withText("Sample")), 1)).check(matches(isDisplayed()));
        onView(hasItemAtPosition(hasDescendant(withText("Throwaway Item")), 0)).check(matches(isDisplayed()));

    }

    @Test
    public void testBulkDelete() {
        //Perform bulk delete
        wait(2000);
        try {
            onView(withText("Item One")).perform(longClick());
        } catch (Exception e) {
            onView(isRoot()).perform(swipeUp());
            onView(withText("Item One")).perform(longClick());
            onView(isRoot()).perform(swipeDown());
        }

        try {
            onView(withText("Sample")).perform(longClick());
        } catch (Exception e) {
            onView(isRoot()).perform(swipeUp());
            onView(withText("Sample")).perform(longClick());
            onView(isRoot()).perform(swipeDown());
        }

        try {
            onView(withText("Throwaway Item")).perform(longClick());
        } catch (Exception e) {
            onView(isRoot()).perform(swipeUp());
            onView(withText("Throwaway Item")).perform(longClick());
            onView(isRoot()).perform(swipeDown());
        }

        try {
            onView(withText("Item 2")).perform(longClick());
        } catch (Exception e) {
            onView(isRoot()).perform(swipeUp());
            onView(withText("Item 2")).perform(longClick());
            onView(isRoot()).perform(swipeDown());
        }

        try {
            onView(withText("Random Sample")).perform(longClick());
        } catch (Exception e) {
            onView(isRoot()).perform(swipeUp());
            onView(withText("Random Sample")).perform(longClick());
            onView(isRoot()).perform(swipeDown());
        }

        onView(withId(R.id.bulk_delete)).perform(click());
        onView(withText("Yes")).perform(click());

        // Check if items are deleted
        wait(2000);
        onView(withText("Item One")).check(doesNotExist());
        onView(withText("Sample")).check(doesNotExist());
        onView(withText("Throwaway Item")).check(doesNotExist());
        onView(withText("Item 2")).check(doesNotExist());
        onView(withText("Random Sample")).check(doesNotExist());
    }
}
