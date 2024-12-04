package org.technoserve.cafetrac.ui.components

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

import com.example.cafetrac.ui.screens.farms.LocationPermissionRequest
import com.example.cafetrac.ui.screens.farms.addFarm
import com.example.cafetrac.ui.screens.farms.createImageFile
import com.example.cafetrac.ui.screens.farms.formatInput
import com.example.cafetrac.ui.screens.farms.isLocationEnabled
import com.example.cafetrac.ui.screens.farms.promptEnableLocation
import com.example.cafetrac.ui.screens.farms.readStoredValue
import com.example.cafetrac.ui.screens.farms.toLatLngList
import com.example.cafetrac.ui.screens.farms.truncateToDecimalPlaces
import com.example.cafetrac.ui.screens.farms.validateSize
import com.example.cafetrac.utils.convertSize
import com.example.cafetrac.utils.hasLocationPermission
import org.technoserve.cafetrac.utils.map.getCenterOfPolygon
import org.technoserve.cafetrac.viewmodels.FarmViewModel
import org.technoserve.cafetrac.viewmodels.FarmViewModelFactory
import org.technoserve.cafetrac.viewmodels.MapViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLngBounds
import org.joda.time.Instant
import org.technoserve.cafetrac.ui.components.GenderDropdown
import org.technoserve.cafetrac.ui.components.ImagePicker
import org.technoserve.cafetrac.ui.components.isSystemInDarkTheme
import org.technoserve.cafetraorg.technoserve.cafetrac.R
import java.io.IOException
import java.io.InputStream
import java.util.Objects
import java.util.UUID
import java.util.regex.Pattern


/**
 *
 * FarmForm.kt
 *
 */

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun FarmForm(
    navController: NavController,
    siteId: Long,
    coordinatesData: List<Pair<Double, Double>>?,
    accuracyArrayData: List<Float?>?
) {
    val context = LocalContext.current as Activity
    var isValid by remember { mutableStateOf(true) }
    var farmerName by rememberSaveable { mutableStateOf("") }
    var memberId by rememberSaveable { mutableStateOf("") }
    var farmerPhoto by rememberSaveable { mutableStateOf("") }
    var village by rememberSaveable { mutableStateOf("") }
    var district by rememberSaveable { mutableStateOf("") }

    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var govtIdNumber by remember { mutableStateOf("") }
    var numberOfTrees by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var photo by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    var latitude by rememberSaveable { mutableStateOf("") }
    var longitude by rememberSaveable { mutableStateOf("") }
    var accuracyArray by rememberSaveable { mutableStateOf(listOf<Float>()) }
    val items = listOf("Ha", "Acres", "Sqm", "Timad", "Fichesa", "Manzana", "Tarea")
    var expanded by remember { mutableStateOf(false) }
//    var selectedUnit by remember { mutableStateOf(items[0]) }
    val sharedPref = context.getSharedPreferences("FarmCollector", Context.MODE_PRIVATE)

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val farmViewModel: FarmViewModel = viewModel(
        factory = FarmViewModelFactory(context.applicationContext as Application)
    )

    val mapViewModel: MapViewModel = viewModel()
    // Read initial value from SharedPreferences
    var size by rememberSaveable { mutableStateOf(readStoredValue(sharedPref)) }
    var selectedUnit by rememberSaveable { mutableStateOf(sharedPref.getString("selectedUnit", items[0]) ?: items[0]) }
    var isValidSize by remember { mutableStateOf(true) }
    var isFormSubmitted by remember { mutableStateOf(false) }
    // Regex pattern to check for scientific notation
    val scientificNotationPattern = Pattern.compile("([+-]?\\d*\\.?\\d+)[eE][+-]?\\d+")

    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        context.packageName + ".provider", file
    )
    val showDialog = remember { mutableStateOf(false) }
    val showLocationDialog = remember { mutableStateOf(false) }
    val showLocationDialogNew = remember { mutableStateOf(false) }

    // Function to update the selected unit
    fun updateSelectedUnit(newUnit: String) {
        selectedUnit = newUnit
        sharedPref.edit().putString("selectedUnit", newUnit).apply()
    }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Access location services
            } else {
                // Handle the denied permission
                Toast.makeText(
                    context,
                    "Location permission is required to access this feature.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    fun fetchLocationAndNavigate() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000 // Update interval in milliseconds
            fastestInterval = 5000 // Fastest update interval in milliseconds
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.lastLocation?.let { lastLocation ->
                        // Handle the new location
                        latitude = "${lastLocation.latitude}"
                        longitude = "${lastLocation.longitude}"

                        // Navigate to 'setPolygon' if conditions are met
                        navController.currentBackStackEntry?.arguments?.putParcelable(
                            "farmData",
                            null
                        )
                        navController.navigate("setPolygon")
                        mapViewModel.clearCoordinates()
                    }
                }
            },
            Looper.getMainLooper()
        )
    }


    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // Update the value from SharedPreferences when the screen is resumed
                size = sharedPref.getString("plot_size", "") ?: ""
                selectedUnit = sharedPref.getString("selectedUnit", "Ha") ?: "Ha"
