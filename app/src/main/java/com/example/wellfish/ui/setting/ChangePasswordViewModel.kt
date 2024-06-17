package com.example.wellfish.ui.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wellfish.data.repository.UserRepository
import com.example.wellfish.data.response.ChangePasswordResponse
import com.example.wellfish.ui.utils.ResultState
import kotlinx.coroutines.launch

class ChangePasswordViewModel(private val repository: UserRepository): ViewModel() {
    private val _changePasswordResult = MutableLiveData<ResultState<ChangePasswordResponse>>()
    val changePasswordResult: LiveData<ResultState<ChangePasswordResponse>> = _changePasswordResult

    fun changePassword(oldPassword: String, newPassword: String, newPasswordConfirmation: String) {
        viewModelScope.launch {
            repository.changePassword(oldPassword, newPassword, newPasswordConfirmation).observeForever {
                _changePasswordResult.value = it
            }
        }
    }

}