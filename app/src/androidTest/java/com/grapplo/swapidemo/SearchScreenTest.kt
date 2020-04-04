package com.grapplo.swapidemo

import android.widget.ProgressBar
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.squareup.rx2.idler.Rx2Idler
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.TestScheduler
import org.hamcrest.core.IsNot.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class SearchScreenTest {

    @Before
    fun setup() {
        RxJavaPlugins.setComputationSchedulerHandler { testScheduler }
        RxJavaPlugins.setIoSchedulerHandler { testScheduler }
    }

    private val testScheduler = TestScheduler()

    @Test
    fun searchFieldVisibleAndEditable_isTrue() {
        launchActivity<MainActivity>()
        onView(withId(R.id.search_input)).check(matches(isDisplayed()))
        onView(withId(R.id.search_input)).check(matches(isEnabled()))
    }

    @Test
    fun searchFieldIsEmpty_isTrue() {
        launchActivity<MainActivity>()
        onView(withId(R.id.search_input)).check(matches(withText("")))
    }

    @Test
    fun searchFieldCanBeTypedIn_isTrue() {
        launchActivity<MainActivity>()
        onView(withId(R.id.search_input)).perform(typeText("Wookie"))
        onView(withText("Wookie")).check(matches(isDisplayed()))
    }

    @Test
    fun loadingHiddenWhenNoSearchOnGoing() {
        launchActivity<MainActivity>()
        onView(isAssignableFrom(ProgressBar::class.java)).check(matches(not(isDisplayed())))
    }

    @Test
    fun loadingShowsUpUponSearch() {
        launchActivity<MainActivity>()
        onView(withId(R.id.search_input)).perform(typeText("Yoda"))
        testScheduler.advanceTimeBy(3, TimeUnit.SECONDS)
        onView(isAssignableFrom(ProgressBar::class.java)).check(matches(isDisplayed()))
    }
}
