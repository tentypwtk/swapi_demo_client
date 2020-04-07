package com.grapplo.swapidemo

import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.GsonBuilder
import com.grapplo.swapidemo.api.ApiClient
import com.grapplo.swapidemo.api.response.SwapiResponse
import com.grapplo.swapidemo.domain.Person
import com.grapplo.swapidemo.domain.Planet
import com.grapplo.swapidemo.presentation.SearchResultAdapter
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Single
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.TestScheduler
import org.hamcrest.Matchers.allOf
import org.hamcrest.core.IsNot.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock
import org.mockito.Mockito
import java.io.BufferedReader
import java.io.InputStreamReader
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
        apiClient = declareMock {
            given(searchPlanet(any())).willReturn(
                Single.just(exampleResponse).delay(3, TimeUnit.SECONDS)
            )
        }
        launchActivity<MainActivity>()
        onView(withId(R.id.search_input)).perform(typeText("A   "))
        testScheduler.advanceTimeBy(3, TimeUnit.SECONDS)
        Thread.sleep(100) // Espresso can be moody when it comes to ProgressBar, need a workaround
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
        onView(allOf(isAssignableFrom(TextView::class.java), withText("Planet"))).check(
            matches(
                isDisplayed()
            )
        )
        onView(allOf(isAssignableFrom(TextView::class.java), withText("Person"))).check(
            matches(
                isDisplayed()
            )
        )
    }

    @Test
    fun changingSpinnerChangesApiEndpoint() {
        // given
        apiClient = declareMock {
            given(searchPlanet(any())).willReturn(Single.just(exampleResponse))
            given(searchPerson(any())).willReturn(
                Single.just(
                    SwapiResponse<Person>(
                        0,
                        null,
                        null,
                        arrayListOf()
                    )
                )
            )
        }
        launchActivity<MainActivity>()

        // when
        onView(withId(R.id.subject_picker)).perform(click())
        onView(withText("Person")).perform(click())
        onView(withId(R.id.search_input)).perform(typeText("A")) // search "A" within people
        testScheduler.advanceTimeBy(3, TimeUnit.SECONDS)
        onView(withId(R.id.subject_picker)).perform(click())
        onView(withText("Planet")).perform(click()) // search "A" within planets
        testScheduler.advanceTimeBy(3, TimeUnit.SECONDS)

        // then
        verify(apiClient).searchPerson(any())
        verify(apiClient).searchPlanet(any())
    }

    @Test
    fun pickingFromListOpensDetailsScreen() {
        // given
        apiClient = declareMock {
            given(searchPlanet(any())).willReturn(Single.just(exampleResponse))
        }

        // when
        launchActivity<MainActivity>()
        onView(withId(R.id.search_input)).perform(typeText("A"))
        testScheduler.advanceTimeBy(3, TimeUnit.SECONDS)
        onView(withId(R.id.recycler_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<SearchResultAdapter.VH>(0, click())
        )

        Thread.sleep(1000)
        onView(withText("Alderaan")).check(matches(isDisplayed()))
    }

    companion object {

        private val gson = GsonBuilder().create()

        private val examplePlanet = "planet.example.txt".readResource<Planet>()
        private val exampleResponse = SwapiResponse(0, null, null, listOf(examplePlanet))

        private inline fun <reified T> String.readResource() =
            com.grapplo.swapidemo.test.R::class.java
                .classLoader
                ?.getResource(this)
                ?.let { BufferedReader(InputStreamReader(it.openStream())) }
                ?.readText()
                ?.let {
                    gson.fromJson(it, T::class.java)
                }
                ?: throw Exception("Can't open test data")
    }
}
