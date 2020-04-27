package com.mobile.diastore

import android.app.Application
import com.mobile.diastore.di.networkModule
import com.mobile.diastore.di.viewModels
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class DiaStoreApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@DiaStoreApplication)
            modules(listOf(viewModels, networkModule))
        }
    }
}