package com.grapplo.swapidemo.base

import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter

object VisibilityBindings {
    @JvmStatic
    @BindingAdapter("android:visibility")
    fun View.setVisibility(isVisible: Boolean) {
        if (this.isVisible != isVisible) {
            this.isVisible = isVisible
        }
    }
}