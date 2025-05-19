package com.example.aigiri

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.aigiri.ui.screens.SendOtpScreen
import com.example.aigiri.ui.screens.SplashScreen
import com.example.aigiri.ui.screens.VerifyOtpScreen
import com.example.aigiri.ui.screens.WelcomeScreen
import com.example.aigiri.viewmodel.SendOtpViewModel
import com.example.aigiri.viewmodel.VerifyOtpViewModel


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val sendOtpViewModel = remember { SendOtpViewModel(AppContainer.otpRepository) }
    val verifyOtpViewModel = remember { VerifyOtpViewModel(AppContainer.otpRepository) }
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(onSplashComplete = {
                navController.navigate("welcome") {
                    popUpTo("splash") { inclusive = true }  // optional: clear splash from backstack
                }
            })
        }
        composable("welcome") {
            WelcomeScreen(navController=navController)
        }



        composable("send-otp") {
            SendOtpScreen(navController = navController, viewModel = sendOtpViewModel)
        }

        composable(
            route = "verify_otp/{phoneNumber}/{verificationId}",
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
                viewModel = verifyOtpViewModel
            )
        }

        // Profile Setup screen, reached after OTP is verified
//        composable(
//            route = "profile_setup/{phoneNumber}",
//            arguments = listOf(navArgument("phoneNumber") { type = NavType.StringType })
//        ) { backStackEntry ->
//            val phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?: ""
//            ProfileSetupScreen()
//        }

//        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
//        val isFirstRun = sharedPref.getBoolean("is_first_run", true)
//        if (isFirstRun) {
//            // Go to phone verification screen
//            sharedPref.edit().putBoolean("is_first_run", false).apply()
//        } else {
//            // Go to login screen
//        }
    }
}
