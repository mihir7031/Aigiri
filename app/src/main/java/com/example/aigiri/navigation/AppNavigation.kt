package com.example.aigiri.navigation

import android.app.Application
<<<<<<< HEAD
=======
import androidx.compose.material3.Text
>>>>>>> recovered-work
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
<<<<<<< HEAD
=======
import androidx.navigation.NavController
>>>>>>> recovered-work
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.aigiri.network.*
<<<<<<< HEAD
=======
import com.example.aigiri.network.NetworkClient.liveSessionApi
>>>>>>> recovered-work
import com.example.aigiri.ui.screens.*
import com.example.aigiri.viewmodel.*
import com.example.aigiri.repository.*
import com.example.aigiri.ui.components.LiveButton

<<<<<<< HEAD
=======
import java.net.URLDecoder
>>>>>>> recovered-work


@Composable
fun AppNavigation(startDestination: String, tokenManager: TokenManager) {
    val navController = rememberNavController()
    val context= LocalContext.current
// ViewModels
    val signupViewModel = remember {
<<<<<<< HEAD
        SignupViewModel(OtpRepository(), userRepository = UserRepository())
    }

    val verifyOtpViewModel = remember {
        VerifyOtpViewModel(OtpRepository(), userRepository = UserRepository())
=======
        SignupViewModel(AppContainer.otpRepository, userRepository = UserRepository())
    }

    val verifyOtpViewModel = remember {
        VerifyOtpViewModel(AppContainer.otpRepository, userRepository = UserRepository())
>>>>>>> recovered-work
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
<<<<<<< HEAD
    val ChatViewModel= remember { ChatViewModel(ChatRepository()) }
    val SOSViewModel=remember{SOSViewModel(SOSRepository(context =context), emergencyRepository = EmergencyContactsRepository(), userRepository = UserRepository(), tokenManager = TokenManager(context))}
=======


>>>>>>> recovered-work
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
<<<<<<< HEAD
                liveStreamViewModel = liveStreamViewModel,
                SOSViewModel = SOSViewModel,
                Context = context
=======
                liveStreamViewModel = liveStreamViewModel
>>>>>>> recovered-work
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
<<<<<<< HEAD
=======
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
>>>>>>> recovered-work
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
<<<<<<< HEAD
        composable("chatbot") {
            ChatScreen(ChatViewModel,navController)
        }
=======
>>>>>>> recovered-work





    }
}
