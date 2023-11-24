package com.example.accidentdetectionapp.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.accidentdetectionapp.presentation.viewmodel.AuthenticationViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.accidentdetectionapp.domain.entity.LoginRequest

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    authViewModel: AuthenticationViewModel = hiltViewModel(), // This line is changed
    onNavigateToSignUp: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val isLoading by authViewModel.isLoading.collectAsState()
    val error by authViewModel.error.collectAsState()
    val loginSuccess by authViewModel.loginSuccess.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val keyboardController = LocalSoftwareKeyboardController.current
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(loginSuccess) {
        if (loginSuccess) {
            authViewModel.resetLoginSuccess()
            onLoginSuccess() // Navigate to dashboard// Reset the success state
        }
    }


    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Username") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                        authViewModel.login(LoginRequest(email,password))
                    })
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        keyboardController?.hide()
                        authViewModel.login(LoginRequest(email,password))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Login")
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = onNavigateToSignUp) {
                    Text("Don't have an account? Sign Up")
                }
            }

            error?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = it, color = Color.Red)
                // Clear the error after displaying it
                LaunchedEffect(it) {
                    authViewModel.clearError()
                }
            }
        }
    }
}