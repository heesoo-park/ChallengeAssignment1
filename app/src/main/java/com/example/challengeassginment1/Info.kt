package com.example.challengeassginment1

import android.os.Parcelable
import com.example.challengeassginment1.signup.SignUpUserEntity

// 아이디/비밀번호 저장공간
object Info {
    private val info_list = mutableListOf<SignUpUserEntity>()

    fun setInfo(name: String, email: String, password: String) {
        info_list += SignUpUserEntity(name, email, password)
    }

    fun getInfo(name: String): SignUpUserEntity {
        for (user in info_list) {
            if (user.name == name) {
                return user
            }
        }

        return SignUpUserEntity("", "", "")
    }

    fun findInfo(name: String, email: String, password: String): Boolean =
        info_list.contains(SignUpUserEntity(name, email, password))

    fun editInfo(email: String, signUpUserEntity: SignUpUserEntity) {
        var target = info_list.find { it.email == email }
        target?.name = signUpUserEntity.name
        target?.email = signUpUserEntity.email
        target?.password = signUpUserEntity.password
    }
}