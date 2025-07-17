package com.example.aigiri.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aigiri.model.LiveSession
import com.example.aigiri.repository.LiveSessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LiveViewModel(
    private val repository: LiveSessionRepository
) : ViewModel() {

    private val _liveSession = MutableStateFlow<LiveSession?>(null)
    val liveSession: StateFlow<LiveSession?> = _liveSession

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun onLiveButtonClick() {
        viewModelScope.launch {
            try {
                val session = repository.startLiveSession()
                if (session != null) {
                    _liveSession.value = session
                } else {
                    _error.value = "Missing token or user ID"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            }
        }
    }
    fun clearSession() {
        _liveSession.value = null
    }

}
