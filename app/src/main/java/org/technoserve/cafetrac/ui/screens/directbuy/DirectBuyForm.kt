package org.technoserve.cafetrac.ui.screens.directbuy

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.technoserve.cafetraorg.technoserve.cafetrac.R
import com.example.cafetrac.database.models.CollectionSite
import com.example.cafetrac.database.models.DirectBuy
import com.example.cafetrac.database.models.Farm
import org.technoserve.cafetrac.ui.components.CreateFarmerDialog
import org.technoserve.cafetrac.ui.components.DatePickerDialog
import org.technoserve.cafetrac.ui.components.ImagePicker
import org.technoserve.cafetrac.ui.components.TimePickerDialog
import org.technoserve.cafetrac.ui.components.isSystemInDarkTheme
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DirectBuyForm(
    collectionSites: List<CollectionSite>,
    farmers: List<Farm>,
    onSubmit: (DirectBuy) -> Unit,
    navController: NavController,
) {
    var date by remember { mutableStateOf(LocalDate.now()) }
    var time by remember { mutableStateOf(LocalTime.now()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var location by remember { mutableStateOf("") }
    var selectedSiteName by remember { mutableStateOf("") }
    var selectedSiteId by remember { mutableLongStateOf(0L) } // Store selected site ID
    var selectedFarmer by remember { mutableStateOf<Farm?>(null) }
    var farmerSearch by remember { mutableStateOf("") }
    var farmerNumber by remember { mutableStateOf("") }
    var farmerName by remember { mutableStateOf("") }
    var cherrySold by remember { mutableStateOf("") }
    var pricePerKg by remember { mutableStateOf("") }
    var paid by remember { mutableStateOf("") }
    var photo by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    var expandedSites by remember { mutableStateOf(false) }
    var expandedFarmers by remember { mutableStateOf(false) }
    var showCreateFarmerDialog by remember { mutableStateOf(false) }

    // State for managing the filtered farmers based on the selected site
    var filteredFarmers by remember { mutableStateOf<List<Farm>>(emptyList()) }


    var validationErrors by remember { mutableStateOf(emptyList<String>()) } // Store validation errors
    val focusRequesterLocation = FocusRequester()
    val focusRequesterSite = FocusRequester()
    val focusRequesterFarmer = FocusRequester()
    val focusRequesterCherrySold = FocusRequester()
    val focusRequesterPricePerKg = FocusRequester()
    val focusRequesterPaid = FocusRequester()

    // Focus manager
    val focusManager = LocalFocusManager.current

    // Handle focus change when "Next" is pressed
    fun onNextFocus(currentField: FocusRequester, nextField: FocusRequester) {
        currentField.requestFocus()
        focusManager.moveFocus(FocusDirection.Down)
        nextField.requestFocus()
    }


    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color.Black else Color.White
    val inputLabelColor = if (isDarkTheme) Color.LightGray else Color.DarkGray
    val inputTextColor = if (isDarkTheme) Color.White else Color.Black
    val buttonColor = if (isDarkTheme) Color.Black else Color.White
    val inputBorder = if (isDarkTheme) Color.LightGray else Color.DarkGray

    // Update the filteredFarmers whenever the selected site changes
    LaunchedEffect(selectedSiteName) {
        filteredFarmers = farmers.filter { it.siteId == selectedSiteId }
    }

    // Get the strings from resources within a @Composable context
    val locationError = stringResource(id = R.string.location)
    val siteError = stringResource(id = R.string.select_or_create_site)
    val farmerError = stringResource(id = R.string.farmer_name)
    val cherryError = stringResource(id = R.string.cherry_sold)
    val priceError = stringResource(id = R.string.price_per_kg)
    val paidError = stringResource(id = R.string.paid)

    // Form validation function
    fun validateForm(
        location: String,
        selectedSiteName: String,
        farmerName: String,
        cherrySold: String,
        pricePerKg: String
    ): List<String> {
        val errors = mutableListOf<String>()
        if (location.isBlank()) errors.add(locationError)
        if (selectedSiteName.isBlank()) errors.add(siteError)
        if (farmerName.isBlank()) errors.add(farmerError)
        if (cherrySold.isBlank() || cherrySold.toDoubleOrNull() == null) errors.add(cherryError)
        if (pricePerKg.isBlank() || pricePerKg.toDoubleOrNull() == null) errors.add(priceError)
        if (paid.isBlank() || paid.toDoubleOrNull() == null) errors.add(paidError)
        return errors
    }

    // Define string constants
    val title = stringResource(id = R.string.direct_buy)
    val dateLabel = stringResource(id = R.string.date)
    val timeLabel = stringResource(id = R.string.time)
    val locationLabel = stringResource(id = R.string.location)
    val selectSiteLabel = stringResource(id = R.string.select_or_create_site)
    val selectFarmerLabel = stringResource(id = R.string.farmer_name)
    val cherrySoldLabel = stringResource(id = R.string.cherry_sold)
    val pricePerKgLabel = stringResource(id = R.string.price_per_kg)
    val paidLabel = stringResource(id = R.string.paid)
    val submitLabel = stringResource(id = R.string.submit)


    if (showDatePicker) {
        DatePickerDialog(
            onDateSelected = { selectedDate ->
                date = selectedDate
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    if (showTimePicker) {
        TimePickerDialog(
            onTimeSelected = { selectedTime ->
                time = selectedTime
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }

    if (showCreateFarmerDialog) {
        CreateFarmerDialog(
            navController = navController,
            siteId = selectedSiteId, // Pass the selected site ID
            onDismiss = { showCreateFarmerDialog = false }
        )
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
//            // Date input
//            OutlinedTextField(
//                value = date.format(DateTimeFormatter.ISO_LOCAL_DATE),
//                onValueChange = { },
//                label = { Text(stringResource(id = R.string.date)) },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .focusRequester(focusRequesterLocation),
//                readOnly = true,
//                enabled = false,
//                trailingIcon = {
//                    IconButton(onClick = { showDatePicker = true }) {
//                        Icon(imageVector = Icons.Default.DateRange, contentDescription = null)
//                    }
//                }
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//
//            // Time input
//            OutlinedTextField(
//                value = time.format(DateTimeFormatter.ofPattern("HH:mm")),
//                onValueChange = { },
//                label = { Text(stringResource(id = R.string.time)) },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .focusRequester(focusRequesterSite),
//                readOnly = true,
//                enabled = false,
//                trailingIcon = {
//                    IconButton(onClick = { showTimePicker = true }) {
//                        Icon(imageVector = Icons.Default.DateRange, contentDescription = null)
//                    }
//                }
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            // Location input
//            OutlinedTextField(
//                value = location,
//                onValueChange = { location = it },
//                label = { Text(stringResource(id = R.string.location)) },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .focusRequester(focusRequesterLocation),
//                keyboardOptions = KeyboardOptions.Default.copy(
//                    imeAction = ImeAction.Next
//                ),
//                keyboardActions = KeyboardActions(
//                    onNext = {
//                        onNextFocus(focusRequesterLocation, focusRequesterSite)
//                    }
//                ),
//                isError = validationErrors.contains(stringResource(id = R.string.location))
//            )
//            if (validationErrors.contains(stringResource(id = R.string.location))) {
//                Text(
//                    text = stringResource(id = R.string.required_field),
//                    color = MaterialTheme.colorScheme.error,
//                    style = MaterialTheme.typography.bodySmall
//                )
//            }
//            Spacer(modifier = Modifier.height(8.dp))

            // Dropdown for selecting site name
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            onNextFocus(focusRequesterSite, focusRequesterFarmer)
                        }
                    ),
                    value = selectedSiteName,
                    onValueChange = { selectedSiteName = it },
                    label = { Text(selectSiteLabel) },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { expandedSites = true }) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = stringResource(id = R.string.select_site_name)
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequesterSite),
                    isError = validationErrors.contains(selectSiteLabel)
                )

                DropdownMenu(
                    expanded = expandedSites,
                    modifier = Modifier.background(MaterialTheme.colorScheme.background),
                    onDismissRequest = { expandedSites = false }
                ) {
                    // Existing site names
                    collectionSites.forEach { site ->
                        DropdownMenuItem(
                            text = { Text(site.name) },
                            modifier = Modifier.background(MaterialTheme.colorScheme.background),
                            onClick = {
                                selectedSiteName = site.name
                                selectedSiteId = site.siteId
                                expandedSites = false

                                // Filter farmers based on the selected site
                                filteredFarmers = farmers.filter { it.siteId == site.siteId }

                            }
                        )
                    }

                    // Option to create a new site name
                    DropdownMenuItem(
                        text = { Text(stringResource(id = R.string.create_new_site)) },
                        modifier = Modifier.background(MaterialTheme.colorScheme.background),
                        onClick = {
                            expandedSites = false
                            selectedSiteName = "" // Clear the selected site name
                            navController.navigate("addSite") // Navigate to the add site screen
                        }
                    )
                }
            }
            if (validationErrors.contains(selectSiteLabel)) {
                Text(
                    text = stringResource(id = R.string.required_field),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Dropdown for farmer name
            ExposedDropdownMenuBox(
                expanded = expandedFarmers,
                onExpandedChange = { expandedFarmers = !expandedFarmers }
            ) {
                OutlinedTextField(
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            onNextFocus(focusRequesterFarmer, focusRequesterCherrySold)
                        }
                    ),
                    value = farmerName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(selectFarmerLabel) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFarmers) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequesterFarmer)
                        .menuAnchor(),
                    isError = validationErrors.contains(selectFarmerLabel)
                )
                ExposedDropdownMenu(
                    expanded = expandedFarmers,
                    modifier = Modifier.background(MaterialTheme.colorScheme.background),
                    onDismissRequest = { expandedFarmers = false }
                ) {
                    filteredFarmers.forEach { farmer ->
                        DropdownMenuItem(
                            text = { Text(farmer.farmerName) },
                            modifier = Modifier.background(MaterialTheme.colorScheme.background),
                            onClick = {
                                selectedFarmer = farmer
                                farmerName= farmer.farmerName
                                farmerNumber= ""
                                farmerSearch = ""
                                expandedFarmers = false
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text("Create New Farmer") },
                        modifier = Modifier.background(MaterialTheme.colorScheme.background),
                        onClick = {
                            showCreateFarmerDialog = true
                            expandedFarmers = false
                        }
                    )
                }
            }
            if (validationErrors.contains(selectFarmerLabel)) {
                Text(
                    text = stringResource(id = R.string.required_field),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Location input
            OutlinedTextField(
                value = if (selectedFarmer != null) {
                    "${selectedFarmer!!.latitude}, ${selectedFarmer!!.longitude}"
                } else {
                    ""
                },
                onValueChange = { location = it }, // Handle the input change
                label = { Text(stringResource(id = R.string.location)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequesterLocation),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        onNextFocus(focusRequesterLocation, focusRequesterSite)
                    }
                ),
                isError = validationErrors.contains(stringResource(id = R.string.location))
            )

            if (validationErrors.contains(stringResource(id = R.string.location))) {
                Text(
                    text = stringResource(id = R.string.required_field),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }


            Spacer(modifier = Modifier.height(8.dp))

            // Farmer Number input (for new Farmer)
            if (selectedFarmer != null) {
                OutlinedTextField(
                    value = farmerNumber,
                    onValueChange = { farmerNumber = it },
                    label = { Text("Farmer Number") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        imeAction =  ImeAction.Next,
                        keyboardType = KeyboardType.Number
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = farmerSearch,
                    onValueChange = {  farmerSearch = it },
                    label = { Text("Farmer Search") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        imeAction =  ImeAction.Next,
                        keyboardType = KeyboardType.Number
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Cherry sold input
            OutlinedTextField(
                value = cherrySold,
                onValueChange = { cherrySold = it },
                label = { Text(cherrySoldLabel) },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequesterCherrySold),
                keyboardOptions = KeyboardOptions(
                    imeAction =  ImeAction.Next,
                    keyboardType = KeyboardType.Number
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        onNextFocus(focusRequesterCherrySold, focusRequesterPricePerKg)
                    }
                ),
                isError = validationErrors.contains(cherrySoldLabel)
            )
            if (validationErrors.contains(cherrySoldLabel)) {
                Text(
                    text = stringResource(id = R.string.invalid_value),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Price per kg input
            OutlinedTextField(
                singleLine = true,
                value = pricePerKg,
                onValueChange = { pricePerKg = it },
                label = { Text(pricePerKgLabel) },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequesterPricePerKg),
                keyboardOptions = KeyboardOptions(
                    imeAction =  ImeAction.Next,
                    keyboardType = KeyboardType.Number
                ),
                isError = validationErrors.contains(pricePerKgLabel)
            )

            if (validationErrors.contains(pricePerKgLabel)) {
                Text(
                    text = stringResource(id = R.string.invalid_value),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Paid input
            OutlinedTextField(
                value = paid,
                onValueChange = { paid = it },
                label = { Text(paidLabel) },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequesterPaid),
                keyboardOptions = KeyboardOptions(
                    imeAction =  ImeAction.Done,
                    keyboardType = KeyboardType.Number
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                ),
                isError = validationErrors.contains(paidLabel)
            )

            if (validationErrors.contains(paidLabel)) {
                Text(
                    text = stringResource(id = R.string.invalid_value),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Pick Image Button
            ImagePicker { uri ->
                photoUri = uri
                photo = uri?.toString() ?: ""
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Submit button
            Button(
                onClick = {
                    validationErrors = validateForm(
                        location = location,
                        selectedSiteName = selectedSiteName,
                        farmerName = farmerName,
                        cherrySold = cherrySold,
                        pricePerKg = pricePerKg
                    )
                    if (validationErrors.isEmpty()) {
                        // If form is valid, proceed with submission
                        onSubmit(
                            DirectBuy(
                                date = date,
                                time = time.format(DateTimeFormatter.ofPattern("HH:mm")),
                                location = location,
                                siteName = selectedSiteName,
                                farmerSearch = farmerSearch,
                                farmerNumber = farmerNumber,
                                farmerName = farmerName,
                                cherrySold = cherrySold.toDoubleOrNull() ?: 0.0,
                                pricePerKg = pricePerKg.toDoubleOrNull() ?: 0.0,
                                paid = paid.toDoubleOrNull() ?: 0.0,
                                photo = photo,
                                photoUri = photoUri.toString()
                            )
                        )
                        navController.navigate("bought_items_direct_buy")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(submitLabel)
            }
        }
    }
}
