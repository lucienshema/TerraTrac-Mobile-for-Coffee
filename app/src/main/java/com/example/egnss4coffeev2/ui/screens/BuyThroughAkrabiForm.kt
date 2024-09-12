package com.example.egnss4coffeev2.ui.screens


import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.egnss4coffeev2.database.BuyThroughAkrabi
import com.example.egnss4coffeev2.database.CollectionSite
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.egnss4coffeev2.R
import com.example.egnss4coffeev2.database.Akrabi
import com.example.egnss4coffeev2.database.AkrabiViewModel
import com.example.egnss4coffeev2.database.DirectBuy
import com.example.egnss4coffeev2.database.Farm
import com.example.egnss4coffeev2.utils.Language
import com.example.egnss4coffeev2.utils.LanguageViewModel
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.delay


@Composable
fun ImagePicker(
    onImagePicked: (Uri?) -> Unit
) {
    // State to hold the picked photo URI
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher for picking images from the gallery
    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        photoUri = uri
        onImagePicked(uri) // Notify the parent composable of the selected image URI
    }

    // Function to initiate the photo picker
    fun pickPhoto() {
        pickImageLauncher.launch("image/*")
    }

    Column(modifier = Modifier
        .fillMaxSize()) {

        // Display the image if one is selected
        if (photoUri != null) {
            Image(
                painter = rememberAsyncImagePainter(photoUri),
                contentDescription = "Selected Photo",
                modifier = Modifier
                    .size(200.dp) // Increased the size for better visibility
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(4.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Button to remove the photo
            IconButton(
                onClick = {
                    photoUri = null
                    onImagePicked(null) // Notify that the photo was removed
                }
            ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove Photo")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // OutlinedTextField acting as the photo selection button
            OutlinedTextField(
                value = stringResource(R.string.select_photo),
                onValueChange = {}, // No-op since the field is read-only
                label = { Text(text=stringResource(R.string.photo)) },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { pickPhoto() }) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = "Pick Photo")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { pickPhoto() } // Allow clicking anywhere on the text field to pick a photo
            )
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            // Blocks Sunday and Saturday from being selected.
            @RequiresApi(Build.VERSION_CODES.O)
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val dayOfWeek = Instant.ofEpochMilli(utcTimeMillis)
                    .atZone(ZoneId.of("UTC"))
                    .toLocalDate()
                    .dayOfWeek
                return dayOfWeek != DayOfWeek.SUNDAY && dayOfWeek != DayOfWeek.SATURDAY
            }
            // Allow selecting dates from the current year forward.
            @RequiresApi(Build.VERSION_CODES.O)
            override fun isSelectableYear(year: Int): Boolean {
                return year >= LocalDate.now().year
            }
        }
    )

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Select Date") },
        text = {
            Column {
                DatePicker(state = datePickerState)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Selected date: ${datePickerState.selectedDateMillis?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    } ?: "No selection"}",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val selectedDate = Instant.ofEpochMilli(it)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        onDateSelected(selectedDate)
                    }
                    onDismiss()
                }
            ) {
                Text(text=stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text=stringResource(R.string.cancel))
            }
        }
    )
}




