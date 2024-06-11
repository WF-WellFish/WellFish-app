package com.example.wellfish.ui.login

import androidx.lifecycle.ViewModel
import com.example.wellfish.data.pref.UserModel
import com.example.wellfish.data.repository.UserRepository

class LoginViewModel (private val repository: UserRepository) : ViewModel() {

    //diisi setalah manggil api dan edit repositry sama kayak registerviewmodel
    fun login(username: String, password: String) = repository.login(username, password)

    // use suspend to make a network call
    suspend fun saveSession(userModel: UserModel) {
        repository.saveSession(userModel)
    }
}