package com.example.aigiri.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aigiri.repository.OtpRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class OtpUiState(
    val otpInput: List<String> = List(6) { "" },
    val timeLeft: Int = 60,
    val error: String = "",
    val verificationId: String = ""
)

class VerifyOtpViewModel(
    private val otpRepository: OtpRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OtpUiState())
    val uiState: StateFlow<OtpUiState> = _uiState.asStateFlow()

    init {
        startTimer()
    }

    fun updateOtp(index: Int, value: String) {
        val newOtp = _uiState.value.otpInput.toMutableList()
        newOtp[index] = value
        _uiState.update { it.copy(otpInput = newOtp) }
    }

    fun setVerificationId(id: String) {
        _uiState.update { it.copy(verificationId = id) }
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (_uiState.value.timeLeft > 0) {
                delay(1000)
                _uiState.update { it.copy(timeLeft = it.timeLeft - 1) }
            }
        }
    }

    fun verifyOtp(navToNext: () -> Unit) {
        val enteredOtp = _uiState.value.otpInput.joinToString("")
        if (enteredOtp == _uiState.value.verificationId) {
            navToNext()
        } else {
            _uiState.update { it.copy(error = "Incorrect OTP") }
        }
    }

    fun resendOtp(phoneNumber: String, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            val newOtp = (100000..999999).random().toString()
            try {
                val response = otpRepository.sendOtp(phoneNumber, newOtp)
                if (response.isSuccess) {
                    _uiState.update {
                        it.copy(
                            timeLeft = 60,
                            error = "",
                            otpInput = List(6) { "" },
                            verificationId = newOtp
                        )
                    }
                    startTimer()
                    onSuccess(newOtp)
                } else {
                    _uiState.update { it.copy(error = "Failed to resend OTP") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Exception: ${e.localizedMessage}") }
            }
        }
    }
}
