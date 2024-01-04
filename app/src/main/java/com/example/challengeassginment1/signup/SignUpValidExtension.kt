package com.example.challengeassginment1.signup

object SignUpValidExtension {
    fun String.includeDotCom() = this.contains(".com")

    fun String.includeSpecialCharacters() = Regex("[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]+").containsMatchIn(this)
    fun String.includeUpperCase() = Regex("[A-Z]").containsMatchIn(this)
    fun String.includeNumber() = Regex("[0-9]").containsMatchIn(this)
}