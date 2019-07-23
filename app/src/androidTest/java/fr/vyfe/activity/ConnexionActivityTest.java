package fr.vyfe.activity;


import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import fr.vyfe.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ConnexionActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void connexionActivityTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.et_mail),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.linear_layout_add),
                                        1),
                                0)));
        appCompatEditText.perform(scrollTo(), click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.et_mail),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.linear_layout_add),
                                        1),
                                0)));
        appCompatEditText2.perform(scrollTo(), replaceText("claire@vyfe.fr"), closeSoftKeyboard());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.et_mail), withText("claire@vyfe.fr"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.linear_layout_add),
                                        1),
                                0)));
        appCompatEditText3.perform(pressImeActionButton());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.et_password),
                        childAtPosition(
                                allOf(withId(R.id.linear_password),
                                        childAtPosition(
                                                withId(R.id.linear_layout_add),
                                                2)),
                                0)));
        appCompatEditText4.perform(scrollTo(), replaceText("admin01"), closeSoftKeyboard());

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.et_password), withText("admin01"),
                        childAtPosition(
                                allOf(withId(R.id.linear_password),
                                        childAtPosition(
                                                withId(R.id.linear_layout_add),
                                                2)),
                                0)));
        appCompatEditText5.perform(pressImeActionButton());

        ViewInteraction appCompatImageView = onView(
                allOf(withId(R.id.iv_show_password),
                        childAtPosition(
                                allOf(withId(R.id.linear_password),
                                        childAtPosition(
                                                withId(R.id.linear_layout_add),
                                                2)),
                                1)));
        appCompatImageView.perform(scrollTo(), click());



        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.btn_connect), withText("Se connecter"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.linear_layout_add),
                                        3),
                                2)));
        appCompatButton3.perform(scrollTo(), click());
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
