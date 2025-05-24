package com.example.aigiri.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aigiri.model.OtpRequest
import com.example.aigiri.model.User
import com.example.aigiri.repository.OtpRepository
import com.example.aigiri.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch



sealed class SendOtpUiState {
    object Idle : SendOtpUiState()
    object Loading : SendOtpUiState()
    data class Success(val phoneNumber: String, val otp: String) : SendOtpUiState()
    data class Error(val message: String) : SendOtpUiState()
}

class SignupViewModel(
    private val otpRepository: OtpRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SendOtpUiState>(SendOtpUiState.Idle)
    val uiState: StateFlow<SendOtpUiState> = _uiState
    private var tempUser: User? = null
    val usernameTaken = MutableStateFlow(false)
    val emailTaken = MutableStateFlow(false)
    val phoneTaken = MutableStateFlow(false)

    private fun generateOtp(): String = (100000..999999).random().toString()

    fun checkUsername(username: String) {
        viewModelScope.launch {
            userRepository.isUsernameTaken(username).onSuccess {
                usernameTaken.value = it
            }
        }
    }

    fun checkEmail(email: String) {
        if (email.isBlank()) {
            emailTaken.value = false
            return
        }
        viewModelScope.launch {
            userRepository.isEmailTaken(email).onSuccess {
                emailTaken.value = it
            }
        }
    }

    fun checkPhone(phone: String) {
        // Validate format locally
        val isValid =
            phone.startsWith("+") && phone.length == 13 && phone.drop(1).all { it.isDigit() }

        if (!isValid) {
            phoneTaken.value = false // Clear existing state
            return
        }

        // Check availability only if format is valid
        viewModelScope.launch {
            userRepository.isPhoneTaken(phone).onSuccess {
                phoneTaken.value = it
            }
        }
    }

    fun sendOtp(phoneNumber: String) {
        val isValid = phoneNumber.startsWith("+") && phoneNumber.length == 13 && phoneNumber.drop(1)
            .all { it.isDigit() }

        if (!isValid) {
            _uiState.value =
                SendOtpUiState.Error("Enter a valid phone number (e.g., +919876543210)")
            return
        }

        val otp = generateOtp()
        _uiState.value = SendOtpUiState.Loading

        viewModelScope.launch {
            try {
                val result = otpRepository.sendOtp(phoneNumber, otp)
                if (result.isSuccess) {
                    _uiState.value = SendOtpUiState.Success(phoneNumber, otp)
                } else {
                    // get error message from failure exception
                    val errorMessage = result.exceptionOrNull()?.message ?: "Something went wrong"
                    _uiState.value = SendOtpUiState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _uiState.value = SendOtpUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}







