package com.example.mapsapp.ui.screens

import android.R.color.white
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mapsapp.utils.AuthState
import com.example.mapsapp.utils.SharedPreferencesHelper
import com.example.mapsapp.viewmodels.AuthViewModel
import com.example.mapsapp.viewmodels.AuthViewModelFactory
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    navigateToSignUp: () -> Unit,
    navigateToHome: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(SharedPreferencesHelper(context))
    )

    val authState by viewModel.authState.observeAsState()
    val showError by viewModel.showError.observeAsState(false)
    val email by viewModel.email.observeAsState("")
    val password by viewModel.password.observeAsState("")

    if (authState == AuthState.Authenticated) {
        navigateToHome()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0XFF000113)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Text("Login", color = Color(0xFFFFFFFF), fontSize = 35.sp, fontWeight = FontWeight.Bold)
            if (showError) {
                val errorMessage = (authState as AuthState.Error).message
                if (errorMessage.contains("invalid_credentials")) {
                    Toast.makeText(context, "Invalid credentials", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "An error has ocurred", Toast.LENGTH_LONG).show()
                }
                viewModel.errorMessageShowed()
            }
            Spacer(modifier = Modifier.fillMaxHeight(0.1f))
            TextField(
                value = email,
                onValueChange = { viewModel.editEmail(it) },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth(0.8f),
                colors = TextFieldDefaults.colors(
                    unfocusedPlaceholderColor = Color(0XFF000113),
                    focusedPlaceholderColor = Color(0XFF000113),
                    unfocusedContainerColor = Color(0XFF000113),
                    focusedContainerColor = Color(0XFF000113)
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.fillMaxHeight(0.08f))
            TextField(
                value = password,
                onValueChange = { viewModel.editPassword(it) },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(0.8f),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = TextFieldDefaults.colors(
                    unfocusedPlaceholderColor = Color(0XFF000113),
                    focusedPlaceholderColor = Color(0XFF000113),
                    unfocusedContainerColor = Color(0XFF000113),
                    focusedContainerColor = Color(0XFF000113)
                )
            )
            Spacer(modifier = Modifier.fillMaxHeight(0.15f))

            Button(
                onClick = { viewModel.signIn() },
                modifier = Modifier.fillMaxWidth(0.5f).height(50.dp),
                enabled = email.isNotEmpty() && password.isNotEmpty()
            ) {
                Text("Iniciar sesión")
            }

            Spacer(modifier = Modifier.fillMaxHeight(0.2f))

            Row() {
                Text("No tienes cuenta?  ", color = Color(0xFFFFFFFF))
                Text(
                    "Regístrate",
                    modifier = Modifier.clickable(onClick = navigateToSignUp),
                    color = Color(0xFF79ADF2)
                )
            }
        }
    }
}
