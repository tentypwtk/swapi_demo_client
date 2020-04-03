package com.grapplo.swapidemo.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseStateViewModel<T: Any> : ViewModel() {
    open val state = MutableLiveData<T>()

    fun toState(s: T) {
        state.postValue(s)
    }

    fun <T> MutableLiveData<T>.initWith(initialValue: T): MutableLiveData<T> =
        apply { value = initialValue }

    private val compositeDisposable = CompositeDisposable()

    fun Disposable.toDisposables() = compositeDisposable.add(this)
}
