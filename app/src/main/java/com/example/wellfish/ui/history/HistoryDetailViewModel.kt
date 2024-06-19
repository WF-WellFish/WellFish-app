package com.example.wellfish.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.wellfish.data.repository.UserRepository
import com.example.wellfish.data.response.ClassificationHistoryDetailResponse
import com.example.wellfish.ui.utils.ResultState

class HistoryDetailViewModel(private val repository: UserRepository) : ViewModel() {
    fun getClassificationHistoryDetail(id: String): LiveData<ResultState<ClassificationHistoryDetailResponse>> {
        return repository.getClassificationHistoryDetail(id)
    }
}