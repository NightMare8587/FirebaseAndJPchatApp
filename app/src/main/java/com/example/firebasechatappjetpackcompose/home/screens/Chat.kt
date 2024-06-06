package com.example.firebasechatappjetpackcompose.home.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firebasechatappjetpackcompose.model.ChatModel
import kotlin.random.Random

@Composable
fun ChatScreen(
    innerPadding: PaddingValues,
    currentUserUID: String,
    myList: List<ChatModel>,
    messageToSend: (String) -> Unit
) {
    var userMessage by remember { mutableStateOf(TextFieldValue("")) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(0.9f)
                .fillMaxWidth(),
            reverseLayout = true
        ) {
            itemsIndexed(items = myList) { index, item ->
                ChatMessage(item, currentUserUID)
            }
        }

        Row(
            modifier = Modifier
                .weight(0.1f)
                .fillMaxWidth()
        ) {
            TextField(
                value = userMessage,
                onValueChange = { change -> userMessage = change },
                placeholder = { Text("Enter your message") }, modifier = Modifier.weight(0.9f)
            )
            IconButton(onClick = {
                if (userMessage.text.isNotEmpty()) {
                    messageToSend(userMessage.text)
                    userMessage = TextFieldValue("")
                }
            }, modifier = Modifier.weight(0.1f)) {
                Icon(imageVector = Icons.Default.Send, contentDescription = null)
            }
        }
    }
}

@Composable
fun ChatMessage(item: ChatModel, currentUserUID: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(25.dp))
            .padding(16.dp)
            .background(
                color = if (currentUserUID != item.userUID) returnRandomColor() else Color.Yellow
            )

    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp, 0.dp),
            horizontalAlignment = if (currentUserUID != item.userUID) Alignment.Start else Alignment.End
        ) {
            Text(item.username, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(item.message, fontSize = 22.sp, fontWeight = FontWeight.Light)
        }
    }
}

fun returnRandomColor(): Color {
    val random = Random.nextInt(until = 5) + 1

    return when (random) {
        1 -> Color.Cyan
        2 -> Color.LightGray
        3 -> Color.Gray
        4 -> Color.Green
        else -> Color.Magenta
    }
}
