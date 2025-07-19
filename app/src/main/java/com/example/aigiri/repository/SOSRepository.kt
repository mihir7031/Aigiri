package com.example.aigiri.repository

class SOSRepository(
    private val api: SOSApiService,
    private val context: Context
) {

    fun isInternetAvailable(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    suspend fun sendSOSOnline(request: SOSRequest): Result<Unit> {
        return runCatching {
            api.sendSOS(request)
        }.map { it.body() ?: Unit }
    }

    fun sendSOSViaSms(contacts: List<String>, message: String) {
        val smsManager = SmsManager.getDefault()
        contacts.forEach {
            smsManager.sendTextMessage(it, null, message, null, null)
        }
    }
}
