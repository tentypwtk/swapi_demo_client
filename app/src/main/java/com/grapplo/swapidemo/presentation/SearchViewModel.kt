package com.grapplo.swapidemo.presentation

import androidx.lifecycle.MutableLiveData
import com.grapplo.swapidemo.api.ApiClient
import com.grapplo.swapidemo.base.BaseStateViewModel
import com.grapplo.swapidemo.base.StateFail
import com.grapplo.swapidemo.base.StateLoading
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

class SearchViewModel constructor(val apiClient: ApiClient) :
    BaseStateViewModel<SearchViewModel.State>() {

    val searchPhrase = MutableLiveData<String>().apply {
        observeForever { _searchProjection.onNext(it) }
    }

    val result = MutableLiveData<String>()

    private val _searchProjection = BehaviorSubject.create<String>()

    init {
        toState(State.Idle)
        createSearchProjection()
    }

    private fun createSearchProjection() {
        _searchProjection
            .debounce(2, TimeUnit.SECONDS)
            .distinctUntilChanged()
            .switchMapSingle { phrase ->
                apiClient.searchPlanet(phrase)
                    .doOnSubscribe { toState(State.Searching(phrase)) }
                    .map { result ->
                        phrase to result
                    }
            }
            .subscribeOn(Schedulers.computation())
            .subscribe({ (phrase, results) ->
                toState(State.Result(phrase, results))
            }, {
                toState(State.Error(it))
            }).toDisposables()
    }

    sealed class State {

        object Idle : State()
        data class Searching(val phrase: String) : State(), StateLoading
        data class Result(val phrase: String, val result: List<String>) : State()
        data class Error(override val throwable: Throwable) : State(), StateFail
    }
}
