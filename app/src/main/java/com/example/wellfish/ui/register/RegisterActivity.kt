package com.example.wellfish.ui.register

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.wellfish.data.helper.ViewModelFactory
import com.example.wellfish.databinding.ActivityRegisterBinding
import com.example.wellfish.ui.custom.InputPassword
import com.example.wellfish.ui.login.LoginActivity
import com.example.wellfish.ui.utils.ResultState

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var inputPassword: InputPassword

    private val viewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()

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

    @SuppressLint("SuspiciousIndentation")
    private fun setupAction() {
        binding.signupButtonRegister.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val username = binding.UsernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            viewModel.register(name, username, password).observe(this) { result ->
                when (result) {
                    is ResultState.Success -> {
                        binding.pbLoading.visibility = View.INVISIBLE
                        Log.d("RegisterActivity", "Registration successful")
                        showSuccessDialog(username)
                    }

                    is ResultState.Error -> {
                        binding.pbLoading.visibility = View.INVISIBLE
                        Log.e("RegisterActivity", "Registration error: ${result.error}")
                        Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                    }
                    is ResultState.Loading -> {
                        binding.pbLoading.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun showSuccessDialog(username: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Yeay!")
            setMessage("Your account with username $username is now ready.")
            setPositiveButton("Next") { _, _ ->
                navigateToLoginActivity()
            }
            create()
            show()
        }
    }

    private fun navigateToLoginActivity() {
        Log.d("RegisterActivity", "Navigating to LoginActivity")
        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
