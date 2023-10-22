package com.example.dodogram.data.repository.login

import com.example.dodogram.core.common.Result
import com.example.dodogram.domain.model.LoggedInUser
import com.example.dodogram.domain.repository.LoginRepository
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FacebookLoginRepository @Inject constructor(val firebaseAuth: FirebaseAuth,val callbackManager: CallbackManager,val loginManager: LoginManager):LoginRepository{
    override fun login(username: String?, password: String?): Flow<Result<LoggedInUser>> {
        TODO("Not yet implemented")
    }

}