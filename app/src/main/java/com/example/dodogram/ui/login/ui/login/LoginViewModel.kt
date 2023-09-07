package com.example.dodogram.ui.login.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import androidx.lifecycle.liveData
import com.example.dodogram.R
import com.example.dodogram.ui.login.data.LoginRepository
import com.example.dodogram.ui.login.data.Result
import com.example.dodogram.ui.login.data.model.LoginPageState
import javax.inject.Inject

class LoginViewModel @Inject constructor (private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginRegisterUiState = MutableLiveData<LoginPageState>()
    val loginRegisterUiState:LiveData<LoginPageState> = _loginRegisterUiState

    val liveData:LiveData<LoginFormState> = liveData {
        this.latestValue

    }

    private val _registerForm = MutableLiveData<LoginFormState>()
    val registerFormState: LiveData<LoginFormState> = _registerForm


    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _registerResult = MutableLiveData<RegisterResult>()
    val registerResult:LiveData<RegisterResult> = _registerResult

    fun loginRegisterUIState(uiState:LoginPageState){
        _loginRegisterUiState.value = uiState
    }

    fun login(username: String, password: String) {
        // can be launched in a separate asynchronous job
        val result = loginRepository.login(username, password)

        if (result is Result.Success) {
            _loginResult.value =
                LoginResult(success = LoggedInUserView(displayName = result.data.displayName))
        } else {
            _loginResult.value = LoginResult(error = R.string.login_failed)
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameLoginUserError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordLoginUserError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValidLoginUser = true)
        }
    }

    fun register(username: String, password: String) {
        // can be launched in a separate asynchronous job
        val result = loginRepository.login(username, password)

        if (result is Result.Success) {
            _registerResult.value =
                RegisterResult(success = LoggedInUserView(displayName = result.data.displayName))
        } else {
            _registerResult.value = RegisterResult(error = R.string.login_failed)
        }
    }

    fun registerDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _registerForm.value = LoginFormState(usernameRegisterUserError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _registerForm.value = LoginFormState(passwordRegisterUserError = R.string.invalid_password)
        } else {
            _registerForm.value = LoginFormState(isDataValidRegisterUser = true)
        }
    }


    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}