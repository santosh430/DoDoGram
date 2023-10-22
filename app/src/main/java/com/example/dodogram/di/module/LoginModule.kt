package com.example.dodogram.di.module

import com.example.dodogram.data.repository.login.EmailLoginRepository
import com.example.dodogram.data.repository.login.FacebookLoginRepository
import com.example.dodogram.data.repository.login.GoogleLoginRepository
import com.example.dodogram.domain.repository.LoginRepository
import dagger.Binds
import dagger.Module

@Module
abstract class LoginModule {

    @Binds
    abstract fun providesEmailLoginRepository(emailLoginRepository: EmailLoginRepository):LoginRepository

    @Binds
    abstract fun providesFacebookLoginRepository(facebookLoginRepository: FacebookLoginRepository):LoginRepository

    @Binds
    abstract fun providesGoogleLoginRepository(googleLoginRepository: GoogleLoginRepository):LoginRepository
}