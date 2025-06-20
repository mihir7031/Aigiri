package com.example.aigiri.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aigiri.TokenManager
import com.example.aigiri.model.LoginRequest
import com.example.aigiri.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val userId: String) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel(
    private val tokenManager: TokenManager,
    context: Context
) : ViewModel() {

    private val repository = AuthRepository(context)

    private val _loginState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val loginState: StateFlow<LoginUiState> = _loginState

    fun login(identifier: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginUiState.Loading
            try {
                val response = repository.login(LoginRequest(identifier, password))
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        tokenManager.saveTokenAndUserId(body.token, body.userId)
                        _loginState.value = LoginUiState.Success(body.userId)
                    } else {
                        _loginState.value = LoginUiState.Error("Empty response")
                    }
                } else {
                    val error = response.errorBody()?.string() ?: response.message()
                    _loginState.value = LoginUiState.Error("Login failed: $error")
                }
            } catch (e: Exception) {
                _loginState.value = LoginUiState.Error("Error: ${e.localizedMessage}")
            }
        }
    }


    fun resetState() {
        _loginState.value = LoginUiState.Idle
    }
}
