package com.example.wellfish.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.wellfish.data.api.ApiService
import com.example.wellfish.data.pref.UserModel
import com.example.wellfish.data.pref.UserPreference
import com.example.wellfish.data.response.ClassificationFishErrorResponse
import com.example.wellfish.data.response.ClassificationFishResponse
import com.example.wellfish.data.response.EditProfileErrorResponse
import com.example.wellfish.data.response.EditProfileResponse
import com.example.wellfish.data.response.ErrorResponse
import com.example.wellfish.data.response.LoginResponse
//import com.example.wellfish.data.response.LogoutResponse
import com.example.wellfish.data.response.RegisterResponse
import com.example.wellfish.ui.utils.ResultState
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.IOException

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
){

    fun signup(name: String, username: String, password: String): LiveData<ResultState<RegisterResponse>> = liveData {
        emit(ResultState.Loading)
        try {
            val response = apiService.register(name, username, password)
            if (response.status == "success") {
                emit(ResultState.Success(response))
            } else {
                val detailedErrors = response.data?.errors
                val errorMessage = detailedErrors?.username?.joinToString(", ") ?: response.message ?: "Unknown error"
                emit(ResultState.Error(errorMessage))
            }
        } catch (e: HttpException) {
            val error = e.response()?.errorBody()?.string()
            if (error != null) {
                try {
                    val errorResponse = Gson().fromJson(error, RegisterResponse::class.java)
                    val detailedErrors = errorResponse.data?.errors
                    val errorMessage = detailedErrors?.username?.joinToString(", ") ?: errorResponse.message ?: "Unknown error"
                    emit(ResultState.Error(errorMessage))
                } catch (e: Exception) {
                    emit(ResultState.Error("Error parsing error response"))
                }
            } else {
                emit(ResultState.Error("Unknown error"))
            }
        } catch (e: IOException) {
            emit(ResultState.Error("Network error, please try again"))
        } catch (e: Exception) {
            emit(ResultState.Error("Unknown error"))
        }
    }

    fun login(username: String, password:String): LiveData<ResultState<LoginResponse>> = liveData {
        emit(ResultState.Loading)
        try {
            val response = apiService.login(username, password)
            emit(ResultState.Success(response))
        } catch (e: HttpException) {
            val error = e.response()?.errorBody()?.string()
            if (error != null) {
                try {
                    val errorResponse = Gson().fromJson(error, ErrorResponse::class.java)
                    emit(ResultState.Error(errorResponse.message))
                } catch (e: Exception) {
                    emit(ResultState.Error("Error Parsing error response"))
                }
            } else {
                emit(ResultState.Error("Unknown error"))
            }
        }
    }

    //new3/3
    /*
    fun logout(): LiveData<ResultState<LogoutResponse>> = liveData {
        emit(ResultState.Loading)
        val session = userPreference.getSession().first()
        val token = session.token

        try {
            val response = apiService.logout("Bearer $token")
            if (response.status == "success") {
                userPreference.logout()
                emit(ResultState.Success(response))
            } else {
                if (response.message == "Unauthenticated.") {
                    userPreference.logout()
                }
                emit(ResultState.Error(response.message ?: "Unknown error"))
            }
        } catch (e: HttpException) {
            val error = e.response()?.errorBody()?.string()
            if (error != null) {
                try {
                    val errorResponse = Gson().fromJson(error, ErrorResponse::class.java)
                    emit(ResultState.Error(errorResponse.message))
                } catch (e: Exception) {
                    emit(ResultState.Error("Error parsing error response"))
                }
            } else {
                emit(ResultState.Error("Unknown error"))
            }
        } catch (e: IOException) {
            emit(ResultState.Error("Network error, please try again"))
        } catch (e: Exception) {
            emit(ResultState.Error("Unknown error"))
        }
    }
    */

    fun classifyFish(image: MultipartBody.Part): LiveData<ResultState<ClassificationFishResponse>> = liveData {
        emit(ResultState.Loading)
        try {
            //val token = userPreference.getSession().first().token
            //val response = apiService.classifyFish("Bearer $token", image)
            val response = apiService.classifyFish( image) //baru
            emit(ResultState.Success(response))
        } catch (e: HttpException) {
            val error = e.response()?.errorBody()?.string()
            if (error != null) {
                try {
                    val errorResponse = Gson().fromJson(error, ClassificationFishErrorResponse::class.java)
                    val errorMessage = errorResponse.data?.errors?.image?.joinToString(", ") ?: errorResponse.message ?: "Unknown error"
                    emit(ResultState.Error(errorMessage))
                } catch (e: Exception) {
                    emit(ResultState.Error("Error parsing error response"))
                }
            } else {
                emit(ResultState.Error("Unknown error"))
            }
        } catch (e: IOException) {
            emit(ResultState.Error("Network error, please try again"))
        } catch (e: Exception) {
            emit(ResultState.Error("Unknown error"))
        }
    }

    fun updateProfile(name: String, profilePicture: MultipartBody.Part?): LiveData<ResultState<EditProfileResponse>> = liveData {
        emit(ResultState.Loading)
        try {
            //val token = "Bearer ${userPreference.getSession().first().token}"
            val nameRequestBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
            val methodRequestBody = "PUT".toRequestBody("text/plain".toMediaTypeOrNull())

            Log.d("UserRepository", "Updating profile with name: $name and profilePicture: $profilePicture")

            //val response = apiService.updateProfile(token, nameRequestBody, methodRequestBody, profilePicture)
            val response = apiService.updateProfile(nameRequestBody, methodRequestBody, profilePicture)

            if (response.isSuccessful) {
                response.body()?.let {
                    emit(ResultState.Success(it))
                    Log.d("UserRepository", "Profile update successful: $it")
                } ?: emit(ResultState.Error("Unknown error: response body is null"))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, EditProfileErrorResponse::class.java)
                emit(ResultState.Error(errorResponse.message ?: "Unknown error"))
            }
        } catch (e: HttpException) {
            val error = e.response()?.errorBody()?.string()
            if (error != null) {
                try {
                    val errorResponse = Gson().fromJson(error, EditProfileErrorResponse::class.java)
                    emit(ResultState.Error(errorResponse.message ?: "Unknown error"))
                } catch (e: Exception) {
                    emit(ResultState.Error("Error parsing error response"))
                }
            } else {
                emit(ResultState.Error("Unknown error"))
            }
        } catch (e: IOException) {
            emit(ResultState.Error("Network error, please try again"))
        } catch (e: Exception) {
            emit(ResultState.Error("Unknown error"))
        }
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    //new done
    suspend fun logout(){
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun clearInstance() {
            instance = null
        }

        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }

}