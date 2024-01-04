package com.example.challengeassginment1

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.example.challengeassginment1.Info.editInfo
import com.example.challengeassginment1.Info.setInfo
import com.example.challengeassginment1.SignUpValidExtension.includeDotCom
import com.example.challengeassginment1.SignUpValidExtension.includeNumber
import com.example.challengeassginment1.SignUpValidExtension.includeSpecialCharacters
import com.example.challengeassginment1.SignUpValidExtension.includeUpperCase

class SignUpActivity : AppCompatActivity() {
    private val editSignUpName: EditText by lazy { findViewById(R.id.edit_signup_name) }
    private val editSignUpEmailFront: EditText by lazy { findViewById(R.id.edit_signup_email_front) }
    private val editSignUpEmailBack: EditText by lazy { findViewById(R.id.edit_signup_email_back) }
    private val editSignUpPassword: EditText by lazy { findViewById(R.id.edit_signup_password) }
    private val editSignUpPasswordCheck: EditText by lazy { findViewById(R.id.edit_signup_password_check) }

    private val editTexts
        get() = listOf(
            editSignUpName,
            editSignUpEmailFront,
            editSignUpEmailBack,
            editSignUpPassword,
            editSignUpPasswordCheck
        )

    private val tvSignUpNameError: TextView by lazy { findViewById(R.id.tv_signup_name_error) }
    private val tvSignUpEmailError: TextView by lazy { findViewById(R.id.tv_signup_email_error) }
    private val tvSignUpPasswordError: TextView by lazy { findViewById(R.id.tv_signup_password_error) }
    private val tvSignUpPasswordCheckError: TextView by lazy { findViewById(R.id.tv_signup_password_check_error) }

    private val spinnerSignUp: Spinner by lazy { findViewById(R.id.spinner_signup) }
    private val btnSignup: Button by lazy { findViewById(R.id.btn_signup_check) }

    // 저장될 이메일 주소(xxx.com의 형식)
    private var serviceProvider = ""

