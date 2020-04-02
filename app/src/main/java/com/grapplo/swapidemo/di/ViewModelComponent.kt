package com.grapplo.swapidemo.di

import com.grapplo.swapidemo.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object ViewModelComponent {
    val module = module {
        viewModel { SearchViewModel() }
    }
}