package com.example.aigiri

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.aigiri.repository.UserRepository
import com.example.aigiri.ui.screens.*
import com.example.aigiri.viewmodel.*

@Composable
fun AppNavigation(startDestination: String) {
    val navController = rememberNavController()
    val context = LocalContext.current

    // ViewModels
    val signupViewModel = remember {
        SignupViewModel(AppContainer.otpRepository, userRepository = UserRepository())
    }
    val verifyOtpViewModel = remember {
        VerifyOtpViewModel(AppContainer.otpRepository)
    }
    val loginViewModel = remember {
        LoginViewModel(TokenManager(context), context)
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable("splash") {
            SplashScreen(onSplashComplete = {
                navController.navigate("welcome") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }
        composable("welcome") {
            WelcomeScreen(navController = navController)
        }
        composable("signup") {
            SignUpScreen(navController = navController, viewModel = signupViewModel)
        }
        composable("login") {
            LoginScreen(navController = navController, viewModel = loginViewModel)
        }
        composable(
            "verify_otp/{phoneNumber}/{verificationId}",
            arguments = listOf(
                navArgument("phoneNumber") { type = NavType.StringType },
                navArgument("verificationId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?: ""
            val verificationId = backStackEntry.arguments?.getString("verificationId") ?: ""
            VerifyOtpScreen(
                navController = navController,
                phoneNumber = phoneNumber,
                verificationId = verificationId,
                viewModel = verifyOtpViewModel,
                signupViewModel = signupViewModel
            )
        }
        composable("dashboard") {
            DashboardScreen()
        }
        composable("forgot_password") {
            ForgotPasswordScreen()
        }
    }
}

//Logout Logic
//viewModelScope.launch {
//    tokenManager.clearToken()
//    navController.navigate("login") {
//        popUpTo("dashboard") { inclusive = true }
//    }
//}
