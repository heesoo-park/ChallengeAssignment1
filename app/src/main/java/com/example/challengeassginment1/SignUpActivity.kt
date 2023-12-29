package com.example.challengeassginment1

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.example.challengeassginment1.Info.editInfo
import com.example.challengeassginment1.Info.setInfo

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
    private val userEmail: String = ""
    // 추가한 부분 ***********************************************************

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        // 추가한 부분 ***********************************************************
        type = intent.getStringExtra("type") ?: ""
        if (type != "signup") btnSignup.text = getString(R.string.sign_up_edit)
        // 추가한 부분 ***********************************************************
        initView()
    }

    private fun initView() {
        setOnFocusChangeListener()
        setTextChangedListener()
        setServiceProvider()
        setBtnSignupClickListener()
    }

    // 회원가입 버튼 클릭 이벤트 함수
    private fun setBtnSignupClickListener() {
        btnSignup.setOnClickListener {
            val name = editSignUpName.text.toString()
            // 이메일 입력칸의 내용과 이메일 주소 입력칸의 내용을 중간에 @ 포함해서 합치기
            val email = editSignUpEmailFront.text.toString() + "@" + serviceProvider
            val password = editSignUpPassword.text.toString()

            // 추가한 부분 ***********************************************************
            if (type != "signup") {
                editInfo(userEmail, Info.User(name, email, password))
            } else {
                // 정보 저장
                setInfo(name, email, password)
            }
            // 추가한 부분 ***********************************************************

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
        btnSignup.isEnabled = tvSignUpNameError.text.isEmpty()
                && editSignUpName.text.isNotEmpty()
                && tvSignUpEmailError.text.isEmpty()
                && editSignUpEmailFront.text.isNotEmpty()
                && tvSignUpPasswordError.text.isEmpty()
                && (tvSignUpPasswordCheckError.text.toString() == getString(R.string.sign_up_password_check_ok))
    }

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
                    getString(R.string.sign_up_password_hint)
                } else {
                    changeTextColor(tvSignUpPasswordError, R.color.red)
                    printPasswordErrorText()
                }
            // 비밀번호가 입력되어있고 비밀번호 확인 입력칸이 비어있는 경우에 에러 텍스트 노출
            editSignUpPasswordCheck -> tvSignUpPasswordCheckError.text =
                printPasswordCheckErrorText()
        }
    }

    private fun printNameErrorText(): String = if (editSignUpName.text.toString().isEmpty()) {
        getString(R.string.sign_up_name_error)
    } else {
        ""
    }

    private fun printEmailErrorText(): String {
        val frontText = editSignUpEmailFront.text.toString()
        val backText = editSignUpEmailBack.text.toString()
        return when {
            backText.isNotEmpty() -> {
                if (!backText.contains(".com")) {
                    getString(R.string.sign_up_email_back_format_error)
                } else {
                    serviceProvider = backText
                    ""
                }
            }

            backText.isEmpty() && frontText.isNotEmpty() -> {
                if (editSignUpEmailBack.visibility != View.GONE) {
                    getString(R.string.sign_up_email_back_error)
                } else {
                    ""
                }
            }

            frontText.isEmpty() -> getString(R.string.sign_up_email_error)
            else -> ""
        }
    }

    private fun printPasswordErrorText(): String {
        val text = editSignUpPassword.text.toString()
        val specialCharacterRegex = Regex("[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]+")
        val upperCaseRegex = Regex("[A-Z]")
        val numberRegex = Regex("[0-9]")
        return when {
            // 비밀번호의 길이 체크하고 에러 텍스트 노출
            text.length < 8 || text.length >= 20 -> getString(R.string.sign_up_password_error_length)
            // 비밀번호의 대문자 포함 여부 체크하고 에러 텍스트 노출
            !upperCaseRegex.containsMatchIn(text) -> getString(R.string.sign_up_password_error_upper)
            // 비밀번호의 숫자 포함 여부 체크하고 에러 텍스트 노출
            !numberRegex.containsMatchIn(text) -> getString(R.string.sign_up_password_error_number)
            // 비밀번호의 특수문자 포함 여부 체크하고 에러 텍스트 노출
            !specialCharacterRegex.containsMatchIn(text) -> getString(R.string.sign_up_password_error_special)
            // 통과
            else -> ""
        }
    }

    private fun printPasswordCheckErrorText(): String =
        if (editSignUpPassword.text.toString() != editSignUpPasswordCheck.text.toString()) {
            changeTextColor(tvSignUpPasswordCheckError, R.color.red)
            getString(R.string.sign_up_password_check_error)
        } else {
            changeTextColor(tvSignUpPasswordCheckError, R.color.blue)
            getString(R.string.sign_up_password_check_ok)
        }

    private fun changeTextColor(textView: TextView, color: Int) {
        textView.setTextColor(
            ContextCompat.getColor(
                this@SignUpActivity,
                color
            )
        )
    }
}