package com.example.aigiri.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.aigiri.viewmodel.PermissionViewModel
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.launch


@Composable
fun GrantPermissionScreen(
    viewModel: PermissionViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Permission launchers
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.onPermissionResult(Manifest.permission.ACCESS_FINE_LOCATION, isGranted)
        if (!isGranted) openAppSettings(context)
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.onPermissionResult(Manifest.permission.CAMERA, isGranted)
        if (!isGranted) openAppSettings(context)
    }

    val microphonePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.onPermissionResult(Manifest.permission.RECORD_AUDIO, isGranted)
        if (!isGranted) openAppSettings(context)
    }

    val contactsPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.onPermissionResult(Manifest.permission.READ_CONTACTS, isGranted)
        if (!isGranted) openAppSettings(context)
    }

    val notificationsPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            viewModel.onPermissionResult(Manifest.permission.POST_NOTIFICATIONS, isGranted)
            if (!isGranted) openNotificationSettings(context)
        }
    }

    // Observing permission states
    val locationPermissionGranted by viewModel.locationPermissionGranted.collectAsState()
    val cameraPermissionGranted by viewModel.cameraPermissionGranted.collectAsState()
    val microphonePermissionGranted by viewModel.microphonePermissionGranted.collectAsState()
    val contactsPermissionGranted by viewModel.contactsPermissionGranted.collectAsState()
    val notificationsPermissionGranted by viewModel.notificationsPermissionGranted.collectAsState()

    //Added
    //To-do backhandler going to login and clear the stack
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Grant Permissions",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text("Required Permissions", style = MaterialTheme.typography.titleLarge)

                Text(
                    text = "To provide a seamless experience, we ask for these permissions upfront. " +
                            "In urgent scenarios—like calls or location sharing—you won't have time to grant them. " +
                            "Please enable them now to avoid interruptions later.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                )

                PermissionItem(
                    title = "Location",
                    isGranted = locationPermissionGranted,
                    onRequestPermission = {
                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                )

                PermissionItem(
                    title = "Camera",
                    isGranted = cameraPermissionGranted,
                    onRequestPermission = {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                )

                PermissionItem(
                    title = "Microphone",
                    isGranted = microphonePermissionGranted,
                    onRequestPermission = {
                        microphonePermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                )

                PermissionItem(
                    title = "Contacts",
                    isGranted = contactsPermissionGranted,
                    onRequestPermission = {
                        contactsPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                    }
                )

                PermissionItem(
                    title = "Notifications",
                    isGranted = notificationsPermissionGranted,
                    onRequestPermission = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notificationsPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Signup successful!",duration=SnackbarDuration.Indefinite)
                        }
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = false }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4BEE6)),
                    enabled = viewModel.allPermissionsGranted()
                ) {
                    Text("Finish", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun PermissionItem(
    title: String,
    isGranted: Boolean,
    onRequestPermission: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isGranted,
            onCheckedChange = { if (!isGranted) onRequestPermission() },
            enabled = !isGranted,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = Color.Gray
            )
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

// Helper functions
private fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", context.packageName, null)
    intent.data = uri
    context.startActivity(intent)
}

private fun openNotificationSettings(context: Context) {
    val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
    } else {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            .setData(Uri.fromParts("package", context.packageName, null))
    }
    context.startActivity(intent)
}
