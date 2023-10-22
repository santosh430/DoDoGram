package com.example.dodogram

import android.app.Application
import android.util.Log
import com.example.dodogram.di.component.DaggerMyApplicationComponent

private const val TAG = "MyApplication"
class MyApplication :Application() {

    override fun onCreate() {
        super.onCreate()
        val appComponent = DaggerMyApplicationComponent.create()
        Log.d(TAG,"viewmodel : ${appComponent.getLoginViewModel()}")
    }
}