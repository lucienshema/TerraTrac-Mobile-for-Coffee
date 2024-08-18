package com.example.egnss4coffeev2.ui.screens

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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.egnss4coffeev2.database.Akrabi
import com.example.egnss4coffeev2.database.AkrabiViewModel
import com.example.egnss4coffeev2.database.DirectBuy
import com.example.egnss4coffeev2.database.Farm
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
                value = "Select a Photo",
                onValueChange = {}, // No-op since the field is read-only
                label = { Text("Photo") },
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
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
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
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
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

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Buy Through Akrabi") }
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
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .clickable { showDatePicker = true }
//                    ) {
//                        OutlinedTextField(
//                            value = date.format(DateTimeFormatter.ISO_LOCAL_DATE),
//                            onValueChange = { },
//                            label = { Text("Date") },
//                            modifier = Modifier.fillMaxWidth(),
//                            readOnly = true,
//                            enabled = false
//                        )
//                    }
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    // Time input
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .clickable { showTimePicker = true }
//                    ) {
//                        OutlinedTextField(
//                            value = time.format(DateTimeFormatter.ofPattern("HH:mm")),
//                            onValueChange = { },
//                            label = { Text("Time") },
//                            modifier = Modifier.fillMaxWidth(),
//                            readOnly = true,
//                            enabled = false
//                        )
//                    }
//                    Spacer(modifier = Modifier.height(8.dp))

                    // Location input
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Location") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Dropdown for selecting site name
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedSiteName,
                            onValueChange = { selectedSiteName = it },
                            label = { Text("Select or Create a new Site") },
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { expandedSites = true }) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Select Site Name"
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
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
                                text = { Text("Create New Site") },
                                onClick = {
                                    expandedSites = false
                                    selectedSiteName = "" // Clear the selected site name
                                    navController.navigate("addSite") // Navigate to the add site screen
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    ExposedDropdownMenuBox(
                        expanded = expandedAkrabis,
                        onExpandedChange = { expandedAkrabis = !expandedAkrabis }
                    ) {
                        OutlinedTextField(
                            value = selectedAkrabi?.akrabiName ?: "Select or Create Akrabi",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Akrabi") },
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
                                text = { Text("Create New Akrabi") },
                                onClick = {
                                    // Handle navigation to the CreateAkrabiForm
                                    navController.navigate("akrabi_list_screen")
                                    expandedAkrabis = false
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Akrabi Number input (for new Akrabi)
                    if (selectedAkrabi == null) {
                        OutlinedTextField(
                            value = akrabiNumber,
                            onValueChange = { akrabiNumber = it },
                            label = { Text("Akrabi Number") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Akrabi Name input (for new Akrabi)
                    if (selectedAkrabi == null) {
                        OutlinedTextField(
                            value = akrabiName,
                            onValueChange = { akrabiName = it },
                            label = { Text("Akrabi Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Cherry sold input
                    OutlinedTextField(
                        value = cherrySold,
                        onValueChange = { cherrySold = it },
                        label = { Text("Cherry Sold (kg)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Price per kg input
                    OutlinedTextField(
                        value = pricePerKg,
                        onValueChange = { pricePerKg = it },
                        label = { Text("Price per kg") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Paid input
                    OutlinedTextField(
                        value = paid,
                        onValueChange = { paid = it },
                        label = { Text("Paid") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Pick Image Button
                    ImagePicker { uri ->
                        photoUri = uri
                        photo = uri?.toString() ?: ""
                    }

                    // Display selected image
                    photoUri?.let {
                        Image(
                            painter = rememberImagePainter(it),
                            contentDescription = "Selected image",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Gray)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Submit button
                    Button(
                        onClick = {
                            if (selectedAkrabi == null) {
                                val newAkrabi = Akrabi(
                                    akrabiNumber = akrabiNumber,
                                    akrabiName = akrabiName,
                                    siteName = selectedSiteName
                                )
                                onCreateAkrabi(newAkrabi)
                            }
                            val buyThroughAkrabi = BuyThroughAkrabi(
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
                            onSubmit(buyThroughAkrabi)

                            navController.navigate("bought_items_buy_through_Akrabi")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Submit")
                    }
                }
            }
}




@OptIn(ExperimentalMaterial3Api::class)
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

    // Update the filteredFarmers whenever the selected site changes
    LaunchedEffect(selectedSiteName) {
        filteredFarmers = farmers.filter { it.siteId == selectedSiteId }
    }


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
                title = { Text("Direct Buy") }
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
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .clickable { showDatePicker = true }
//            ) {
//                OutlinedTextField(
//                    value = date.format(DateTimeFormatter.ISO_LOCAL_DATE),
//                    onValueChange = { },
//                    label = { Text("Date") },
//                    modifier = Modifier.fillMaxWidth(),
//                    readOnly = true,
//                    enabled = false
//                )
//            }
//            Spacer(modifier = Modifier.height(8.dp))
//
//            // Time input
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .clickable { showTimePicker = true }
//            ) {
//                OutlinedTextField(
//                    value = time.format(DateTimeFormatter.ofPattern("HH:mm")),
//                    onValueChange = { },
//                    label = { Text("Time") },
//                    modifier = Modifier.fillMaxWidth(),
//                    readOnly = true,
//                    enabled = false
//                )
//            }
//            Spacer(modifier = Modifier.height(8.dp))

            // Location input
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Dropdown for selecting site name
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedSiteName,
                    onValueChange = { selectedSiteName = it },
                    label = { Text("Select or Create a new Site") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { expandedSites = true }) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select Site Name"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
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
                        text = { Text("Create New Site") },
                        onClick = {
                            expandedSites = false
                            selectedSiteName = "" // Clear the selected site name
                            navController.navigate("addSite") // Navigate to the add site screen
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Dropdown for farmer name
            ExposedDropdownMenuBox(
                expanded = expandedFarmers,
                onExpandedChange = { expandedFarmers = !expandedFarmers }
            ) {
                OutlinedTextField(
                    value = farmerName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Farmer Name") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFarmers) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedFarmers,
                    onDismissRequest = { expandedFarmers = false }
                ) {
                    filteredFarmers.forEach { farmer ->
                        DropdownMenuItem(
                            text = { Text(farmer.farmerName) },
                            onClick = {
                                farmerName = farmer.farmerName
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

            Spacer(modifier = Modifier.height(8.dp))

            // Cherry sold input
            OutlinedTextField(
                value = cherrySold,
                onValueChange = { cherrySold = it },
                label = { Text("Cherry Sold (kg)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Price per kg input
            OutlinedTextField(
                value = pricePerKg,
                onValueChange = { pricePerKg = it },
                label = { Text("Price per kg") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Paid input
            OutlinedTextField(
                value = paid,
                onValueChange = { paid = it },
                label = { Text("Paid") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Pick Image Button
            ImagePicker { uri ->
                photoUri = uri
                photo = uri?.toString() ?: ""
            }

            // Display selected image
            photoUri?.let {
                Image(
                    painter = rememberImagePainter(it),
                    contentDescription = "Selected image",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Gray)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Submit button
            Button(
                onClick = {
                    val directBuy = DirectBuy(
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
                    onSubmit(directBuy)

                    navController.navigate("bought_items_direct_buy")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit")
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

    var dropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        OutlinedTextField(
            value = akrabiNumber,
            onValueChange = { akrabiNumber = it },
            label = { Text("Akrabi Number") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = akrabiName,
            onValueChange = { akrabiName = it },
            label = { Text("Akrabi Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Dropdown for selecting site name
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedSiteName,
                onValueChange = { selectedSiteName = it },
                label = { Text("Site Name") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { dropdownExpanded = true }) {
                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Select Site Name")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            DropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false }
            ) {
                // Existing site names
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
                    text = { Text("Create New Site") },
                    onClick = {
                        dropdownExpanded = false
                        selectedSiteName = "" // Clear the selected site name
                        navController.navigate("addSite") // Navigate to the add site screen
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onCancel) {
                Text("Cancel")
            }

            Button(onClick = {
                val updatedAkrabi = Akrabi(
                    id = akrabi?.id ?: 0, // Use existing ID if editing
                    akrabiNumber = akrabiNumber,
                    akrabiName = akrabiName,
                    siteName = selectedSiteName
                )
                onSubmit(updatedAkrabi)
            }) {
                Text("Submit")
            }
        }
    }
}



@Composable
fun CreateAkrabiFormScreen(navController: NavController,akrabiViewModel: AkrabiViewModel,collectionSites: List<CollectionSite>) {
    CreateAkrabiForm(
        navController = navController,
        title="Create Akrabi Form",
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
    akrabis: List<Akrabi>,
    onEdit: (Akrabi) -> Unit,
    onDelete: (Akrabi) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
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
                        .padding(vertical = 8.dp),
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
                                text = "Number: ${akrabi.akrabiNumber}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Name: ${akrabi.akrabiName}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Site: ${akrabi.siteName}",
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
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AkrabiListScreenScreen(navController: NavController) {
    val viewModel: AkrabiViewModel = viewModel()
    val akrabis by viewModel.akrabis.observeAsState(emptyList())

    var showDialog by remember { mutableStateOf(false) }
    var akrabiToDelete by remember { mutableStateOf<Akrabi?>(null) }

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
                Text(text = "Confirm Delete")
            },
            text = {
                Text(text = "Are you sure you want to delete ${akrabiToDelete?.akrabiName}? This action cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        akrabiToDelete?.let { viewModel.deleteAkrabi(it) }
                        showDialog = false
                        akrabiToDelete = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog = false
                        akrabiToDelete = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Akrabi List") },
                    navigationIcon = {
                        IconButton(onClick = { isDrawerOpen = !isDrawerOpen }) {
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


            AkrabiListScreen(
                akrabis = akrabis,
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
        )
    }

    // Sidebar Drawer Overlay
    // if (drawerVisible) {
    if (isDrawerOpen) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x99000000)) // Semi-transparent background
                // .clickable { drawerVisible = false }, // Dismiss drawer on background click
                .clickable { isDrawerOpen = false },
            contentAlignment = Alignment.TopStart
        ) {
            Column(
                //modifier = Modifier
                modifier = gestureModifier
                    .fillMaxHeight()
                    .width(250.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Menu",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Divider()
                Spacer(modifier = Modifier.height(16.dp))

                DrawerItem(
                    text = "Home",
                    onClick = {
                        navController.navigate("home")
                        // drawerVisible = false
                        isDrawerOpen = false
                    }
                )
                DrawerItem(
                    text = "Akrabi  Registration",
                    onClick = {
                        navController.navigate("akrabi_list_screen")
                        //drawerVisible = false
                        isDrawerOpen = false
                    }
                )

                DrawerItem(
                    text = "Collection Site Registaration",
                    onClick = {
                        navController.navigate("siteList")
                        // drawerVisible = false
                        isDrawerOpen = false
                    }
                )

                DrawerItem(
                    text = "Farmer Registaration",
                    onClick = {
                        //val siteId = farmViewModel.getLastSiteId()
//                            navController.navigate("farmList/$siteId")
                        navController.navigate("siteList")
                        // drawerVisible = false
                        isDrawerOpen = false
                    }
                )


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

    // Logging for debugging
    println("Akrabi ID $akrabiId")
    println("Akrabi $akrabi")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
//        Text(
//            text = "Edit Akrabi Form",
//            style = MaterialTheme.typography.headlineMedium
//        )
        // Display a loading indicator while waiting for data
        if (akrabi == null) {
            CircularProgressIndicator(modifier = Modifier.fillMaxSize())
        } else {
            CreateAkrabiForm(
                navController = navController,
                title="Edit Akrabi Form",
                akrabi = akrabi, // Pass existing data to pre-fill the form
                collectionSites = collectionSites, // Get the site names for dropdown
                onSubmit = { updatedAkrabi ->
                    viewModel.updateAkrabi(updatedAkrabi)
                    navController.navigate("akrabi_list_screen")
                },
                onCancel = {
                    navController.navigate("akrabi_list_screen")
                }
            )
        }
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BuyThroughAkrabiFormScreen(navController: NavController) {
    // Assume you have collectionSites and akrabis available here
    var collectionSites by remember { mutableStateOf(listOf<CollectionSite>()) }
    var akrabis by remember { mutableStateOf(listOf<Akrabi>()) }

    BuyThroughAkrabiForm(
        collectionSites = collectionSites,
        akrabis = akrabis,
        onCreateAkrabi = { newAkrabi ->
            // Navigate to CreateAkrabiFor
            navController.navigate("create_akrabi_form")
        },
        onSubmit = { buyThroughAkrabi ->
            // Handle form submission
            // e.g., save data or navigate to another screen
        },
        navController = navController
    )
}
