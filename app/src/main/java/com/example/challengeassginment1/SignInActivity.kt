package com.example.challengeassginment1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.challengeassginment1.Info.findInfo

class SignInActivity : AppCompatActivity() {
    // 회원가입 액티비티와 테이터를 주고 받기 위한 변수
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        val editId: EditText = findViewById(R.id.edit_signin_id)
        val editPassword: EditText = findViewById(R.id.edit_signin_password)
        val btnLogin: Button = findViewById(R.id.btn_signin_login)
        val btnSignup: Button = findViewById(R.id.btn_signin_signup)

        var name: String = ""

        // registerForActivityResult 변수 세팅
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                name = result.data?.getStringExtra("name") ?: ""
                val email = result.data?.getStringExtra("email") ?: ""
                val password = result.data?.getStringExtra("password") ?: ""

                editId.setText(email)
                editPassword.setText(password)
            }
        }

        // 로그인 버튼 클릭 이벤트
        btnLogin.setOnClickListener {
            // 아이디나 비밀번호를 입력하지 않은 경우
            if (editId.text.isEmpty() || editPassword.text.isEmpty()) {
                Toast.makeText(this, "이메일/비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val email = editId.text.toString()
            val password = editPassword.text.toString()

            // 아이디와 비밀번호 정보가 일치하지 않는 경우
            if (!findInfo(name, email, password)) {
                Toast.makeText(this, "존재하지 않는 이메일 또는 틀린 비밀번호입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
            // Home 액티비티로 이동
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("name", name)
            intent.putExtra("email", email)
            intent.putExtra("password", password)
            startActivity(intent)
        }

        // 회원가입 버튼 클릭 이벤트
        btnSignup.setOnClickListener {
            // startActivity가 아닌 launch를 사용
            // 회원가입 액티비티에서 보내주는값을 받아올 수 있음
            val intent = Intent(this, SignUpActivity::class.java)
            intent.putExtra("type", "signup")
            resultLauncher.launch(intent)
        }
    }

    // 다른 액티비티로 가서 화면이 사라지는 경우
    override fun onStop() {
        super.onStop()

        val editId: EditText = findViewById(R.id.edit_signin_id)
        val editPassword: EditText = findViewById(R.id.edit_signin_password)

        editId.text = null
        editPassword.text = null
    }
}