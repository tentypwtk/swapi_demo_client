package com.grapplo.swapidemo.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.grapplo.swapidemo.BuildConfig
import com.grapplo.swapidemo.api.ApiClient
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object AppComponent {
    val module = module {
        factory<OkHttpClient> {
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }).build()
        }

        factory<Retrofit> {
            Retrofit
                .Builder()
                .client(get())
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