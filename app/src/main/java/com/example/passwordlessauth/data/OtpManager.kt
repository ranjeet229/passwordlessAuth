package com.example.passwordlessauth.data

import timber.log.Timber
import kotlin.random.Random

data class OtpData(
    val otp: String,
    val expiryTime: Long,
    val attempts: Int
)

object OtpManager {

    private val otpStore = mutableMapOf<String, OtpData>()
    private const val OTP_validity_MS = 60_000L // 60 seconds
    private const val MAX_ATTEMPTS = 3

    fun generateOtp(email: String): String {
        val otp = (100000..999999).random().toString()
        val expiryTime = System.currentTimeMillis() + OTP_validity_MS
        
        // Reset attempts and store new OTP
        otpStore[email] = OtpData(otp, expiryTime, 0)
        
        Timber.d("OTP generated for $email: $otp") 
        // In a real app, this would be sent via SMS/Email. 
        // Here we just log it as per requirements.
        
        return otp
    }

    sealed class ValidationResult {
        object Success : ValidationResult()
        object Incorrect : ValidationResult()
        object Expired : ValidationResult()
        object AttemptsExceeded : ValidationResult()
        object Error : ValidationResult() // Fallback
    }

    fun validateOtp(email: String, inputOtp: String): ValidationResult {
        val data = otpStore[email] ?: return ValidationResult.Error

        if (System.currentTimeMillis() > data.expiryTime) {
            Timber.d("OTP expired for $email")
            return ValidationResult.Expired
        }

        if (data.attempts >= MAX_ATTEMPTS) {
            Timber.d("OTP attempts exceeded for $email")
            return ValidationResult.AttemptsExceeded
        }

        if (data.otp == inputOtp) {
            Timber.d("OTP success for $email")
            otpStore.remove(email) // Clear OTP after success? 
            // Requirements say: "Invalidate any previous OTP for that email" when generating new one.
            // Usually, we should verify and then maybe clear OR keep a session.
            // But let's follow the simple flow. 
            return ValidationResult.Success
        } else {
             // Increment attempts
            val newAttempts = data.attempts + 1
            otpStore[email] = data.copy(attempts = newAttempts)
            Timber.d("OTP failed for $email. Attempt: $newAttempts")
            
            if (newAttempts >= MAX_ATTEMPTS) {
                 return ValidationResult.AttemptsExceeded
            }
            return ValidationResult.Incorrect
        }
    }
    
    fun clearOtp(email: String) {
        otpStore.remove(email)
    }
}
