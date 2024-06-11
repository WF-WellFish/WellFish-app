package com.example.wellfish.data.api

import com.example.wellfish.data.response.LoginResponse
import com.example.wellfish.data.response.RegisterResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("username") username: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST ("login")
    suspend fun login (
        @Field("username") username: String,
        @Field("password") password: String
    ): LoginResponse
}