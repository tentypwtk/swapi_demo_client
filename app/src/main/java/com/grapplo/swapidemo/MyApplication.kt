package com.grapplo.swapidemo

import android.app.Application
import com.grapplo.swapidemo.di.AppComponent
import com.grapplo.swapidemo.di.ViewModelComponent
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

val appModules = listOf(
    AppComponent.module,
    ViewModelComponent.module
)

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
            modules(appModules)
        }
    }

}
