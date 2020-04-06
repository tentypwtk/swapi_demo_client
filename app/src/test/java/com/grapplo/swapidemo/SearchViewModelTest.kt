package com.grapplo.swapidemo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.grapplo.swapidemo.api.ApiClient
import com.grapplo.swapidemo.api.response.SwapiResponse
import com.grapplo.swapidemo.domain.Planet
import com.grapplo.swapidemo.presentation.SearchViewModel
import com.nhaarman.mockitokotlin2.argumentCaptor
import io.reactivex.Single
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.TestScheduler
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock
import org.mockito.BDDMockito.*
import org.mockito.Mockito
import java.util.concurrent.TimeUnit

class SearchViewModelTest : KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(appModules)
    }

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
    }

    private lateinit var api: ApiClient

    private val testScheduler = TestScheduler()

    @Before
    fun setup() {
        api = declareMock {
            given(searchPlanet(anyString())).willReturn(Single.just(emptyResponse))
        }

        RxJavaPlugins.setIoSchedulerHandler { testScheduler }
        RxJavaPlugins.setComputationSchedulerHandler { testScheduler }
    }

    @After
    fun clean() {
        stopKoin()
    }

    private val viewModel: SearchViewModel by inject()

    private val searchPhraseCaptor = argumentCaptor<String>()

    @Test
    fun `updated search phrase triggers api calls`() {
        // when
        viewModel.searchPhraseUI.postValue("ok")
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        // then
        verify(api).searchPlanet("ok")
    }

    @Test
    fun `typing fast doesn't trigger many calls`() {
        // when
        viewModel.searchPhraseUI.postValue("first")
        viewModel.searchPhraseUI.postValue("another")
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        // then
        verify(api, times(1)).searchPlanet(searchPhraseCaptor.capture())
        assertThat(searchPhraseCaptor.allValues).isEqualTo(listOf("another"))
    }

    @Test
    fun `waiting after typing makes it trigger more calls`() {
        // when
        viewModel.searchPhraseUI.postValue("first")
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS)
        viewModel.searchPhraseUI.postValue("another")
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        // then
        verify(api, times(2)).searchPlanet(searchPhraseCaptor.capture())
        assertThat(searchPhraseCaptor.allValues).isEqualTo(listOf("first", "another"))
    }

    @Test
    fun `call only when distinct phrase`() {
        // when
        viewModel.searchPhraseUI.postValue("phrase")
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS) // first call
        viewModel.searchPhraseUI.postValue("phras")
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        viewModel.searchPhraseUI.postValue("phrase")
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS) // no call, same text

        // then
        verify(api, times(1)).searchPlanet(searchPhraseCaptor.capture())
        assertThat(searchPhraseCaptor.allValues).isEqualTo(listOf("phrase"))
    }

    @Test
    fun `loading state upon search`() {
        // given
        declareMock<ApiClient> {
            given(searchPlanet(anyString())).willReturn(
                Single.just(emptyResponse).delay(4, TimeUnit.SECONDS)
            )
        }

        // when
        viewModel.searchPhraseUI.postValue("phrase")
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS) // first call

        // then
        assertThat(viewModel.state.value is SearchViewModel.State.Searching).isTrue()
    }

    @Test
    fun `search phrase completes without errors`() {
        viewModel.searchPhrase("anything", SearchViewModel.SearchMode.PLANET).test().assertNoErrors().assertComplete()
    }

    companion object {
        val emptyResponse = SwapiResponse<Planet>(0, previous = null, next = null, results = emptyList())
    }
}