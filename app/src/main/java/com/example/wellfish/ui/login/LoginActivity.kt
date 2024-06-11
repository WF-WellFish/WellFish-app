package com.example.wellfish.ui.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.wellfish.data.helper.ViewModelFactory
import com.example.wellfish.data.pref.UserModel
import com.example.wellfish.databinding.ActivityLoginBinding
import com.example.wellfish.ui.custom.InputPassword
import com.example.wellfish.ui.custom.InputUsername
import com.example.wellfish.ui.main.MainActivity
import com.example.wellfish.ui.utils.ResultState
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var inputUsername: InputUsername
    private lateinit var inputPassword: InputPassword

    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()

        inputUsername = binding.usernameEditText
        inputPassword = binding.passwordEditText
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.apply {
                show(WindowInsets.Type.statusBars())
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.loginButtonLogin.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            viewModel.login(username, password).observe(this) { result ->
                when(result) {
                    is ResultState.Success -> {
                        binding.pbLoading.visibility = View.INVISIBLE
                        val user = result.data.data?.user
                        val token = result.data.data?.token
                        if (user != null && token != null) {
                            Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                            saveSession(
                                UserModel(
                                    user.name!!,
                                    user.id.toString(),
                                    token,
                                    true
                                )
                            )
                        } else {
                            Toast.makeText(this, "Unexpected response format", Toast.LENGTH_SHORT).show()
                        }
                    }
                    is ResultState.Loading -> {
                        binding.pbLoading.visibility = View.VISIBLE
                    }
                    is ResultState.Error -> {
                        binding.pbLoading.visibility = View.INVISIBLE
                        val errorMessage = result.error
                        if (errorMessage.contains("invalid_credentials")) {
                            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Login failed: $errorMessage", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }


    private fun saveSession(session: UserModel) {
        lifecycleScope.launch {
            viewModel.saveSession(session)
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            ViewModelFactory.clearInstance()
            startActivity(intent)
        }
    }

}