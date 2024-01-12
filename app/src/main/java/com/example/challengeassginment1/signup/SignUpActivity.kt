package com.example.challengeassginment1.signup

import android.R
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.example.challengeassginment1.databinding.ActivitySignupBinding

class SignUpActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ENTRY_TYPE = "extra_entry_type"
        const val EXTRA_USER_ENTITY = "extra_user_entity"

        // 인텐트를 받는 함수
        // SignUpActivity를 실행시킬 때에 대한 스펙(인터페이스)
        // 한정된 정보를 받기 위한 인터페이스
        fun newIntent(
            context: Context,
            entryType: SignUpEntryType,
            userEntity: SignUpUserEntity
        ): Intent = Intent(
            context,
            SignUpActivity::class.java
        ).apply {
            // enum class 프로퍼티에 ordinal을 붙인다는 건 해당 프로퍼티의 인덱스(Int)를 가져온다는 것
            putExtra(EXTRA_ENTRY_TYPE, entryType.ordinal)
            putExtra(EXTRA_USER_ENTITY, userEntity)
        }
    }

    // 뷰바인딩 변수
    private val binding by lazy {
        ActivitySignupBinding.inflate(layoutInflater)
    }

    // EditText들을 모아두는 리스트
    private val editTexts
        get() = listOf(
            binding.editSignupName,
            binding.editSignupEmailFront,
            binding.editSignupEmailBack,
            binding.editSignupPassword,
            binding.editSignupPasswordCheck
        )

    // 인텐트로 넘어온 엔트리 타입을 저장하는 변수
    private val entryType: SignUpEntryType by lazy {
        SignUpEntryType.getEntryType(
            intent?.getIntExtra(
                EXTRA_ENTRY_TYPE,
                SignUpEntryType.CREATE.ordinal
            )
        )
    }

    // 인텐트로 넘어온 유저 정보를 저장하는 변수
    private val userEntity: SignUpUserEntity? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(EXTRA_USER_ENTITY, SignUpUserEntity::class.java)
        } else {
            intent?.getParcelableExtra(EXTRA_USER_ENTITY)
        }
    }

    // 뷰모델 생성
    private val viewModel by lazy {
        ViewModelProvider(this@SignUpActivity, SignUpViewModelFactory(this, entryType, userEntity))[SignUpViewModel::class.java]
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initView()
    }

    // 초기 뷰 세팅
    private fun initView() {
        initViewModel()
        setOnFocusChangeListener()
        setTextChangedListener()
        setServiceProvider()
        setBtnSignupClickListener()
        setViewsText()
    }

    // 초기 뷰모델 세팅
    private fun initViewModel() = with(viewModel) {
        userInfo.observe(this@SignUpActivity) {
            // 이전 액티비티로 보낼 값 설정
            val newIntent = Intent()
            newIntent.putExtra("info", it)
            setResult(RESULT_OK, newIntent)
            finish()
        }
        name.observe(this@SignUpActivity) {
            binding.editSignupName.setText(it)
        }
        email.observe(this@SignUpActivity) {
            binding.editSignupEmailFront.setText(it)
        }
        emailProvider.observe(this@SignUpActivity) {
            binding.editSignupEmailBack.setText(it)
        }
        password.observe(this@SignUpActivity) {
            binding.editSignupPassword.setText(it)
        }
        passwordCheck.observe(this@SignUpActivity) {
            binding.editSignupPasswordCheck.setText(it)
        }
        nameErrorMsg.observe(this@SignUpActivity) {
            binding.tvSignupNameError.text = getString(it)
        }
        emailErrorMsg.observe(this@SignUpActivity) {
            binding.tvSignupEmailError.text = getString(it)
        }
        passwordErrorMsg.observe(this@SignUpActivity) {
            binding.tvSignupPasswordError.text = getString(it)
        }
        passwordCheckErrorMsg.observe(this@SignUpActivity) {
            binding.tvSignupPasswordCheckError.text = getString(it)
        }
        signUpText.observe(this@SignUpActivity) {
            binding.btnSignupCheck.text = getString(it)
        }
        emailProviderVisible.observe(this@SignUpActivity) {
            binding.editSignupEmailBack.isVisible = it
        }
        spinnerIdx.observe(this@SignUpActivity) {
            binding.spinnerSignup.setSelection(it)
        }
        signupButtonEnable.observe(this@SignUpActivity) {
            binding.btnSignupCheck.isEnabled = it
        }
        passwordTextColor.observe(this@SignUpActivity) {
            binding.tvSignupPasswordError.setTextColor(
                ContextCompat.getColor(
                    this@SignUpActivity,
                    it
                )
            )
        }
        passwordCheckTextColor.observe(this@SignUpActivity) {
            binding.tvSignupPasswordCheckError.setTextColor(
                ContextCompat.getColor(
                    this@SignUpActivity,
                    it
                )
            )
        }
    }

    // 기본 EditText와 TextView 텍스트 세팅
    private fun setViewsText() {
        viewModel.setViewsText()
    }

    // 회원가입 버튼 클릭 이벤트 함수
    private fun setBtnSignupClickListener() {
        with(binding.btnSignupCheck) {
            setOnClickListener {
                // 엔트리 타입에 따라 버튼 클릭 시 수행하는 함수 변경
                viewModel.onClickSignUp(
                    binding.editSignupName.text.toString(),
                    binding.editSignupEmailFront.text.toString() + "@" + binding.editSignupEmailBack.text.toString(),
                    binding.editSignupPassword.text.toString()
                )
            }
        }
    }

    // Focus 변화를 감지하는 리스너 함수
    private fun setOnFocusChangeListener() {
        editTexts.forEach { et ->
            et.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus.not()) {
                    et.setErrorText()
                }
            }
        }
    }

    // Text 변화를 감지하는 리스너 함수
    private fun setTextChangedListener() {
        editTexts.forEach { et ->
            et.addTextChangedListener {
                et.setErrorText()
            }
        }
    }

    // 스피너 세팅 & 선택 리스너 함수
    private fun setServiceProvider() {
        // 스피너 어댑터 변수
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, viewModel.serviceProviderStringList)
        // 어댑터 연결
        binding.spinnerSignup.adapter = adapter

        // Spinner 아이템 선택 리스너
        binding.spinnerSignup.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            // 아이템을 선택하는 경우 호출
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.checkSpinnerIdx(position)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) = Unit
        }
    }

    // EditText의 에러 텍스트를 담당하는 확장함수
    private fun EditText.setErrorText() {
        when (this) {
            // 입력칸이 비어있는 경우에 에러 텍스트 노출
            binding.editSignupName -> printNameErrorText()
            // 이메일 입력칸과 비교한 후에 에러 텍스트 노출
            binding.editSignupEmailFront, binding.editSignupEmailBack -> printEmailErrorText()
            // 초기 세팅으로 노출
            binding.editSignupPassword -> printPasswordErrorText()
            // 비밀번호가 입력되어있고 비밀번호 확인 입력칸이 비어있는 경우에 에러 텍스트 노출
            binding.editSignupPasswordCheck -> printPasswordCheckErrorText()
        }

        // 회원가입(회원수정) 버튼의 사용가능 판별 함수
        viewModel.isButtonEnable()
    }

    // 이름 에러 텍스트 출력 함수
    private fun printNameErrorText() {
        viewModel.checkValidName(binding.editSignupName.text.toString())
    }

    // 이메일 에러 텍스트 출력 함수
    private fun printEmailErrorText() {
        viewModel.checkValidEmail(
            binding.editSignupEmailFront.text.toString(),
            binding.editSignupEmailBack.text.toString()
        )
    }

    // 비밀번호 에러 텍스트 출력 함수
    private fun printPasswordErrorText() {
        viewModel.checkValidPassword(binding.editSignupPassword.text.toString())
    }

    // 비밀번호 확인 에러 텍스트 출력 함수
    private fun printPasswordCheckErrorText() {
        viewModel.checkValidPasswordCheck(
            binding.editSignupPassword.text.toString(),
            binding.editSignupPasswordCheck.text.toString()
        )
    }
}