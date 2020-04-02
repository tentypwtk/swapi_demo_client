package com.grapplo.swapidemo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchViewModel : ViewModel() {
    val test = MutableLiveData<String>()

    init {
        test.postValue("Hello")
    }
}
