package com.example.aigiri.ui.components
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.example.aigiri.viewmodel.LiveStreamViewModel

@Composable
fun LiveButton(viewModel: LiveStreamViewModel, navController: NavController) {
    Button(onClick = {
        viewModel.prepareLiveSession()
        navController.navigate("livecall")
    }) {
        Text("Go Live")
    }
}