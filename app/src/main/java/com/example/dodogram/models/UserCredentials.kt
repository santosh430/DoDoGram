package com.example.dodogram.models

import android.net.Uri

data class UserCredentials(
    val uid:String,
    var userName: String?,
    var profilePicUrl:Uri?,
    var phoneNumber:String?,
    var email:String?
)
