package com.example.datastoragesample.application

import android.app.Application
import android.content.Context
import com.facebook.stetho.Stetho

class App : Application() {

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        initStetho()
    }

    private fun initStetho() {
        Stetho.initializeWithDefaults(this)
    }

    companion object {

        lateinit var instance: App

        fun applicationContext(): Context = instance.applicationContext
    }
}