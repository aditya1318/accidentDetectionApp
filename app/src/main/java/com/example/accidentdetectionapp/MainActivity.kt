package com.example.accidentdetectionapp
import android.Manifest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cloudinary.android.MediaManager
import com.example.accidentdetectionapp.presentation.screens.DashboardScreen
import com.example.accidentdetectionapp.presentation.screens.LoginScreen
import com.example.accidentdetectionapp.presentation.screens.RegisterScreen
import com.example.accidentdetectionapp.ui.theme.AccidentDetectionAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val config = HashMap<String, String>()
        config["cloud_name"] = "dgy5td0i8"
        config["api_key"] = "919234643189542"
        config["api_secret"] = "8Hu9FslByOMdAtJ10kU4wiNR3HM"
        MediaManager.init(this, config)


        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            // Check if all permissions are granted
            val allGranted = permissions.entries.all { it.value }
            if (allGranted) {
                // All permissions are granted, continue with the app
            } else {
                // Handle the case where one or more permissions are denied
                // You may want to show a dialog or a message to the user explaining why these permissions are necessary
            }
        }

        // Request permissions
        permissionLauncher.launch(arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ))

        setContent {
            val navController = rememberNavController()
            AccidentDetectionAppTheme {
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        LoginScreen(
                            onNavigateToSignUp = {
                                navController.navigate("register")
                            },
                            onLoginSuccess = {
                                navController.navigate("dashboard")
                            }
                        )
                    }
                    composable("register") {
                        RegisterScreen(onNavigateToLogin = {
                            navController.popBackStack() // This will navigate back to the previous screen in the stack
                        })
                    }
                    composable("dashboard") {
                        DashboardScreen(

                        )
                    }


                }
            }
        }
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
    AccidentDetectionAppTheme {
        Greeting("Android")
    }
}