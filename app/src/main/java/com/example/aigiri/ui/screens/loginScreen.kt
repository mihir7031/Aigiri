package com.example.aigiri.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.aigiri.viewmodel.LoginUiState

import com.example.aigiri.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = viewModel()
) {
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isValid = identifier.isNotBlank() && password.isNotBlank()

    val loginState by viewModel.loginState.collectAsState()

    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Login", fontSize = 26.sp, color = Color(0xFF6A1B9A))

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = identifier,
            onValueChange = { identifier = it },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
//            trailingIcon = {
//                val visibilityIcon = if (passwordVisible)
//                    Icons.Default.Visibility
//                else
//                    Icons.Default.VisibilityOff
//
//                IconButton(onClick = { passwordVisible = !passwordVisible }) {
//                    Icon(
//                        imageVector = visibilityIcon,
//                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
//                    )
//                }
//            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Forgot Password?",
            color = Color(0xFF6A1B9A),
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.End)
                .clickable {},
            textDecoration = TextDecoration.Underline
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.login(identifier, password) },
            enabled = isValid && loginState !is LoginUiState.Loading,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Login", color = Color.White, fontSize = 16.sp)
        }

        // Show loading indicator
        if (loginState is LoginUiState.Loading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }

        // Show error message
        if (loginState is LoginUiState.Error) {
            Spacer(modifier = Modifier.height(16.dp))
            val message = (loginState as LoginUiState.Error).message
            Text(text = message, color = Color.Red, fontSize = 14.sp)
        }

        // Navigate on success
        if (loginState is LoginUiState.Success) {
            LaunchedEffect(Unit) {
                navController.navigate("home") // Replace "home" with your actual screen
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Create an Account",
            color = Color(0xFF6A1B9A),
            fontSize = 14.sp,
            modifier = Modifier.clickable {
                navController.navigate("signup")
            },
            textDecoration = TextDecoration.Underline
        )
    }
}
