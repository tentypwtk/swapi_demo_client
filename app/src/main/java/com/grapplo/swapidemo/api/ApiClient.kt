package com.grapplo.swapidemo.api

import com.grapplo.swapidemo.api.response.SwapiResponse
import com.grapplo.swapidemo.domain.Planet
import io.reactivex.Single
import retrofit2.http.*

interface ApiClient {
    @GET("planets")
    fun searchPlanet(@Query("search") phrase: String): Single<SwapiResponse<Planet>>
}