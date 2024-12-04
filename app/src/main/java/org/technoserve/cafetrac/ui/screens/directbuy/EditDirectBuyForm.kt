package com.example.cafetrac.ui.screens.directbuy

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import org.technoserve.cafetraorg.technoserve.cafetrac.R
import org.technoserve.cafetrac.ui.components.PhotoPicker
import org.technoserve.cafetrac.viewmodels.FarmViewModel
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditDirectBuyForm(
    itemId: Long,
    farmViewModel: FarmViewModel,
    navController: NavController
) {
    // Fetch the item data based on the itemId
    val item by farmViewModel.getBoughtItemDirectBuyById(itemId).collectAsStateWithLifecycle(null)

    var date by remember { mutableStateOf(LocalDate.now()) }
    var time by remember { mutableStateOf(LocalTime.now()) }
    var location by remember { mutableStateOf("") }
    var selectedSiteName by remember { mutableStateOf("") }
    var farmerSearch by remember { mutableStateOf("") }
    var farmerNumber by remember { mutableStateOf("") }
    var farmerName by remember { mutableStateOf("") }
    var cherrySold by remember { mutableStateOf("") }
    var pricePerKg by remember { mutableStateOf("") }
    var paid by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    // State for showing confirmation dialog
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(item) {
        item?.let {
            date = it.date
            time = LocalTime.parse(it.time)
            location = it.location
            selectedSiteName = it.siteName
            farmerSearch = it.farmerSearch
            farmerNumber = it.farmerNumber
            farmerName = it.farmerName
            cherrySold = it.cherrySold.toString()
            pricePerKg = it.pricePerKg.toString()
            paid = it.paid.toString()
            photoUri = it.photoUri?.let { Uri.parse(it) }
        }
    }


    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            photoUri = it
        }
    }

    // Implement photo picker logic
    fun pickPhoto() {
        pickImageLauncher.launch("image/*")
    }

    // Implement photo removal logic
    fun removePhoto() {
        photoUri = null
    }

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
    val SiteLabel = stringResource(id = R.string.site_name)
    val FarmerLabel = stringResource(id = R.string.farmer_name)
    val cherrySoldLabel = stringResource(id = R.string.cherry_sold)
    val pricePerKgLabel = stringResource(id = R.string.price_per_kg)
    val paidLabel = stringResource(id = R.string.paid)
    val submitLabel = stringResource(id = R.string.submit)

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text= stringResource(id = R.string.confirm_update)) },
            text = { Text(text= stringResource(id = R.string.are_you_sure_save_the_changes)) },
            confirmButton = {
                Button(
                    onClick = {
                        // Perform the update and navigate back
                        item?.let {
                            val updatedItem = it.copy(
                                date = date,
                                time = time.toString(),
                                location = location,
                                siteName = selectedSiteName,
                                farmerSearch = farmerSearch,
                                farmerNumber = farmerNumber,
                                farmerName = farmerName,
                                cherrySold = cherrySold.toDoubleOrNull() ?:0.0,
                                pricePerKg = pricePerKg.toDoubleOrNull() ?: 0.0,
                                paid = paid.toDoubleOrNull() ?: 0.0,
                                photoUri = photoUri?.toString()
                            )
                            farmViewModel.updateDirectBuy(updatedItem)
                            navController.popBackStack()
                        }
                        showDialog = false
                    }
                ) {
                    Text(text= stringResource(id= R.string.save_changes))
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false }
                ) {
                    Text(text= stringResource(id= R.string.cancel))
                }
            },
            containerColor = MaterialTheme.colorScheme.background, // Background that adapts to light/dark
            tonalElevation = 6.dp // Adds a subtle shadow for better UX
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.update_bought_item)) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

//        // Date field
//        TextField(
//            value = date.toString(),
//            onValueChange = { /* Disable manual input, use a date picker instead */ },
//            label = { Text("Date") },
//            readOnly = true,
//            trailingIcon = {
//                IconButton(onClick = { /* Show date picker dialog */ }) {
//                    Icon(imageVector = Icons.Default.DateRange, contentDescription = null)
//                }
//            }
//        )
//
//        // Time field
//        TextField(
//            value = time.toString(),
//            onValueChange = { /* Disable manual input, use a time picker instead */ },
//            label = { Text("Time") },
//            readOnly = true,
//            trailingIcon = {
//                IconButton(onClick = { /* Show time picker dialog */ }) {
//                    Icon(imageVector = Icons.Default.DateRange, contentDescription = null)
//                }
//            }
//        )
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
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

            OutlinedTextField(
                singleLine = true,
                value = selectedSiteName,
                onValueChange = { selectedSiteName = it },
                label = { Text(text = stringResource(id = R.string.site_name)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = {
                        onNextFocus(focusRequesterSite, focusRequesterFarmer)
                    }
                ),
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequesterSite),
                isError = validationErrors.contains(SiteLabel)
            )

            if (validationErrors.contains(SiteLabel)) {
                Text(
                    text = stringResource(id = R.string.required_field),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

//            TextField(
//                value = farmerSearch,
//                onValueChange = { farmerSearch = it },
//                label = { Text("Farmer Search") }
//            )

            OutlinedTextField(
                value = farmerName,
                onValueChange = { farmerName = it },
                label = { Text(text = stringResource(id = R.string.farmer_name)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = {
                        onNextFocus(focusRequesterFarmer, focusRequesterCherrySold)
                    }
                ),
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequesterFarmer),
                isError = validationErrors.contains(FarmerLabel)
            )

            if (validationErrors.contains(FarmerLabel)) {
                Text(
                    text = stringResource(id = R.string.required_field),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            OutlinedTextField(
                value = farmerNumber,
                onValueChange = { farmerNumber = it },
                label = { Text(text= stringResource(id= R.string.farmer_number)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Number
                )
            )

            if (validationErrors.contains(farmerNumber)) {
                Text(
                    text = stringResource(id = R.string.invalid_value),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            OutlinedTextField(
                value = cherrySold,
                onValueChange = { cherrySold = it },
                label = { Text(text = stringResource(id = R.string.cherry_sold)) },
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequesterCherrySold),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
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

            OutlinedTextField(
                value = pricePerKg,
                onValueChange = { pricePerKg = it },
                label = { Text(text = stringResource(id = R.string.price_per_kg)) },
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequesterPricePerKg),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
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

            OutlinedTextField(
                value = paid,
                onValueChange = { paid = it },
                label = { Text(text = stringResource(id = R.string.paid)) },
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequesterPaid),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
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

            // Photo Picker
            PhotoPicker(
                photoUri = photoUri,
//            onPhotoSelected = { uri -> photoUri = uri },
                onPickPhotoClick = { pickPhoto() },
                onRemovePhotoClick = { removePhoto() }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(text= stringResource(id = R.string.cancel), color = Color.White)
                }
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
                            showDialog = true
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text= stringResource(id = R.string.save_changes))
                }
            }
        }
    }
}