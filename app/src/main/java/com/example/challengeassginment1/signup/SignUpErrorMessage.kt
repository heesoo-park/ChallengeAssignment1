package com.example.challengeassginment1.signup

import androidx.annotation.StringRes
import com.example.challengeassginment1.R

enum class SignUpErrorMessage(
    @StringRes val message: Int
) {
    NAME_BLANK(R.string.sign_up_name_error),

    EMAIL_BLANK(R.string.sign_up_email_error),
    EMAIL_COM(R.string.sign_up_email_back_format_error),
    EMAIL_SERVICE_PROVIDER(R.string.sign_up_email_back_error),

    PASSWORD_HINT(R.string.sign_up_password_hint),
    PASSWORD_LENGTH(R.string.sign_up_password_error_length),
    PASSWORD_SPECIAL_CHARACTERS(R.string.sign_up_password_error_special),
    PASSWORD_UPPER_CASE(R.string.sign_up_password_error_upper),
    PASSWORD_NUMBER(R.string.sign_up_password_error_number),
    PASSWORD_CHECK_NOT(R.string.sign_up_password_check_error),
    PASSWORD_CHECK_OK(R.string.sign_up_password_check_ok),

    PASS(R.string.sign_up_pass)
}