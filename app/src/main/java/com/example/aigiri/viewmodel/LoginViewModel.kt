package com.example.aigiri.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val userId: String) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    private val _loginState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val loginState: StateFlow<LoginUiState> = _loginState

    fun login(identifier: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginUiState.Loading
            try {
                var userDoc = usersCollection.whereEqualTo("email", identifier).get().await()

                if (userDoc.isEmpty) {
                    userDoc = usersCollection.whereEqualTo("username", identifier).get().await()
                }
                if (userDoc.isEmpty) {
                    userDoc = usersCollection.whereEqualTo("phoneNo", identifier).get().await()
                }
                if (userDoc.isEmpty) {
                    _loginState.value = LoginUiState.Error("User not found")
                    return@launch
                }

                val user = userDoc.documents.first()
                val email = user.getString("email") ?: ""

                if (email.isEmpty()) {
                    _loginState.value = LoginUiState.Error("User email not found")
                    return@launch
                }

                // Firebase Auth sign-in with email and password
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            _loginState.value = LoginUiState.Success(auth.currentUser?.uid ?: "")
                        } else {
                            _loginState.value =
                                LoginUiState.Error(task.exception?.localizedMessage ?: "Login failed")
                        }
                    }

            } catch (e: Exception) {
                _loginState.value = LoginUiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }
}

