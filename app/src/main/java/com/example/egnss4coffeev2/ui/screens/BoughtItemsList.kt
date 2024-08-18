package com.example.egnss4coffeev2.ui.screens

import android.annotation.SuppressLint
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.egnss4coffeev2.database.BuyThroughAkrabi
import com.example.egnss4coffeev2.database.DirectBuy
import com.example.egnss4coffeev2.database.FarmViewModel
import kotlinx.coroutines.delay
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




@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BoughtItemsList(
    farmViewModel: FarmViewModel,
    onItemClick: (BuyThroughAkrabi) -> Unit,
    navController: NavController
) {
    val boughtItems by farmViewModel.boughtItems.collectAsStateWithLifecycle(initialValue = emptyList())

    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var filteredItems = boughtItems.filter {
        it.akrabiName.contains(searchQuery, ignoreCase = true) ||
                it.location.contains(searchQuery, ignoreCase = true)
    }

    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    var showDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<BuyThroughAkrabi?>(null) }

    // var drawerVisible by remember { mutableStateOf(false) }

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
            title = { Text(text = "Confirm Delete") },
            text = { Text(text = "Are you sure you want to delete this item? This action cannot be undone.") },
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
            // Main content for Direct Buy
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Bought Items") },
                        navigationIcon = {
//                            IconButton(onClick = { drawerVisible = !drawerVisible }) {
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
                    // Floating Action Button (FAB)
                    FloatingActionButton(
                        onClick = { navController.navigate("buy_through_akrabi/add") },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 8.dp, bottom = 72.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Buy Through Akrabi")
                    }
                }
            )
            { paddingValues ->
                // Main content
                Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                    if (isSearchActive) {
                        // Search TextField
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                                .padding(top = 8.dp), // Add padding to push it down
                            singleLine = true,
                            leadingIcon = {
                                IconButton(onClick = { isSearchActive = false ; searchQuery = "" }) {
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
                    DateRangePicker(
                        startDate = startDate,
                        endDate = endDate,
                        onStartDateChange = { startDate = it },
                        onEndDateChange = { endDate = it },
                        onApply = { farmViewModel.filterBoughtItems(startDate, endDate) },
                        onClear = {
                            startDate = ""
                            endDate = ""
                            farmViewModel.clearFilter()
                        }
                    )

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            isLoading -> {
                                CircularProgressIndicator() // Show a loading spinner
                            }

                            filteredItems.isEmpty() -> {
                                Text("No items bought yet")
                            }

                            else -> {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(
                                        horizontal = 16.dp,
                                        vertical = 8.dp
                                    )
                                ) {
                                    items(filteredItems) { item ->
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
                    }
                }
            }
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
                            val siteId = farmViewModel.getLastSiteId()
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

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BoughtItemsListDirectBuy(
    farmViewModel: FarmViewModel,
    onItemClick: (DirectBuy) -> Unit,
    navController: NavController
) {
    val boughtItemsDirectBuy by farmViewModel.boughtItemsDirectBuy.collectAsStateWithLifecycle(initialValue = emptyList())


    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var filteredItems = boughtItemsDirectBuy.filter {
        it.farmerName.contains(searchQuery, ignoreCase = true) ||
                it.location.contains(searchQuery, ignoreCase = true)
    }

    var isLoading by remember { mutableStateOf(true) }

    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    var showDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<DirectBuy?>(null) }

    var drawerVisible by remember { mutableStateOf(false) }


    // Simulate data loading delay
    LaunchedEffect(Unit) {
        delay(2000) // Simulate loading time
        isLoading = false
    }

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
        // Main content for Direct Buy
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Bought Items") },
                    navigationIcon = {
                        IconButton(onClick = { drawerVisible = !drawerVisible }) {
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
                        navController.navigate("direct_buy/add")
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end=8.dp, bottom =72.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Direct Buy")
                }
            }
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                if (isSearchActive) {
                    // Search TextField
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .padding(top = 8.dp), // Add padding to push it down
                        singleLine = true,
                        leadingIcon = {
                            IconButton(onClick = { isSearchActive = false ; searchQuery = "" }) {
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
                        farmViewModel.clearFilterDirectBuy()
                    }
                )
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        isLoading -> {
                            CircularProgressIndicator() // Show a loading spinner
                        }

                        filteredItems.isEmpty() -> {
                            Text("No items bought yet")
                        }

                        else -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                items(filteredItems) { item ->
                                    BoughtItemCardDirectBuy(
                                        item = item,
                                        onClick = {
                                            navController.navigate("bought_item_detail_direct_buy/${item.id}")
                                        },
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
                }
            }
        }

        // Sidebar Drawer Overlay
        if (drawerVisible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x99000000)) // Semi-transparent background
                    .clickable { drawerVisible = false }, // Dismiss drawer on background click
                contentAlignment = Alignment.TopStart
            ) {
                Column(
                    modifier = Modifier
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
                            drawerVisible = false
                        }
                    )

                    DrawerItem(
                        text = "Akrabi  Registration",
                        onClick = {
                            navController.navigate("akrabi_list_screen")
                            drawerVisible = false
                        }
                    )

                    DrawerItem(
                        text = "Collection Site Registaration",
                        onClick = {
                            navController.navigate("siteList")
                            drawerVisible = false
                        }
                    )

                    DrawerItem(
                        text = "Farmer Registaration",
                        onClick = {
                            val siteId = farmViewModel.getLastSiteId()
//                            navController.navigate("farmList/$siteId")
                            navController.navigate("siteList")
                            drawerVisible = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DrawerItem(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DirectBuyDetailScreen(directBuy: DirectBuy, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Item Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Text(
                text = "Direct Buy Details",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Display the image if available
            directBuy.photoUri?.let { imageUrl ->
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = "Bought Item Image",
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
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DetailText(label = "Date", value = directBuy.date.toString())
                    DetailText(label = "Time", value = directBuy.time)
                    DetailText(label = "Location", value = directBuy.location)
                    DetailText(label = "Farmer", value = directBuy.farmerName)
                    DetailText(label = "Collection Site", value = directBuy.siteName)
                    DetailText(label = "Quantity (kg)", value = "${directBuy.cherrySold} kg")
                    DetailText(label = "Price per kg", value = "$${directBuy.pricePerKg}")
                    DetailText(label = "Total Paid", value = "$${directBuy.paid}", bold = true)
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BoughtItemDetailScreen(buyThroughAkrabi: BuyThroughAkrabi, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Item Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Text(
                text = "Item Details",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Display the image if available
            buyThroughAkrabi.photoUri?.let { imageUrl ->
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = "Bought Item Image",
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
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DetailText(label = "Date", value = buyThroughAkrabi.date.toString())
                    DetailText(label = "Time", value = buyThroughAkrabi.time)
                    DetailText(label = "Location", value = buyThroughAkrabi.location)
                    DetailText(label = "Akrabi Name", value = buyThroughAkrabi.akrabiName)
                    DetailText(label = "Collection Site", value = buyThroughAkrabi.siteName)
                    DetailText(label = "Quantity (kg)", value = "${buyThroughAkrabi.cherrySold} kg")
                    DetailText(label = "Price per kg", value = "$${buyThroughAkrabi.pricePerKg}")
                    DetailText(label = "Total Paid", value = "$${buyThroughAkrabi.paid}", bold = true)
                }
            }
        }
    }
}

@Composable
fun DetailText(label: String, value: String, bold: Boolean = false) {
    Column(
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary)
        )
        Text(
            text = value,
            style = if (bold) MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            else MaterialTheme.typography.bodyLarge
        )
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp), // Added padding around the card for better spacing
        onClick = onClick,
        elevation = CardDefaults.cardElevation(4.dp) // Optional: add subtle elevation
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp), // Adjusted padding to make it more compact
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column( modifier = Modifier.weight(1f)) {
                Text(
                    text = item.akrabiName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Site: ${item.siteName}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Cherry Sold: ${item.cherrySold} kg",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Total Paid: $${item.paid}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(
                modifier = Modifier.padding(end = 0.dp)
                    .padding(horizontal = 4.dp)
            ) {
                IconButton(onClick = onEditClick, modifier = Modifier.size(36.dp) ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDeleteClick,
                    modifier = Modifier
                        .size(36.dp)
                        .offset(x = (-8).dp) ) {
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BoughtItemCardDirectBuy(
    item: DirectBuy,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp), // Added padding around the card for better spacing
        onClick = onClick,
        elevation = CardDefaults.cardElevation(4.dp) // Optional: add subtle elevation
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp), // Adjusted padding to make it more compact
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                text = item.farmerName,
                style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Site: ${item.siteName}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Cherry Sold: ${item.cherrySold} kg",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Total Paid: $${item.paid}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(
                modifier = Modifier.padding(end = 0.dp)
                    .padding(horizontal = 4.dp)
            ) {
                IconButton(onClick = onEditClick, modifier = Modifier.size(36.dp) ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDeleteClick,
                    modifier = Modifier
                        .size(36.dp)
                        .offset(x = (-8).dp) ) {
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { showDialog = true },
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
