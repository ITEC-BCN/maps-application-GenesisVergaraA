package com.example.mapsapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.TextField

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mapsapp.utils.SharedPreferencesHelper
import com.example.mapsapp.viewmodels.AuthViewModel
import com.example.mapsapp.viewmodels.AuthViewModelFactory
import com.example.mapsapp.utils.AuthState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navigateToSignIn: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(SharedPreferencesHelper(context))
    )

    val authState by viewModel.authState.observeAsState()
    val showError by viewModel.showError.observeAsState(false)
    val email by viewModel.email.observeAsState("")
    val password by viewModel.password.observeAsState("")


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0XFF000113)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Regístrate", color = Color.White, fontSize = 30.sp)
        Spacer(modifier = Modifier.fillMaxHeight(0.1f))
        if (showError) {
            val error = (authState as? AuthState.Error)?.message ?: ""
            when {
                error.contains("invalid_credentials") -> {
                    Toast.makeText(context, "Credenciales inválidas", Toast.LENGTH_LONG).show()
                }

                error.contains("weak_password") -> {
                    Toast.makeText(
                        context,
                        "La contraseña debe tener al menos 6 caracteres",
                        Toast.LENGTH_LONG
                    ).show()
                }

                else -> {
                    Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show()
                }
            }
            viewModel.errorMessageShowed()
        }
        TextField(
            value = email,
            onValueChange = { viewModel.editEmail(it) },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(0.8f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = TextFieldDefaults.colors(
                unfocusedPlaceholderColor = Color(0XFF000113),
                focusedPlaceholderColor = Color(0XFF000113),
                unfocusedContainerColor = Color(0XFF000113),
                focusedContainerColor = Color(0XFF000113)
            )
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

        Spacer(modifier = Modifier.fillMaxHeight(0.08f))

        Button(
            onClick = { viewModel.signUp() },
            modifier = Modifier.fillMaxWidth(0.8f).height(50.dp),
            enabled = email.isNotEmpty() && password.length >= 6
        ) {
            Text("Registrarse")
        }

        Spacer(modifier = Modifier.fillMaxHeight(0.2f))


        Row(modifier = Modifier.fillMaxWidth(0.7f), Arrangement.SpaceEvenly) {
            Text("¿Ya tienes cuenta?", color = Color(0xFFFFFFFF))
            Text(
                "Inicia Sesión",
                modifier = Modifier.clickable(onClick = navigateToSignIn),
                color = Color(0xFF79ADF2)
            )
        }
    }


}

