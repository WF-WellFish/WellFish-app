package com.example.wellfish.ui.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wellfish.data.pref.UserModel
import com.example.wellfish.data.response.EditProfileResponse
import com.example.wellfish.data.repository.UserRepository
import com.example.wellfish.ui.utils.ResultState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class EditProfileViewModel(private val repository: UserRepository) : ViewModel() {
    private val _currentUser = MutableLiveData<UserModel>() //nama
    val currentUser: LiveData<UserModel> = _currentUser

    private val _editProfileResponse = MutableLiveData<ResultState<EditProfileResponse>>()
    val editProfileResponse: LiveData<ResultState<EditProfileResponse>> = _editProfileResponse

    init {
        viewModelScope.launch {
            val session = repository.getSession().first()
            _currentUser.value = session
        }
    }

    fun updateProfile(newName: String, profilePicture: MultipartBody.Part?) {
        viewModelScope.launch {
            _editProfileResponse.value = ResultState.Loading
            repository.updateProfile(newName, profilePicture).observeForever { result ->
                _editProfileResponse.value = result

                if (result is ResultState.Success) {
                    _currentUser.value?.let {
                        val updatedProfilePicture = result.data.data?.profilePicture ?: it.profilePicture
                        val updatedUser = it.copy(name = newName, profilePicture = updatedProfilePicture)
                        _currentUser.value = updatedUser
                    } ?: run {
                        _editProfileResponse.value = ResultState.Error("Current user is null")
                    }
                }
            }
        }
    }
}
