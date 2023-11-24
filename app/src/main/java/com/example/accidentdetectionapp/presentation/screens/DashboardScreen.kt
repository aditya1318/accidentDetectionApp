        package com.example.accidentdetectionapp.presentation.screens

        import android.Manifest
        import android.annotation.SuppressLint
        import android.content.Context
        import android.content.pm.PackageManager
        import android.graphics.Bitmap
        import android.graphics.fonts.FontFamily
        import android.location.Location
        import android.net.Uri
        import android.os.Environment
        import android.provider.MediaStore
        import android.provider.OpenableColumns
        import android.util.Log
        import androidx.activity.compose.rememberLauncherForActivityResult
        import androidx.activity.result.contract.ActivityResultContracts
        import androidx.compose.foundation.Image
        import androidx.compose.foundation.background
        import androidx.compose.foundation.clickable
        import androidx.compose.foundation.layout.*
        import androidx.compose.foundation.shape.RoundedCornerShape
        import androidx.compose.material.*
        import androidx.compose.material.SnackbarDefaults.backgroundColor
        import androidx.compose.material.icons.Icons
        import androidx.compose.material.icons.filled.ArrowDropDown
        import androidx.compose.material.icons.filled.CameraAlt
        import androidx.compose.runtime.*
        import androidx.compose.ui.Alignment
        import androidx.compose.ui.Modifier
        import androidx.compose.ui.graphics.Color
        import androidx.compose.ui.platform.LocalContext
        import androidx.compose.ui.res.painterResource
        import androidx.compose.ui.text.font.FontWeight
        import androidx.compose.ui.text.font.FontWeight.Companion.Bold
        import androidx.compose.ui.text.style.TextAlign
        import androidx.compose.ui.unit.dp
        import androidx.compose.ui.window.Dialog
        import androidx.core.app.ActivityCompat
        import androidx.core.content.FileProvider
        import androidx.documentfile.provider.DocumentFile
        import androidx.hilt.navigation.compose.hiltViewModel
        import com.example.accidentdetectionapp.R
        import com.example.accidentdetectionapp.domain.entity.AnalysisResult
        import com.example.accidentdetectionapp.domain.entity.CreateAccidentRequest
        import com.example.accidentdetectionapp.domain.entity.EmergencyService
        import com.example.accidentdetectionapp.domain.entity.ServiceType
        import com.example.accidentdetectionapp.domain.entity.UserLocation
        import com.example.accidentdetectionapp.presentation.viewmodel.AccidentViewModel
        import com.example.accidentdetectionapp.presentation.viewmodel.TensorFlowViewModel
        import com.example.accidentdetectionapp.utils.SessionManager
        import com.google.android.gms.location.LocationServices
        import java.io.File
        import java.text.SimpleDateFormat
        import java.util.Date
        import java.util.Locale

        @SuppressLint("UnusedMaterialScaffoldPaddingParameter", "StateFlowValueCalledInComposition")
        @Composable
        fun DashboardScreen(
            tensorFlowViewModel: TensorFlowViewModel = hiltViewModel(),
            accidentViewModel: AccidentViewModel = hiltViewModel(),

        ) {
            var accidentStatus by remember { mutableStateOf("") }

            val context = LocalContext.current
            var currentPhotoPath by remember { mutableStateOf("") } // Define currentPhotoPath here

            val sessionManager = remember { SessionManager(context) }

            var imageUri by remember { mutableStateOf<Uri?>(null) }
            var showCamera by remember { mutableStateOf(false) }
            val launcher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success && imageUri != null) {
                    Log.d("DashboardScreen", "Image captured, URI: $imageUri")
                    val bitmap = uriToBitmap(context, imageUri!!)
                    tensorFlowViewModel.analyzeImage(bitmap)
                } else {
                    Log.d("DashboardScreen", "Image capture failed or URI is null")
                }
            }

            val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
            var currentLocation by remember { mutableStateOf<Location?>(null) }
            var showDialog by remember { mutableStateOf(false) }
            var analysisCompleted by remember { mutableStateOf(false) }
            var analysisResultState by remember { mutableStateOf<AnalysisResult?>(null) }

            LaunchedEffect(Unit) {
                try {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                            currentLocation = location
                        }
                    } else {
                        // Request permissions
                    }
                } catch (e: Exception) {
                    // Handle exceptions
                }
            }

            val analysisResult by tensorFlowViewModel.analysisResult.collectAsState()
            LaunchedEffect(analysisResult) {
                analysisResult?.let {
                    Log.d("result", "Label: ${it.label}")
                    if (it.label == "0 Non-Accident") {
                        accidentStatus = "Not Accident"
                    } else {
                        showDialog = true
                        accidentStatus = "Accident Detected"
                    }
                }
            }


            val cameraPermissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    showCamera = true
                    val file = createImageFile(context)
                    imageUri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.provider",
                        file
                    )
                    currentPhotoPath = file.absolutePath // Update currentPhotoPath
                    launcher.launch(imageUri)
                }
            }

            val isLoading by tensorFlowViewModel.isLoading.collectAsState()
            val error by tensorFlowViewModel.error.collectAsState()

            ShowAccidentReportDialogIfNeeded(showDialog, onDismiss = {
                showDialog = false
            }, onSubmit = { description, emergencyServices -> // Remove closeDialog from here

                val token = sessionManager.fetchAuthToken() ?: ""
                val userId = sessionManager.fetchUserDetails()?._id ?: "UserID"
                Log.d("submit func", "called $token $userId")

                val imagePath = currentPhotoPath
                Log.d("DashboardScreen", "Converted image path: $imagePath")
                val createAccidentRequest = CreateAccidentRequest(
                    reportedBy = userId,
                    location = UserLocation(
                        coordinates = listOf(
                            currentLocation?.longitude ?: 0.0,
                            currentLocation?.latitude ?: 0.0
                        )

                    ),
                    photo = imagePath,
                    description = description,
                    emergencyServicesNotified = emergencyServices,
                    token = token
                )
                accidentViewModel.createAccident(createAccidentRequest)
            })


            Scaffold(
                topBar = { DashboardTopBar() },
                content = {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        DashboardContent(
                            onCameraClick = {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            },
                                    accidentStatus = accidentStatus


                        )

                        if (isLoading) {
                            CircularProgressIndicator()
                        }

                        error?.let {
                            Log.e("DashboardScreen", "Error during analysis: $it")
                            Text("Error: $it", color = Color.Red)
                        }
                    }
                }
            )


        }
        fun createImageFile(context: Context): File {
            // Create an image file name
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
            )
        }



        // ... rest of the code for DashboardTopBar, DashboardContent, uriToBitmap, createImageFile, and AccidentReportDialog ...


        @Composable
        fun DashboardTopBar() {
            TopAppBar(
                title = { Text("Dashboard", color = Color.White) },
                backgroundColor = Color.Blue,
                contentColor = Color.White
            )
        }

        @Composable
        fun DashboardContent(onCameraClick: () -> Unit,accidentStatus: String) {
            val pastelBackgroundColor = Color(0xFFFFE5CE)
            val statusText = if (accidentStatus == "Not Accident") {
                "The analyzed image does not indicate an accident. Please try again if needed."
            } else if(accidentStatus == "Accident Detected") {
                "Accident detected in the image. Proceed with caution and necessary actions."
            } else {
                "Awaiting Image"
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()

                    .background(pastelBackgroundColor), // Set the background color here
            horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Your Volcano Status Card
                Box(
                    modifier = Modifier.fillMaxWidth().background(pastelBackgroundColor),


                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Replace with actual image resource
                        Image(
                            painter = painterResource(id = R.drawable.carsaccident),
                            contentDescription = "Volcano Status"
                        )
                        Text(
                            text = "Accident Status",
                            style = MaterialTheme.typography.subtitle1
                        )
                        Button(
                            onClick = { /* TODO: Handle status click */ },
                            colors = if (accidentStatus == "Not Accident") {
                                ButtonDefaults.buttonColors(backgroundColor = Color.Green)
                            } else if(accidentStatus == "Accident Detected") {
                                ButtonDefaults.buttonColors(backgroundColor = Color.Red)
                            }else{
                                ButtonDefaults.buttonColors(backgroundColor = Color.LightGray)

                            },
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .fillMaxWidth(0.4f),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(text = accidentStatus, color =Color.White , fontWeight= FontWeight.Bold)
                        }
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.body2,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                // Report Incident Button
                Button(
                    onClick = onCameraClick,
                    modifier = Modifier
                        .fillMaxWidth(0.5F)
                        .height(50.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
                ) {
                    Icon(Icons.Filled.CameraAlt, contentDescription = "Camera")
                    Spacer(Modifier.width(8.dp))
                    Text("Report Incident")
                }
            }
        }


        fun uriToBitmap(context: Context, uri: Uri): Bitmap {
            return MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }


        fun uriToFilePath(context: Context, uri: Uri): String {
            var filePath: String = ""
            if (uri.scheme.equals("content")) {
                val cursor = context.contentResolver.query(uri, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        // Use DocumentFile for better handling of different types of URIs
                        val documentFile = DocumentFile.fromSingleUri(context, uri)
                        filePath = documentFile?.uri?.path ?: ""
                    }
                }
            } else if (uri.scheme.equals("file")) {
                filePath = uri.path ?: ""
            }
            return filePath
        }






        @Composable
        fun AccidentReportDialog(
            onDismiss: () -> Unit,
            onSubmit: (String, List<EmergencyService>) -> Unit
        ) {
            var description by remember { mutableStateOf("") }
            var expanded by remember { mutableStateOf(false) }
            var selectedServiceType by remember { mutableStateOf(ServiceType.police) }
            val serviceTypes = ServiceType.values()

            Dialog(onDismissRequest = onDismiss) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 50.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colors.surface
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Report Accident", style = MaterialTheme.typography.h6)

                        Spacer(modifier = Modifier.height(16.dp))

                        // Description TextField
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Dropdown for Service Type
                        OutlinedTextField(
                            value = selectedServiceType.name,
                            onValueChange = { },
                            label = { Text("Service Type") },
                            trailingIcon = { Icon(Icons.Filled.ArrowDropDown, "Dropdown") },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth().clickable { expanded = true }
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            serviceTypes.forEach { serviceType ->
                                DropdownMenuItem(onClick = {
                                    selectedServiceType = serviceType
                                    expanded = false
                                }) {
                                    Text(serviceType.name)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Submit Button
                        Button(
                            onClick = {
                                onSubmit(description, listOf(EmergencyService(selectedServiceType)))
                                onDismiss() // Directly use onDismiss here to close the dialog
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
                        ) {
                            Text("Submit Report", color = Color.White)
                        }
                    }
                }
            }
        }
        @Composable
        fun ShowAccidentReportDialogIfNeeded(
            showDialog: Boolean,
            onDismiss: () -> Unit,
            onSubmit: (String, List<EmergencyService>) -> Unit
        ) {
            if (showDialog) {
                AccidentReportDialog(onDismiss = onDismiss, onSubmit = onSubmit)
            }
        }