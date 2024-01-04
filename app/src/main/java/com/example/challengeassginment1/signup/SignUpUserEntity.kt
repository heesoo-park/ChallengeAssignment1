package com.example.challengeassginment1.signup

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SignUpUserEntity(
    var name: String,
    var email: String,
    var password: String,
) : Parcelable
