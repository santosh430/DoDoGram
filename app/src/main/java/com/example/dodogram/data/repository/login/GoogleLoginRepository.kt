package com.example.dodogram.data.repository.login

import com.example.dodogram.core.common.Result
import com.example.dodogram.domain.model.LoggedInUser
import com.example.dodogram.domain.repository.LoginRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GoogleLoginRepository @Inject constructor(val firebaseAuth: FirebaseAuth): LoginRepository {
    override fun login(username: String?, password: String?): Flow<Result<LoggedInUser>> {
        TODO("Not yet implemented")
    }
}