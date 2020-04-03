package com.grapplo.swapidemo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.grapplo.swapidemo.api.ApiClient
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
            given(searchPlanet(anyString())).willReturn(Single.just(emptyList()))
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
        viewModel.searchPhrase.postValue("ok")

        // then
        verify(api).searchPlanet("ok")
    }

    @Test
    fun `typing fast doesn't trigger many calls`() {
        // when
        viewModel.searchPhrase.postValue("first")
        viewModel.searchPhrase.postValue("another")
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        // then
        verify(api, times(1)).searchPlanet(searchPhraseCaptor.capture())
        assertThat(searchPhraseCaptor.allValues).isEqualTo(listOf("another"))
    }

    @Test
    fun `waiting after typing makes it trigger more calls`() {
        // when
        viewModel.searchPhrase.postValue("first")
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS)
        viewModel.searchPhrase.postValue("another")
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        // then
        verify(api, times(2)).searchPlanet(searchPhraseCaptor.capture())
        assertThat(searchPhraseCaptor.allValues).isEqualTo(listOf("first", "another"))
    }

    @Test
    fun `call only when distinct phrase`() {
        // when
        viewModel.searchPhrase.postValue("phrase")
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS) // first call
        viewModel.searchPhrase.postValue("phras")
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        viewModel.searchPhrase.postValue("phrase")
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS) // no call, same text

        // then
        verify(api, times(1)).searchPlanet(searchPhraseCaptor.capture())
        assertThat(searchPhraseCaptor.allValues).isEqualTo(listOf("phrase"))
    }
}