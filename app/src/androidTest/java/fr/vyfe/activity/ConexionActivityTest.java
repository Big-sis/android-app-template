package fr.vyfe.activity;


import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.text.InputType;
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

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import fr.vyfe.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withInputType;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;


@LargeTest
@RunWith(AndroidJUnit4.class)
public class ConexionActivityTest {

    @Rule
    public IntentsTestRule<ConnexionActivity> mActivityRule = new IntentsTestRule<>(ConnexionActivity.class);

    @Before
    public void stubAllExternalIntents() {
        // By default Espresso Intents does not stub any Intents. Stubbing needs to be setup before
        // every test run. In this case all external Intents will be blocked.
        intending(not(isInternal())).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
    }

    @Test
    public void contactUsTest() {

        ViewInteraction createAccountBtn = onView(
                allOf(withId(R.id.btn_create_account),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                2)));
        createAccountBtn.check(matches(withText("Create an account")));

        createAccountBtn.perform(click());
        createAccountBtn.check(matches(withText("Contact us: www.vyfe.fr")));

        createAccountBtn.perform(click());

        intended(allOf(
                hasAction(Intent.ACTION_VIEW),
                hasData(Uri.parse("https://vyfe.fr/"))));
    }

    @Test
    public void lostPasswordTest() {

        ViewInteraction lostPasswordBtn = onView(
                allOf(withId(R.id.tv_lost_password), withText("lost password"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.linear_layout_add),
                                        3),
                                1)));
        lostPasswordBtn.perform(click());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.edit_mail),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText("coeurro@gmail.com"), closeSoftKeyboard());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.btn_reset), withText("Send"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction button = onView(
                allOf(withId(R.id.btn_back),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        button.check(matches(isDisplayed()));

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.btn_back), withText("Back"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatButton3.perform(click());
    }

    @Test
    public void connexionTest() {

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.et_mail),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.linear_layout_add),
                                        1),
                                0)));
        appCompatEditText4.perform(replaceText("coeurro@gmail.com"), closeSoftKeyboard());

        ViewInteraction appCompatEditText7 = onView(
                allOf(withId(R.id.et_password),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.linear_layout_add),
                                        2),
                                0)));
        appCompatEditText7.perform(replaceText("azerty1234"), closeSoftKeyboard());

        ViewInteraction appCompatImageView = onView(
                allOf(withId(R.id.iv_show_password),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.linear_layout_add),
                                        2),
                                1)));
        appCompatImageView.perform(click());

        ViewInteraction editText2 = onView(
                allOf(withId(R.id.et_password), isDisplayed()));
        editText2.check(matches(withText("azerty1234")));
        editText2.check(matches(allOf(withInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD))));

        appCompatImageView.perform(click());

        editText2.check(matches(allOf(withInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD))));

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.btn_connect), withText("Log in"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.linear_layout_add),
                                        3),
                                2)));
        appCompatButton4.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


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
