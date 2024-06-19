package com.example.wellfish.data.api

import com.example.wellfish.data.response.ChangePasswordResponse
import com.example.wellfish.data.response.ClassificationFishResponse
import com.example.wellfish.data.response.ClassificationHistoryDetailResponse
import com.example.wellfish.data.response.ClassificationHistoryResponse
import com.example.wellfish.data.response.EditProfileResponse
import com.example.wellfish.data.response.LoginResponse
import com.example.wellfish.data.response.LogoutResponse
import com.example.wellfish.data.response.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

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

    //new2/3
    @GET("logout")
    suspend fun logout() : LogoutResponse

    //new 1/
    @Multipart
    @POST("classification")
    suspend fun classifyFish(
        //@Header("Authorization") token: String, //token di ApiConfig
        @Part image: MultipartBody.Part
    ): ClassificationFishResponse

    @Multipart
    @POST("profile")
    suspend fun updateProfile(
        //@Header("Authorization") token: String, //token di ApiConfig
        @Part("name") name: RequestBody,
        @Part("_method") method: RequestBody,
        @Part profilePicture: MultipartBody.Part?
    ): Response<EditProfileResponse>

    @FormUrlEncoded
    @POST("change-password")
    suspend fun changePassword(
        @Field("old_password") oldPassword: String,
        @Field("new_password") newPassword: String,
        @Field("new_password_confirmation") newPasswordConfirmation: String
    ): ChangePasswordResponse

    @GET("classification-history")
    suspend fun classificationHistory() : ClassificationHistoryResponse

    @GET("classification-history/{id}")
    suspend fun classificationHistoryDetail(
        @Path("id") id: String
    ) : ClassificationHistoryDetailResponse
}