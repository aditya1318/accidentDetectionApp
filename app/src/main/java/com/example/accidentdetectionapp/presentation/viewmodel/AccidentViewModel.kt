package com.example.accidentdetectionapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.accidentdetectionapp.data.model.accidentResponse.accidentResponseDto
import com.example.accidentdetectionapp.domain.entity.CreateAccidentRequest
import com.example.accidentdetectionapp.domain.repository.AccidentRepository
import com.example.accidentdetectionapp.domain.repository.ImageUploadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class AccidentViewModel @Inject constructor(
    private val accidentRepository: AccidentRepository,
    private val imageUploadRepository: ImageUploadRepository
) : ViewModel() {

    private val _accidentState = MutableStateFlow<AccidentState>(AccidentState.Idle)
    val accidentState: StateFlow<AccidentState> = _accidentState

    fun createAccident(createAccidentRequest: CreateAccidentRequest) {
        viewModelScope.launch {
            _accidentState.value = AccidentState.Loading
            try {
                val imageUrl = try {
                    imageUploadRepository.uploadImage(createAccidentRequest.photo)
                } catch (e: Exception) {
                    Log.e("AccidentViewModel", "Image upload failed: ${e.message}")
                    "" // Return an empty string to indicate failure
                }

                if (imageUrl.isNotEmpty()) {
                    try {
                        Log.d("AccidentViewModel", "Image Data: ${imageUrl}")
                        val updatedRequest = createAccidentRequest.copy(photo = imageUrl)
                        val response = accidentRepository.createAccident(updatedRequest)
                        handleResponse(response)
                    }catch(e :Exception){
                        Log.e("AccidentViewModel", "Error : ${e.message}")

                    }
                } else {
                    _accidentState.value = AccidentState.Error("Failed to upload image")
                }
            } catch (e: Exception) {
                Log.e("AccidentViewModel", "Error in createAccident: ${e.message}")
                _accidentState.value = AccidentState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }



    private fun handleResponse(response: Response<accidentResponseDto>) {
        if (response.isSuccessful && response.body() != null) {
            _accidentState.value = AccidentState.Success(response.body()!!)
        } else {
            _accidentState.value = AccidentState.Error("Error creating accident")
        }
    }

    sealed class AccidentState {
        object Idle : AccidentState()
        object Loading : AccidentState()
        data class Success(val accidentResponseDto: accidentResponseDto) : AccidentState()
        data class Error(val message: String) : AccidentState()
    }
}
