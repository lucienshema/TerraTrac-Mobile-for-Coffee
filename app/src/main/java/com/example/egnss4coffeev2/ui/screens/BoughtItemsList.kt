package com.example.egnss4coffeev2.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.egnss4coffeev2.database.BuyThroughAkrabi
import com.example.egnss4coffeev2.database.DirectBuy
import com.example.egnss4coffeev2.database.FarmViewModel
import com.example.egnss4coffeev2.database.converters.DateConverter
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class PickImageContract : ActivityResultContract<Void?, Uri?>() {
    override fun createIntent(context: Context, input: Void?): Intent = Intent(Intent.ACTION_PICK).apply {
        type = "image/*"
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return if (resultCode == android.app.Activity.RESULT_OK) {
            intent?.data
        } else {
            null
        }
    }
}

@Composable
fun PhotoPicker(
    photoUri: Uri?,
    //onPhotoSelected: (Uri) -> Unit,
    onPickPhotoClick: () -> Unit,
    onRemovePhotoClick: () -> Unit
) {
    Column {
        // Display the image if it's selected
        if (photoUri != null) {
            Image(
                painter = rememberAsyncImagePainter(photoUri),
                contentDescription = "Selected Photo",
                modifier = Modifier
                    .size(100.dp)
                    .padding(bottom = 8.dp),
                contentScale = ContentScale.Crop
            )
            // Button to remove the photo
            IconButton(
                onClick = { onRemovePhotoClick() },
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove Photo")
            }
        }

        // TextField for showing the photo selection option
        TextField(
            value = photoUri?.toString() ?: "No Photo Selected",
            onValueChange = {},
            label = { Text("Photo") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { onPickPhotoClick() }) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "Pick Photo")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateRangePicker(
    startDate: String,
    endDate: String,
    onStartDateChange: (String) -> Unit,
    onEndDateChange: (String) -> Unit,
    onApply: () -> Unit,
    onClear: () -> Unit // New parameter for clearing the date selection
) {
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Select Date Range", style = MaterialTheme.typography.labelLarge)

        Spacer(modifier = Modifier.height(8.dp))
        // Row for Start Date and End Date Pickers
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            OutlinedTextField(
                value = startDate,
                onValueChange = { /* Disable manual input */ },
                label = { Text("Start Date") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp), // Adds space between the two text fields
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showStartDatePicker = true }) {
                        Icon(imageVector = Icons.Default.DateRange, contentDescription = null)
                    }
                }
            )

            OutlinedTextField(
                value = endDate,
                onValueChange = { /* Disable manual input */ },
                label = { Text("End Date") },
                modifier = Modifier.weight(1f),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showEndDatePicker = true }) {
                        Icon(imageVector = Icons.Default.DateRange, contentDescription = null)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        // Row for Apply and Clear Buttons
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(
                onClick = onApply,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp), // Adds space between the buttons
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), // Reduces button size
                enabled = startDate.isNotBlank() && endDate.isNotBlank()
            ) {
                Text("Apply")
            }

            Button(
                onClick = onClear,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), // Reduces button size
                enabled = startDate.isNotBlank() && endDate.isNotBlank()
            ) {
                Text("Clear")
            }
        }
        // Start Date Picker Dialog
        if (showStartDatePicker) {
            android.app.DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    onStartDateChange("${year}-${month + 1}-${dayOfMonth}")
                    showStartDatePicker = false
                },
                LocalDate.now().year,
                LocalDate.now().monthValue - 1,
                LocalDate.now().dayOfMonth
            ).apply {
                setOnDismissListener { showStartDatePicker = false }
            }.show()
        }

        // End Date Picker Dialog
        if (showEndDatePicker) {
            android.app.DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    onEndDateChange("${year}-${month + 1}-${dayOfMonth}")
                    showEndDatePicker = false
                },
                LocalDate.now().year,
                LocalDate.now().monthValue - 1,
                LocalDate.now().dayOfMonth
            ).apply {
                setOnDismissListener { showEndDatePicker = false }
            }.show()
        }
    }
}




