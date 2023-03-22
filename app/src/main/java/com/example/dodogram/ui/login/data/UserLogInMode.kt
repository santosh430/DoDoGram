package com.example.dodogram.ui.login.data

import android.os.Parcelable
import com.example.dodogram.enums.LoginMode
import kotlinx.android.parcel.Parcelize

sealed class UserLogInMode:Parcelable{

    @Parcelize
    data class SignIn(val loginMode: LoginMode): UserLogInMode()
    @Parcelize
    data class Register(val loginMode: LoginMode):UserLogInMode()
}



