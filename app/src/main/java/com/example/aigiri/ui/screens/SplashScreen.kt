package com.example.aigiri.ui.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aigiri.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashComplete: () -> Unit) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        delay(2000)
        onSplashComplete()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.durga),
            contentDescription = "Shield Icon",
            modifier = Modifier.size(50.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Aigiri",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6A1B9A)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Safety in Your Hands, Strength in Your Voice",
            fontSize = 16.sp,
            color = Color.Gray
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewSplashScreen() {
    SplashScreen(onSplashComplete = {})
}
