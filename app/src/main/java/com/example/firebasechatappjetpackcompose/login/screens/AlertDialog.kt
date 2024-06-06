package com.example.firebasechatappjetpackcompose.login.screens

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountAlertDialog() {
    AlertDialog(
        onDismissRequest = {

        }
    ) {
        Surface(
            modifier = Modifier.width(200.dp).height(200.dp)
            , shape = MaterialTheme.shapes.large
        ) {
            Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text("Creating account for you in 4..3..2..1", fontSize = 18.sp, textAlign = TextAlign.Center)
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileAlertDialog(imageUri : Uri, context : Context, dismissDialog : () -> Unit) {
    AlertDialog(onDismissRequest = {
        dismissDialog.invoke()
    }, properties = DialogProperties()) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(
                model = ImageRequest.Builder(context).data(imageUri).build(),
                contentDescription = "",
                modifier = Modifier.width(300.dp).height(300.dp)
            )
        }
    }
}