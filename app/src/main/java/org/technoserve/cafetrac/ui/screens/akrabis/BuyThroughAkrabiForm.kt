package com.example.cafetrac.ui.screens.akrabis


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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cafetrac.database.models.BuyThroughAkrabi
import com.example.cafetrac.database.models.CollectionSite
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavController

import com.example.cafetrac.database.models.Akrabi

import com.example.cafetrac.ui.screens.farms.siteID
import org.technoserve.cafetrac.ui.components.DatePickerDialog
import org.technoserve.cafetrac.ui.components.ImagePicker
import org.technoserve.cafetrac.ui.components.ImportFileDialog
import org.technoserve.cafetrac.ui.components.TimePickerDialog
import org.technoserve.cafetrac.ui.components.isSystemInDarkTheme
import org.technoserve.cafetraorg.technoserve.cafetrac.R

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BuyThroughAkrabiForm(
    collectionSites: List<CollectionSite>,
    akrabis: List<Akrabi>,
    onCreateAkrabi: (Akrabi) -> Unit,
    onSubmit: (BuyThroughAkrabi) -> Unit,
    navController: NavController,
) {
    var date by remember { mutableStateOf(LocalDate.now()) }
    var time by remember { mutableStateOf(LocalTime.now()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var location by remember { mutableStateOf("") }
    var selectedSiteName by remember { mutableStateOf("") }
    var akrabiSearch by remember { mutableStateOf("") }
    var akrabiNumber by remember { mutableStateOf("") }
    var akrabiName by remember { mutableStateOf("") }
    var cherrySold by remember { mutableStateOf("") }
    var pricePerKg by remember { mutableStateOf("") }
    var paid by remember { mutableStateOf("") }
    var photo by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    var selectedAkrabi by remember { mutableStateOf<Akrabi?>(null) }

    var expandedSites by remember { mutableStateOf(false) }
    var expandedAkrabis by remember { mutableStateOf(false) }


    var validationErrors by remember { mutableStateOf(emptyList<String>()) } // Store validation errors
    val focusRequesterLocation = FocusRequester()
    val focusRequesterSite = FocusRequester()
    val focusRequesterAkrabi = FocusRequester()
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

    // Get the strings from resources within a @Composable context
    val locationError = stringResource(id = R.string.location)
    val siteError = stringResource(id = R.string.select_or_create_site)
    val akrabiError = stringResource(id = R.string.farmer_name)
    val cherryError = stringResource(id = R.string.cherry_sold)
    val priceError = stringResource(id = R.string.price_per_kg)
    val paidError = stringResource(id = R.string.paid)



    // Form validation function
    fun validateFormBuyThroughAkrabi(
        location: String,
        selectedSiteName: String,
        akrabiName: String,
        cherrySold: String,
        pricePerKg: String
    ): List<String> {
        val errors = mutableListOf<String>()
        if (location.isBlank()) errors.add(locationError)
        if (selectedSiteName.isBlank()) errors.add(siteError)
        if (akrabiName.isBlank()) errors.add(akrabiError)
        if (cherrySold.isBlank() || cherrySold.toDoubleOrNull() == null) errors.add(cherryError)
        if (pricePerKg.isBlank() || pricePerKg.toDoubleOrNull() == null) errors.add(priceError)
        if (paid.isBlank() || paid.toDoubleOrNull() == null) errors.add(paidError)
        return errors
    }

    // Define string constants
    val title = stringResource(id = R.string.buy_through_akrabi)
    val dateLabel = stringResource(id = R.string.date)
    val timeLabel = stringResource(id = R.string.time)
    val locationLabel = stringResource(id = R.string.location)
    val akrabiLabel = selectedAkrabi?.akrabiName ?: stringResource(id = R.string.select_or_create_akrabi)
    val akrabiNumberLabel = stringResource(id = R.string.akrabi_number)
    val selectSiteLabel = stringResource(id = R.string.select_or_create_site)
    val selectAkrabiLabel = stringResource(id = R.string.akrabi_name)
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

    var showImportDialog by remember { mutableStateOf(false) }

    // Show import dialog when triggered
    if (showImportDialog) {
        ImportFileDialog(
            siteId = siteID,
            onDismiss = { showImportDialog = false },
            navController = navController
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
                    // Location input
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

                    Spacer(modifier = Modifier.height(8.dp))

                    // Dropdown for selecting site name
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    onNextFocus(focusRequesterSite, focusRequesterAkrabi)
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
                                        expandedSites = false
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

                    ExposedDropdownMenuBox(
                        expanded = expandedAkrabis,
                        onExpandedChange = { expandedAkrabis = !expandedAkrabis }
                    ) {
                        OutlinedTextField(
                            value =  akrabiLabel,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(akrabiLabel) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedAkrabis) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedAkrabis,
                            modifier = Modifier.background(MaterialTheme.colorScheme.background),
                            onDismissRequest = { expandedAkrabis = false }
                        ) {
                            // Show existing Akrabi items
                            akrabis.forEach { akrabi ->
                                DropdownMenuItem(
                                    text = { Text(akrabi.akrabiName) },
                                    modifier = Modifier.background(MaterialTheme.colorScheme.background),
                                    onClick = {
                                        selectedAkrabi = akrabi
                                        akrabiNumber = akrabi.akrabiNumber
                                        akrabiName = akrabi.akrabiName
                                        expandedAkrabis = false
                                    }
                                )
                            }

                            // Add option to create new Akrabi
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.create_new_akrabi)) },
                                modifier = Modifier.background(MaterialTheme.colorScheme.background),
                                onClick = {
                                    // Handle navigation to the CreateAkrabiForm
                                    navController.navigate("akrabi_list_screen")
                                    expandedAkrabis = false
                                }
                            )
                        }
                    }

                    if (validationErrors.contains(selectAkrabiLabel)) {
                        Text(
                            text = stringResource(id = R.string.required_field),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Akrabi Number input (for new Akrabi)
                    if (selectedAkrabi != null) {
                        OutlinedTextField(
                            value = akrabiNumber,
                            onValueChange = { akrabiNumber = it },
                            label = { Text(akrabiNumberLabel) },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(
                                imeAction =  ImeAction.Next,
                                keyboardType = KeyboardType.Number
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = akrabiSearch,
                            onValueChange = { akrabiSearch = it },
                            label = { Text("Akrabi Search") },
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
                    // Improved Import Button:
                    Button(
                        onClick = { showImportDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.icons8_import_file_48),
                            contentDescription = "Import",
                            modifier = Modifier.size(24.dp),
                        )
                        Text(
                            text = stringResource(id = R.string.import_file),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Submit button
                    Button(
                        onClick = {
                            validationErrors = validateFormBuyThroughAkrabi(
                                location = location,
                                selectedSiteName = selectedSiteName,
                                akrabiName = akrabiName,
                                cherrySold = cherrySold,
                                pricePerKg = pricePerKg
                            )
                            if (validationErrors.isEmpty()) {
                                // If form is valid, proceed with submission
                                onSubmit(
                                    BuyThroughAkrabi(
                                        date = date,
                                        time = time.format(DateTimeFormatter.ofPattern("HH:mm")),
                                        location = location,
                                        siteName = selectedSiteName,
                                        akrabiSearch = akrabiSearch,
                                        akrabiNumber = selectedAkrabi?.akrabiNumber ?: akrabiNumber,
                                        akrabiName = selectedAkrabi?.akrabiName ?: akrabiName,
                                        cherrySold = cherrySold.toDoubleOrNull() ?: 0.0,
                                        pricePerKg = pricePerKg.toDoubleOrNull() ?: 0.0,
                                        paid = paid.toDoubleOrNull() ?: 0.0,
                                        photo = photo,
                                        photoUri = photoUri.toString()
                                )
                                )
                                navController.navigate("bought_items_buy_through_Akrabi")
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