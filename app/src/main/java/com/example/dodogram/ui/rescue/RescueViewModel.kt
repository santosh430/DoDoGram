package com.example.dodogram.ui.rescue

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RescueViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is rescue Fragment"
    }
    val text: LiveData<String> = _text
}