@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BoughtItemsList(
    farmViewModel: FarmViewModel,
    onItemClick: (BuyThroughAkrabi) -> Unit,
    navController: NavController
) {
    val boughtItems by farmViewModel.boughtItems.collectAsStateWithLifecycle(initialValue = emptyList())

    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    var showDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<BuyThroughAkrabi?>(null) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(text = "Confirm Delete")
            },
            text = {
                Text(text = "Are you sure you want to delete this item? This action cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        itemToDelete?.let { farmViewModel.deleteBoughtItemBuyThroughAkrabi(it) }
                        showDialog = false
                        itemToDelete = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog = false
                        itemToDelete = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Bought Items",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            DateRangePicker(
                startDate = startDate,
                endDate = endDate,
                onStartDateChange = { startDate = it },
                onEndDateChange = { endDate = it },
                onApply = {
                    farmViewModel.filterBoughtItems(startDate, endDate)
                },
                onClear = {
                    startDate = ""
                    endDate = ""
                    // Optionally refresh the list with no filter
                    farmViewModel.filterBoughtItems("", "")
                }
            )

            if (boughtItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No items bought yet")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(boughtItems) { item ->
                        BoughtItemCard(
                            item = item,
                            onClick = { onItemClick(item) },
                            onEditClick = {
                                navController.navigate("buy_through_akrabi/edit/${item.id}")
                            },
                            onDeleteClick = {
                                itemToDelete = item
                                showDialog = true
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        // Floating Action Button (FAB)
        FloatingActionButton(
            onClick = {
                navController.navigate("buy_through_akrabi/add")
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Buy Through Akrabi")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BoughtItemsListDirectBuy(
    farmViewModel: FarmViewModel,
    onItemClick: (DirectBuy) -> Unit,
    navController: NavController
) {
    val boughtItemsDirectBuy by farmViewModel.boughtItemsDirectBuy.collectAsStateWithLifecycle(initialValue = emptyList())

    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    var showDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<DirectBuy?>(null) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(text = "Confirm Delete")
            },
            text = {
                Text(text = "Are you sure you want to delete this item? This action cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        itemToDelete?.let { farmViewModel.deleteBoughtItemDirectBuy(it) }
                        showDialog = false
                        itemToDelete = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog = false
                        itemToDelete = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Bought Items",
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            DateRangePicker(
                startDate = startDate,
                endDate = endDate,
                onStartDateChange = { startDate = it },
                onEndDateChange = { endDate = it },
                onApply = {
                    farmViewModel.filterBoughtItemsDirectBuy(startDate, endDate)
                },
                onClear = {
                    startDate = ""
                    endDate = ""
                    farmViewModel.clearFilter()
                }
            )

            if (boughtItemsDirectBuy.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No items bought yet")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(boughtItemsDirectBuy) { item ->
                        BoughtItemCardDirectBuy(
                            item = item,
                            onClick = { onItemClick(item) },
                            onEditClick = {
                                navController.navigate("direct_buy/edit/${item.id}")
                            },
                            onDeleteClick = {
                                itemToDelete = item
                                showDialog = true
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        // Floating Action Button (FAB)
        FloatingActionButton(
            onClick = {
                navController.navigate("direct_buy/add")
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Direct Buy")
        }
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BoughtItemCard(
    item: BuyThroughAkrabi,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Date: ${item.date.format(DateTimeFormatter.ISO_LOCAL_DATE)}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Time: ${item.time}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Site: ${item.siteName}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Akrabi: ${item.akrabiName}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Cherry Sold: ${item.cherrySold} kg",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Price per kg: $${item.pricePerKg}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Total Paid: $${item.paid}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Row {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BoughtItemCardDirectBuy(
    item: DirectBuy,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Date: ${item.date.format(DateTimeFormatter.ISO_LOCAL_DATE)}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Time: ${item.time}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Site: ${item.siteName}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Akrabi: ${item.farmerName}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Cherry Sold: ${item.cherrySold} kg",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Price per kg: $${item.pricePerKg}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Total Paid: $${item.paid}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
            Row {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}

@Composable
fun BoughtItemDetailScreen(
    itemId: Long,
    farmViewModel: FarmViewModel,
    onNavigateBack: () -> Unit
) {
    // Fetch the specific item using the itemId
    val item by farmViewModel.getBoughtItemById(itemId).collectAsStateWithLifecycle(initialValue = null)

    item?.let { buyThroughAkrabi ->
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Item Details", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Date: ${buyThroughAkrabi.date}")
            Text("Time: ${buyThroughAkrabi.time}")
            Text("Location: ${buyThroughAkrabi.location}")
            Text("Site Name: ${buyThroughAkrabi.siteName}")
            Text("Akrabi Name: ${buyThroughAkrabi.akrabiName}")
            Text("Cherry Sold: ${buyThroughAkrabi.cherrySold} kg")
            Text("Price per kg: $${buyThroughAkrabi.pricePerKg}")
            Text("Total Paid: $${buyThroughAkrabi.paid}")

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onNavigateBack) {
                Text("Back to List")
            }
        }
    } ?: Text("Loading...")
}


@Composable
fun BoughtItemDetailScreenDirectBuy(
    itemId: Long,
    farmViewModel: FarmViewModel,
    onNavigateBack: () -> Unit
) {
    // Fetch the specific item using the itemId
    val item by farmViewModel.getBoughtItemDirectBuyById(itemId).collectAsStateWithLifecycle(initialValue = null)

    item?.let { buyThroughAkrabi ->
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Item Details", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Date: ${buyThroughAkrabi.date}")
            Text("Time: ${buyThroughAkrabi.time}")
            Text("Location: ${buyThroughAkrabi.location}")
            Text("Site Name: ${buyThroughAkrabi.siteName}")
            Text("Akrabi Name: ${buyThroughAkrabi.farmerName}")
            Text("Cherry Sold: ${buyThroughAkrabi.cherrySold} kg")
            Text("Price per kg: $${buyThroughAkrabi.pricePerKg}")
            Text("Total Paid: $${buyThroughAkrabi.paid}")

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onNavigateBack) {
                Text("Back to List")
            }
        }
    } ?: Text("Loading...")
}


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

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirm Update") },
            text = { Text("Are you sure you want to save these changes?") },
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
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Update Bought Item", style = MaterialTheme.typography.headlineMedium)

        // Date field
        TextField(
            value = date.toString(),
            onValueChange = { /* Disable manual input, use a date picker instead */ },
            label = { Text("Date") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { /* Show date picker dialog */ }) {
                    Icon(imageVector = Icons.Default.DateRange, contentDescription = null)
                }
            }
        )

        // Time field
        TextField(
            value = time.toString(),
            onValueChange = { /* Disable manual input, use a time picker instead */ },
            label = { Text("Time") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { /* Show time picker dialog */ }) {
                    Icon(imageVector = Icons.Default.DateRange, contentDescription = null)
                }
            }
        )

        TextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location") }
        )

        TextField(
            value = selectedSiteName,
            onValueChange = { selectedSiteName = it },
            label = { Text("Site Name") }
        )

        TextField(
            value = farmerSearch,
            onValueChange = { farmerSearch = it },
            label = { Text("Farmer Search") }
        )

        TextField(
            value = farmerNumber,
            onValueChange = { farmerNumber = it },
            label = { Text("Farmer Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        TextField(
            value = farmerName,
            onValueChange = { farmerName = it },
            label = { Text("Farmer Name") }
        )

        TextField(
            value = cherrySold,
            onValueChange = { cherrySold = it },
            label = { Text("Cherry Sold") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        TextField(
            value = pricePerKg,
            onValueChange = { pricePerKg = it },
            label = { Text("Price Per Kg") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        TextField(
            value = paid,
            onValueChange = { paid = it },
            label = { Text("Paid") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        // Photo Picker
        PhotoPicker(
            photoUri = photoUri,
//            onPhotoSelected = { uri -> photoUri = uri },
            onPickPhotoClick = { pickPhoto() },
            onRemovePhotoClick = { removePhoto() }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { showDialog = true },
                modifier = Modifier.weight(1f)
            ) {
                Text("Save Changes")
            }

            Button(
                onClick = { navController.popBackStack() }, // Navigate back without saving
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }
        }
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditBuyThroughAkrabiForm(
    itemId: Long,
    farmViewModel: FarmViewModel,
    navController: NavController
) {
    // Fetch the item data based on the itemId
    val item by farmViewModel.getBoughtItemById(itemId).collectAsStateWithLifecycle(null)

    var date by remember { mutableStateOf(LocalDate.now()) }
    var time by remember { mutableStateOf(LocalTime.now()) }
    var location by remember { mutableStateOf("") }
    var selectedSiteName by remember { mutableStateOf("") }
    var akrabiSearch by remember { mutableStateOf("") }
    var akrabiNumber by remember { mutableStateOf("") }
    var akrabiName by remember { mutableStateOf("") }
    var cherrySold by remember { mutableStateOf("") }
    var pricePerKg by remember { mutableStateOf("") }
    var paid by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    var showConfirmationDialog by remember { mutableStateOf(false) }
    LaunchedEffect(item) {
        item?.let {
            date = it.date // Assuming date is stored as a String in ISO format
            time = LocalTime.parse(it.time) // Assuming time is stored as a String in ISO format
            location = it.location
            selectedSiteName = it.siteName
            akrabiSearch = it.akrabiSearch
            akrabiNumber = it.akrabiNumber
            akrabiName = it.akrabiName
            cherrySold = it.cherrySold.toString()
            pricePerKg = it.pricePerKg.toString()
            paid = it.paid.toString()
            photoUri = it.photo?.let { Uri.parse(it) } // Assuming photo is stored as a String URI
        }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            photoUri = it
        }
    }

    // Call this when the user clicks the "Pick Photo" button
    fun pickPhoto() {
        pickImageLauncher.launch("image/*")
    }

    // Call this when the user clicks the "Remove Photo" button
    fun removePhoto() {
        photoUri = null
    }

    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text(text = "Confirm Update") },
            text = { Text(text = "Are you sure you want to save these changes?") },
            confirmButton = {
                Button(
                    onClick = {
                            item?.let {
                                val updatedItem = it.copy(
                                    date = date,
                                    time = time.toString(),
                                    location = location,
                                    siteName = selectedSiteName,
                                    akrabiSearch = akrabiSearch,
                                    akrabiNumber = akrabiNumber,
                                    akrabiName = akrabiName,
                                    cherrySold = cherrySold.toDouble(),
                                    pricePerKg = pricePerKg.toDouble(),
                                    paid = paid.toDouble(),
                                    photoUri = photoUri?.toString()
                                )
                                farmViewModel.updateBuyThroughAkrabi(updatedItem)
                                navController.popBackStack()
                            }
                        showConfirmationDialog = false
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showConfirmationDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Update Bought Item", style = MaterialTheme.typography.headlineMedium)

        // Date and Time pickers
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextField(
                value = date.toString(),
                onValueChange = { date = LocalDate.parse(it) },
                label = { Text("Date") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { /* Show date picker */ }) {
                        Icon(imageVector = Icons.Default.DateRange, contentDescription = "Pick Date")
                    }
                }
            )
            TextField(
                value = time.toString(),
                onValueChange = { time = LocalTime.parse(it) },
                label = { Text("Time") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { /* Show time picker */ }) {
                        Icon(imageVector = Icons.Default.DateRange, contentDescription = "Pick Time")
                    }
                }
            )
        }

        TextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location") }
        )

        TextField(
            value = selectedSiteName,
            onValueChange = { selectedSiteName = it },
            label = { Text("Site Name") }
        )

        TextField(
            value = akrabiSearch,
            onValueChange = { akrabiSearch = it },
            label = { Text("Akrabi Search") }
        )

        TextField(
            value = akrabiNumber,
            onValueChange = { akrabiNumber = it },
            label = { Text("Akrabi Number") }
        )

        TextField(
            value = akrabiName,
            onValueChange = { akrabiName = it },
            label = { Text("Akrabi Name") }
        )

        TextField(
            value = cherrySold,
            onValueChange = { cherrySold = it },
            label = { Text("Cherry Sold") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        TextField(
            value = pricePerKg,
            onValueChange = { pricePerKg = it },
            label = { Text("Price Per Kg") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        TextField(
            value = paid,
            onValueChange = { paid = it },
            label = { Text("Paid") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        PhotoPicker(
            photoUri = photoUri,
//            onPhotoSelected = { uri -> photoUri = uri },
            onPickPhotoClick = { pickPhoto() },
            onRemovePhotoClick = { removePhoto() },
        )


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { showConfirmationDialog = true },
                modifier = Modifier.weight(1f)
            ) {
                Text("Save Changes")
            }

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Cancel", color = Color.White)
            }
        }
    }
}
