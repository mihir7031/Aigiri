package com.example.aigiri.model

data class User(
    val uid: String = "",        // Firebase Auth UID
    val phoneNo: String = "",
    val email: String = "",
    val password: String=""
)
