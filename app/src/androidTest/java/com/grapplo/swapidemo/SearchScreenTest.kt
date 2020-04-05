package com.grapplo.swapidemo

import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.grapplo.swapidemo.api.ApiClient
import com.grapplo.swapidemo.api.response.SwapiResponse
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Single
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.TestScheduler
import org.hamcrest.CoreMatchers.any
import org.hamcrest.Matchers.allOf
import org.hamcrest.core.IsNot.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.Koin
import org.koin.test.KoinTest
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock
import org.mockito.Mockito
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class SearchScreenTest : KoinTest {

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
    }

    lateinit var apiClient: ApiClient

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

    @Test
    fun dropdownToPickPlanetsOrPeopleIsVisible() {
        launchActivity<MainActivity>()
        onView(isAssignableFrom(Spinner::class.java)).check(matches(isDisplayed()))
    }

    @Test
    fun dropdownOffersPlanetsAndPeopleToPickFrom() {
        launchActivity<MainActivity>()
        onView(withId(R.id.subject_picker)).perform(click())
        onView(allOf(isAssignableFrom(TextView::class.java), withText("Planet"))).check(matches(isDisplayed()))
        onView(allOf(isAssignableFrom(TextView::class.java), withText("Person"))).check(matches(isDisplayed()))
    }

    @Test
    fun changingSpinnerChangesApiEndpoint() {
        // given
        apiClient = declareMock {
            given(searchPlanet(any())).willReturn(Single.just(SwapiResponse(0, null, null, emptyList())))
            given(searchPerson(any())).willReturn(Single.just(SwapiResponse(0, null, null, emptyList())))
        }
        launchActivity<MainActivity>()

        // when
        onView(withId(R.id.subject_picker)).perform(click())
        onView(withText("Person")).perform(click())
        onView(withId(R.id.search_input)).perform(typeText("A"))
        testScheduler.advanceTimeBy(3, TimeUnit.SECONDS)
        onView(withId(R.id.subject_picker)).perform(click())
        onView(withText("Planet")).perform(click())
        onView(withId(R.id.search_input)).perform(typeText("B"))
        testScheduler.advanceTimeBy(3, TimeUnit.SECONDS)

        // then
        verify(apiClient, times(1)).searchPlanet(any())
        verify(apiClient, times(1)).searchPerson(any())
    }




}
