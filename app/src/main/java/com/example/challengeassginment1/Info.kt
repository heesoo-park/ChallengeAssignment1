package com.example.challengeassginment1

import android.os.Parcelable
import com.example.challengeassginment1.signup.SignUpUserEntity

// 아이디/비밀번호 저장공간
object Info {
    private val info_list = mutableListOf<SignUpUserEntity>()

    fun setInfo(userInfo: SignUpUserEntity?) {
        if (userInfo != null) {
            info_list.add(userInfo)
        }
    }

    fun getInfo(name: String): SignUpUserEntity {
        for (user in info_list) {
            if (user.name == name) {
                return user
            }
        }

        return SignUpUserEntity("", "", "")
    }

    fun findInfo(info: SignUpUserEntity): Boolean =
        info_list.contains(info)

    fun editInfo(email: String?, userInfo: SignUpUserEntity?) {
        var target = info_list.find { it.email == email }
        target?.name = userInfo?.name ?: ""
        target?.email = userInfo?.email ?: ""
        target?.password = userInfo?.password ?: ""
    }
}