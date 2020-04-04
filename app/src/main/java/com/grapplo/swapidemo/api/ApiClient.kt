package com.grapplo.swapidemo.api

import com.grapplo.swapidemo.api.response.SwapiResponse
import com.grapplo.swapidemo.domain.Person
import com.grapplo.swapidemo.domain.Planet
import io.reactivex.Single
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiClient {
    @POST("/planet/")
    fun searchPlanet(@Query("search") phrase: String): Single<SwapiResponse<Planet>>
}