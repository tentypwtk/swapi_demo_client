package com.grapplo.swapidemo

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class SearchScreenTest {

    @get:Rule
    var activityScenarioRule = activityScenarioRule<MainActivity>()

    @Test
    fun searchFieldVisibleAndEditable_isTrue() {
        onView(withId(R.id.search_input)).check(matches(isDisplayed()))
        onView(withId(R.id.search_input)).check(matches(isEnabled()))
    }

    @Test
    fun searchFieldIsEmpty_isTrue() {
        onView(withId(R.id.search_input)).check(matches(withText("")))
    }

    @Test
    fun searchFieldCanBeTypedIn_isTrue() {
        onView(withId(R.id.search_input)).perform(typeText("Wookie"))
        onView(withText("Wookie")).check(matches(isDisplayed()))
    }


}
