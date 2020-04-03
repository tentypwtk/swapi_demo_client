package com.grapplo.swapidemo.api

import io.reactivex.Single
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Url

interface ApiClient {
    @FormUrlEncoded
    @POST("/people/")
    fun searchPlanet(@Path("search") phrase: String): Single<List<String>>
}