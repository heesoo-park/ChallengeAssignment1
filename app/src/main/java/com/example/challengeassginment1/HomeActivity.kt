package com.example.challengeassginment1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.challengeassginment1.signup.SignUpActivity
import com.example.challengeassginment1.signup.SignUpEntryType
import com.example.challengeassginment1.signup.SignUpUserEntity

class HomeActivity : AppCompatActivity() {
    // 회원가입 액티비티와 테이터를 주고 받기 위한 변수
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    private val tvHomeName: TextView by lazy { findViewById(R.id.tv_home_name) }
    private val tvHomeId: TextView by lazy { findViewById(R.id.tv_home_id) }
    private lateinit var name: String
    private lateinit var email: String
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        name = intent.getStringExtra("name").toString()
        email = intent.getStringExtra("email").toString()
        password = intent.getStringExtra("password").toString()

        tvHomeName.text = name
        tvHomeId.text = email

        // registerForActivityResult 변수 세팅
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    name = result.data?.getStringExtra("name") ?: ""
                    email = result.data?.getStringExtra("email") ?: ""

                    tvHomeName.text = name
                    tvHomeId.text = email
                }
            }

        // 추가한 부분 ***********************************************************
        val userEdit: Button = findViewById(R.id.btn_home_user_edit)
        userEdit.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            intent.putExtra("email", email)
            // resultLauncher.launch(intent)

            resultLauncher.launch(
                SignUpActivity.newIntent(
                    context = this@HomeActivity,
                    entryType = SignUpEntryType.UPDATE,
                    userEntity = SignUpUserEntity(
                        name = name,
                        email = email,
                        password = password
                    )
                )
            )
        }
        // 추가한 부분 ***********************************************************
    }
}