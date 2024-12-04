package com.example.cafetrac.ui.screens.farms

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Looper
import android.view.KeyEvent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import org.technoserve.cafetraorg.technoserve.cafetrac.R
import org.technoserve.cafetrac.database.models.Farm
import com.example.cafetrac.database.models.Language
import org.technoserve.cafetrac.database.models.ParcelablePair
import org.technoserve.cafetrac.ui.components.CustomDrawer
import org.technoserve.cafetrac.ui.components.GenderDropdown
import org.technoserve.cafetrac.ui.components.isSystemInDarkTheme
import org.technoserve.cafetrac.utils.convertSize
import org.technoserve.cafetrac.utils.hasLocationPermission
import org.technoserve.cafetrac.viewmodels.FarmViewModel
import org.technoserve.cafetrac.viewmodels.FarmViewModelFactory
import org.technoserve.cafetrac.viewmodels.LanguageViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import org.joda.time.Instant
import org.technoserve.cafetrac.ui.screens.farms.LocationPermissionRequest
import org.technoserve.cafetrac.ui.screens.farms.createImageFile
import org.technoserve.cafetrac.ui.screens.farms.isLocationEnabled
import org.technoserve.cafetrac.ui.screens.farms.promptEnableLocation
import org.technoserve.cafetrac.ui.screens.farms.siteID
import org.technoserve.cafetrac.ui.screens.farms.truncateToDecimalPlaces
import org.technoserve.cafetrac.ui.screens.farms.updateFarm
import java.util.Objects
import java.util.regex.Pattern


