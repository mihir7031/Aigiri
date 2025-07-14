package com.example.aigiri.model
data class EmergencyContact(
    var id: String = "",           // Firestore doc ID (optional initially)
    var name: String = "",
    var phoneNumber: String = "",
    var priority: Int = 0          // Used for sorting contacts
)
