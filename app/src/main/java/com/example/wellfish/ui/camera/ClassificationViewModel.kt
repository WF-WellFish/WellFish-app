package com.example.wellfish.ui.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wellfish.data.repository.UserRepository
import com.example.wellfish.data.response.ClassificationFishResponse
import com.example.wellfish.ui.utils.ResultState
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class ClassificationViewModel(private val repository: UserRepository) : ViewModel() {

    private val _classificationResult = MutableLiveData<ResultState<ClassificationFishResponse>>()
    val classificationResult: LiveData<ResultState<ClassificationFishResponse>> get() = _classificationResult

    fun classifyFish(image: MultipartBody.Part) {
        viewModelScope.launch {
            _classificationResult.value = ResultState.Loading
            val result = repository.classifyFish(image)
            result.observeForever { state ->
                _classificationResult.value = state
            }
        }
    }

}