@SuppressLint("MissingPermission", "UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun UpdateFarmForm(
    navController: NavController,
    farmId: Long?,
    listItems: List<Farm>,
    languageViewModel: LanguageViewModel,
    darkMode: MutableState<Boolean>,
    languages: List<Language>
) {
    val floatValue = 123.45f
    val item = listItems.find { it.id == farmId } ?: Farm(
        siteId = 0L,
        farmerPhoto = "Default photo",
        farmerName = "Default Farmer",
        memberId = "",
        village = "Default Village",
        district = "Default District",
        purchases = floatValue,
        size = floatValue,
        latitude = "0.0", // Default latitude
        longitude = "0.0", // Default longitude
        coordinates = null,
        accuracyArray = null,
        age = 0,  // Default age
        gender = "",  // Default gender
        govtIdNumber = "",  // Default government ID number
        numberOfTrees = 0,  // Default number of trees
        phone = "",  // Default phone
        photo = "",  // Default photo
        synced = false,
        scheduledForSync = false,
        createdAt = 1L,
        updatedAt = 1L
    )

    val context = LocalContext.current as Activity
    var farmerName by remember { mutableStateOf(item.farmerName) }
    var memberId by remember { mutableStateOf(item.memberId) }
    var farmerPhoto by remember { mutableStateOf(item.farmerPhoto) }
    var village by remember { mutableStateOf(item.village) }
    var district by remember { mutableStateOf(item.district) }
    var age by remember { mutableStateOf(item.age.toString()) }
    var gender by remember { mutableStateOf(item.gender) }
    var govtIdNumber by remember { mutableStateOf(item.govtIdNumber.toString()) }
    var phone by remember { mutableStateOf(item.phone) }
    var numberOfTrees by remember { mutableStateOf(item.numberOfTrees.toString()) }

    //var photo by remember { mutableStateOf(item.photo)}
    // var photoUri by remember { mutableStateOf(Uri.parse(item.photo))}

    var size by remember { mutableStateOf(item.size.toString()) }
    var latitude by remember { mutableStateOf(item.latitude) }
    var longitude by remember { mutableStateOf(item.longitude) }
    var coordinates by remember { mutableStateOf(item.coordinates) }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val farmViewModel: FarmViewModel =
        viewModel(
            factory = FarmViewModelFactory(context.applicationContext as Application),
        )
    val showDialog = remember { mutableStateOf(false) }
    val showLocationDialog = remember { mutableStateOf(false) }
    val showLocationDialogNew = remember { mutableStateOf(false) }
    val showPermissionRequest = remember { mutableStateOf(false) }
    val file = context.createImageFile()
    val uri =
        FileProvider.getUriForFile(
            Objects.requireNonNull(context),
            context.packageName + ".provider",
            file,
        )
    var expanded by remember { mutableStateOf(false) }
    val items = listOf("Ha", "Acres", "Sqm", "Timad", "Fichesa", "Manzana", "Tarea")
    var selectedUnit by remember { mutableStateOf(items[0]) }

    val scientificNotationPattern = Pattern.compile("([+-]?\\d*\\.?\\d+)[eE][+-]?\\d+")

    LaunchedEffect(Unit) {
        if (!isLocationEnabled(context)) {
            showLocationDialog.value = true
        }
    }

    // Define string constants
    val titleText = stringResource(id = R.string.enable_location_services)
    val messageText = stringResource(id = R.string.location_services_required_message)
    val enableButtonText = stringResource(id = R.string.enable)

    // Dialog to prompt user to enable location services
    if (showLocationDialog.value) {
        AlertDialog(
            onDismissRequest = { showLocationDialog.value = false },
            title = { Text(titleText) },
            text = { Text(messageText) },
            confirmButton = {
                Button(onClick = {
                    showLocationDialog.value = false
                    promptEnableLocation(context)
                }) {
                    Text(enableButtonText)
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
                    Text(stringResource(id = R.string.cancel))
                }
            },
        )
    }

    if (navController.currentBackStackEntry!!.savedStateHandle.contains("coordinates")) {
        val parcelableCoordinates = navController.currentBackStackEntry!!
            .savedStateHandle
            .get<List<ParcelablePair>>("coordinates")

        coordinates = parcelableCoordinates?.map { Pair(it.first, it.second) }
    }


    val fillForm = stringResource(id = R.string.fill_form)

    fun validateForm(): Boolean {
        var isValid = true

        if (farmerName.isBlank()) {
            isValid = false
        }

        if (village.isBlank()) {
            isValid = false
        }

        if (district.isBlank()) {
            isValid = false
        }

        if (size.toFloatOrNull()?.let { it > 0 } != true) {
            isValid = false
        }

        if (latitude.isBlank() || longitude.isBlank()) {
            isValid = false
        }

        return isValid
    }

    /**
     * Updating Farm details
     * Before sending to the database
     */

    fun updateFarmInstance() {
        val isValid = validateForm()
        if (isValid) {
            item.farmerPhoto = ""
            item.farmerName = farmerName
            item.memberId = memberId
            item.latitude = latitude
            item.village = village
            item.district = district
            item.age = age?.toInt()
            item.gender = gender
            item.govtIdNumber = govtIdNumber
            item.phone = phone
            item.numberOfTrees = numberOfTrees.toIntOrNull() ?: 0
            item.photo = "" // Default photo
            item.longitude = longitude
            if ((size.toDoubleOrNull()?.let { convertSize(it, selectedUnit).toFloat() }
                    ?: 0f) >= 4) {
                if ((coordinates?.size ?: 0) < 3) {
                    Toast
                        .makeText(
                            context,
                            R.string.error_polygon_points,
                            Toast.LENGTH_SHORT,
                        ).show()
                    return
                }
                item.coordinates =
                    coordinates?.plus(coordinates?.first()) as List<Pair<Double, Double>>
            } else {
                item.coordinates = listOf(
                    Pair(
                        item.longitude.toDoubleOrNull() ?: 0.0,
                        item.latitude.toDoubleOrNull() ?: 0.0
                    )
                ) // Example default value
            }
            item.size = convertSize(size.toDouble(), selectedUnit).toFloat()
            item.purchases = 0.toFloat()
            item.updatedAt = Instant.now().millis
            updateFarm(farmViewModel, item)
            item.needsUpdate = false
            val returnIntent = Intent()
            context.setResult(Activity.RESULT_OK, returnIntent)
            navController.navigate("farmList/$siteID")
        } else {
            Toast.makeText(context, fillForm, Toast.LENGTH_SHORT).show()
        }
    }
    // Confirm farm update and ask if they wish to capture new polygon
    if (showDialog.value) {
        AlertDialog(
            modifier = Modifier.padding(horizontal = 32.dp),
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = stringResource(id = R.string.update_farm)) },
            text = {
                Column {
                    Text(text = stringResource(id = R.string.confirm_update_farm))
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    updateFarmInstance()
                }) {
                    Text(text = stringResource(id = R.string.update_farm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick =
                    {
                        showDialog.value = false
                        navController.navigate("setPolygon")
                    },
                ) {
                    Text(text = stringResource(id = R.string.set_polygon))
                }
            },
        )
    }

    val scrollState = rememberScrollState()
    val (focusRequester1) = FocusRequester.createRefs()
    val (focusRequester2) = FocusRequester.createRefs()
    val (focusRequester3) = FocusRequester.createRefs()

    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color.Black else Color.White
    val inputLabelColor = if (isDarkTheme) Color.LightGray else Color.DarkGray
    val inputTextColor = if (isDarkTheme) Color.White else Color.Black
    val buttonColor = if (isDarkTheme) Color.Black else Color.White
    val inputBorder = if (isDarkTheme) Color.LightGray else Color.DarkGray

    var drawerVisible by remember { mutableStateOf(false) }
    val currentLanguage by languageViewModel.currentLanguage.collectAsState()
    val sharedPreferences = context.getSharedPreferences("theme_mode", Context.MODE_PRIVATE)



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
            hasToShowDialog = showLocationDialogNew.value,
        )
    }

