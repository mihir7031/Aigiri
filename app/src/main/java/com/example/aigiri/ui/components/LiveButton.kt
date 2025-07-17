package com.example.aigiri.ui.components
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.aigiri.viewmodel.LiveViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

@Composable
fun LiveButton(viewModel: LiveViewModel, navController: NavController) {
    val session by viewModel.liveSession.collectAsState()
    val error by viewModel.error.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.align(Alignment.Center)) {
            Button(
                onClick = { viewModel.onLiveButtonClick() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF800080))
            ) {
                Text(text = "Live", color = Color.White)
            }

            error?.let {
                Text("❌ $it", color = Color.Red)
                LaunchedEffect(it) {
                    scope.launch {
                        snackbarHostState.showSnackbar("Error: $it")
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }

    LaunchedEffect(session) {
        session?.let {
            // Save token/wsUrl before navigation
            navController.currentBackStackEntry?.savedStateHandle?.set("token", it.token)
            navController.currentBackStackEntry?.savedStateHandle?.set("wsUrl", it.wsUrl)

            navController.navigate("liveCall")
            viewModel.clearSession()
        }
    }



}
