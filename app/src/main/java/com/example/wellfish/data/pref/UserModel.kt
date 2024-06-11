package com.example.wellfish.data.pref

data class UserModel(
    val username: String,
    val password: String,
    val token: String,
    val isLogin: Boolean = false
)
