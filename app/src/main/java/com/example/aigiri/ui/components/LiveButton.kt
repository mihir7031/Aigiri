package com.example.aigiri.ui.components

import android.net.Uri
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.aigiri.viewmodel.LiveViewModel

@Composable
fun LiveButton(viewModel: LiveViewModel,navController: NavController) {
    Button(
        onClick = { viewModel.onLiveButtonClick() },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF800080))
    ) {
        Text(text = "Live", color = Color.White)
    }

    val session by viewModel.liveSession.collectAsState()
    val error by viewModel.error.collectAsState()


    session?.let {
        navController.navigate("liveCall/${Uri.encode(it.token)}/${Uri.encode(it.wsUrl)}")
    }

    error?.let {
        Text("❌ $it", color = Color.Red)
    }
}
