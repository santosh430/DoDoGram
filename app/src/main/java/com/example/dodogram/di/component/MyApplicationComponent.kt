package com.example.dodogram.di.component

import com.example.dodogram.di.module.ApplicationModule
import com.example.dodogram.presentation.MainActivity
import com.example.dodogram.presentation.ui.login.LoginActivity
import com.example.dodogram.presentation.viewmodel.LoginViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class])
interface MyApplicationComponent {
    fun getLoginViewModel(): LoginViewModel

    fun injectLoginActivity(loginActivity: LoginActivity)

    fun injectMainActivity(mainActivity: MainActivity)
}