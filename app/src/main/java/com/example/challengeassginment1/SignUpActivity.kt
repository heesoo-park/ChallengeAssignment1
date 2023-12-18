package com.example.challengeassginment1

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.example.challengeassginment1.Info.setInfo

class SignUpActivity : AppCompatActivity() {
    private lateinit var editSignUpName: EditText
    private lateinit var editSignUpEmailFront: EditText
    private lateinit var editSignUpEmailBack: EditText
    private lateinit var spinnerSignUp: Spinner
    private lateinit var editSignUpPassword: EditText
    private lateinit var editSignUpPasswordCheck: EditText
    private lateinit var btnSignup: Button

    private lateinit var tvSignUpNameError: TextView
    private lateinit var tvSignUpEmailError: TextView
    private lateinit var tvSignUpPasswordError: TextView
    private lateinit var tvSignUpPasswordCheckError: TextView

    // 저장될 이메일 주소(xxx.com의 형식)
    private var serviceProvider = ""

    // 이메일 주소를 올바르게 입력했는지
    private var isServiceProviderOk = true

    // 비밀번호를 올바르게 입력했는지
    private var isPasswordOk = false

    // 비밀번호 확인을 올바르게 입력했는지
    private var isPasswordCheckOk = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        initView()

        // Spinner에 넣을 데이터 가져오기
        val spinnerData = resources.getStringArray(R.array.service_provider)
        // 어댑터 설정
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerData)
        // 어댑터 연결
        spinnerSignUp.adapter = adapter

        // 이름 입력칸에서 Focus out할 때 조건 확인 후 에러 텍스트 노출
        setOnFocusChangeListener(editSignUpName, tvSignUpNameError, arrayOf("이름을 입력해주세요"))
        // 이메일 입력칸에서 Focus out할 때 조건 확인 후 에러 텍스트 노출
        setOnFocusChangeListener(editSignUpEmailFront, tvSignUpEmailError, arrayOf("이메일을 입력해주세요"))
        // 이메일 주소 입력칸에서 Focus out할 때 조건 확인 후 에러 텍스트 노출
        setOnFocusChangeListener(
            editSignUpEmailBack,
            tvSignUpEmailError,
            arrayOf("이메일을 입력해주세요", "이메일 주소를 입력해주세요")
        )
        // 비밀번호 입력칸에서 Focus out할 때 조건 확인 후 에러 텍스트 노출
        setOnFocusChangeListener(
            editSignUpPassword,
            tvSignUpPasswordError,
            arrayOf("8자 이상,20자 미만,특수문자,대문자,숫자 포함"),
            color = R.color.grey
        )
        // 비밀번호 확인 입력칸에서 Focus out할 때 조건 확인 후 에러 텍스트 노출
        setOnFocusChangeListener(editSignUpPasswordCheck, tvSignUpPasswordCheckError, arrayOf(""))

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
                    in 0..2 -> {
                        // 서비스 제공자 칸을 숨기기
                        editSignUpEmailBack.visibility = View.GONE
                        // 해당 아이템 내용을 저장
                        serviceProvider = parent?.getItemAtPosition(position).toString()
                        isServiceProviderOk = true
                    }
                    // 직접 입력을 선택한 경우
                    else -> {
                        // 서비스 제공자 칸을 보여주기
                        editSignUpEmailBack.visibility = View.VISIBLE
                        serviceProvider = ""
                        isServiceProviderOk = false
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) = Unit
        }

        // 이름 입력칸 텍스트 변경 리스너
        addTextChangedListener(editSignUpName, tvSignUpNameError, arrayOf("", "이름을 입력해주세요."))
        // 이메일 입력칸 텍스트 변경 리스너
        addTextChangedListener(
            editSignUpEmailFront,
            tvSignUpEmailError,
            arrayOf("", "이메일을 입력해주세요.")
        )
        // 이메일 주소 입력칸 텍스트 변경 리스너
        addTextChangedListener(
            editSignUpEmailBack,
            tvSignUpEmailError,
            arrayOf("", "올바른 형식이 아닙니다.(예: xxx.com)")
        )
        // 비밀번호 입력칸 텍스트 변경 리스너
        addTextChangedListener(
            editSignUpPassword,
            tvSignUpPasswordError,
            arrayOf(
                "",
                "비밀번호는 최소 8자 이상, 최대 20자 미만",
                "1개 이상의 영어 대문자 필요",
                "1개 이상의 숫자 필요",
                "1개 이상의 특수문자(!@#\$%^+-=) 필요",
                "8자 이상,20자 미만,특수문자,대문자,숫자 포함"
            ),
            R.color.grey
        )
        // 비밀번호 확인 입력칸 텍스트 변경 리스너
        addTextChangedListener(
            editSignUpPasswordCheck,
            tvSignUpPasswordCheckError,
            arrayOf("", "비밀번호 불일치", "비밀번호 일치"),
            R.color.blue
        )

        // 회원가입 버튼 클릭 이벤트
        btnSignup.setOnClickListener {
            // 입력해야하는 정보 중 하나라도 입력되지 않은 경우
            if (editSignUpName.text.isEmpty() || editSignUpEmailFront.text.isEmpty() || serviceProvider.isEmpty() || editSignUpPassword.text.isEmpty() || editSignUpPasswordCheck.text.isEmpty()) {
                Toast.makeText(this, "입력되지 않은 정보가 있습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // 이메일 주소 조건을 만족시키지 못한 경우
            if (!isServiceProviderOk) {
                Toast.makeText(this, "이메일 주소 조건을 만족시키지 못했습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // 비밀번호 조건을 만족시키지 못한 경우
            if (!isPasswordOk || !isPasswordCheckOk) {
                Toast.makeText(this, "비밀번호 조건을 만족시키지 못했습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 이메일 입력칸의 내용과 이메일 주소 입력칸의 내용을 중간에 @ 포함해서 합치기
            val email = editSignUpEmailFront.text.toString() + "@" + serviceProvider
            val password = editSignUpPassword.text.toString()

            // 정보 저장
            setInfo(email, password)
            // 로그인 액티비티로 보낼 값 설정
            intent.putExtra("email", email)
            intent.putExtra("password", password)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun initView() {
        editSignUpName = findViewById(R.id.edit_signup_name)
        editSignUpEmailFront = findViewById(R.id.edit_signup_email_front)
        editSignUpEmailBack = findViewById(R.id.edit_signup_email_back)
        spinnerSignUp = findViewById(R.id.spinner_signup)
        editSignUpPassword = findViewById(R.id.edit_signup_password)
        editSignUpPasswordCheck = findViewById(R.id.edit_signup_password_check)
        btnSignup = findViewById(R.id.btn_signup_check)

        tvSignUpNameError = findViewById(R.id.tv_signup_name_error)
        tvSignUpEmailError = findViewById(R.id.tv_signup_email_error)
        tvSignUpPasswordError = findViewById(R.id.tv_signup_password_error)
        tvSignUpPasswordCheckError = findViewById(R.id.tv_signup_password_check_error)
    }

    // 에러 텍스트 노출을 담당하는 함수
    private fun printErrorText(textView: TextView, s: String, color: Int = R.color.red) {
        textView.text = s
        textView.setTextColor(ContextCompat.getColor(this, color))
    }

    // Focus 변화를 감지하는 리스너 함수
    private fun setOnFocusChangeListener(
        editText: EditText,
        textView: TextView,
        s: Array<String>,
        color: Int = R.color.red
    ) {
        editText.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            // Focus out 됐다면
            if (!hasFocus) {
                when (editText) {
                    editSignUpName, editSignUpEmailFront, editSignUpPasswordCheck -> {
                        // 입력칸이 비어있는 경우에 에러 텍스트 노출
                        if (editText.text.isEmpty()) printErrorText(textView, s[0])
                    }

                    editSignUpEmailBack -> {
                        // 이메일 입력칸과 비교한 후에 에러 텍스트 노출
                        if (editText.text.isEmpty() && editSignUpEmailFront.text.isEmpty()) {
                            printErrorText(textView, s[0])
                        } else if (editText.text.isEmpty() && editSignUpEmailFront.text.isNotEmpty()) {
                            printErrorText(textView, s[1])
                        }
                    }

                    editSignUpPassword -> {
                        // 초기 세팅으로 노출
                        if (editText.text.isEmpty()) printErrorText(textView, s[0], color)
                    }
                }
            }
        }
    }

    private fun addTextChangedListener(
        editText: EditText,
        textView: TextView,
        s: Array<String>,
        color: Int = R.color.red
    ) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // 입력 중일 때는 에러 텍스트 숨기기
                if (p0.toString().isNotEmpty()) printErrorText(textView, s[0])
            }

            override fun afterTextChanged(p0: Editable?) {
                when (editText) {
                    editSignUpName, editSignUpEmailFront -> {
                        // 입력칸이 비어있다면 에러 텍스트 노출
                        if (p0.toString().isEmpty()) printErrorText(textView, s[1])
                    }

                    editSignUpEmailBack -> {
                        // 내용에 .com이 포함되어있지 않다면 에러 텍스트 노출
                        if (!p0.toString().contains(".com")) {
                            printErrorText(textView, s[1])
                            isServiceProviderOk = false
                        } else { // 내용에 .com이 포함되어 있다면 에러 텍스트 숨기기
                            printErrorText(textView, s[0])
                            serviceProvider = p0.toString()
                            isServiceProviderOk = true
                        }
                    }

                    editSignUpPassword -> {
                        // 내용이 비어있지 않다면 비밀번호 강도 체크
                        if (p0.toString().isNotEmpty()) {
                            isPasswordOk = when {
                                // 비밀번호의 길이 체크하고 에러 텍스트 노출
                                p0.toString().length < 8 || p0.toString().length >= 20 -> {
                                    printErrorText(textView, s[1])
                                    false
                                }
                                // 비밀번호의 대문자 포함 여부 체크하고 에러 텍스트 노출
                                !p0.toString().contains("[A-Z]".toRegex()) -> {
                                    printErrorText(textView, s[2])
                                    false
                                }
                                // 비밀번호의 숫자 포함 여부 체크하고 에러 텍스트 노출
                                !p0.toString().contains("[0-9]".toRegex()) -> {
                                    printErrorText(textView, s[3])
                                    false
                                }
                                // 비밀번호의 특수문자 포함 여부 체크하고 에러 텍스트 노출
                                !p0.toString().contains("[!@#$%^+\\-=]".toRegex()) -> {
                                    printErrorText(textView, s[4])
                                    false
                                }
                                // 강도 체크 통과했으면 비밀번호 사용가능(통과)
                                else -> {
                                    printErrorText(textView, s[0])
                                    true
                                }
                            }
                        } else {
                            // 내용이 비어있다면 초기 텍스트로 변경
                            printErrorText(textView, s[5], color)
                            isPasswordOk = false
                        }
                    }

                    editSignUpPasswordCheck -> {
                        // 비밀번호 입력칸의 내용이 비어져있지 않다면
                        if (editSignUpPassword.text.toString().isNotEmpty()) {
                            isPasswordCheckOk = when {
                                // 입력한 내용과 비밀번호가 다를 경우 에러 텍스트 노출
                                p0.toString() != editSignUpPassword.text.toString() -> {
                                    printErrorText(textView, s[1])
                                    false
                                }
                                // 입력한 내용과 비밀번호가 같은 경우 성공 텍스트 노출
                                else -> {
                                    printErrorText(textView, s[2], color)
                                    true
                                }
                            }
                        } else {
                            // 비밀번호 입력칸의 내용이 비어있다면 에러 텍스트 숨기기
                            printErrorText(textView, s[0])
                            isPasswordCheckOk = false
                        }
                    }
                }
            }
        })
    }
}