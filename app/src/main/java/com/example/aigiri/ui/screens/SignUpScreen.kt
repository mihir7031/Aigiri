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
import com.example.aigiri.viewmodel.SendOtpUiState
import com.example.aigiri.viewmodel.SignupViewModel

@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: SignupViewModel
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    val countryCodes = listOf("+91", "+1", "+44")
    var selectedCountryCode by remember { mutableStateOf(countryCodes[0]) }
    var phoneNumber by remember { mutableStateOf("") }

    val fullPhoneNumber = selectedCountryCode + phoneNumber

    val isPasswordValid = isValidPassword(password)

    val state by viewModel.uiState.collectAsState()
    val usernameTaken by viewModel.usernameTaken.collectAsState()
    val emailTaken by viewModel.emailTaken.collectAsState()
    val phoneTaken by viewModel.phoneTaken.collectAsState()

    val isUsernameValid = username.isNotBlank()
    val isConfirmPasswordValid = confirmPassword == password && confirmPassword.isNotBlank()
    val isPhoneValid = phoneNumber.length == 10 && phoneNumber.all { it.isDigit() }
    val isEmailValid = email.isEmpty() || android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    val isFormValid = isUsernameValid && isPasswordValid && isConfirmPasswordValid &&
            isPhoneValid && isEmailValid &&
            !usernameTaken && !emailTaken && !phoneTaken

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Create your account", fontSize = 22.sp, color = Color(0xFF6A1B9A))
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                viewModel.checkUsername(it)
            },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            isError = (!isUsernameValid && username.isNotEmpty()) || usernameTaken
        )
        if (!isUsernameValid && username.isNotEmpty()) {
            Text("Username is required", color = Color.Red, fontSize = 12.sp)
        } else if (usernameTaken) {
            Text("Username already exists", color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            isError = password.isNotEmpty() && !isPasswordValid
        )
        if (password.isNotEmpty() && !isPasswordValid) {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (password.length < 8) {
                    Text("Password must be at least 8 characters", color = Color.Red, fontSize = 12.sp)
                }
                if (!password.any { it.isUpperCase() }) {
                    Text("Password must contain at least one uppercase letter", color = Color.Red, fontSize = 12.sp)
                }
                if (!password.any { it.isLowerCase() }) {
                    Text("Password must contain at least one lowercase letter", color = Color.Red, fontSize = 12.sp)
                }
                if (!password.any { it.isDigit() }) {
                    Text("Password must contain at least one digit", color = Color.Red, fontSize = 12.sp)
                }
                if (!password.any { "!@#\$%^&*()_+=-{}[]|:;\"'<>,.?/".contains(it) }) {
                    Text("Password must contain at least one special character", color = Color.Red, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            isError = confirmPassword.isNotEmpty() && !isConfirmPasswordValid
        )
        if (confirmPassword.isNotEmpty() && !isConfirmPasswordValid) {
            Text("Passwords do not match", color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                viewModel.checkEmail(it)
            },
            label = { Text("Email (Optional)") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            isError = email.isNotEmpty() && (!isEmailValid || emailTaken)
        )
        if (email.isNotEmpty() && !isEmailValid) {
            Text("Enter a valid email address", color = Color.Red, fontSize = 12.sp)
        } else if (emailTaken) {
            Text("Email is already in use", color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var expanded by remember { mutableStateOf(false) }

            Box(modifier = Modifier.wrapContentSize()) {
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.width(100.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(selectedCountryCode)
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    countryCodes.forEach { code ->
                        DropdownMenuItem(
                            text = { Text(code) },
                            onClick = {
                                selectedCountryCode = code
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = {
                    if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                        phoneNumber = it
                        viewModel.checkPhone(fullPhoneNumber)
                    }
                },
                label = { Text("Phone Number") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                isError = phoneNumber.isNotEmpty() && (!isPhoneValid || phoneTaken)
            )
        }

        if (phoneNumber.isNotEmpty() && !isPhoneValid) {
            Text("Enter a valid 10-digit phone number", color = Color.Red, fontSize = 12.sp)
        } else if (phoneTaken) {
            Text("Phone number already in use", color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { viewModel.sendOtp(fullPhoneNumber) },
            enabled = isFormValid,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A))
        ) {
            Text("Next", color = Color.White)
        }

        when (state) {
            is SendOtpUiState.Loading -> CircularProgressIndicator()
            is SendOtpUiState.Error -> Text((state as SendOtpUiState.Error).message, color = Color.Red)
            is SendOtpUiState.Success -> {
                val s = state as SendOtpUiState.Success
                LaunchedEffect(state) {
                    navController.navigate("verify_otp/${s.phoneNumber}/${s.otp}")
                }
            }
            else -> {}
        }
    }
}

// Helper function
fun isValidPassword(password: String): Boolean {
    return password.length >= 8 &&
            password.any { it.isUpperCase() } &&
            password.any { it.isLowerCase() } &&
            password.any { it.isDigit() } &&
            password.any { "!@#\$%^&*()_+=-{}[]|:;\"'<>,.?/".contains(it) }
}
