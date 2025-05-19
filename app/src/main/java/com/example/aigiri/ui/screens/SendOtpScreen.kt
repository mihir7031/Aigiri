package com.example.aigiri.ui.screens
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.aigiri.viewmodel.SendOtpUiState
import com.example.aigiri.viewmodel.SendOtpViewModel

@Composable
fun SendOtpScreen(
    navController: NavController,
    viewModel: SendOtpViewModel
) {
    var phoneNumber by remember { mutableStateOf("+91") }
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Enter your phone number", fontSize = 22.sp)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { if (it.length <= 13) phoneNumber = it },
            label = { Text("+91XXXXXXXXXX") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { viewModel.sendOtp(phoneNumber) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A))
        ) {
            Text("Send OTP", color = Color.White)
        }

        when (state) {
            is SendOtpUiState.Loading -> CircularProgressIndicator()
            is SendOtpUiState.Error -> Text((state as SendOtpUiState.Error).message, color = Color.Red)
            is SendOtpUiState.Success -> {
                val s = state as SendOtpUiState.Success
                LaunchedEffect(Unit) {
                    navController.navigate("verify_otp/${s.phoneNumber}/${s.otp}")
                }
            }
            else -> {}
        }

    }
}

