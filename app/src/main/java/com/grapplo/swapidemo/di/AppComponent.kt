package com.grapplo.swapidemo.di

import com.grapplo.swapidemo.BuildConfig
import com.grapplo.swapidemo.api.ApiClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object AppComponent {
    val module = module {
        factory<Retrofit> {
            Retrofit
                .Builder()
                .baseUrl(BuildConfig.API_BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        factory<ApiClient> {
            get<Retrofit>().create()
        }
    }
}