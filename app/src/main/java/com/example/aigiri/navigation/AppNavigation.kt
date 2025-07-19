package com.example.aigiri.navigation

import android.app.Application
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.aigiri.network.*
import com.example.aigiri.network.NetworkClient.liveSessionApi
import com.example.aigiri.ui.screens.*
import com.example.aigiri.viewmodel.*
import com.example.aigiri.repository.*
import com.example.aigiri.ui.components.LiveButton

import java.net.URLDecoder


@Composable
fun AppNavigation(startDestination: String, tokenManager: TokenManager) {
    val navController = rememberNavController()
    val context= LocalContext.current
// ViewModels
    val signupViewModel = remember {
        SignupViewModel(AppContainer.otpRepository, userRepository = UserRepository())
    }

    val verifyOtpViewModel = remember {
        VerifyOtpViewModel(AppContainer.otpRepository, userRepository = UserRepository())
    }

    val loginViewModel = remember {
        LoginViewModel(tokenManager, EmergencyContactsRepository(), context)
    }

    val dashboardViewModel = remember {
        DashboardViewModel(tokenManager)
    }

    val emergencyContactsViewModel = remember {
        EmergencyContactsViewModel(EmergencyContactsRepository(), tokenManager)
    }

    val permissionViewModel: PermissionViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(
            context.applicationContext as Application
        )
    )
    val liveStreamViewModel=remember{LiveStreamViewModel(tokenManager = TokenManager(context =context))}
    val settingsViewModel= remember { SettingsViewModel(tokenManager = TokenManager(context)) }


    NavHost(navController = navController, startDestination = startDestination) {
        composable("splash") {
            SplashScreen(onSplashComplete = {
                navController.navigate("welcome") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }
        composable("add_contacts") {
            EmergencyContactsScreen(navController = navController, viewModel = emergencyContactsViewModel)
        }
        composable("login") {
            LoginScreen(navController = navController, viewModel = loginViewModel, tokenManager = tokenManager)
        }
        composable("welcome") {
            WelcomeScreen(navController)
        }
        composable("signup")
        {
            SignUpScreen(navController,signupViewModel)
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
                viewModel = verifyOtpViewModel,
                signupViewModel = signupViewModel
            )
        }

        composable("dashboard") {
            DashboardScreen(
                viewModel = dashboardViewModel,
                navController = navController,
                tokenManager = tokenManager,
                liveStreamViewModel = liveStreamViewModel
            )
        }

        composable("forgot_password") {
            ForgotPasswordScreen()
        }

        composable("history") {
            HistoryScreen()
        }

        composable("add_contacts") {
            EmergencyContactsScreen(navController, emergencyContactsViewModel)
        }

        composable("LiveButton") {
            LiveButton(liveStreamViewModel,navController)

        }
        composable("permission")
        {
            GrantPermissionScreen(viewModel = permissionViewModel,navController=navController)
        }
//        composable("liveCall") {
//            val token = navController.previousBackStackEntry
//                ?.savedStateHandle?.get<String>("token")
//            val wsUrl = navController.previousBackStackEntry
//                ?.savedStateHandle?.get<String>("wsUrl")
//
//            if (token != null && wsUrl != null) {
//                LiveCallScreen(token = token, wsUrl = wsUrl) {
//                    navController.popBackStack()
//                }
//            }
//        }
        composable("livecall") {
            LiveStreamScreen(liveStreamViewModel, navController = navController)
        }
        composable("setting") {
            SettingsScreen(
                navController = navController,
                onBackClick = {
                    navController.navigate("dashboard") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                viewModel = settingsViewModel
            )

        }





    }
}
