package com.example.challengeassginment1.signup

import android.content.Context
import android.widget.ArrayAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.challengeassginment1.Info.editInfo
import com.example.challengeassginment1.Info.setInfo
import com.example.challengeassginment1.R
import com.example.challengeassginment1.signup.SignUpValidExtension.includeDotCom
import com.example.challengeassginment1.signup.SignUpValidExtension.includeNumber
import com.example.challengeassginment1.signup.SignUpValidExtension.includeSpecialCharacters
import com.example.challengeassginment1.signup.SignUpValidExtension.includeUpperCase

class SignUpViewModel(
    private val context: Context,
    private val entryType: SignUpEntryType,
    private val userEntity: SignUpUserEntity
) : ViewModel() {
    // 서비스 제공자 리스트
    var serviceProviderStringList: List<String>

    private val _userInfo: MutableLiveData<SignUpUserEntity> = MutableLiveData()
    val userInfo: LiveData<SignUpUserEntity> get() = _userInfo

    private val _name: MutableLiveData<String> = MutableLiveData()
    val name: LiveData<String> get() = _name

    private val _email: MutableLiveData<String> = MutableLiveData()
    val email: LiveData<String> get() = _email

    private val _emailProvider: MutableLiveData<String?> = MutableLiveData()
    val emailProvider: LiveData<String?> get() = _emailProvider

    private val _password: MutableLiveData<String> = MutableLiveData()
    val password: LiveData<String> get() = _password

    private val _passwordCheck: MutableLiveData<String> = MutableLiveData()
    val passwordCheck: LiveData<String> get() = _passwordCheck

    private val _nameErrorMsg: MutableLiveData<Int> = MutableLiveData()
    val nameErrorMsg: LiveData<Int> get() = _nameErrorMsg

    private val _emailErrorMsg: MutableLiveData<Int> = MutableLiveData()
    val emailErrorMsg: LiveData<Int> get() = _emailErrorMsg

    private val _passwordErrorMsg: MutableLiveData<Int> = MutableLiveData()
    val passwordErrorMsg: LiveData<Int> get() = _passwordErrorMsg

    private val _passwordCheckErrorMsg: MutableLiveData<Int> = MutableLiveData()
    val passwordCheckErrorMsg: LiveData<Int> get() = _passwordCheckErrorMsg

    private val _signUpText: MutableLiveData<Int> = MutableLiveData()
    val signUpText: LiveData<Int> get() = _signUpText

    private val _emailProviderVisible: MutableLiveData<Boolean> = MutableLiveData()
    val emailProviderVisible: LiveData<Boolean> get() = _emailProviderVisible

    private val _spinnerIdx: MutableLiveData<Int> = MutableLiveData()
    val spinnerIdx: LiveData<Int> get() = _spinnerIdx

    private val _signupButtonEnable: MutableLiveData<Boolean> = MutableLiveData()
    val signupButtonEnable: LiveData<Boolean> get() = _signupButtonEnable

    private val _passwordTextColor: MutableLiveData<Int> = MutableLiveData()
    val passwordTextColor: LiveData<Int> get() = _passwordTextColor

    private val _passwordCheckTextColor: MutableLiveData<Int> = MutableLiveData()
    val passwordCheckTextColor: LiveData<Int> get() = _passwordCheckTextColor

    init {
        _name.value = userEntity.name
        _email.value = userEntity.email
        _password.value = userEntity.password
        // 리소스 아이디만 보내기
        serviceProviderStringList = listOf(
            context.getString(R.string.sign_up_email_provider_gmail),
            context.getString(R.string.sign_up_email_provider_kakao),
            context.getString(R.string.sign_up_email_provider_naver),
            context.getString(R.string.sign_up_email_provider_direct)
        )
    }

    fun setViewsText() {
        if (entryType == SignUpEntryType.CREATE) {
            _emailProvider.value = serviceProviderStringList[0]
            _signUpText.value = R.string.sign_up_confirm
            return
        }

        // 회원수정 페이지일 때 초기 세팅
        _name.value = userEntity.name
        _email.value = userEntity.email.substringBefore('@')
        _emailProvider.value = userEntity.email.substringAfter('@')
        _password.value = userEntity.password
        _passwordCheck.value = userEntity.password
        _passwordErrorMsg.value = SignUpErrorMessage.PASS.message
        _passwordCheckErrorMsg.value = SignUpErrorMessage.PASSWORD_CHECK_OK.message
        _signUpText.value = R.string.sign_up_edit

        val idx = serviceProviderStringList.indexOf(userEntity.email.substringAfter('@'))
        _spinnerIdx.value = if (idx < 0) {
            serviceProviderStringList.lastIndex
        } else {
            idx
        }

        isButtonEnable()
    }

    fun checkSpinnerIdx(position: Int) {
        when (position) {
            // 직접 입력을 선택하지 않은 경우
            in 0..2 -> {
                setEmailBackViewAndServiceProvider(false, serviceProviderStringList[position])
            }
            // 직접 입력을 선택한 경우
            else -> {
                if (serviceProviderStringList.contains(emailProvider.value)) {
                    setEmailBackViewAndServiceProvider(true, null)
                } else {
                    setEmailBackViewAndServiceProvider(true, emailProvider.value)
                }
            }
        }
    }

    private fun setEmailBackViewAndServiceProvider(type: Boolean, s: String?) {
        // 서비스 제공자 뷰 노출 처리
        _emailProviderVisible.value = type
        // 해당 아이템 내용을 저장
        _emailProvider.value = s
    }

    fun isButtonEnable() {
        _signupButtonEnable.value =
            nameErrorMsg.value == SignUpErrorMessage.PASS.message
                    && emailErrorMsg.value == SignUpErrorMessage.PASS.message
                    && passwordErrorMsg.value == SignUpErrorMessage.PASS.message
                    && passwordCheckErrorMsg.value == SignUpErrorMessage.PASSWORD_CHECK_OK.message
    }

    fun checkValidName(text: String) {
        _nameErrorMsg.value =
            if (text.isEmpty()) {
                SignUpErrorMessage.NAME_BLANK
            } else {
                SignUpErrorMessage.PASS
            }.message
    }

    fun checkValidEmail(frontText: String, backText: String) {
        _emailErrorMsg.value =
            when (backText) {
                in serviceProviderStringList -> {
                    when {
                        frontText.isEmpty() -> SignUpErrorMessage.EMAIL_BLANK
                        else -> SignUpErrorMessage.PASS
                    }
                }

                else -> {
                    when {
                        frontText.isEmpty() -> SignUpErrorMessage.EMAIL_BLANK
                        backText.isEmpty() -> SignUpErrorMessage.EMAIL_SERVICE_PROVIDER
                        backText.includeDotCom().not() -> SignUpErrorMessage.EMAIL_COM
                        else -> SignUpErrorMessage.PASS
                    }
                }
            }.message
    }

    fun checkValidPassword(text: String) {
        if (text.isEmpty()) {
            changePasswordErrorTextColor(R.color.grey)
            _passwordErrorMsg.value = SignUpErrorMessage.PASSWORD_HINT.message
        } else {
            changePasswordErrorTextColor(R.color.red)
            _passwordErrorMsg.value =
                when {
                    // 비밀번호의 길이 체크하고 에러 텍스트 노출
                    text.length < 8 || text.length >= 20 -> SignUpErrorMessage.PASSWORD_LENGTH
                    // 비밀번호의 대문자 포함 여부 체크하고 에러 텍스트 노출
                    text.includeUpperCase().not() -> SignUpErrorMessage.PASSWORD_UPPER_CASE
                    // 비밀번호의 숫자 포함 여부 체크하고 에러 텍스트 노출
                    text.includeNumber().not() -> SignUpErrorMessage.PASSWORD_NUMBER
                    // 비밀번호의 특수문자 포함 여부 체크하고 에러 텍스트 노출
                    text.includeSpecialCharacters()
                        .not() -> SignUpErrorMessage.PASSWORD_SPECIAL_CHARACTERS
                    // 통과
                    else -> SignUpErrorMessage.PASS
                }.message
        }
    }

    fun checkValidPasswordCheck(passwordText: String, checkText: String) {
        _passwordCheckErrorMsg.value =
            if (passwordText != checkText) {
                changePasswordCheckErrorTextColor(R.color.red)
                SignUpErrorMessage.PASSWORD_CHECK_NOT
            } else {
                changePasswordCheckErrorTextColor(R.color.blue)
                SignUpErrorMessage.PASSWORD_CHECK_OK
            }.message
    }

    private fun changePasswordErrorTextColor(color: Int) {
        _passwordTextColor.value = color
    }

    private fun changePasswordCheckErrorTextColor(color: Int) {
        _passwordCheckTextColor.value = color
    }

    fun onClickSignUp(name: String, email: String, password: String) {
        _userInfo.value = SignUpUserEntity(name, email, password)
        when (entryType) {
            SignUpEntryType.UPDATE -> editInfo(
                userInfo.value?.email,
                userInfo.value
            )

            else -> setInfo(userInfo.value)
        }
    }
}