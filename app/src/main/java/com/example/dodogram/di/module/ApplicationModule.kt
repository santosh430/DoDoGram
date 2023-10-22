package com.example.dodogram.di.module

import com.example.dodogram.data.repository.login.EmailLoginRepository
import com.example.dodogram.domain.repository.LoginRepository
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth():FirebaseAuth{
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage():FirebaseFirestore{
        return Firebase.firestore
    }

    @Provides
    @Singleton
    fun provideEmailLoginRepository(firebaseAuth: FirebaseAuth):LoginRepository{
        return EmailLoginRepository(firebaseAuth)
    }

    fun provideCallbackManager():CallbackManager{
        return CallbackManager.Factory.create()
    }

    fun provideLoginManager():LoginManager{
        return LoginManager.getInstance()
    }
}