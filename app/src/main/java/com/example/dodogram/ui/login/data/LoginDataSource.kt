package com.example.dodogram.ui.login.data

import com.example.dodogram.ui.login.data.model.LoggedInUser
import com.google.firebase.auth.FirebaseAuth
import java.io.IOException
import javax.inject.Inject

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource @Inject constructor() {

    fun login(username: String, password: String): Result<LoggedInUser> {
        return try {
            val user = LoggedInUser(java.util.UUID.randomUUID().toString(), username)
            Result.Success(user)
        } catch (e: Throwable) {
            Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
    }
}