package com.example.wellfish.ui.register

import androidx.lifecycle.ViewModel
import com.example.wellfish.data.repository.UserRepository

class RegisterViewModel(private val repository: UserRepository): ViewModel() {

    fun register(name: String, username: String, password: String) =
        repository.signup(name, username, password)
}