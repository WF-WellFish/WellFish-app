package com.example.wellfish.data.pref

data class UserModel(
    val id: String,
    val name: String,
    val username: String,
    val password: String,
    val profilePicture: String,
    val token: String,
    val isLogin: Boolean = false
)
