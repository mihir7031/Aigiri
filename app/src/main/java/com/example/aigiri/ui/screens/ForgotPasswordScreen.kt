//package com.example.aigiri.ui.screens
//
//@Composable
//fun ForgotPasswordScreen(
//    navController: NavController,
//    viewModel: LoginViewModel = viewModel()
//) {
//    var email by remember { mutableStateOf("") }
//    var message by remember { mutableStateOf<String?>(null) }
//    var isLoading by remember { mutableStateOf(false) }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(24.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Text("Forgot Password", fontSize = 26.sp, color = Color(0xFF6A1B9A))
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        OutlinedTextField(
//            value = email,
//            onValueChange = { email = it },
//            label = { Text("Enter your registered Email") },
//            singleLine = true,
//            modifier = Modifier.fillMaxWidth(),
//            shape = RoundedCornerShape(12.dp),
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
//        )
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        Button(
//            onClick = {
//                isLoading = true
//                viewModel.sendPasswordResetEmail(email.trim()) { success, errorMsg ->
//                    isLoading = false
//                    message = if (success) {
//                        "Reset email sent! Check your inbox."
//                    } else {
//                        errorMsg
//                    }
//                }
//            },
//            enabled = email.isNotBlank() && !isLoading,
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(48.dp),
//            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A)),
//            shape = RoundedCornerShape(12.dp)
//        ) {
//            if (isLoading) {
//                CircularProgressIndicator(
//                    color = Color.White,
//                    strokeWidth = 2.dp,
//                    modifier = Modifier.size(24.dp)
//                )
//            } else {
//                Text("Send Reset Email", color = Color.White, fontSize = 16.sp)
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        message?.let {
//            Text(
//                text = it,
//                color = if (it.startsWith("Reset email sent")) Color(0xFF388E3C) else MaterialTheme.colorScheme.error,
//                modifier = Modifier.padding(8.dp)
//            )
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Text(
//            "Back to Login",
//            color = Color(0xFF6A1B9A),
//            fontSize = 14.sp,
//            modifier = Modifier.clickable {
//                navController.popBackStack()
//            },
//            textDecoration = TextDecoration.Underline
//        )
//    }
//}
