package com.example.aigiri.viewmodel


import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aigiri.repository.OtpRepository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


sealed class SendOtpUiState {
    object Idle : SendOtpUiState()
    object Loading : SendOtpUiState()
    data class Success(val phoneNumber: String, val otp: String) : SendOtpUiState()
    data class Error(val message: String) : SendOtpUiState()
}


class SendOtpViewModel  constructor(
    private val otpRepository: OtpRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SendOtpUiState>(SendOtpUiState.Idle)
    val uiState: StateFlow<SendOtpUiState> = _uiState

    private fun generateOtp(): String = (100000..999999).random().toString()

    fun sendOtp(phoneNumber: String) {
        if (phoneNumber.length != 13) {
            _uiState.value = SendOtpUiState.Error("Enter a valid phone number (e.g., +918320200160)")
            return
        }

        val otp = generateOtp()
        _uiState.value = SendOtpUiState.Loading

        viewModelScope.launch {
            val result = otpRepository.sendOtp(phoneNumber, otp)
            if (result.isSuccess) {
                _uiState.value = SendOtpUiState.Success(phoneNumber, otp)
            } else {
                _uiState.value = SendOtpUiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

}


