package com.example.dodogram.di.component

import com.example.dodogram.di.module.ApplicationModule
import com.example.dodogram.presentation.viewmodel.LoginViewModel
import dagger.Component

@Component(modules = [ApplicationModule::class])
interface MyApplicationComponent {
    fun getLoginViewModel(): LoginViewModel
}