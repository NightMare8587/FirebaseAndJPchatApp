package com.example.firebasechatappjetpackcompose.login.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.firebasechatappjetpackcompose.R


@Composable
fun CircularImage(imageUri : String?, onClick: () -> Unit) {
    val TAG = "CircularImage"
    Log.d(TAG, "CircularImage: $imageUri")
    Card(
        shape = CircleShape,
        modifier = Modifier
            .size(200.dp)
            .clip(CircleShape)
            .clickable { onClick() }
            .background(Color.Gray),

        elevation = CardDefaults.cardElevation(8.dp)
    ) {

        if(imageUri != null) {
            Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = null,
                modifier = Modifier.size(200.dp),
                contentScale = ContentScale.FillBounds
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = "Default Image",
                modifier = Modifier.size(200.dp),
                contentScale = ContentScale.FillBounds
            )
        }
    }

}