//    Column(
//        modifier =
//        Modifier
//            .fillMaxWidth()
//            .background(backgroundColor)
//            .padding(16.dp)
////            .verticalScroll(state = scrollState),
//    ) {
//        FarmListHeader(
//            title = stringResource(id = R.string.update_farm),
//            onSearchQueryChanged = {},
//            onAddFarmClicked = { /* Handle adding a farm here */ },
//            onBackClicked = { navController.popBackStack() },
//            onBackSearchClicked = {},
//            showAdd = false,
//            showSearch = false,
//            selectedItemsCount = 0,
//            selectAllEnabled = false,
//            isAllSelected =false,
//            onSelectAllChanged = { null},
//            darkMode = darkMode,
//            languages = languages,
//            languageViewModel = languageViewModel,
//            navController = navController
//        )
//
//        Column(
//            modifier =
//            Modifier
//                .fillMaxWidth()
//                .background(backgroundColor)
//                .padding(8.dp)
//                .verticalScroll(state = scrollState),
//        ) {

    // Composable content
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(id = R.string.update_farm)) },
//                    navigationIcon = {
//                        IconButton(onClick = { drawerVisible = !drawerVisible }) {
//                            Icon(Icons.Default.Menu, contentDescription = "Menu")
//                        }
//                    }
//                    actions = {
//                        IconButton(onClick = { isSearchActive = !isSearchActive }) {
//                            Icon(
//                                if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
//                                contentDescription = if (isSearchActive) "Close Search" else "Search"
//                            )
//                        }
//                    }
                )
            },