@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onTimeSelected: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    var showingPicker by remember { mutableStateOf(true) }
    val state = rememberTimePickerState()
    val formatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val configuration = LocalConfiguration.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (showingPicker) "Select Time" else "Enter Time") },
        text = {
            Column {
                if (showingPicker && configuration.screenHeightDp > 400) {
                    TimePicker(state = state)
                } else {
                    TimeInput(state = state)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val selectedTime = LocalTime.of(state.hour, state.minute)
                    onTimeSelected(selectedTime)
                    onDismiss()
                }
            ) {
                Text(text=stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text=stringResource(R.string.cancel))
            }
        },
        icon = {
            if (configuration.screenHeightDp > 400) {
                IconButton(onClick = { showingPicker = !showingPicker }) {
                    Icon(
                        imageVector = if (showingPicker) Icons.Default.Create else Icons.Default.DateRange,
                        contentDescription = if (showingPicker) "Switch to Text Input" else "Switch to Touch Input"
                    )
                }
            }
        }
    )
}



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
//                    // Date input
//                    OutlinedTextField(
//                        value = date.format(DateTimeFormatter.ISO_LOCAL_DATE),
//                        onValueChange = { },
//                        label = { Text(stringResource(id = R.string.date)) },
//                        modifier = Modifier
//                            .fillMaxWidth(),
//                        readOnly = true,
//                        enabled = false,
//                        trailingIcon = {
//                            IconButton(onClick = { showDatePicker = true }) {
//                                Icon(imageVector = Icons.Default.DateRange, contentDescription = null)
//                            }
//                        }
//                    )
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    // Time input
//                    OutlinedTextField(
//                        value = time.format(DateTimeFormatter.ofPattern("HH:mm")),
//                        onValueChange = { },
//                        label = { Text(stringResource(id = R.string.time)) },
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .focusRequester(focusRequesterSite),
//                        readOnly = true,
//                        enabled = false,
//                        trailingIcon = {
//                            IconButton(onClick = { showTimePicker = true }) {
//                                Icon(imageVector = Icons.Default.DateRange, contentDescription = null)
//                            }
//                        }
//                    )
//                    Spacer(modifier = Modifier.height(8.dp))
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
                            onDismissRequest = { expandedSites = false }
                        ) {
                            // Existing site names
                            collectionSites.forEach { site ->
                                DropdownMenuItem(
                                    text = { Text(site.name) },
                                    onClick = {
                                        selectedSiteName = site.name
                                        expandedSites = false
                                    }
                                )
                            }

                            // Option to create a new site name
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.create_new_site)) },
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
                            onDismissRequest = { expandedAkrabis = false }
                        ) {
                            // Show existing Akrabi items
                            akrabis.forEach { akrabi ->
                                DropdownMenuItem(
                                    text = { Text(akrabi.akrabiName) },
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
                    onDismissRequest = { expandedSites = false }
                ) {
                    // Existing site names
                    collectionSites.forEach { site ->
                        DropdownMenuItem(
                            text = { Text(site.name) },
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
                    onDismissRequest = { expandedFarmers = false }
                ) {
                    filteredFarmers.forEach { farmer ->
                        DropdownMenuItem(
                            text = { Text(farmer.farmerName) },
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


@Composable
fun CreateFarmerDialog(
    navController: NavController,
    siteId: Long, // Accept site ID as a parameter
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(
                onClick = {
                    /// navController.navigate("addFarm/$siteId") // Navigate to addFarm with siteId
                    navController.navigate("siteList") // Navigate to addFarm with siteId
                }
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Create New Farmer") },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenderDropdown(gender: String, onGenderSelected: (String) -> Unit) {
    // Options for gender dropdown
    val genderOptions = listOf("Male", "Female", "Other")

    // State to manage the expanded state of the dropdown
    var expanded by remember { mutableStateOf(false) }

    // State to track the selected gender
    var selectedGender by remember { mutableStateOf(gender) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedGender,
            onValueChange = { /* Do nothing here, selection happens via dropdown */ },
            label = { Text("Gender") },
            readOnly = true, // Ensure the text field is read-only, dropdown handles selection
            modifier = Modifier
                .menuAnchor() // Anchor the dropdown to the text field
                .fillMaxWidth(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )

        // The dropdown menu that shows the gender options
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            genderOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        selectedGender = option
                        onGenderSelected(option)
                        expanded = false // Close the dropdown menu
                    }
                )
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAkrabiForm(
    navController: NavController, // Pass NavController for navigation
    akrabi: Akrabi? = null,
    title: String,
    collectionSites: List<CollectionSite>,
    onSubmit: (Akrabi) -> Unit,
    onCancel: () -> Unit
) {
    var akrabiNumber by remember { mutableStateOf(akrabi?.akrabiNumber ?: "") }
    var akrabiName by remember { mutableStateOf(akrabi?.akrabiName ?: "") }
    var selectedSiteName by remember { mutableStateOf(akrabi?.siteName ?: "") }

    var age by remember { mutableStateOf(akrabi?.age?.toString() ?: "") }
    //var gender by remember { mutableStateOf(akrabi?.gender ?: "") }

    var gender by remember { mutableStateOf("") }

    var woreda by remember { mutableStateOf(akrabi?.woreda ?: "") }
    var kebele by remember { mutableStateOf(akrabi?.kebele ?: "") }
    var govtIdNumber by remember { mutableStateOf(akrabi?.govtIdNumber ?: "") }
    var phone by remember { mutableStateOf(akrabi?.phone ?: "") }
    var photo by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf(akrabi?.photoUri ?: "") }

    var dropdownExpanded by remember { mutableStateOf(false) }
    var validationErrors by remember { mutableStateOf(emptyList<String>()) } // Store validation errors

    // Focus management
    val focusRequesterSite = remember { FocusRequester() }
    val focusRequesterAkrabi = remember { FocusRequester() }
    val focusRequesterAkrabiNumber = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    // Get string resources for validation
    val akrabiNumberError = stringResource(id = R.string.invalid_value)
    val siteError = stringResource(id = R.string.required_field)
    val akrabiNameError = stringResource(id = R.string.required_field)

    // Form validation function
    fun validateForm(): List<String> {
        val errors = mutableListOf<String>()
        if (akrabiNumber.isBlank()) errors.add(akrabiNumberError)
        if (selectedSiteName.isBlank()) errors.add(siteError)
        if (akrabiName.isBlank()) errors.add(akrabiNameError)
        return errors
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

            OutlinedTextField(
                value = akrabiNumber,
                onValueChange = { akrabiNumber = it },
                label = { Text(text = stringResource(id = R.string.akrabi_number)) },
                isError = validationErrors.contains(akrabiNumberError),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequesterAkrabiNumber),
                keyboardOptions = KeyboardOptions(
                    imeAction =  ImeAction.Next,
                    keyboardType = KeyboardType.Number
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusRequesterAkrabi.requestFocus() }
                )
            )
            if (validationErrors.contains(akrabiNumberError)) {
                Text(
                    text = akrabiNumberError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = akrabiName,
                onValueChange = { akrabiName = it },
                label = { Text(text = stringResource(id = R.string.akrabi_name)) },
                isError = validationErrors.contains(akrabiNameError),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequesterAkrabi),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusRequesterSite.requestFocus() }
                )
            )
            if (validationErrors.contains(akrabiNameError)) {
                Text(
                    text = akrabiNameError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Dropdown for selecting site name
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedSiteName,
                    onValueChange = { selectedSiteName = it },
                    label = { Text(text = stringResource(id = R.string.select_or_create_site)) },
                    readOnly = true,
                    isError = validationErrors.contains(siteError),
                    trailingIcon = {
                        IconButton(onClick = { dropdownExpanded = true }) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select Site Name"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequesterSite)
                )

                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    collectionSites.forEach { site ->
                        DropdownMenuItem(
                            text = { Text(site.name) },
                            onClick = {
                                selectedSiteName = site.name
                                dropdownExpanded = false
                            }
                        )
                    }

                    // Option to create a new site name
                    DropdownMenuItem(
                        text = { Text(stringResource(id = R.string.create_new_site)) },
                        onClick = {
                            dropdownExpanded = false
                            selectedSiteName = "" // Clear the selected site name
                            navController.navigate("addSite") // Navigate to the add site screen
                        }
                    )
                }
            }

            if (validationErrors.contains(siteError)) {
                Text(
                    text = siteError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))


            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text(text = "Age") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

//            OutlinedTextField(
//                value = gender,
//                onValueChange = { gender = it },
//                label = { Text(text = "Gender") },
//                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
//                modifier = Modifier.fillMaxWidth()
//            )

            GenderDropdown(gender = gender, onGenderSelected = { selectedGender ->
                gender = selectedGender
            })


            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = woreda,
                onValueChange = { woreda = it },
                label = { Text(text = "Woreda") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = kebele,
                onValueChange = { kebele = it },
                label = { Text(text = "Kebele") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = govtIdNumber,
                onValueChange = { govtIdNumber = it },
                label = { Text(text = "Govt ID Number") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text(text = "Phone") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Pick Image Button
            ImagePicker { uri ->
                photoUri = uri.toString()
                photo = uri.toString()
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                    Text(text = stringResource(id = R.string.cancel), color = Color.White)
                }

                Button(onClick = {
                    validationErrors = validateForm() // Validate the form
                    if (validationErrors.isEmpty()) {
                        val updatedAkrabi = Akrabi(
                            id = akrabi?.id ?: 0, // Use existing ID if editing
                            akrabiNumber = akrabiNumber,
                            akrabiName = akrabiName,
                            siteName = selectedSiteName,
                            age = age.toIntOrNull() ?: 0,
                            gender = gender,
                            woreda = woreda,
                            kebele = kebele,
                            govtIdNumber = govtIdNumber,
                            phone = phone,
                            photoUri = photoUri
                        )
                        onSubmit(updatedAkrabi)
                    }
                }) {
                    Text(text = stringResource(id = R.string.submit))
                }
            }
        }
    }
}




@Composable
fun CreateAkrabiFormScreen(navController: NavController,akrabiViewModel: AkrabiViewModel,collectionSites: List<CollectionSite>) {
    CreateAkrabiForm(
        navController = navController,
        title=stringResource(id=R.string.create_akrabi_form),
        akrabi = null,
        collectionSites = collectionSites,
        onSubmit = { newAkrabi ->
            // Handle Akrabi creation, e.g., update the list
            akrabiViewModel.insertAkrabi(newAkrabi)
            // After creating, navigate back
            navController.navigate("akrabi_list_screen")
            // After creating, navigate back
           // navController.popBackStack("buy_through_akrabi_form", true)
        },
        onCancel = {
            // Navigate back without creating
            navController.popBackStack()
        }
    )
}
@Composable
fun AkrabiListScreen(
    akrabis: List<Akrabi>?,
    isLoading: Boolean, // Add a flag to indicate loading state
    onViewDetails: (Akrabi) -> Unit,
    onEdit: (Akrabi) -> Unit,
    onDelete: (Akrabi) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Show Skeleton UI when loading
        if (isLoading) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(5) { // Show a fixed number of skeleton items
                    SkeletonAkrabiItem()
                }
            }
        } else if (akrabis != null && akrabis.isNotEmpty()) {
            // Akrabi List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(akrabis.size) { index ->
                    val akrabi = akrabis[index]
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { onViewDetails(akrabi) },
                        elevation = CardDefaults.cardElevation(4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "${stringResource(id=R.string.akrabi_number)}: ${akrabi.akrabiNumber}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "${stringResource(id=R.string.akrabi_name)}: ${akrabi.akrabiName}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "${stringResource(id=R.string.site_name)}: ${akrabi.siteName}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }

                            Row {
                                IconButton(onClick = { onEdit(akrabi) }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }

                                IconButton(onClick = { onDelete(akrabi) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color.Red
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Handle the empty state if no akrabis are available
            Text(
                text = stringResource(id = R.string.no_results_found),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun SkeletonAkrabiItem() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shimmer(), // Apply shimmer effect
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(20.dp)
                        .background(Color.Gray, shape = RoundedCornerShape(4.dp))
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(20.dp)
                        .background(Color.Gray, shape = RoundedCornerShape(4.dp))
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(20.dp)
                        .background(Color.Gray, shape = RoundedCornerShape(4.dp))
                )
            }

            Row {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color.Gray, shape = RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color.Gray, shape = RoundedCornerShape(4.dp))
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AkrabiListScreenScreen(navController: NavController, darkMode: MutableState<Boolean>,
                           languageViewModel: LanguageViewModel,
                           languages: List<Language>) {
    val viewModel: AkrabiViewModel = viewModel()
    val akrabis by viewModel.akrabis.observeAsState(emptyList())

    var showDialog by remember { mutableStateOf(false) }
    var akrabiToDelete by remember { mutableStateOf<Akrabi?>(null) }

    val currentLanguage by languageViewModel.currentLanguage.collectAsState()
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("theme_mode", Context.MODE_PRIVATE)

    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var filteredItems = akrabis.filter {
        it.akrabiName.contains(searchQuery, ignoreCase = true)
    }

    // Drawer state
    var drawerOffset by remember { mutableStateOf(0f) }
    val drawerWidth = 250.dp
    val drawerWidthPx = with(LocalDensity.current) { drawerWidth.toPx() }

    var isDrawerOpen by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(true) }

    var drawerVisible by remember { mutableStateOf(false) }

    // Simulate data loading delay
    LaunchedEffect(Unit) {
        delay(2000) // Simulate loading time
        isLoading = false
    }

    // Handle drawer gesture
    val gestureModifier = Modifier
        .offset(x = drawerOffset.dp)
        .pointerInput(Unit) {
            detectDragGestures { _, dragAmount ->
                drawerOffset = (drawerOffset + dragAmount.x).coerceIn(0f, drawerWidthPx)
                isDrawerOpen = drawerOffset > 0
            }
        }


    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(text=stringResource(id=R.string.confirm))
            },
            text = {
                Text(text=stringResource(id=R.string.item_will_be_deleted))
            },
            confirmButton = {
                Button(
                    onClick = {
                        akrabiToDelete?.let { viewModel.deleteAkrabi(it) }
                        showDialog = false
                        akrabiToDelete = null
                    }
                ) {
                    Text(text=stringResource(id=R.string.delete))
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog = false
                        akrabiToDelete = null
                    }
                ) {
                    Text(text=stringResource(id=R.string.cancel))
                }
            },
            containerColor = MaterialTheme.colorScheme.background, // Background that adapts to light/dark
            tonalElevation = 6.dp // Adds a subtle shadow for better UX
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text= stringResource(id=R.string.akrabi_list)) },
                    navigationIcon = {
                        IconButton(onClick = { drawerVisible = ! drawerVisible }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = { isSearchActive = !isSearchActive }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        navController.navigate("create_akrabi_form")
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Akrabi")
                }
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                ) {
                    if (isSearchActive) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                                .padding(top = 8.dp),
                            singleLine = true,
                            leadingIcon = {
                                IconButton(onClick = { isSearchActive = false; searchQuery = "" }) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = "Close Search")
                                }
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear Search")
                                    }
                                }
                            }
                        )
                    }


                    if (filteredItems.isEmpty()) {
                         // Show "List not found" message
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(id = R.string.no_results_found),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
//                        Box(
//                            modifier = Modifier.fillMaxSize(),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Image(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(16.dp, 8.dp),
//                                painter = painterResource(id = R.drawable.no_data2),
//                                contentDescription = null
//                            )
//                        }
                    } else {


                        AkrabiListScreen(
                            akrabis = filteredItems,
                            isLoading= isLoading,
                            onViewDetails = { akrabi ->
                                // Navigate to the View Akrabi details screen
                                navController.navigate("akrabiDetails/${akrabi.id}")
                            },
                            onEdit = { akrabi ->
                                // Navigate to the Edit Akrabi form with pre-filled data
                                navController.navigate("edit_akrabi_form/${akrabi.id}")
                            },
                            onDelete = { akrabi ->
                                akrabiToDelete = akrabi
                                showDialog = true
                            }
                        )
                    }
                }
            }
        )
    }

    if (drawerVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x99000000))
                .clickable { drawerVisible = false },
            contentAlignment = Alignment.TopStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(250.dp)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .padding(16.dp)
                ) {
                    // Header
                    Text(
                        text = stringResource(id = R.string.menu),
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Divider()

                    // Scrollable Content
                    Box(modifier = Modifier.weight(1f)) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(bottom = 64.dp)
                        ) {
                            item {
                                DrawerItem(
                                    text = stringResource(id = R.string.home),
                                    painter = painterResource(R.drawable.home),
                                    onClick = {
                                        // navController.navigate("shopping")
                                        //navController.previousBackStackEntry
                                        drawerVisible = false
                                    }
                                )
                            }
                            item {
                                DrawerItem(
                                    text = stringResource(id = R.string.collection_site_registration),
                                    painter = painterResource(R.drawable.add_collection_site),
                                    onClick = {
                                        navController.navigate("siteList")
                                        drawerVisible = false
                                    }
                                )
                            }
                            item {
                                DrawerItem(
                                    text = stringResource(id = R.string.farmer_registration),
                                    painter = painterResource(R.drawable.person_add),
                                    onClick = {
                                        navController.navigate("siteList")
                                        drawerVisible = false
                                    }
                                )
                            }

                            item {
                                DrawerItem(
                                    text = stringResource(id = R.string.akrabi_registration),
                                    painter = painterResource(R.drawable.person_add),
                                    onClick = {
                                        navController.navigate("akrabi_list_screen")
                                        drawerVisible = false
                                    }
                                )
                            }

                            item {
                                Divider()
                            }
                            item {
                                // Dark Mode Toggle
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.light_dark_theme),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Switch(
                                        checked = darkMode.value,
                                        onCheckedChange = {
                                            darkMode.value = it
                                            sharedPreferences.edit().putBoolean("dark_mode", it).apply()
                                        }
                                    )
                                }
                            }
                            item {
                                Divider()
                            }
                            // using checkbox

                            item {
                                Text(
                                    text = stringResource(id = R.string.select_language),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Box(
                                    modifier = Modifier
                                        .width(230.dp)
                                        .padding(8.dp)
                                ) {
                                    var expanded by remember { mutableStateOf(false) } // Ensure expanded is inside the Box
                                    OutlinedButton(
                                        onClick = { expanded = true },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(text = currentLanguage.displayName, color = MaterialTheme.colorScheme.onBackground)

                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false },
                                        modifier = Modifier
                                            .width(230.dp) // Set the width of the DropdownMenu to match the Box
                                            .background(MaterialTheme.colorScheme.background) // Set the background color to white for visibility
                                    ) {
                                        languages.forEach { language ->
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        text = language.displayName,
                                                        color = MaterialTheme.colorScheme.onBackground
                                                    )
                                                },
                                                onClick = {
                                                    languageViewModel.selectLanguage(language, context)
                                                    expanded = false
                                                },
                                                modifier = Modifier
                                                    .background(MaterialTheme.colorScheme.background) // Ensure each menu item has a white background
                                            )
                                        }
                                    }
                                }
                            }

                            item {
                                // Logout Item
                                DrawerItem(
                                    text = stringResource(id = R.string.logout),
                                    painter = painterResource(R.drawable.logout),
                                    onClick = {
                                        // Call your logout function here
                                        // navigate to login screen or refresh UI
                                        navController.navigate("home")
                                        drawerVisible = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun EditAkrabiScreen(
    akrabiId:   Long, // Ensure this matches the type in your ViewModel
    collectionSites: List<CollectionSite>,
    viewModel: AkrabiViewModel,
    navController: NavController
) {
    // Observe LiveData from ViewModel
    val akrabi by viewModel.getAkrabiById(akrabiId).observeAsState()

    // State to control the visibility of the confirmation dialog
    var showConfirmationDialog by remember { mutableStateOf(false) }

    // Store the updated Akrabi object temporarily before confirmation
    var updatedAkrabi: Akrabi? by remember { mutableStateOf(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Display a loading indicator while waiting for data
        if (akrabi == null) {
            CircularProgressIndicator(modifier = Modifier.fillMaxSize())
        } else {
            CreateAkrabiForm(
                navController = navController,
                title= stringResource(id=R.string.edit_akrabi_form),
                akrabi = akrabi, // Pass existing data to pre-fill the form
                collectionSites = collectionSites, // Get the site names for dropdown
                onSubmit = { tempUpdatedAkrabi ->
                    // Store the updated Akrabi and show the confirmation dialog
                    updatedAkrabi = tempUpdatedAkrabi
                    showConfirmationDialog = true
                },
                onCancel = {
                    navController.navigate("akrabi_list_screen")
                }
            )
        }

        // Show confirmation dialog if the flag is set to true
        if (showConfirmationDialog && updatedAkrabi != null) {
            AlertDialog(
                onDismissRequest = { showConfirmationDialog = false },
                title = { Text(text = stringResource(id = R.string.confirm_update)) },
                text = { Text(text = stringResource(id = R.string.are_you_sure_save_the_changes)) },
                confirmButton = {
                    Button(onClick = {
                        // Update the Akrabi and navigate back to the list screen
                        viewModel.updateAkrabi(updatedAkrabi!!)
                        showConfirmationDialog = false
                        navController.navigate("akrabi_list_screen")
                    }) {
                        Text(text = stringResource(id = R.string.yes))
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        // Dismiss the dialog without updating
                        showConfirmationDialog = false
                    }) {
                        Text(text = stringResource(id = R.string.no))
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AkrabiDetailScreen(
    akrabi: Akrabi,
    navController: NavHostController,
    akrabiViewModel: AkrabiViewModel,
    onBack: () -> Unit // Callback for navigation back
) {
    var showDialog by remember { mutableStateOf(false) } // State to show/hide delete confirmation dialog
    var itemToDelete by remember { mutableStateOf<Akrabi?>(null) } // State to hold the item to delete

    fun deleteAkrabi(akrabi: Akrabi, akrabiViewModel: AkrabiViewModel) {
        akrabiViewModel.deleteAkrabi(akrabi)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.akrabi_details)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 64.dp), // Adjust the top padding here to move the Card closer to the header
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally // Center content horizontally within Column
            ) {
                // Display the image if available (non-null and non-empty)
                if (akrabi.photoUri?.isNotBlank() == true) {
                    Image(
                        painter = rememberAsyncImagePainter(akrabi.photoUri),
                        contentDescription = stringResource(R.string.bought_item_image_description),
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(4.dp),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }
                // Card for item details
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp), // Padding for horizontal spacing
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DetailText(label = stringResource(R.string.akrabi_number), value = akrabi.akrabiNumber)
                        DetailText(label = stringResource(R.string.akrabi_name), value = akrabi.akrabiName)
                        DetailText(label = stringResource(R.string.site_name), value = akrabi.siteName)
                        DetailText(label = stringResource(R.string.age), value = akrabi.age.toString())
                        DetailText(label = stringResource(R.string.gender), value = akrabi.gender)
                        DetailText(label = stringResource(R.string.woreda), value = akrabi.woreda)
                        DetailText(label = stringResource(R.string.kebele), value = akrabi.kebele)
                        DetailText(label = stringResource(R.string.gov_id_number), value = akrabi.govtIdNumber)
                        DetailText(label = stringResource(R.string.phone), value = akrabi.phone)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Row for icons
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .align(Alignment.CenterHorizontally) // Center icons horizontally
                ) {
                    IconButton(
                        onClick = {
                            // Navigate to edit screen
                            navController.navigate("edit_akrabi_form/${akrabi.id}")
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp)) // Space between icons
                    IconButton(
                        onClick = {
                            // Trigger the confirmation dialog
                            itemToDelete = akrabi
                            showDialog = true
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.Red
                        )
                    }
                }
            }
        }

        // Delete Confirmation Dialog
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = stringResource(id = R.string.confirm)) },
                text = { Text(text = stringResource(id = R.string.are_you_sure)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            itemToDelete?.let { deleteAkrabi(it, akrabiViewModel) } // Perform the delete action
                            showDialog = false // Close the dialog
                            onBack() // Navigate back after deletion
                        }
                    ) {
                        Text(text = stringResource(id = R.string.confirm))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                }
            )
        }
    }
}



