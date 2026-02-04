package com.example.passwordlessauth.viewmodel

sealed interface AuthState {
    object Login : AuthState
    
    data class Otp(
        val email: String,
        val error: String? = null,
        val isLoading: Boolean = false
    ) : AuthState

    data class Session(
        val email: String,
        val startTime: Long,
        val durationSeconds: Long
    ) : AuthState
}
