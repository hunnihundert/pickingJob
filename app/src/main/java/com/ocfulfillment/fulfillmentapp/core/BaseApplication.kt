package com.ocfulfillment.fulfillmentapp.core

import android.app.Application
import com.ocfulfillment.fulfillmentapp.di.remoteModule
import com.ocfulfillment.fulfillmentapp.di.repositoryModule
import com.ocfulfillment.fulfillmentapp.di.viewmodelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@BaseApplication)
            modules(
                remoteModule,
                repositoryModule,
                viewmodelModule
            )
        }
    }
}