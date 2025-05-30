package com.example.aigiri

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun RootApp() {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    val token by tokenManager.tokenFlow.collectAsState(initial = null)

    val startDestination = if (token.isNullOrEmpty()) {
        "splash"
    } else {
        "dashboard"
    }

    AppNavigation(startDestination)
}
