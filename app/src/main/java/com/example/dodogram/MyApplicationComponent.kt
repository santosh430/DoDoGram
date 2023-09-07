package com.example.dodogram

import com.example.dodogram.ui.login.ui.login.LoginViewModel
import dagger.Component

@Component(modules = [ApplicationModule::class])
interface MyApplicationComponent {
    fun getLoginViewModel():LoginViewModel
}