package com.example.aigiri.model

data class EmergencyContact(
    val name: String = "",
    val phoneNumber: String = "",
    val priority: Int = 0
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "phoneNumber" to phoneNumber,
            "priority" to priority
        )
    }
}
