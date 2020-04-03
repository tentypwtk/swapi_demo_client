package com.grapplo.swapidemo.base

interface StateLoading
interface StateFail {
    val throwable: Throwable
}