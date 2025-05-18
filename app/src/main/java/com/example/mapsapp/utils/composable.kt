package com.example.mapsapp.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun textFieldColors() = TextFieldDefaults.colors(
    unfocusedContainerColor = Color(0XFF000113),
    focusedContainerColor = Color(0XFF000113),
    unfocusedTextColor = Color.White,
    focusedTextColor = Color.White,
    unfocusedLabelColor = Color.White,
    focusedLabelColor = Color.White,
    cursorColor = Color.White
)


@Composable
fun Loading(text: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        Arrangement.Center,
        Alignment.CenterHorizontally
    ) {
        Text(text)
        CircularProgressIndicator(color = Color.White)
    }
}