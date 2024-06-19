package com.example.wellfish.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.wellfish.data.repository.UserRepository
import com.example.wellfish.data.response.ClassificationHistoryResponse
import com.example.wellfish.ui.utils.ResultState

class HistoryViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun getClassificationHistory(): LiveData<ResultState<ClassificationHistoryResponse>> {
        return userRepository.getClassificationHistory()
    }

}