//            floatingActionButton = {
//                FloatingActionButton(
//                    onClick = {
//                        navController.navigate("addSite")
//                    },
//                    modifier = Modifier.padding(16.dp)
//                ) {
//                    Icon(Icons.Default.Add, contentDescription = "Add Site")
//                }
//            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .padding(8.dp)
                        .verticalScroll(state = rememberScrollState())
                ) {

                    OutlinedTextField(
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions =
                        KeyboardActions(
                            onDone = { focusRequester1.requestFocus() },
                        ),
                        value = farmerName,
                        onValueChange = { farmerName = it },
                        label = {
                            Text(
                                stringResource(id = R.string.farm_name),
                                color = inputLabelColor
                            )
                        },
                        isError = farmerName.isBlank(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White, // Set the container (background) color
                            errorLeadingIconColor = Color.Red,
                            cursorColor = inputTextColor,
                            errorCursorColor = Color.Red,
                            focusedBorderColor = inputBorder,
                            unfocusedBorderColor = inputBorder,
                            errorBorderColor = Color.Red
                        ),
                        modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .onKeyEvent {
                                if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                                    focusRequester1.requestFocus()
                                    true
                                }
                                false
                            },
                    )
                    OutlinedTextField(
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions =
                        KeyboardActions(
                            onDone = { focusRequester1.requestFocus() },
                        ),
                        value = memberId,
                        onValueChange = { memberId = it },
                        label = {
                            Text(
                                stringResource(id = R.string.member_id),
                                color = inputLabelColor
                            )
                        },
                        modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .onKeyEvent {
                                if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                                    focusRequester1.requestFocus()
                                }
                                false
                            },
                    )
                    OutlinedTextField(
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions =
                        KeyboardActions(
                            onDone = { focusRequester2.requestFocus() },
                        ),
                        value = village,
                        onValueChange = { village = it },
                        label = {
                            Text(
                                stringResource(id = R.string.village),
                                color = inputLabelColor
                            )
                        },
                        isError = village.isBlank(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White, // Set the container (background) color
                            errorLeadingIconColor = Color.Red,
                            cursorColor = inputTextColor,
                            errorCursorColor = Color.Red,
                            focusedBorderColor = inputBorder,
                            unfocusedBorderColor = inputBorder,
                            errorBorderColor = Color.Red
                        ),
                        modifier =
                        Modifier
                            .focusRequester(focusRequester1)
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                    )
                    OutlinedTextField(
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions =
                        KeyboardActions(
                            onDone = { focusRequester3.requestFocus() },
                        ),
                        value = district,
                        onValueChange = { district = it },
                        label = {
                            Text(
                                stringResource(id = R.string.district),
                                color = inputLabelColor
                            )
                        },
                        isError = district.isBlank(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White, // Set the container (background) color
                            errorLeadingIconColor = Color.Red,
                            cursorColor = inputTextColor,
                            errorCursorColor = Color.Red,
                            focusedBorderColor = inputBorder,
                            unfocusedBorderColor = inputBorder,
                            errorBorderColor = Color.Red
                        ),
                        modifier =
                        Modifier
                            .focusRequester(focusRequester2)
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                    )

                    OutlinedTextField(
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions =
                        KeyboardActions(
                            onDone = { focusRequester3.requestFocus() },
                        ),
                        value = age,
                        onValueChange = { age = it },
                        label = {
                            Text(
                                stringResource(id = R.string.age),
                                color = inputLabelColor
                            )
                        },
                        isError = district.isBlank(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White, // Set the container (background) color
                            errorLeadingIconColor = Color.Red,
                            cursorColor = inputTextColor,
                            errorCursorColor = Color.Red,
                            focusedBorderColor = inputBorder,
                            unfocusedBorderColor = inputBorder,
                            errorBorderColor = Color.Red
                        ),
                        modifier =
                        Modifier
                            .focusRequester(focusRequester2)
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                    )


                    // Gender (Dropdown)
                    gender?.let {
                        GenderDropdown(
                            gender = it,
                            onGenderSelected =
                            { selectedGender ->
                                gender = selectedGender
                            }
                        )
                    }

                    // Govt ID Number
                    govtIdNumber?.let {
                        OutlinedTextField(
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            value = it,
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
                    }

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
                    phone?.let {
                        OutlinedTextField(
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            value = it,
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
                    }

//            // Pick Image Button
//            ImagePicker { uri ->
//                photoUri = uri
//                photo = uri?.toString() ?: ""
//            }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        OutlinedTextField(
                            singleLine = true,
                            value = truncateToDecimalPlaces(size, 9),
                            onValueChange = {
                                size = it
                            },
//                value =  truncateToDecimalPlaces(formatInput(size),9),
//                onValueChange = { inputValue ->
//                    val formattedValue = when {
//                        validateSize(inputValue) -> inputValue
//                        // Check if the input is in scientific notation
//                        scientificNotationPattern.matcher(inputValue).matches() -> {
//                            truncateToDecimalPlaces(formatInput(inputValue),9)
//                        }
//                        else -> inputValue
//                    }
//
//                    // Update the size state with the formatted value
//                    size = formattedValue
//                },
                            keyboardOptions =
                            KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number,
                            ),
                            label = {
                                Text(
                                    stringResource(id = R.string.size_in_hectares) + " (*)",
                                    color = inputLabelColor
                                )
                            },
                            isError = size.toFloatOrNull() == null || size.toFloat() <= 0, // Validate size
                            colors =
                            TextFieldDefaults.textFieldColors(
                                containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White, // Set the container (background) color
                                errorLeadingIconColor = Color.Red,
                                cursorColor = inputTextColor,
                                errorCursorColor = Color.Red,
                                focusedIndicatorColor = inputBorder,
                                unfocusedIndicatorColor = inputBorder,
                                errorIndicatorColor = Color.Red,
                            ),
                            modifier =
                            Modifier
                                .focusRequester(focusRequester3)
                                .weight(1f)
                                .padding(bottom = 4.dp),
                        )

                        Spacer(modifier = Modifier.width(8.dp))
                        // Size measure
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = {
                                expanded = !expanded
                            },
                            modifier = Modifier.weight(1f),
                        ) {
                            OutlinedTextField(
                                readOnly = true,
                                value = selectedUnit,
                                onValueChange = { },
                                label = { Text(stringResource(R.string.unit)) },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = expanded,
                                    )
                                },
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White,
                                ),
                                modifier = Modifier.menuAnchor(),
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = {
                                    expanded = false
                                },
                            ) {
                                items.forEach { selectionOption ->
                                    DropdownMenuItem(
                                        { Text(text = selectionOption) },
                                        onClick = {
                                            selectedUnit = selectionOption
                                            expanded = false
                                        },
                                    )
                                }
                            }
                        }
                    }

