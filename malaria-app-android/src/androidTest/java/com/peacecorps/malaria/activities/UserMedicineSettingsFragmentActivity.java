package com.peacecorps.malaria.activities;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.peacecorps.malaria.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class UserMedicineSettingsFragmentActivityTest {

    @Rule
    public ActivityTestRule<UserMedicineSettingsFragmentActivity> mActivityTestRule = new ActivityTestRule<>(UserMedicineSettingsFragmentActivity.class);

    @Test
    public void userMedicineSettingsFragmentActivityTest() {
        ViewInteraction textView = onView(
                allOf(withId(R.id.user_medicine_settings_activity_setup_label), withText("Set Up"),
                        childAtPosition(
                                allOf(withId(R.id.user_medicine_settings_activity_container),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class),
                                                0)),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("Set Up")));

        ViewInteraction button = onView(
                allOf(withId(R.id.user_medicine_settings_activity_done_button),
                        childAtPosition(
                                allOf(withId(R.id.user_medicine_settings_activity_container),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class),
                                                0)),
                                2),
                        isDisplayed()));
        button.check(matches(isDisplayed()));

        ViewInteraction spinner = onView(
                withId(R.id.user_medicine_settings_activity_drug_select_spinner));
        spinner.perform(scrollTo(), click());

        ViewInteraction button2 = onView(
                allOf(withId(R.id.infoDrug),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.TableLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        button2.check(matches(isDisplayed()));

        ViewInteraction tableLayout = onView(
                allOf(withClassName(is("android.widget.TableLayout")), isDisplayed()));
        tableLayout.perform(click());

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.user_medicine_settings_activity_time_pick_button), withText("Pick Time")));
        textView2.perform(scrollTo(), click());

        ViewInteraction button3 = onView(
                allOf(withId(android.R.id.button1), withText("OK"), isDisplayed()));
        button3.perform(click());

        ViewInteraction button4 = onView(
                allOf(withId(R.id.user_medicine_settings_activity_done_button), withText("DONE"),
                        withParent(withId(R.id.user_medicine_settings_activity_container))));
        button4.perform(scrollTo(), click());

        ViewInteraction button5 = onView(
                allOf(withId(R.id.tempButton), isDisplayed()));
        button5.perform(click());

        ViewInteraction button6 = onView(
                allOf(withId(R.id.badgeScreen),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        button6.check(matches(isDisplayed()));

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
