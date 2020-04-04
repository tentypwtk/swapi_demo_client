package com.grapplo.swapidemo.api.response

data class SwapiResponse<T : Any>(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<T>
)
