package com.example.hoard;


import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static org.hamcrest.Matchers.anything;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withChild;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static androidx.test.espresso.matcher.ViewMatchers.withParentIndex;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.google.common.base.Predicates.instanceOf;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.RecyclerViewActions;
//import androidx.test.espresso.contrib.RecyclerViewActions;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.JMock1Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;

import static java.util.EnumSet.allOf;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.android.material.textview.MaterialTextView;

import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class UserAuthTest {

    /* StackOverflow: https://stackoverflow.com/questions/38314077/how-to-click-a-clickablespan-using-espresso
    * accessed Dec- 02-2023
     */
    public static ViewAction clickClickableSpan(final CharSequence textToClick) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return Matchers.instanceOf(TextView.class);
            }

            @Override
            public void perform(UiController uiController, View view) {
                TextView textView = (TextView) view;
                SpannableString spannableString = (SpannableString) textView.getText();

                if (spannableString.length() == 0) {
                    // TextView is empty, nothing to do
                    throw new NoMatchingViewException.Builder()
                            .includeViewHierarchy(true)
                            .withRootView(textView)
                            .build();
                }

                // Get the links inside the TextView and check if we find textToClick
                ClickableSpan[] spans = spannableString.getSpans(0, spannableString.length(), ClickableSpan.class);
                if (spans.length > 0) {
                    ClickableSpan spanCandidate;
                    for (ClickableSpan span : spans) {
                        spanCandidate = span;
                        int start = spannableString.getSpanStart(spanCandidate);
                        int end = spannableString.getSpanEnd(spanCandidate);
                        CharSequence sequence = spannableString.subSequence(start, end);
                        if (textToClick.toString().equals(sequence.toString())) {
                            span.onClick(textView);
                            return;
                        }
                    }
                }

                // textToClick not found in TextView
                throw new NoMatchingViewException.Builder()
                        .includeViewHierarchy(true)
                        .withRootView(textView)
                        .build();

            }

            @Override
            public String getDescription() {
                return "clicking on a ClickableSpan";
            }

        };
    }

    public void wait(int mili){

        try {
            Thread.sleep(mili); // Adjust the delay as needed
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean isSnackbarDisplayedWithMessage(String message) {
        try {
            onView(withId(com.google.android.material.R.id.snackbar_text))
                    .check(matches(withText(message)));
            return true;
        } catch (NoMatchingViewException e) {
            return false;
        }
    }

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);


    public void tearDown() {
        ItemDBController dbController = ItemDBController.getInstance();
        dbController.deleteAccount();
    }




    @Test
    public void testCreateAccount(){
        // Check if TextLinks are present
        onView(withId(R.id.clickableSignIn)).perform(clickClickableSpan("Sign up"));

        onView(withId(R.id.usernameInputLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.emailInputLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.passwordInputLayout)).check(matches(isDisplayed()));

        // Type the username for account creation
        onView(withId(R.id.usernameInput))
                .perform(ViewActions.typeText("unittest"));

        // Type the email for account creation
        onView(withId(R.id.emailInput))
                .perform(ViewActions.typeText("unittest@email.com"));

        // Type the password for account creation
        onView(withId(R.id.passwordInput))
                .perform(ViewActions.typeText("unittest"));
        closeSoftKeyboard();

        wait(1000);
        // Click the login button
        onView(withId(R.id.signInButton))
                .perform(click());

        // go back to sign in page
        wait(1000);
        onView(withId(R.id.signInButton))
                .perform(click());

        // Assert that the next screen (ListScreen) is displayed
        wait(2000);
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void testSignIn() {
        onView(withId(R.id.emailInputLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.passwordInputLayout)).check(matches(isDisplayed()));

        // Type the email for account creation
        onView(withId(R.id.emailInput))
                .perform(ViewActions.typeText("unittest@email.com"));

        // Type the password for account creation
        onView(withId(R.id.passwordInput))
                .perform(ViewActions.typeText("unittest"));
        closeSoftKeyboard();

        // Click the login button
        onView(withId(R.id.signInButton))
                .perform(click());

        // Assert that the next screen (ListScreen) is displayed
        wait(2000);
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.topAppBar)))
                .check(matches(hasDescendant(withText("unittest"))));
//        onView(withId(R.id.topAppBar)).check(matches(withText("unittest")));

    }

    @Test
    public void testAccountDeletion() {
        // Check if TextLinks are present
        onView(withId(R.id.clickableSignIn)).perform(clickClickableSpan("Sign up"));

        onView(withId(R.id.usernameInputLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.emailInputLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.passwordInputLayout)).check(matches(isDisplayed()));

        // Type the username for account creation
        onView(withId(R.id.usernameInput))
                .perform(ViewActions.typeText("DeleteAccount"));

        // Type the email for account creation
        onView(withId(R.id.emailInput))
                .perform(ViewActions.typeText("unittestDeleteAccount@email.com"));

        // Type the password for account creation
        onView(withId(R.id.passwordInput))
                .perform(ViewActions.typeText("unittestDeleteAccount"));
        closeSoftKeyboard();

        wait(1000);
        // Click the login button
        onView(withId(R.id.signInButton))
                .perform(click());

        // go back to sign in page
        wait(1000);
        onView(withId(R.id.signInButton))
                .perform(click());
        wait(2000);
        onView(withId(R.id.nav_profile))
                .perform(click());

        wait(2000);

        onData(anything()).inAdapterView(withId(R.id.profileAuthOptions)).atPosition(1).perform(click());
        onView(withText("Confirm Account Deletion"))
                .inRoot(isDialog()) // Specify that the view should be in a dialog
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withId(R.id.loginHeader)).check(matches(isDisplayed()));

    }



//    public static Matcher<View> hasLinks() {
//        return new TypeSafeMatcher<View>() {
//            @Override
//            public void describeTo(Description description) {
//                description.appendText("TextView should have clickable spans");
//            }
//
//            @Override
//            protected boolean matchesSafely(View item) {
//                if (!(item instanceof TextView)) {
//                    return false;
//                }
//                TextView textView = (TextView) item;
//                Spanned spanned = (Spanned) textView.getText();
//                URLSpan[] spans = spanned.getSpans(0, spanned.length(), URLSpan.class);
//                return spans.length > 0;
//            }
//
//        };
//    }

}



