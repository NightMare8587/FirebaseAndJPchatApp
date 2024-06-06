package com.example.firebasechatappjetpackcompose

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.firebasechatappjetpackcompose.Utils.FirebaseUtils
import com.example.firebasechatappjetpackcompose.login.screens.LoginScreen
import com.example.firebasechatappjetpackcompose.ui.theme.FirebaseChatAppJetpackComposeTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var firebaseUtils: FirebaseUtils
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // checks weather account is created and also profile is created on firestore
        // if yes then auto log in
        if(firebaseUtils.checkIfCurrenUserLoggedIn() && firebaseUtils.checkIfAccountIsCreated()) {
            launchHomeActivity()
        }
        enableEdgeToEdge()
        setContent {
            FirebaseChatAppJetpackComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginScreen(innerPadding,firebaseUtils) {
                        launchHomeActivity()
                    }
                }
            }
        }
    }

    private fun launchHomeActivity() {
        startActivity(
            Intent(this@MainActivity,HomeActivity::class.java)
        )
        finish()
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FirebaseChatAppJetpackComposeTheme {
        Greeting("Android")
    }
}