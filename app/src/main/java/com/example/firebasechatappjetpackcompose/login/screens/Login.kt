package com.example.firebasechatappjetpackcompose.login.screens

import android.Manifest
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firebasechatappjetpackcompose.R
import com.example.firebasechatappjetpackcompose.Utils.FirebaseUtils
import com.example.firebasechatappjetpackcompose.Utils.Utils
import com.example.firebasechatappjetpackcompose.Utils.Utils.checkIfEmailIsValid
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LoginScreen(innerPadding: PaddingValues,firebaseUtils : FirebaseUtils, isAccountLoginSuccess : (Boolean) -> Unit) {
    val TAG = "LoginScreen"
    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_MEDIA_IMAGES
        )
    } else {
        listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }
    var imageUri by remember { mutableStateOf<String?>(null) }
    val permissionsState = rememberMultiplePermissionsState(permissions = permissions)
    var requestPermissions by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri.toString().also {
            Log.d(TAG, "LoginScreen: imageUri :  $imageUri")
            imageUri = it 
        }
    }
    var showAccountCreatingDialog by remember { mutableStateOf(false) }
    var showCreateAccountButton by remember { mutableStateOf(false) }
    var emailText by remember { mutableStateOf(TextFieldValue("")) }
    var passwordText by remember { mutableStateOf(TextFieldValue("")) }
    var usernameText by remember { mutableStateOf(TextFieldValue("")) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .imePadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val context = LocalContext.current
        Surface(
            modifier = Modifier
                .width(200.dp)
                .height(200.dp)
        ) {
            CircularImage(imageUri) {
                launcher.launch("image/*")
            }
        }

        TextField(
            value = emailText,
            onValueChange = { emailTexts ->
                emailText = emailTexts
                checkIfCreateAccountButtonToBeShown(emailText, passwordText, usernameText) {
                    showCreateAccountButton = it
                }
            },
            label = { Text("Enter Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        )
        TextField(
            value = usernameText,
            onValueChange = { username ->
                usernameText = username
                checkIfCreateAccountButtonToBeShown(emailText, passwordText, usernameText) {
                    showCreateAccountButton = it
                }
            },
            label = { Text("Enter Username") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        )
        TextField(
            value = passwordText,
            onValueChange = { password ->
                passwordText = password
                checkIfCreateAccountButtonToBeShown(emailText, passwordText, usernameText) {
                    showCreateAccountButton = it
                }
            },
            visualTransformation = if(isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image : Painter = if(isPasswordVisible)
                    painterResource(id = R.drawable.baseline_visibility_24)
                else
                    painterResource(id = R.drawable.baseline_visibility_off_24)

                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(painter = image, contentDescription = null)
                }
            },
            label = { Text("Enter Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        )
        if(showAccountCreatingDialog)
            AccountAlertDialog()
        Spacer(modifier = Modifier.height(25.dp))

        AnimatedVisibility(
            showCreateAccountButton,
            enter = slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth }
            ),
            exit = fadeOut(
                targetAlpha = 0f
            )
        ) {
            TextButton(onClick = {
                /**
                 * Call it after sometime
                 * directly launching will lead to app crash
                 * launch after user clicks something permissions
                 */
                when {
                    permissionsState.allPermissionsGranted -> {
                        showAccountCreatingDialog = true
                        firebaseUtils.checkIfUsernameAlreadyExists(usernameText.text) {
                            isUsernameAlreadyTaken ->
                            // if username is already taken then don't proceed
                            if(isUsernameAlreadyTaken) {
                                Toast.makeText(context,"Username already taken",Toast.LENGTH_SHORT).show()
                                showAccountCreatingDialog = false
                                return@checkIfUsernameAlreadyExists
                            }
                            if(checkIfEmailIsValid(emailText.text) && Utils.isPasswordLengthValid(passwordText.text) ) {
                                firebaseUtils.createChatAccount(emailText.text,passwordText.text,usernameText.text,imageUri) { isSuccessOrFailure ->
                                    if(isSuccessOrFailure) {
                                        //launch home activity
                                        showCreateAccountButton = false
                                        showAccountCreatingDialog = false
                                        isAccountLoginSuccess(true)
                                    }
                                }
                            } else {
                                showAccountCreatingDialog = false
                                Log.d(TAG, "LoginScreen: invalid email address or password length is too short")
                                Toast.makeText(context, "Invalid email or password", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    permissionsState.shouldShowRationale -> {
                        permissionsState.launchMultiplePermissionRequest()
                    }

                    else -> {
                        permissionsState.launchMultiplePermissionRequest()
                    }
                }
            }) {
                Row {

                    Text("Create Account", fontSize = 20.sp)
                }

            }
        }

    }

}

fun checkIfCreateAccountButtonToBeShown(
    emailTexts: TextFieldValue,
    passwordText: TextFieldValue,
    usernameText: TextFieldValue,
    isCreateAccountButtonToBeShown: (Boolean) -> Unit
) {
    if (emailTexts.text.isNotEmpty() && passwordText.text.isNotEmpty() && usernameText.text.isNotEmpty())
        isCreateAccountButtonToBeShown(true)
    else
        isCreateAccountButtonToBeShown(false)
}