//        Spacer(modifier = Modifier.height(16.dp)) // Add space between the latitude and longitude input fields
                    if ((size.toDoubleOrNull()?.let { convertSize(it, selectedUnit).toFloat() }
                            ?: 0f) < 4f) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            OutlinedTextField(
                                readOnly = true,
                                value = latitude,
                                onValueChange = { latitude = it },
                                label = {
                                    Text(
                                        stringResource(id = R.string.latitude),
                                        color = inputLabelColor
                                    )
                                },
                                modifier =
                                Modifier
                                    .weight(1f)
                                    .padding(bottom = 4.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp)) // Add space between the latitude and longitude input fields
                            OutlinedTextField(
                                readOnly = true,
                                value = longitude,
                                onValueChange = { longitude = it },
                                label = {
                                    Text(
                                        stringResource(id = R.string.longitude),
                                        color = inputLabelColor
                                    )
                                },
                                modifier =
                                Modifier
                                    .weight(1f)
                                    .padding(bottom = 16.dp),
                            )
                        }
                    }
                    Button(
                        onClick = {
                            showPermissionRequest.value = true
                            if (!isLocationEnabled(context)) {
                                showLocationDialog.value = true
                            } else {
                                if (isLocationEnabled(context) && context.hasLocationPermission()) {
//                        if (size.toFloatOrNull() != null && size.toFloat() < 4) {
                                    if (size.toDoubleOrNull()
                                            ?.let { convertSize(it, selectedUnit).toFloat() }
                                            ?.let { it < 4f } == true
                                    ) {
                                        // Simulate collecting latitude and longitude
                                        if (context.hasLocationPermission()) {
                                            val locationRequest =
                                                LocationRequest.create().apply {
                                                    priority =
                                                        LocationRequest.PRIORITY_HIGH_ACCURACY
                                                    interval =
                                                        10000 // Update interval in milliseconds
                                                    fastestInterval =
                                                        5000 // Fastest update interval in milliseconds
                                                }

                                            fusedLocationClient.requestLocationUpdates(
                                                locationRequest,
                                                object : LocationCallback() {
                                                    override fun onLocationResult(locationResult: LocationResult) {
                                                        locationResult.lastLocation?.let { lastLocation ->
                                                            // Handle the new location
                                                            latitude = "${lastLocation.latitude}"
                                                            longitude = "${lastLocation.longitude}"
                                                            // Log.d("FARM_LOCATION", "loaded success,,,,,,,")
                                                        }
                                                    }
                                                },
                                                Looper.getMainLooper(),
                                            )
                                        }
                                    } else {
                                        if (isLocationEnabled(context)) {
                                            navController.navigate("setPolygon")
                                        }
                                    }
                                } else {
                                    showPermissionRequest.value = true
                                    showLocationDialog.value = true
                                }
                            }
                        },
                        modifier =
                        Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth(0.7f)
                            .padding(bottom = 5.dp),
                        enabled = size.toFloatOrNull() != null,
                    ) {
                        Text(
                            // text = if (size.toFloatOrNull() != null && size.toFloat() < 4) stringResource(id = R.string.get_coordinates) else stringResource(
                            text =
                            if (size.toDoubleOrNull()
                                    ?.let { convertSize(it, selectedUnit).toFloat() }
                                    ?.let { it < 4f } ==
                                true
                            ) {
                                stringResource(id = R.string.get_coordinates)
                            } else {
                                stringResource(
                                    id = R.string.set_new_polygon,
                                )
                            },
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 5.dp),
                        horizontalArrangement = Arrangement.Center // Ensures buttons are spaced evenly
                    ) {

                        Button(
                            onClick = { navController.popBackStack() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text(text = stringResource(id = R.string.cancel), color = Color.White)
                        }
                        Spacer(modifier = Modifier.width(8.dp)) // Adds some space between the buttons
                        Button(
                            onClick = {
                                if (validateForm()) {
                                    showDialog.value = true
                                } else {
                                    Toast.makeText(context, fillForm, Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Text(text = stringResource(id = R.string.update_farm))
                        }
                    }


                }
            }
        )
    }

    CustomDrawer(
        drawerVisible = drawerVisible,
        onClose = { drawerVisible = false },
        navController = navController,
        darkMode = darkMode,
        currentLanguage = currentLanguage,
        languages = languages,
        onLanguageSelected = { language -> languageViewModel.selectLanguage(language, context) },
        onLogout = {
            navController.navigate("home")
            drawerVisible = false
        }
    )
}