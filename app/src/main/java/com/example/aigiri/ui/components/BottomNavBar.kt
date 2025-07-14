package com.example.aigiri.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay

data class NavigationItem(val name: String, val icon: ImageVector, val route: String)

private val bottomNavLeftItems = listOf(
    NavigationItem("Home", Icons.Filled.Home, "dashboard"),
    NavigationItem("History", Icons.Filled.MailOutline, "history")
)

private val bottomNavRightItems = listOf(
    NavigationItem("Chatbot", Icons.Filled.Hub, "chatbot"),
    NavigationItem("Profile", Icons.Filled.Person, "profile")
)

@Composable
fun BottomNavBar(
    navController: NavHostController,
    primaryColor: Color = Color(0xFF6A1B9A),
//    icon highlight color
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = navBackStackEntry?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        NavigationBar(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            containerColor = Color.White,
            tonalElevation = 8.dp
        ) {
            bottomNavLeftItems.forEach { item ->
                NavigationBarItem(
                    icon = { Icon(item.icon, contentDescription = item.name) },
                    label = { Text(item.name) },
                    selected = currentRoute == item.route,
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

            Spacer(modifier = Modifier.width(60.dp)) // Space for FAB

            bottomNavRightItems.forEach { item ->
                NavigationBarItem(
                    icon = { Icon(item.icon, contentDescription = item.name) },
                    label = { Text(item.name) },
                    selected = currentRoute == item.route,
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

        AnimatedSosButton(
            navController = navController,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-10).dp) // slightly lower into nav bar
        )

    }

    }


@Composable
fun AnimatedSosButton(navController: NavHostController, modifier: Modifier) {
    val infiniteTransition = rememberInfiniteTransition()
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    var isPressed by remember { mutableStateOf(false) }
    var navigate by remember { mutableStateOf(false) }

    // Correct usage: perform side effect after state change
    LaunchedEffect(navigate) {
        if (navigate) {
            delay(200)
            isPressed = false
            navController.navigate("add_contacts") {
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
            navigate = false
        }
    }

    val buttonColor = if (isPressed) Color(0xFF4CAF50) else Color.Red

    Box(
        modifier = modifier
            .size(65.dp)
            .drawBehind {
                drawCircle(
                    color = buttonColor.copy(alpha = pulseAlpha),
                    radius = size.minDimension / 2 + 10,
                    style = Stroke(width = 6f)
                )
            },
        contentAlignment = Alignment.Center
    ) {
        FloatingActionButton(
            onClick = {
                isPressed = true
                navigate = true // Triggers LaunchedEffect
            },
            containerColor = buttonColor,
            contentColor = Color.White,
            modifier = Modifier.size(65.dp),
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(8.dp)
        ) {
            Text(
                text = "SOS",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = Color.White
            )
        }
    }
}



@Preview(showSystemUi = true)
@Composable
fun BottomNavBarPreview() {
    val navController = rememberNavController()
    BottomNavBar(navController = navController)
}
