package com.grapplo.swapidemo.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.grapplo.swapidemo.R
import com.grapplo.swapidemo.api.ApiClient
import com.grapplo.swapidemo.base.BaseStateViewModel
import com.grapplo.swapidemo.base.StateFail
import com.grapplo.swapidemo.base.StateLoading
import com.grapplo.swapidemo.domain.SearchResult
import com.grapplo.swapidemo.domain.SwEntity
import com.grapplo.swapidemo.presentation.SearchViewModel.SearchMode.PERSON
import com.grapplo.swapidemo.presentation.SearchViewModel.SearchMode.PLANET
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

class SearchViewModel constructor(
    private val apiClient: ApiClient,
    private val context: Context
) :
    BaseStateViewModel<SearchViewModel.State>() {

    val subject = MutableLiveData<SearchMode>().apply {
        observeForever { _searchSubject.onNext(it) }
    }

    val searchPhraseUI = MutableLiveData<String>().apply {
        observeForever { _searchPhrase.onNext(it) }
    }

    @SuppressLint("DefaultLocale")
    val searchHint = Transformations.map(subject) {
        context.getString(R.string.search_by_hint).format(context.getString(it.label).toLowerCase())
    }

    enum class SearchMode(@StringRes val label: Int) {
        PLANET(R.string.subject_planet),
        PERSON(R.string.subject_person)
    }

    val result = MutableLiveData<List<SearchResult<SwEntity>>>()

    private val _searchPhrase = BehaviorSubject.create<String>()
    private val _searchSubject = BehaviorSubject.createDefault(PLANET)

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
            .switchMap { phrase ->
                _searchSubject
                    .map { subject ->
                        phrase to subject
                    }
            }
            .doOnNext { (phrase, subject) -> toState(State.Searching(phrase, subject)) }
            .switchMapCompletable { (phrase, mode) ->
                if (phrase.isNotBlank()) {
                    searchPhrase(phrase, mode)
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

    fun searchPhrase(
        phrase: String,
        mode: SearchMode
    ): Completable =
        when (mode) {
            PLANET -> apiClient.searchPlanet(phrase)
                .map { response ->
                    response.results.map { SearchResult.PlanetResult(it) }
                }
            PERSON -> apiClient.searchPerson(phrase)
                .map { response ->
                    response.results.map { SearchResult.PersonResult(it) }
                }
        }
            .subscribeOn(Schedulers.computation())
            .doOnSuccess {
                toState(State.Result(phrase, it as List<SearchResult<SwEntity>>))
            }
            .ignoreElement()

    sealed class State {
        object Idle : State()
        data class Searching(val phrase: String, val subject: SearchMode) : State(), StateLoading
        data class Result(val phrase: String, val result: List<SearchResult<SwEntity>>) : State()
        data class Error(override val throwable: Throwable) : State(), StateFail
    }
}
