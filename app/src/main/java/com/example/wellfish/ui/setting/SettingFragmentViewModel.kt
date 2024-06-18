package com.example.wellfish.ui.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wellfish.data.repository.UserRepository
import com.example.wellfish.data.response.LogoutResponse
import com.example.wellfish.ui.utils.ResultState
import kotlinx.coroutines.launch

class SettingFragmentViewModel(private val repository: UserRepository) : ViewModel() {

    private val _logoutResult = MutableLiveData<ResultState<LogoutResponse>>()
    val logoutResult: LiveData<ResultState<LogoutResponse>>
        get() = _logoutResult

    fun logout() {
        viewModelScope.launch {
            val result = repository.logout()
            result.observeForever { logoutResult ->
                _logoutResult.value = logoutResult
            }
        }
    }
}