package com.example.aigiri.repository

import com.example.aigiri.service.OtpApiService

class OtpRepository(
    private val api: OtpApiService
) {
    suspend fun sendOtp(phoneNumber: String, otp: String): Result<Unit> {
        return try {
            val response = api.sendOtp(phoneNumber, otp)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Failed: ${response.code()} ${response.message()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
