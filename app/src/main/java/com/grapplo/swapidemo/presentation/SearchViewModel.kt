package com.grapplo.swapidemo.presentation

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import com.grapplo.swapidemo.R
import com.grapplo.swapidemo.api.ApiClient
import com.grapplo.swapidemo.base.BaseStateViewModel
import com.grapplo.swapidemo.base.StateFail
import com.grapplo.swapidemo.base.StateLoading
import com.grapplo.swapidemo.domain.SearchResult
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

class SearchViewModel constructor(val apiClient: ApiClient) :
    BaseStateViewModel<SearchViewModel.State>() {

    val searchPhraseUI = MutableLiveData<String>().apply {
        observeForever { _searchPhrase.onNext(it) }
    }

    enum class SearchMode(@StringRes label: Int) {
        PLANET(R.string.subject_planet),
        PERSON(R.string.subject_person)
    }

    val result = MutableLiveData<List<SearchResult>>()

    private val _searchPhrase = BehaviorSubject.create<String>()

    init {
        toState(State.Idle)
        projectPhraseToSearch()

        state.observeForever { state ->
            when (state) {
                is State.Result -> {
                    result.postValue(state.result)
                }
                is State.Idle -> {
                    result.postValue(emptyList())
                }
            }
        }
    }

    private fun projectPhraseToSearch() {
        _searchPhrase
            .debounce(2, TimeUnit.SECONDS)
            .distinctUntilChanged()
            .doOnNext { toState(State.Searching(it)) }
            .switchMapCompletable { phrase ->
                if (phrase.isNotBlank()) {
                    searchPhrase(phrase)
                } else {
                    Completable.fromAction { toState(State.Idle) }
                }
            }
            .subscribeOn(Schedulers.computation())
            .subscribe({
                Log.d("OK", "")
            }, {
                toState(State.Error(it))
                Log.e("Err", "ERROR> $it")
            })
            .toDisposables()
    }

    fun searchPhrase(phrase: String): Completable =
        Single.just(phrase)
            .flatMap { apiClient.searchPlanet(it) }
            .doOnSuccess { response ->
                response.results
                    .map { SearchResult.PlanetResult(it) }
                    .toList()
                    .let { list ->
                        toState(State.Result(phrase, list))
                    }
            }
            .ignoreElement()

    sealed class State {
        object Idle : State()
        data class Searching(val phrase: String) : State(), StateLoading
        data class Result(val phrase: String, val result: List<SearchResult>) : State()
        data class Error(override val throwable: Throwable) : State(), StateFail
    }
}
