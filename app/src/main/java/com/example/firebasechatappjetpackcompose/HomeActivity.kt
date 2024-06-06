package com.example.firebasechatappjetpackcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.firebasechatappjetpackcompose.Utils.FirebaseUtils
import com.example.firebasechatappjetpackcompose.home.screens.ChatScreen
import com.example.firebasechatappjetpackcompose.model.ChatModel
import com.example.firebasechatappjetpackcompose.ui.theme.FirebaseChatAppJetpackComposeTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {
    @Inject
    lateinit var firebaseUtils: FirebaseUtils
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FirebaseChatAppJetpackComposeTheme {
                var myList by remember { mutableStateOf(emptyList<ChatModel>()) }
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ChatScreen(innerPadding,firebaseUtils.getCurrentUserUID(),myList) {
                        val map = hashMapOf(
                            "message" to it,
                            "userUID" to firebaseUtils.getCurrentUserUID(),
                            "username" to firebaseUtils.getCurrentUsername()
                        )

                        firebaseUtils.sendMessageInGroupChat(map)
                    }
                }
                firebaseUtils.firestoreChatListener { chatData ->
                    if(chatData.isNotEmpty()) {
                        val newList = myList.toMutableList()
                        newList.addAll(chatData)
                        myList = newList
                    }
                }
            }
        }
    }
}
