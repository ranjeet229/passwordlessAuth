package com.example.passwordlessauth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.passwordlessauth.ui.LoginScreen
import com.example.passwordlessauth.ui.OtpScreen
import com.example.passwordlessauth.ui.SessionScreen
import com.example.passwordlessauth.viewmodel.AuthState
import com.example.passwordlessauth.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: AuthViewModel = viewModel()
                    val uiState = viewModel.uiState.collectAsState().value

                    when (uiState) {
                        is AuthState.Login -> {
                            LoginScreen(
                                onSendOtp = { email -> viewModel.sendOtp(email) }
                            )
                        }
                        is AuthState.Otp -> {
                            OtpScreen(
                                email = uiState.email,
                                error = uiState.error,
                                onValidateOtp = { otp -> viewModel.validateOtp(otp) },
                                onResendOtp = { viewModel.resendOtp() },
                                onBack = { viewModel.resetToLogin() }
                            )
                        }
                        is AuthState.Session -> {
                            SessionScreen(
                                email = uiState.email,
                                startTime = uiState.startTime,
                                durationSeconds = uiState.durationSeconds,
                                onLogout = { viewModel.logout() }
                            )
                        }
                    }
                }
            }
        }
    }
}
