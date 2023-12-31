package com.example.dodogram.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dodogram.domain.usecase.LoginDataSource
import com.example.dodogram.data.repository.login.EmailLoginRepository
import com.google.firebase.auth.FirebaseAuth

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class LoginViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(
                emailLoginRepository = EmailLoginRepository(firebaseAuth = FirebaseAuth.getInstance())
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}