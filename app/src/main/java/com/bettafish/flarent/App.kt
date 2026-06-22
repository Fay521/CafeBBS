package com.bettafish.flarent

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.bettafish.flarent.config.ForumConfig
import com.bettafish.flarent.di.networkModule
import com.bettafish.flarent.di.repositoryModule
import com.bettafish.flarent.di.viewModelModule
import com.bettafish.flarent.utils.PusherManager
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        INSTANCE = this
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(listOf(networkModule, repositoryModule, viewModelModule))
        }

        PusherManager.init(ForumConfig.pusherAppKey, ForumConfig.pusherCluster)

        // Lifecycle-aware Pusher connection management
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                PusherManager.connect()
            }

            override fun onStop(owner: LifecycleOwner) {
                PusherManager.disconnect()
            }
        })
    }

    companion object {
        lateinit var INSTANCE: App
    }
}
