package com.example.accidentdetectionapp.presentation.viewmodel


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.accidentdetectionapp.domain.entity.LoginRequest
import com.example.accidentdetectionapp.domain.entity.SignUpRequest
import com.example.accidentdetectionapp.domain.repository.UserAuthenticationRepository
import com.example.accidentdetectionapp.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val userAuthenticationRepository: UserAuthenticationRepository,
    private val sessionManager: SessionManager // Inject SessionManager
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess = _loginSuccess.asStateFlow()

    private val _signupStatus = MutableStateFlow(SignupStatus.Idle)
    val signupStatus = _signupStatus.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun setError(message: String) {
        _error.value = message
    }

    fun clearError() {
        _error.value = null
    }

    fun login(loginRequest: LoginRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = userAuthenticationRepository.login(loginRequest)
                result.onSuccess { (user, token) ->
                    sessionManager.saveAuthToken(token)
                    sessionManager.saveUserDetails(user)
                    _loginSuccess.value = true // Update success state
                    _isLoading.value = false
                }.onFailure { exception ->
                    _error.value = exception.message ?: "Unknown error"
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }
    fun resetLoginSuccess() {
        _loginSuccess.value = false
    }

    fun signup(signupRequest: SignUpRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            _signupStatus.value = SignupStatus.Loading

            val result = userAuthenticationRepository.register(signupRequest)
            result.onSuccess {
                Log.d("AuthenticationViewModel", "Sign up successful.")
                _signupStatus.value = SignupStatus.Success
            }.onFailure { exception ->
                val errorMessage = exception.message ?: "Unknown error"
                Log.e("AuthenticationViewModel", "Error during signup: $errorMessage")
                _error.value = errorMessage
                _signupStatus.value = SignupStatus.Error
            }.also {
                _isLoading.value = false
            }
        }
    }
}

enum class SignupStatus {
    Idle, Loading, Success, Error
}
