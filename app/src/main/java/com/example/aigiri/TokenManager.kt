package com.example.aigiri

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// Define DataStore at top level
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("auth_pref")

class TokenManager(context: Context) {

    companion object {
        // Define token key once and reuse it everywhere
        private val JWT_TOKEN_KEY = stringPreferencesKey("jwt_token")
    }

    private val dataStore = context.applicationContext.dataStore

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Volatile
    private var cachedToken: String? = null

    val tokenFlow: Flow<String?> = dataStore.data
        .map { preferences -> preferences[JWT_TOKEN_KEY] }

    init {
        scope.launch {
            tokenFlow.collect { cachedToken = it }
        }
    }

    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[JWT_TOKEN_KEY] = token
        }
    }

    suspend fun clearToken() {
        dataStore.edit { preferences ->
            preferences.remove(JWT_TOKEN_KEY)
        }
    }

    fun getCachedToken(): String? = cachedToken
}
