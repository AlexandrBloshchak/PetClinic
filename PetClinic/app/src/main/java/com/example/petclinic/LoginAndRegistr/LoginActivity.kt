package com.example.petclinic.LoginAndRegistr

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.petclinic.databinding.ActivityLoginBinding
import com.example.petclinic.util.HomeActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var b: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.btnDoLogin.setOnClickListener {
            val login = b.etLogin.text.toString().trim()
            val pass = b.etPassword.text.toString().trim()

            val sp = getSharedPreferences("auth", MODE_PRIVATE)
            val savedLogin = sp.getString("login", null)
            val savedPass = sp.getString("pass", null)

            if (login.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Заполни логин и пароль", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (login == savedLogin && pass == savedPass) {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
