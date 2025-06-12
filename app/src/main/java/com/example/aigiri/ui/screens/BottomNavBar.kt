package com.example.aigiri.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationItem(val name: String, val icon: ImageVector, val route: String)

val bottomNavItems = listOf(
    NavigationItem("Home", Icons.Filled.Home, "dashboard"),
    NavigationItem("History", Icons.Filled.MailOutline, "history"),
    NavigationItem("SOS", Icons.Filled.Warning, "sos"),
    NavigationItem("Chatbot", Icons.Filled.Hub, "chatbot")
)

@Composable
fun BottomNavBar(
    navController: NavHostController,
    items: List<NavigationItem> = bottomNavItems,
    primaryColor: Color = Color(0xFF6A1B9A),
) {
    NavigationBar(
        containerColor = Color.White,
        contentColor = primaryColor
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.name) },
                label = { Text(item.name) },
                selected = navController.currentDestination?.route == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = primaryColor,
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = primaryColor,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.White
                )
            )
        }
    }
}
