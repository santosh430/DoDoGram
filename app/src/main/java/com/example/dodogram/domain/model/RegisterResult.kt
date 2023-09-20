package com.example.dodogram.domain.model

import com.example.dodogram.domain.model.LoggedInUserView

data class RegisterResult(
    val success: LoggedInUserView? = null,
    val error: Int? = null
)
