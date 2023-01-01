package com.example.dodogram.ui.login.ui.login

/**
 * Data validation state of the login form.
 */
data class LoginFormState(
    val usernameLoginUserError: Int? = null,
    val passwordLoginUserError: Int? = null,
    val isDataValidLoginUser: Boolean = false,
    val usernameRegisterUserError:Int? = null,
    val passwordRegisterUserError:Int? = null,
    val isDataValidRegisterUser:Boolean = false
)