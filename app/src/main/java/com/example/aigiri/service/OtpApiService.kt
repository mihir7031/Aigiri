package com.example.aigiri.service


import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query

interface OtpApiService {
    @POST("send-otp")
    suspend fun sendOtp(
        @Query("phoneNumber") phoneNumber: String,
        @Query("otp") otp: String
    ): Response<Unit>
}
