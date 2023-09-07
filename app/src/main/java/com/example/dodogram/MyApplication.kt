package com.example.dodogram

import android.app.Application

class MyApplication :Application() {

    override fun onCreate() {
        super.onCreate()
        val appComponent = DaggerMyApplicationComponent.create()
    }
}