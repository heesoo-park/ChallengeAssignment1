package com.example.challengeassginment1

// 아이디/비밀번호 저장공간
object Info {
    private val info_list = mutableListOf<User>()

    fun setInfo(name: String, email: String, password: String) {
        info_list += User(name, email, password)
    }


    fun findInfo(name: String, email: String, password: String): Boolean {
        if (info_list.contains(User(name, email, password))) {

            return true
        }

        return false
    }

    fun editInfo(email: String, user: User) {
        var target = info_list.find { it.email == email }
        target?.name = user.name
        target?.email = user.email
        target?.password = user.password
    }

    data class User (
        var name: String,
        var email: String,
        var password: String,
    )
}