//                delete plot_size from sharedPreference
                with(sharedPref.edit()) {
                    remove("plot_size")
                    remove("selectedUnit")
                    apply()
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (showLocationDialog.value) {
        AlertDialog(
            onDismissRequest = { showLocationDialog.value = false },
            title = { Text(stringResource(id = R.string.enable_location)) },
            text = { Text(stringResource(id = R.string.enable_location_msg)) },
            confirmButton = {
                Button(onClick = {
                    showLocationDialog.value = false
                    promptEnableLocation(context)
                }) {
                    Text(stringResource(id = R.string.yes))
                }
            },
            dismissButton = {
                Button(onClick = {
                    showLocationDialog.value = false
                    Toast.makeText(
                        context,
                        R.string.location_permission_denied_message,
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                    Text(stringResource(id = R.string.no))
                }
            },
            containerColor = MaterialTheme.colorScheme.background, // Background that adapts to light/dark
            tonalElevation = 6.dp // Adds a subtle shadow for better UX
        )
    }

    fun saveFarm() {
        // convert selectedUnit to hectares
        val sizeInHa = convertSize(size.toDouble(), selectedUnit)
        // Add farm
        // Generating a UUID for a new farm before saving it
        val newUUID = UUID.randomUUID()

        val coordinatesSize = coordinatesData?.size ?: 0 // Safely get the size of coordinates, or 0 if null

        Log.d("coordinatesSize", "coordinatesSize : $coordinatesSize")


        val finalAccuracyArray = when {
            accuracyArray.isEmpty() -> emptyList()
            coordinatesSize == 0 -> listOf(accuracyArray[0])
            else -> {
                val result = accuracyArrayData!!.toMutableList()
                if (coordinatesSize > 1) {
                    result.add(accuracyArrayData.last())
                }
                result
            }
        }

        Log.d("Accuracy Array Before", "Accuracy Array Before Saving The farm is set to : $accuracyArray")

        Log.d("finalAccuracyArray", "finalAccuracyArray is set to : $finalAccuracyArray")


        addFarm(
            farmViewModel,
            siteId,
            remote_id = newUUID,
            farmerPhoto,
            farmerName,
            memberId,
            village,
            district,
            0.toFloat(),
            sizeInHa.toFloat(),
            latitude,
            longitude,
            coordinates = coordinatesData?.plus(coordinatesData.first()),
            accuracyArray = finalAccuracyArray,
            age = age.toInt(),  // Default value if null
            gender = gender ?: "",  // Default value if null
            govtIdNumber = govtIdNumber ?: "",  // Default value if null
            numberOfTrees = numberOfTrees.toInt(),  // Default value if null
            phone = phone ?: "",  // Default value if null
            photo = photo ?: "",  // Default value if null
        )
        val returnIntent = Intent()
        context.setResult(Activity.RESULT_OK, returnIntent)
//                    context.finish()
        navController.navigate("farmList/${siteId}")
    }



    if (showDialog.value) {
        AlertDialog(
            modifier = Modifier.padding(horizontal = 32.dp),
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = stringResource(id = R.string.add_farm)) },
            text = {
                Column {
                    Text(text = stringResource(id = R.string.confirm_add_farm))
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    saveFarm()
                }) {
                    Text(text = stringResource(id = R.string.add_farm))
                }
            },
            dismissButton = {
                TextButton(onClick =
                {
                    showDialog.value = false
                    navController.navigate("setPolygon")
                }) {
                    Text(text = stringResource(id = R.string.set_polygon))
                }
            },
            containerColor = MaterialTheme.colorScheme.background, // Background that adapts to light/dark
            tonalElevation = 6.dp // Adds a subtle shadow for better UX
        )
    }

    fun validateForm(): Boolean {
        isValid = true
        if (farmerName.isBlank()) {
            isValid = false
            // You can display an error message for this field if needed
        }

        if (village.isBlank()) {
            isValid = false
            // You can display an error message for this field if needed
        }

        if (district.isBlank()) {
            isValid = false
            // You can display an error message for this field if needed
        }

        if (size.isBlank() || size.toFloatOrNull() == null || size.toFloat() <= 0) {
            isValid = false
            // You can display an error message for this field if needed
        }

        if (selectedUnit.isBlank()) {
            isValid = false
            // You can display an error message for this field if needed
        }

        if (latitude.isBlank() || longitude.isBlank()) {
            isValid = false
            // You can display an error message for these fields if needed
        }

        return isValid
    }

    val scrollState = rememberScrollState()
    val permissionGranted = stringResource(id = R.string.permission_granted)
    val permissionDenied = stringResource(id = R.string.permission_denied)
    val fillForm = stringResource(id = R.string.fill_form)

    val showPermissionRequest = remember { mutableStateOf(false) }

    var imageInputStream: InputStream? = null
    val resultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val treeUri = result.data?.data

                if (treeUri != null) {
                    val takeFlags =
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    context.contentResolver.takePersistableUriPermission(treeUri, takeFlags)

                    // Now, you have permission to write to the selected directory
                    val imageFileName = "image${Instant.now().millis}.jpg"

                    val selectedDir = DocumentFile.fromTreeUri(context, treeUri)
                    val imageFile = selectedDir?.createFile("image/jpeg", imageFileName)

                    imageFile?.uri?.let { fileUri ->
                        try {
                            imageInputStream?.use { input ->
                                context.contentResolver.openOutputStream(fileUri)?.use { output ->
                                    input.copyTo(output)
                                }
                            }

                            // Update the database with the file path
                            farmerPhoto = fileUri.toString()
                            // Update other fields in the Farm object
                            // Then, insert or update the farm object in your database
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
            uri?.let { it1 ->
                imageInputStream = context.contentResolver.openInputStream(it1)

                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                resultLauncher.launch(intent)
            }
        }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            Toast.makeText(context, permissionGranted, Toast.LENGTH_SHORT).show()
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, permissionDenied, Toast.LENGTH_SHORT).show()
        }
    }

    val (focusRequester1) = FocusRequester.createRefs()
    val (focusRequester2) = FocusRequester.createRefs()
    val (focusRequester3) = FocusRequester.createRefs()

    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color.Black else Color.White
    val inputLabelColor = if (isDarkTheme) Color.LightGray else Color.DarkGray
    val inputTextColor = if (isDarkTheme) Color.White else Color.Black
    val buttonColor = if (isDarkTheme) Color.Black else Color.White
    val inputBorder = if (isDarkTheme) Color.LightGray else Color.DarkGray

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(16.dp)
            .verticalScroll(state = scrollState)
    ) {
        OutlinedTextField(
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { focusRequester1.requestFocus() }
            ),
            value = farmerName,
            onValueChange = { farmerName = it },
            label = { Text(stringResource(id = R.string.farm_name) + " (*)",color = inputLabelColor) },
            supportingText = { if (!isValid && farmerName.isBlank()) Text(stringResource(R.string.error_farmer_name_empty) + " (*)") },
            isError = !isValid && farmerName.isBlank(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White, // Set the container (background) color
                errorLeadingIconColor = Color.Red,
                cursorColor = inputTextColor,
                errorCursorColor = Color.Red,
                focusedBorderColor = inputBorder,
                unfocusedBorderColor = inputBorder,
                errorBorderColor = Color.Red
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
                .onKeyEvent {
                    if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                        focusRequester1.requestFocus()
                    }
                    false
                }
        )
        OutlinedTextField(
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { focusRequester1.requestFocus() }
            ),
            value = memberId,
            onValueChange = { memberId = it },
            label = { Text(stringResource(id = R.string.member_id),color = inputLabelColor) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
                .onKeyEvent {
                    if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                        focusRequester1.requestFocus()
                    }
                    false
                }
        )
        OutlinedTextField(
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { focusRequester2.requestFocus() }
            ),
            value = village,
            onValueChange = { village = it },
            label = { Text(stringResource(id = R.string.village) + " (*)",color = inputLabelColor) },
            supportingText = { if (!isValid && village.isBlank()) Text(stringResource(R.string.error_village_empty)) },
            isError = !isValid && village.isBlank(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White, // Set the container (background) color
                errorLeadingIconColor = Color.Red,
                cursorColor = inputTextColor,
                errorCursorColor = Color.Red,
                focusedBorderColor = inputBorder,
                unfocusedBorderColor = inputBorder,
                errorBorderColor = Color.Red
            ),
            modifier = Modifier
                .focusRequester(focusRequester1)
                .fillMaxWidth()
                .padding(bottom =4.dp)
        )
        OutlinedTextField(
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { focusRequester3.requestFocus() }
            ),
            value = district,
            onValueChange = { district = it },
            label = { Text(stringResource(id = R.string.district) + " (*)", color =inputLabelColor) },
            supportingText = { if (!isValid && district.isBlank()) Text(stringResource(R.string.error_district_empty)) },
            isError = !isValid && district.isBlank(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White, // Set the container (background) color
                errorLeadingIconColor = Color.Red,
                cursorColor = inputTextColor,
                errorCursorColor = Color.Red,
                focusedBorderColor = inputBorder,
                unfocusedBorderColor = inputBorder,
                errorBorderColor = Color.Red
            ),
            modifier = Modifier
                .focusRequester(focusRequester2)
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )

        // Age
        OutlinedTextField(
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            value = age,
            onValueChange = { age = it },
            label = { Text("Age", color = inputLabelColor) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = backgroundColor,
                cursorColor = inputTextColor,
                focusedBorderColor = inputBorder,
                unfocusedBorderColor = inputBorder
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )

        // Gender (Dropdown)
        GenderDropdown(gender = gender, onGenderSelected = { selectedGender ->
            gender = selectedGender
        })

        // Govt ID Number
        OutlinedTextField(
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            value = govtIdNumber,
            onValueChange = { govtIdNumber = it },
            label = { Text("Govt ID Number", color = inputLabelColor) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = backgroundColor,
                cursorColor = inputTextColor,
                focusedBorderColor = inputBorder,
                unfocusedBorderColor = inputBorder
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )

        // Number of Trees
        OutlinedTextField(
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            value = numberOfTrees,
            onValueChange = { numberOfTrees = it },
            label = { Text("Number of Trees", color = inputLabelColor) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = backgroundColor,
                cursorColor = inputTextColor,
                focusedBorderColor = inputBorder,
                unfocusedBorderColor = inputBorder
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )

        // Phone
        OutlinedTextField(
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone", color = inputLabelColor) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = backgroundColor,
                cursorColor = inputTextColor,
                focusedBorderColor = inputBorder,
                unfocusedBorderColor = inputBorder
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )
        // Pick Image Button
        ImagePicker { uri ->
            photoUri = uri
            photo = uri?.toString() ?: ""
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                singleLine = true,
                value = truncateToDecimalPlaces(size,9),
                onValueChange = { inputValue ->
                    val formattedValue = when {
                        validateSize(inputValue) -> inputValue
                        // Check if the input is in scientific notation
                        scientificNotationPattern.matcher(inputValue).matches() -> {
                            truncateToDecimalPlaces(formatInput(inputValue),9)
                        }
                        else -> inputValue
                    }

                    // Update the size state with the formatted value
                    size = formattedValue
                    isValidSize = validateSize(formattedValue)

                    // Save to SharedPreferences or perform other actions
                    with(sharedPref.edit()) {
                        putString("plot_size", formattedValue)
                        apply()
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                label = {
                    Text(
                        text = stringResource(id = R.string.size_in_hectares) + " (*)",
                        color = inputLabelColor
                    )
                },
                supportingText = {
                    when {
                        isFormSubmitted && size.isBlank() -> {
                            Text(stringResource(R.string.error_farm_size_empty))
                        }
                        isFormSubmitted && !isValidSize -> {
                            Text(stringResource(R.string.error_farm_size_invalid))
                        }
                    }
                },
                isError = isFormSubmitted && (!isValidSize || size.isBlank()),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White, // Set the container (background) color
                    errorLeadingIconColor = Color.Red,
                    cursorColor = inputTextColor,
                    errorCursorColor = Color.Red,
                    focusedBorderColor = inputBorder,
                    unfocusedBorderColor = inputBorder,
                    errorBorderColor = Color.Red
                ),
                modifier = Modifier
                    .focusRequester(focusRequester3)
                    .fillMaxWidth(0.5f)  // Explicitly set to half width
                    .padding(end = 8.dp)
            )
            Box(
                modifier = Modifier.weight(1f)  // Ensure the dropdown container takes half the width
            ) {
                // Size measure
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = selectedUnit,
                        onValueChange = { },
                        label = { Text(stringResource(R.string.unit)) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = expanded
                            )
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White,
                        ),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        items.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(text = selectionOption) },
                                onClick = {
                                    //selectedUnit = selectionOption
                                    updateSelectedUnit(selectionOption)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // If coordinatesData exists and latitude/longitude are empty, calculate the center
        if (coordinatesData?.isNotEmpty() == true && latitude.isBlank() && longitude.isBlank()) {
            val center = coordinatesData.toLatLngList().getCenterOfPolygon()
            val bounds: LatLngBounds = center
            longitude = bounds.northeast.longitude.toString()
            latitude = bounds.southwest.latitude.toString()
            // Show an overview of the polygon captured, if needed.
        }


        if ((size.toDoubleOrNull()?.let { convertSize(it, selectedUnit).toFloat() } ?: 0f) < 4f) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = latitude,
                    onValueChange = {
                        val parts = it.split(".")
                        if (parts.size == 2 && parts.last().length == 5 ) {
                            val decimalPlaces = parts.last().length
                            val requiredZeros = 6 - decimalPlaces
                            // Append the required number of zeros
                            val formattedLatitude = it.padEnd(it.length + requiredZeros, '0')
                            latitude = formattedLatitude
                        } else if (parts.size == 2 && parts.last().length >= 6) {
                            latitude = it
                        } else {
                            Toast.makeText(
                                context,
                                R.string.error_latitude_decimal_places,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    label = { Text(stringResource(id = R.string.latitude) + " (*)",color = inputLabelColor) },
                    supportingText = {
                        if (!isValid && latitude.split(".").last().length < 6) Text(
                            stringResource(R.string.error_latitude_decimal_places)
                        )
                    },
                    isError = !isValid && latitude.split(".").last().length < 6,
                    colors = TextFieldDefaults.textFieldColors(
                        errorLeadingIconColor = Color.Red,
                        containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White, // Set the container (background) color
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 4.dp)
                )
                Spacer(modifier = Modifier.width(8.dp)) // Add space between the latitude and longitude input fields
                OutlinedTextField(
                    readOnly = true,
                    value = longitude,
                    onValueChange = {
                        val parts = it.split(".")
                        if (parts.size == 2) {
                            val decimalPlaces = parts.last().length
                            val formattedLongitude = if (decimalPlaces == 5 ) {
                                // Append the required number of zeros to the decimal part
                                it.padEnd(it.length + (6 - decimalPlaces), '0')
                            } else {
                                it
                            }
                            longitude = formattedLongitude
                        } else {
                            Toast.makeText(
                                context,
                                R.string.error_longitude_decimal_places,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    label = { Text(stringResource(id = R.string.longitude) + " (*)",color = inputLabelColor) },
                    supportingText = {
                        if (!isValid && longitude.split(".").last().length < 6) Text(
                            stringResource(R.string.error_longitude_decimal_places) + ""
                        )
                    },
                    isError = !isValid && longitude.split(".").last().length < 6,
                    colors = TextFieldDefaults.textFieldColors(
                        errorLeadingIconColor = Color.Red,
                        containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White, // Set the container (background) color
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 4.dp)
                )
            }
        }
        if (showPermissionRequest.value) {
            LocationPermissionRequest(
                onLocationEnabled = {
                    showLocationDialog.value = true
                },
                onPermissionsGranted = {
                    showPermissionRequest.value = false
                },
                onPermissionsDenied = {
                    // Handle permissions denied
                    // Show a message or take appropriate action
                },
                showLocationDialogNew = showLocationDialogNew,
                hasToShowDialog = showLocationDialogNew.value
            )
        }

        // Button to trigger the location permission request
        Button(
            onClick = {
//                val enteredSize = size.toFloatOrNull() ?: 0f
                val enteredSize = size.toDoubleOrNull()?.let { convertSize(it, selectedUnit).toFloat() } ?: 0f
                if (isLocationEnabled(context) && context.hasLocationPermission()) {
                    if (enteredSize < 4f) {
                        val locationRequest = LocationRequest.create().apply {
                            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                            interval = 10000 // Update interval in milliseconds
                            fastestInterval = 5000 // Fastest update interval in milliseconds
                        }

                        fusedLocationClient.requestLocationUpdates(
                            locationRequest,
                            object : LocationCallback() {
                                override fun onLocationResult(locationResult: LocationResult) {
                                    locationResult.lastLocation?.let { lastLocation ->
                                        // Handle the new location
                                        latitude = "${lastLocation.latitude}"
                                        longitude = "${lastLocation.longitude}"
                                    }
                                }
                            },
                            Looper.getMainLooper()
                        )
                    } else {
                        navController.currentBackStackEntry?.arguments?.putParcelable(
                            "farmData",
                            null
                        )
                        navController.navigate("setPolygon")
                        mapViewModel.clearCoordinates()
                    }
                } else {
                    showPermissionRequest.value = true
                    showLocationDialog.value = true
                }
            },
            modifier = Modifier
                .background(buttonColor)
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(0.7f)
                .padding(bottom = 5.dp)
                .height(50.dp),
            enabled = size.isNotBlank()
        ) {
//            val enteredSize = size.toFloatOrNull() ?: 0f
            val enteredSize = size.toDoubleOrNull()?.let { convertSize(it, selectedUnit).toFloat() } ?: 0f

            Text(
                text = if (enteredSize >= 4f) {
                    stringResource(id = R.string.set_polygon)
                } else {
                    stringResource(id = R.string.get_coordinates)
                }
            )
        }
        Button(
            onClick = {
                isFormSubmitted = true
//                Finding the center of the polygon captured
                if (coordinatesData?.isNotEmpty() == true && latitude.isBlank() && longitude.isBlank()) {
                    val center = coordinatesData.toLatLngList().getCenterOfPolygon()
                    val bounds: LatLngBounds = center
                    longitude = bounds.northeast.longitude.toString()
                    latitude = bounds.southwest.latitude.toString()
                    //Show the overview of polygon taken
                }
                if (validateForm()) {
                    // Ask user to confirm before adding farm
                    if (coordinatesData?.isNotEmpty() == true) saveFarm()
                    else showDialog.value = true
                } else {
                    Toast.makeText(context, fillForm, Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .background(buttonColor)
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = stringResource(id = R.string.add_farm))
        }
    }
}
