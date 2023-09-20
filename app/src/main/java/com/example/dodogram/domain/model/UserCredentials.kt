package com.example.dodogram.domain.model

import android.net.Uri

data class UserCredentials(
    val uid:String,
    var userName: String?,
    var profilePicUrl:Uri?,
    var phoneNumber:String?,
    var email:String?,
    val loggedInTime:Long?
)
