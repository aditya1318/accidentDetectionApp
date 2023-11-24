package com.example.accidentdetectionapp.presentation.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.accidentdetectionapp.domain.entity.AnalysisResult
import com.example.accidentdetectionapp.domain.repository.ITensorFlowRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TensorFlowViewModel @Inject constructor(
    private val tensorFlowRepository: ITensorFlowRepository
) : ViewModel() {

    private val _analysisResult = MutableStateFlow<AnalysisResult?>(null)
    val analysisResult = _analysisResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun analyzeImage(image: Bitmap) {
        Log.d("TensorFlowViewModel", "Analyzing image")

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Resize the image to the required dimensions (e.g., 224x224)
                val resizedImage = Bitmap.createScaledBitmap(image, 224, 224, true)

                // Now pass the resized image to the model
                val result = tensorFlowRepository.analyzeImage(resizedImage)
                _analysisResult.value = result
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}

