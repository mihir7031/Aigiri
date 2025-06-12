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
import com.example.aigiri.ui.components.isValidPassword
import com.example.aigiri.ui.components.passwordWarning
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
    var phoneNumber by remember { mutableStateOf("+91") }
    val isPasswordValid = isValidPassword(password)

    val state by viewModel.uiState.collectAsState()
    val usernameTaken by viewModel.usernameTaken.collectAsState()
    val emailTaken by viewModel.emailTaken.collectAsState()
    val phoneTaken by viewModel.phoneTaken.collectAsState()

    val isUsernameValid = username.isNotBlank()
    val isConfirmPasswordValid = confirmPassword.isNotBlank() && confirmPassword == password
    val isPhoneValid = phoneNumber.length == 13

    val isFormValid = isUsernameValid && isPasswordValid && isConfirmPasswordValid && isPhoneValid &&
            !usernameTaken && !emailTaken && !phoneTaken

    val isLoading = state is SendOtpUiState.Loading

    // Handle navigation safely on success
    LaunchedEffect(state) {
        if (state is SendOtpUiState.Success) {
            val successState = state as SendOtpUiState.Success
            navController.navigate("verify_otp/${successState.phoneNumber}/${successState.otp}")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Create your account", fontSize = 22.sp , color = Color(0xFF6A1B9A))

        Spacer(modifier = Modifier.height(16.dp))

        // Username field
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

        // Password field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            isError = password.isNotEmpty() && !isPasswordValid
        )
        val warningMessage = passwordWarning(password)
        if (warningMessage.isNotEmpty()) {
            Text(warningMessage, color = Color.Red, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Confirm Password
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

        // Email
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
            isError = emailTaken
        )
        if (emailTaken) {
            Text("Email is already in use", color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Phone
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = {
                if (it.length <= 13) {
                    phoneNumber = it
                    viewModel.checkPhone(it)
                }
            },
            label = { Text("+91XXXXXXXXXX") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            isError = phoneNumber.isNotEmpty() && (!isPhoneValid || phoneTaken)
        )
        if (phoneNumber.isNotEmpty() && !isPhoneValid) {
            Text("Enter a valid phone number", color = Color.Red, fontSize = 12.sp)
        } else if (isPhoneValid && phoneTaken) {
            Text("Phone number already in use", color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Submit Button
        Button(
            onClick = {
                viewModel.sendOtp(username, password, phoneNumber, email.takeIf { it.isNotBlank() })
            },
            enabled = isFormValid && !isLoading,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A))
        ) {
            Text("Next", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Show loading or error
        when (state) {
            is SendOtpUiState.Loading -> CircularProgressIndicator(color = Color(0xFF6A1B9A))
            is SendOtpUiState.Error -> {
                val errorState = state as SendOtpUiState.Error
                Text(errorState.message, color = Color.Red, fontSize = 14.sp)
            }
            else -> {}
        }
    }
}






