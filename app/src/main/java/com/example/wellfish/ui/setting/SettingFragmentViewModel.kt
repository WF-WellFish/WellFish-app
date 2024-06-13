package com.example.wellfish.ui.setting

import androidx.lifecycle.ViewModel
import com.example.wellfish.data.repository.UserRepository

class SettingFragmentViewModel(private val repository: UserRepository) : ViewModel() {

    suspend fun logout(){
        repository.logout()
    }

}