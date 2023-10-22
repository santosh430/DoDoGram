package com.example.dodogram.domain.repository

import com.example.dodogram.core.common.Result
import com.example.dodogram.domain.model.LoggedInUser
import kotlinx.coroutines.flow.Flow

interface LoginRepository {

    fun login(username: String? = null, password: String? = null):Flow<Result<LoggedInUser>>

}