package com.example.dodogram.core.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

object AppSharedPreferences {

    private var isUserLoggedIn = false

    fun getUserLogInSharedPreference(context: Context):Boolean{
        val sh: SharedPreferences = context.getSharedPreferences("MySharedPref", MODE_PRIVATE)
        return sh.getBoolean(isUserLoggedIn.toString(),false)
    }

    fun setUserLogInSharedPreference(context:Context) {
        // Storing data into SharedPreferences
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val myEdit = sharedPreferences.edit()
        myEdit.putBoolean(isUserLoggedIn.toString(),true)
        myEdit.apply()
    }
}