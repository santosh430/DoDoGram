package com.example.dodogram

import android.app.Application
import com.example.dodogram.di.component.DaggerMyApplicationComponent

class MyApplication :Application() {

    override fun onCreate() {
        super.onCreate()
        val appComponent = DaggerMyApplicationComponent.create()
    }
}