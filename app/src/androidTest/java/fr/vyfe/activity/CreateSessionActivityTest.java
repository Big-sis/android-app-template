package fr.vyfe.activity;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import fr.vyfe.R;
import fr.vyfe.helper.AuthHelper;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CreateSessionActivityTest {

    @Rule
    public IntentsTestRule<CreateSessionActivity> mActivityTestRule = new IntentsTestRule<>(CreateSessionActivity.class);

    @Before
    public void setUp() {
        //AuthHelper.getInstance().signInWithEmailAndPassword("coeurro@gmail.com", "azerty1234", null);
    }

    @Test
    public void createSessionActivity() {

        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.et_video_title2),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        2),
                                1)));
        appCompatEditText6.perform(replaceText("espresso"), closeSoftKeyboard());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatSpinner = onView(
                allOf(withId(R.id.spinner_session_infos)));
        appCompatSpinner.perform(click());

        DataInteraction linearLayout2 = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(5);
        linearLayout2.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.tv_name), withText("good"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.view_foreground),
                                        1),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("good")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.tv_name), withText("bad"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.view_foreground),
                                        1),
                                0),
                        isDisplayed()));
        textView2.check(matches(withText("bad")));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.tv_name), withText("ugly"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.view_foreground),
                                        1),
                                0),
                        isDisplayed()));
        textView3.check(matches(withText("ugly")));

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.button_go), withText("GO"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        4),
                                1)));
        appCompatButton2.perform(click());

        intended(allOf(toPackage("fr.vyfe")));
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