    // 추가한 부분 ***********************************************************
    private var type: String = ""
    private var userEmail: String = ""
    // 추가한 부분 ***********************************************************

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        initView()
    }

    private fun initView() {
        checkEditPage()
        setOnFocusChangeListener()
        setTextChangedListener()
        setServiceProvider()
        setBtnSignupClickListener()
    }

    private fun checkEditPage() {
        type = intent.getStringExtra("type") ?: ""
        if (type != "signup") {
            userEmail = intent.getStringExtra("email").toString()
            val loginUser = Info.getInfo(userEmail)

            editSignUpName.setText(loginUser.name)
            editSignUpEmailFront.setText(loginUser.email.substringBefore('@'))
            editSignUpPassword.setText(loginUser.password)
            tvSignUpPasswordError.text = getString(SignUpErrorMessage.PASS.message)
            editSignUpPasswordCheck.setText(loginUser.password)
            tvSignUpPasswordCheckError.text = getString(SignUpErrorMessage.PASSWORD_CHECK_OK.message)
            printPasswordCheckErrorText()
            btnSignup.text = getString(R.string.sign_up_edit)
        }
    }

    // 회원가입 버튼 클릭 이벤트 함수
    private fun setBtnSignupClickListener() {
        btnSignup.setOnClickListener {
            val name = editSignUpName.text.toString()
            // 이메일 입력칸의 내용과 이메일 주소 입력칸의 내용을 중간에 @ 포함해서 합치기
            val email = editSignUpEmailFront.text.toString() + "@" + serviceProvider
            val password = editSignUpPassword.text.toString()

            if (type != "signup") {
                editInfo(userEmail, Info.User(name, email, password))
            } else {
                // 정보 저장
                setInfo(name, email, password)
            }

            // 로그인 액티비티로 보낼 값 설정
            intent.putExtra("name", name)
            intent.putExtra("email", email)
            intent.putExtra("password", password)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    // Focus 변화를 감지하는 리스너 함수
    private fun setOnFocusChangeListener() {
        editTexts.forEach { et ->
            et.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus.not()) {
                    et.setErrorText()
                    setButtonEnable()
                }
            }
        }
    }

    private fun setTextChangedListener() {
        editTexts.forEach { et ->
            et.addTextChangedListener {
                et.setErrorText()
                setButtonEnable()
            }
        }
    }

    private fun setServiceProvider() {
        // 어댑터 설정
        val adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, listOf(
                getString(R.string.sign_up_email_provider_gmail),
                getString(R.string.sign_up_email_provider_kakao),
                getString(R.string.sign_up_email_provider_naver),
                getString(R.string.sign_up_email_provider_direct)
            )
        )
        // 어댑터 연결
        spinnerSignUp.adapter = adapter

        if (type != "signup") {
            val loginUser = Info.getInfo(userEmail)
            when (loginUser.email.substringAfter('@')) {
                getString(R.string.sign_up_email_provider_gmail) -> spinnerSignUp.setSelection(0)
                getString(R.string.sign_up_email_provider_kakao) -> spinnerSignUp.setSelection(1)
                getString(R.string.sign_up_email_provider_naver) -> spinnerSignUp.setSelection(2)
                else -> {
                    editSignUpEmailBack.setText(loginUser.email.substringAfter('@'))
                    spinnerSignUp.setSelection(3)
                }
            }

            setButtonEnable()
        }

        // Spinner 아이템 선택 리스너
        spinnerSignUp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            // 아이템을 선택하는 경우 호출
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    // 직접 입력을 선택하지 않은 경우
                    in 0..2 -> setEmailBackViewAndServiceProvider(
                        false,
                        parent?.getItemAtPosition(position).toString()
                    )
                    // 직접 입력을 선택한 경우
                    else -> setEmailBackViewAndServiceProvider(true, "")
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) = Unit
        }
    }

    private fun setEmailBackViewAndServiceProvider(type: Boolean, s: String) {
        // 서비스 제공자 뷰 노출 처리
        editSignUpEmailBack.isVisible = type
        // 해당 아이템 내용을 저장
        serviceProvider = s
    }

    private fun setButtonEnable() {
        btnSignup.isEnabled = isButtonEnable()
    }

    private fun isButtonEnable(): Boolean =
        tvSignUpNameError.text.isEmpty()
                && editSignUpName.text.isNotEmpty()
                && tvSignUpEmailError.text.isEmpty()
                && editSignUpEmailFront.text.isNotEmpty()
                && tvSignUpPasswordError.text.isEmpty()
                && (tvSignUpPasswordCheckError.text.toString() == getString(SignUpErrorMessage.PASSWORD_CHECK_OK.message))


    private fun EditText.setErrorText() {
        when (this) {
            // 입력칸이 비어있는 경우에 에러 텍스트 노출
            editSignUpName -> tvSignUpNameError.text = printNameErrorText()
            // 이메일 입력칸과 비교한 후에 에러 텍스트 노출
            editSignUpEmailFront, editSignUpEmailBack -> tvSignUpEmailError.text =
                printEmailErrorText()
            // 초기 세팅으로 노출
            editSignUpPassword ->
                tvSignUpPasswordError.text = if (editSignUpPassword.text.isEmpty()) {
                    changeTextColor(tvSignUpPasswordError, R.color.grey)
                    getString(SignUpErrorMessage.PASSWORD_HINT.message)
                } else {
                    changeTextColor(tvSignUpPasswordError, R.color.red)
                    printPasswordErrorText()
                }
            // 비밀번호가 입력되어있고 비밀번호 확인 입력칸이 비어있는 경우에 에러 텍스트 노출
            editSignUpPasswordCheck -> tvSignUpPasswordCheckError.text =
                printPasswordCheckErrorText()
        }
    }

    private fun printNameErrorText(): String = getString(
        if (editSignUpName.text.toString().isEmpty()) {
            SignUpErrorMessage.NAME_BLANK
        } else {
            SignUpErrorMessage.PASS
        }.message
    )

    private fun printEmailErrorText(): String {
        val frontText = editSignUpEmailFront.text.toString()
        val backText = editSignUpEmailBack.text.toString()
        return getString(
            when {
                backText.isNotEmpty() -> {
                    if (backText.includeDotCom().not()) {
                        SignUpErrorMessage.EMAIL_COM
                    } else {
                        serviceProvider = backText
                        SignUpErrorMessage.PASS
                    }
                }

                backText.isEmpty() && frontText.isNotEmpty() -> {
                    if (editSignUpEmailBack.isVisible) {
                        SignUpErrorMessage.EMAIL_SERVICE_PROVIDER
                    } else {
                        SignUpErrorMessage.PASS
                    }
                }

                frontText.isEmpty() -> SignUpErrorMessage.EMAIL_BLANK
                else -> SignUpErrorMessage.PASS
            }.message
        )
    }

    private fun printPasswordErrorText(): String {
        val text = editSignUpPassword.text.toString()
        return getString(
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
        )
    }

    private fun printPasswordCheckErrorText(): String = getString(
        if (editSignUpPassword.text.toString() != editSignUpPasswordCheck.text.toString()) {
            changeTextColor(tvSignUpPasswordCheckError, R.color.red)
            SignUpErrorMessage.PASSWORD_CHECK_NOT
        } else {
            changeTextColor(tvSignUpPasswordCheckError, R.color.blue)
            SignUpErrorMessage.PASSWORD_CHECK_OK
        }.message
    )

    private fun changeTextColor(textView: TextView, color: Int) {
        textView.setTextColor(
            ContextCompat.getColor(
                this@SignUpActivity,
                color
            )
        )
    }
}