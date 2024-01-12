package com.example.challengeassginment1.signup

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SignUpViewModelFactory(private val context: Context, private val entryType: SignUpEntryType, private val userEntity: SignUpUserEntity?): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            return SignUpViewModel(context, entryType, userEntity ?: SignUpUserEntity("", "", "")) as T
        }
        throw IllegalArgumentException("Unknown viewModel Class")
    }
}