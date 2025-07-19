package com.example.aigiri.ui.components



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.aigiri.model.ChatMessage


@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.sender == "User"
    val backgroundColor = if (isUser) Color(0xFFD8F6CD) else Color(0xFFE0E0E0)
    val alignment = if (isUser) Alignment.End else Alignment.Start

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .background(backgroundColor, RoundedCornerShape(12.dp))
                .padding(12.dp)
                .widthIn(max = 280.dp)
        ) {
            Text(message.message)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                message.time,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}