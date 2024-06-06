package com.example.firebasechatappjetpackcompose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.example.firebasechatappjetpackcompose.Utils.FirebaseUtils
import com.example.firebasechatappjetpackcompose.home.screens.ChatScreen
import com.example.firebasechatappjetpackcompose.login.screens.ProfileAlertDialog
import com.example.firebasechatappjetpackcompose.model.ChatModel
import com.example.firebasechatappjetpackcompose.ui.theme.FirebaseChatAppJetpackComposeTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {
    private val TAG = "HomeActivity"
    @Inject
    lateinit var firebaseUtils: FirebaseUtils
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FirebaseChatAppJetpackComposeTheme {
                var userProfileUrl by remember { mutableStateOf("") }
                var showUserProfileDialog by remember { mutableStateOf(false) }
                var myList by remember { mutableStateOf(emptyList<ChatModel>()) }
                val context = LocalContext.current
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding()
                        .displayCutoutPadding()
                        .statusBarsPadding()
                ) { innerPadding ->
                    ChatScreen(innerPadding, firebaseUtils.getCurrentUserUID(), myList, messageToSend = {
                        val map = hashMapOf(
                            "message" to it,
                            "userUID" to firebaseUtils.getCurrentUserUID(),
                            "username" to firebaseUtils.getCurrentUsername()
                        )

                        firebaseUtils.sendMessageInGroupChat(map)
                    }, onProfileClickeds = {
                        Log.d(TAG, "onCreate: profile clicked $it")
                        firebaseUtils.getCurrentUserProfileImage(it) { image ->
                            Log.d(TAG, "onCreate: image of profile $image")
                            userProfileUrl = image.toString()
                            showUserProfileDialog = true
                        }
                    })

                    if(showUserProfileDialog) {
                        ProfileAlertDialog(userProfileUrl.toUri(),context) {
                            showUserProfileDialog = false
                        }
                    }
                }
                firebaseUtils.firestoreChatListener { chatData ->
                    if (chatData.isNotEmpty()) {
                        val newList = myList.toMutableList()
                        newList.addAll(0, chatData)
                        myList = newList
                    }
                }
            }
        }
    }
}
