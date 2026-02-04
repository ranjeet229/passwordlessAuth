package com.example.passwordlessauth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passwordlessauth.data.OtpManager
import com.example.passwordlessauth.analytics.TimberLogger
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<AuthState>(AuthState.Login)
    val uiState: StateFlow<AuthState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var sessionStartTime: Long = 0

    fun sendOtp(email: String) {
        if (email.isBlank()) return // Basic validation
        
        OtpManager.generateOtp(email)
        _uiState.value = AuthState.Otp(email = email)
    }

    fun validateOtp(otp: String) {
        val currentState = _uiState.value
        if (currentState !is AuthState.Otp) return

        val result = OtpManager.validateOtp(currentState.email, otp)
        
        when (result) {
            OtpManager.ValidationResult.Success -> {
                startSession(currentState.email)
            }
            OtpManager.ValidationResult.Incorrect -> {
                _uiState.update { 
                    if (it is AuthState.Otp) it.copy(error = "Incorrect OTP") else it 
                }
            }
            OtpManager.ValidationResult.Expired -> {
                _uiState.update { 
                    if (it is AuthState.Otp) it.copy(error = "OTP Expired") else it 
                }
            }
            OtpManager.ValidationResult.AttemptsExceeded -> {
                _uiState.update { 
                    if (it is AuthState.Otp) it.copy(error = "Too many attempts. Request new OTP.") else it 
                }
            }
            OtpManager.ValidationResult.Error -> {
                _uiState.update { 
                    if (it is AuthState.Otp) it.copy(error = "Unknown Error") else it 
                }
            }
        }
    }

    fun resendOtp() {
        val currentState = _uiState.value
        if (currentState !is AuthState.Otp) return
        
        OtpManager.generateOtp(currentState.email)
        _uiState.value = AuthState.Otp(email = currentState.email, error = null) // Reset error
    }
    
    fun resetToLogin() {
        _uiState.value = AuthState.Login
    }

    private fun startSession(email: String) {
        sessionStartTime = System.currentTimeMillis()
        _uiState.value = AuthState.Session(email, sessionStartTime, 0)
        
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val duration = (System.currentTimeMillis() - sessionStartTime) / 1000
                _uiState.update {
                    if (it is AuthState.Session) it.copy(durationSeconds = duration) else it
                }
            }
        }
    }

    fun logout() {
        timerJob?.cancel()
        Timber.d("User logged out")
        val currentState = _uiState.value
        if (currentState is AuthState.Session) {
             OtpManager.clearOtp(currentState.email) // Clean up
        }
        _uiState.value = AuthState.Login
    }
    
    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
