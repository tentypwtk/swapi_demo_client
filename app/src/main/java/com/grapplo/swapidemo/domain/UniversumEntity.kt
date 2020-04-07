package com.grapplo.swapidemo.domain

import android.os.Parcelable

interface SwEntity: Parcelable {
    val name: String
    val description: String
}