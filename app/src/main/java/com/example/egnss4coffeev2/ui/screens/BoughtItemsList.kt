package com.example.egnss4coffeev2.ui.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.egnss4coffeev2.R
import com.example.egnss4coffeev2.database.Akrabi
import com.example.egnss4coffeev2.database.AkrabiViewModel
import com.example.egnss4coffeev2.database.BuyThroughAkrabi
import com.example.egnss4coffeev2.database.DirectBuy
import com.example.egnss4coffeev2.database.FarmViewModel
import com.example.egnss4coffeev2.utils.Language
import com.example.egnss4coffeev2.utils.LanguageViewModel
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.delay
import java.io.BufferedWriter
import java.io.File
import java.io.IOException
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


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

        val displayText = photoUri?.toString()?.let {  stringResource(R.string.photo_selected) } ?: stringResource(R.string.no_photo_selected)
        OutlinedTextField(
            value = displayText,
            onValueChange = {}, // No-op since the field is read-only
            label = { Text("Photo") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { onPickPhotoClick()}) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "Pick Photo")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onPickPhotoClick() } // Allow clicking anywhere on the text field to pick a photo
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
        Text(text= stringResource(id =R.string.select_date_range), style = MaterialTheme.typography.labelLarge)

        Spacer(modifier = Modifier.height(8.dp))
        // Row for Start Date and End Date Pickers
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            OutlinedTextField(
                value = startDate,
                onValueChange = { /* Disable manual input */ },
                label = { Text(text= stringResource(id =R.string.start_date), fontSize = 12.sp) },
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
                label = { Text(text= stringResource(id =R.string.end_date),fontSize = 12.sp) },
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
                Text(text= stringResource(id =R.string.apply))
            }

            Button(
                onClick = onClear,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), // Reduces button size
                enabled = startDate.isNotBlank() && endDate.isNotBlank()
            ) {
                Text(text= stringResource(id =R.string.clear))
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
    navController: NavController,
    darkMode: MutableState<Boolean>,
    languageViewModel: LanguageViewModel,
    languages: List<Language>
) {
    val boughtItems by farmViewModel.boughtItems.collectAsStateWithLifecycle(initialValue = emptyList())

    val currentLanguage by languageViewModel.currentLanguage.collectAsState()
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("theme_mode", Context.MODE_PRIVATE)

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

    var drawerVisible by remember { mutableStateOf(false) }

    // Drawer state
    var drawerOffset by remember { mutableStateOf(0f) }
    val drawerWidth = 250.dp
    val drawerWidthPx = with(LocalDensity.current) { drawerWidth.toPx() }

    var isDrawerOpen by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(true) }

    var action by remember { mutableStateOf<Action?>(null) }
    var showFormatDialog by remember { mutableStateOf(false) }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var exportFormat by remember { mutableStateOf("") }
    val activity = context as Activity


    fun createFile(
        context: Context,
        uri: Uri,
    ): Boolean {
        // Get the current date and time
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val filename = if (exportFormat == "CSV") "direct_buy_items_$timestamp.csv" else "direct_buy_items_$timestamp.geojson"

        try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                BufferedWriter(OutputStreamWriter(outputStream)).use { writer ->
                    if (exportFormat == "CSV") {
                        writer.write(
                            "id,akrabi_name,akrabi_number,akrabi_search,site_name,location,cherry_sold,price_per_kg,paid,photo,photo_uri,date,time,location\n"
                        )
                        filteredItems.forEach { buyThroughAkrabi ->
                            val line = "${buyThroughAkrabi.id},${buyThroughAkrabi.akrabiName},${buyThroughAkrabi.akrabiNumber},${buyThroughAkrabi.siteName},${buyThroughAkrabi.location},${buyThroughAkrabi.cherrySold},${buyThroughAkrabi.pricePerKg},${buyThroughAkrabi.paid},${buyThroughAkrabi.photo},${buyThroughAkrabi.photoUri},${buyThroughAkrabi.date},${buyThroughAkrabi.time}\n"
                            writer.write(line)
                        }
                    } else {
                        val geoJson =
                            buildString {
                                append("{\"type\": \"FeatureCollection\", \"features\": [")
                                filteredItems.forEachIndexed { index, buyThroughAkrabi ->

                                    val feature = """
                                    {
                                        "type": "Feature",
                                        "properties": {
                                            "id": "${buyThroughAkrabi.id}",
                                            "date": "${buyThroughAkrabi.date}",
                                            "time": "${buyThroughAkrabi.time}",
                                            "location": "${buyThroughAkrabi.location}",
                                            "site_name": "${buyThroughAkrabi.siteName}",
                                            "akrabi_search": "${buyThroughAkrabi.akrabiSearch}",
                                            "akrabi_number": "${buyThroughAkrabi.akrabiNumber}",
                                            "akrabi_name": "${buyThroughAkrabi.akrabiName}",
                                            "cherry_sold": ${buyThroughAkrabi.cherrySold},
                                            "price_per_kg": ${buyThroughAkrabi.pricePerKg},
                                            "paid": ${buyThroughAkrabi.paid},
                                            "photo": "${buyThroughAkrabi.photo}",
                                            "photo_uri": "${buyThroughAkrabi.photoUri ?: ""}",
                                            "created_at": "${buyThroughAkrabi.date}",
                                            "updated_at": "${buyThroughAkrabi.date}" 
                                        }
                                    }
                                """.trimIndent()

                                    append(feature)
                                    if (index < filteredItems.size - 1) append(",")
                                }
                                append("]}")
                            }
                        writer.write(geoJson)
                    }
                }
            }
            return true
        } catch (e: IOException) {
            Toast.makeText(context, R.string.error_export_msg, Toast.LENGTH_SHORT).show()
            return false
        }
    }

    fun createFileForSharing(): File? {
        // Get the current date and time
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val filename = if (exportFormat == "CSV") "direct_buy_items_$timestamp.csv" else "direct_buy_items_$timestamp.geojson"
        val mimeType = if (exportFormat == "CSV") "text/csv" else "application/geo+json"
        // Get the Downloads directory
        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, filename)

        try {
            file.bufferedWriter().use { writer ->
                if (exportFormat == "CSV") {
                    writer.write(
                        "id,akrabi_name,akrabi_number,akrabi_search,site_name,location,cherry_sold,price_per_kg,paid,photo,photo_uri,date,time,location\n",
                    )
                    filteredItems.forEach { buyThroughAkrabi ->
                        val line = "${buyThroughAkrabi.id},${buyThroughAkrabi.akrabiName},${buyThroughAkrabi.akrabiNumber},${buyThroughAkrabi.akrabiSearch},${buyThroughAkrabi.siteName},${buyThroughAkrabi.location},${buyThroughAkrabi.cherrySold},${buyThroughAkrabi.pricePerKg},${buyThroughAkrabi.paid},${buyThroughAkrabi.photo},${buyThroughAkrabi.photoUri},${buyThroughAkrabi.date},${buyThroughAkrabi.time}\n"
                        writer.write(line)
                    }
                } else {
                    val geoJson =
                        buildString {
                            append("{\"type\": \"FeatureCollection\", \"features\": [")
                            filteredItems.forEachIndexed { index, buyThroughAkrabi->
                                val feature = """
                                    {
                                        "type": "Feature",
                                        "properties": {
                                            "id": "${buyThroughAkrabi.id}",
                                            "date": "${buyThroughAkrabi.date}",
                                            "time": "${buyThroughAkrabi.time}",
                                            "location": "${buyThroughAkrabi.location}",
                                            "site_name": "${buyThroughAkrabi.siteName}",
                                            "akrabi_search": "${buyThroughAkrabi.akrabiSearch}",
                                            "akrabi_number": "${buyThroughAkrabi.akrabiNumber}",
                                            "akrabi_name": "${buyThroughAkrabi.akrabiName}",
                                            "cherry_sold": ${buyThroughAkrabi.cherrySold},
                                            "price_per_kg": ${buyThroughAkrabi.pricePerKg},
                                            "paid": ${buyThroughAkrabi.paid},
                                            "photo": "${buyThroughAkrabi.photo}",
                                            "photo_uri": "${buyThroughAkrabi.photoUri ?: ""}",
                                            "created_at": "${buyThroughAkrabi.date}",
                                            "updated_at": "${buyThroughAkrabi.date}" 
                                        }
                                    }
                                """.trimIndent()

                                append(feature)
                                if (index < filteredItems.size - 1) append(",")
                            }
                            append("]}")
                        }
                    writer.write(geoJson)
                }
            }
            return file
        } catch (e: IOException) {
            Toast.makeText(context, R.string.error_export_msg, Toast.LENGTH_SHORT).show()
            return null
        }
    }

    val createDocumentLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    val context = activity?.applicationContext
                    if (context != null && createFile(context, uri)) {
                        Toast.makeText(context, R.string.success_export_msg, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

    fun initiateFileCreation(activity: Activity) {
        val mimeType = if (exportFormat == "CSV") "text/csv" else "application/geo+json"
        val intent =
            Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = mimeType
                val timestamp =
                    SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val filename =
                    if (exportFormat == "CSV") "buy_through_akrabi_items_$timestamp.csv" else "buy_through_akrabi_items_$timestamp.geojson"
                putExtra(Intent.EXTRA_TITLE, filename)
            }
        createDocumentLauncher.launch(intent)
    }

    // Function to share the file
    fun shareFile(file: File) {
        val fileURI: Uri =
            context.let {
                FileProvider.getUriForFile(
                    it,
                    context.applicationContext.packageName.toString() + ".provider",
                    file,
                )
            }

        val shareIntent =
            Intent(Intent.ACTION_SEND).apply {
                type = if (exportFormat == "CSV") "text/csv" else "application/geo+json"
                putExtra(Intent.EXTRA_SUBJECT, "Buy Through Akrabi Data")
                putExtra(Intent.EXTRA_TEXT, "Sharing the Buy Through Akrabi data file.")
                putExtra(Intent.EXTRA_STREAM, fileURI)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        val chooserIntent = Intent.createChooser(shareIntent, "Share file")
        activity.startActivity(chooserIntent)
    }

    fun exportFile(activity: Activity) {
        showConfirmationDialog = true
    }

    // Function to handle the share action
    fun shareFileAction() {
        showConfirmationDialog = true
    }


    if (showFormatDialog) {
        FormatSelectionDialog(
            onDismiss = { showFormatDialog = false },
            onFormatSelected = { format ->
                exportFormat = format
                showFormatDialog = false

                when (action) {
                    Action.Export -> {
                        // Export all
                        exportFile(activity)
                    }
                    Action.Share -> {
                        // Share all farms
                        shareFileAction()
                    }
                    else -> {}
                }
            }
        )
    }

    if (showConfirmationDialog) {
        ConfirmationDialogBuyThroughAkrabi(
            filteredItems,
            action = action!!, // Ensure action is not null
            // selectedIds = selectedIds,
            onConfirm = {
                when (action) {
                    Action.Export -> initiateFileCreation(activity)
                    Action.Share -> {
                        val file = createFileForSharing()
                        if (file != null) {
                            shareFile(file)
                        }
                    }

                    else -> {}
                }
            },
            onDismiss = { showConfirmationDialog = false },
        )
    }

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
            title = { Text(text = stringResource(id = R.string.confirm)) },
            text = { Text(text = stringResource(id = R.string.delete_this_item_cannot)) },
            confirmButton = {
                Button(
                    onClick = {
                        itemToDelete?.let { farmViewModel.deleteBoughtItemBuyThroughAkrabi(it) }
                        showDialog = false
                        itemToDelete = null
                    }
                ) {
                    Text(text = stringResource(id = R.string.delete))
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog = false
                        itemToDelete = null
                    }
                ) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            },
            containerColor = MaterialTheme.colorScheme.background, // Background that adapts to light/dark
            tonalElevation = 6.dp // Adds a subtle shadow for better UX
        )
    }
    Box(modifier = Modifier.fillMaxSize()) {
        // Main content for Direct Buy
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(id = R.string.bought_items)) },
                    navigationIcon = {
                            IconButton(onClick = { drawerVisible = !drawerVisible }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        if (filteredItems.isNotEmpty()) {
                            IconButton(onClick = {action = Action.Export;showFormatDialog = true}, modifier = Modifier.size(36.dp)) {
                                Icon(
                                    painter = painterResource(id = R.drawable.save),
                                    contentDescription = "Export",
                                    modifier = Modifier.size(24.dp),
                                )
                            }
                        }
                        if (filteredItems.isNotEmpty()) {
                            IconButton(onClick = {action = Action.Share;showFormatDialog = true}, modifier = Modifier.size(36.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
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
                        .padding(end = 0.dp, bottom = 72.dp)
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
                    modifier = Modifier.fillMaxSize().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        isLoading -> {
                            // CircularProgressIndicator() // Show a loading spinner

                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                items(5) {
                                    SkeletonBoughtItemCardBuyThroughAkrabi()
                                }
                            }
                        }

                        filteredItems.isEmpty() -> {
                            Text(text = stringResource(id = R.string.no_bought_items))
//                            Box(
//                                modifier = Modifier.fillMaxSize(),
//                                contentAlignment = Alignment.Center
//                            ) {
//                                Image(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .padding(16.dp, 8.dp),
//                                    painter = painterResource(id = R.drawable.no_data2),
//                                    contentDescription = null
//                                )
//                            }
                        }

                        else -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
//                                    contentPadding = PaddingValues(
//                                        horizontal = 16.dp,
//                                        vertical = 8.dp
//                                    )
                                contentPadding = PaddingValues(bottom = 56.dp)
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
//    if (drawerVisible) {
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(Color(0x99000000))
//                .clickable { drawerVisible = false },
//            contentAlignment = Alignment.TopStart
//        ) {
//            Row(
//                modifier = Modifier
//                    .fillMaxHeight()
//                    .width(250.dp)
//                    .background(MaterialTheme.colorScheme.surface)
//            ) {
//                Column(
//                    modifier = Modifier
//                        .fillMaxHeight()
//                        .weight(1f)
//                        .padding(16.dp)
//                ) {
//                    // Header
//                    Text(
//                        text = stringResource(id = R.string.menu),
//                        style = MaterialTheme.typography.headlineSmall,
//                        modifier = Modifier.padding(bottom = 16.dp)
//                    )
//                    Divider()
//
//                    // Scrollable Content
//                    Box(modifier = Modifier.weight(1f)) {
//                        LazyColumn(
//                            verticalArrangement = Arrangement.spacedBy(16.dp),
//                            contentPadding = PaddingValues(bottom = 64.dp)
//                        ) {
//                            item {
//                                DrawerItem(
//                                    text = stringResource(id = R.string.home),
//                                    painter = painterResource(R.drawable.home),
//                                    onClick = {
//                                        // navController.navigate("shopping")
//                                        //navController.previousBackStackEntry
//                                        drawerVisible = false
//                                    }
//                                )
//                            }
//                            item {
//                                DrawerItem(
//                                    text = stringResource(id = R.string.collection_site_registration),
//                                    painter = painterResource(R.drawable.add_collection_site),
//                                    onClick = {
//                                        navController.navigate("siteList")
//                                        drawerVisible = false
//                                    }
//                                )
//                            }
//                            item {
//                                DrawerItem(
//                                    text = stringResource(id = R.string.farmer_registration),
//                                    painter = painterResource(R.drawable.person_add),
//                                    onClick = {
//                                        navController.navigate("siteList")
//                                        drawerVisible = false
//                                    }
//                                )
//                            }
//
//                            item {
//                                DrawerItem(
//                                    text = stringResource(id = R.string.akrabi_registration),
//                                    painter = painterResource(R.drawable.person_add),
//                                    onClick = {
//                                        navController.navigate("akrabi_list_screen")
//                                        drawerVisible = false
//                                    }
//                                )
//                            }
//
//                            item {
//                                Divider()
//                            }
//                            item {
//                                // Dark Mode Toggle
//                                Row(
//                                    verticalAlignment = Alignment.CenterVertically,
//                                    modifier = Modifier.fillMaxWidth()
//                                ) {
//                                    Text(
//                                        text = stringResource(id = R.string.light_dark_theme),
//                                        style = MaterialTheme.typography.titleMedium
//                                    )
//                                    Spacer(modifier = Modifier.weight(1f))
//                                    Switch(
//                                        checked = darkMode.value,
//                                        onCheckedChange = {
//                                            darkMode.value = it
//                                            sharedPreferences.edit().putBoolean("dark_mode", it).apply()
//                                        }
//                                    )
//                                }
//                            }
//                            item {
//                                Divider()
//                            }
//                            // using checkbox
//
//                            item {
//                                Text(
//                                    text = stringResource(id = R.string.select_language),
//                                    style = MaterialTheme.typography.titleMedium,
//                                    color = MaterialTheme.colorScheme.onBackground
//                                )
//                                Box(
//                                    modifier = Modifier
//                                        .width(230.dp)
//                                        .padding(8.dp)
//                                ) {
//                                    var expanded by remember { mutableStateOf(false) } // Ensure expanded is inside the Box
//                                    OutlinedButton(
//                                        onClick = { expanded = true },
//                                        modifier = Modifier.fillMaxWidth()
//                                    ) {
//                                        Text(text = currentLanguage.displayName, color = MaterialTheme.colorScheme.onBackground)
//
//                                        Icon(
//                                            imageVector = Icons.Default.ArrowDropDown,
//                                            contentDescription = null,
//                                            tint = MaterialTheme.colorScheme.onBackground
//                                        )
//                                    }
//                                    DropdownMenu(
//                                        expanded = expanded,
//                                        onDismissRequest = { expanded = false },
//                                        modifier = Modifier
//                                            .width(230.dp) // Set the width of the DropdownMenu to match the Box
//                                            .background(MaterialTheme.colorScheme.background) // Set the background color to white for visibility
//                                    ) {
//                                        languages.forEach { language ->
//                                            DropdownMenuItem(
//                                                text = {
//                                                    Text(
//                                                        text = language.displayName,
//                                                        color = MaterialTheme.colorScheme.onBackground
//                                                    )
//                                                },
//                                                onClick = {
//                                                    languageViewModel.selectLanguage(language, context)
//                                                    expanded = false
//                                                },
//                                                modifier = Modifier
//                                                    .background(MaterialTheme.colorScheme.background) // Ensure each menu item has a white background
//                                            )
//                                        }
//                                    }
//                                }
//                            }
//
//                            item {
//                                // Logout Item
//                                DrawerItem(
//                                    text = stringResource(id = R.string.logout),
//                                    painter = painterResource(R.drawable.logout),
//                                    onClick = {
//                                        // Call your logout function here
//                                        // navigate to login screen or refresh UI
//                                        navController.navigate("home")
//                                        drawerVisible = false
//                                    }
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }

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

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BoughtItemsListDirectBuy(
    farmViewModel: FarmViewModel,
    onItemClick: (DirectBuy) -> Unit,
    navController: NavController,
    darkMode: MutableState<Boolean>,
    languageViewModel: LanguageViewModel,
    languages: List<Language>
) {
    val boughtItemsDirectBuy by farmViewModel.boughtItemsDirectBuy.collectAsStateWithLifecycle(initialValue = emptyList())

    val currentLanguage by languageViewModel.currentLanguage.collectAsState()
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("theme_mode", Context.MODE_PRIVATE)

    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    val filteredItems = boughtItemsDirectBuy.filter {
        it.farmerName.contains(searchQuery, ignoreCase = true) ||
                it.location.contains(searchQuery, ignoreCase = true)
    }

    var isLoading by remember { mutableStateOf(true) }

    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    var showDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<DirectBuy?>(null) }

    var drawerVisible by remember { mutableStateOf(false) }

    var action by remember { mutableStateOf<Action?>(null) }
    var showFormatDialog by remember { mutableStateOf(false) }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var exportFormat by remember { mutableStateOf("") }
    val activity = context as Activity


    fun createFile(
        context: Context,
        uri: Uri,
    ): Boolean {
        // Get the current date and time
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val filename = if (exportFormat == "CSV") "direct_buy_items_$timestamp.csv" else "direct_buy_items_$timestamp.geojson"

        try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                BufferedWriter(OutputStreamWriter(outputStream)).use { writer ->
                    if (exportFormat == "CSV") {
                        writer.write(
                            "id,farmer_name,farmer_number,farmer_search,site_name,location,cherry_sold,price_per_kg,paid,photo,photo_uri,date,time\n",
                        )
                        filteredItems.forEach { directBuy ->
                            val line = "${directBuy.id},${directBuy.farmerName},${directBuy.farmerNumber},${directBuy.siteName},${directBuy.location},${directBuy.cherrySold},${directBuy.pricePerKg},${directBuy.paid},${directBuy.photo},${directBuy.photoUri},${directBuy.date},${directBuy.time}\n"
                            writer.write(line)
                        }
                    } else {
                        val geoJson =
                            buildString {
                                append("{\"type\": \"FeatureCollection\", \"features\": [")
                                filteredItems.forEachIndexed { index, directBuy ->

                                    val feature = """
                                    {
                                        "type": "Feature",
                                        "properties": {
                                            "id": "${directBuy.id}",
                                            "date": "${directBuy.date}",
                                            "time": "${directBuy.time}",
                                            "location": "${directBuy.location}",
                                            "site_name": "${directBuy.siteName}",
                                            "farmer_search": "${directBuy.farmerSearch}",
                                            "farmer_number": "${directBuy.farmerNumber}",
                                            "farmer_name": "${directBuy.farmerName}",
                                            "cherry_sold": ${directBuy.cherrySold},
                                            "price_per_kg": ${directBuy.pricePerKg},
                                            "paid": ${directBuy.paid},
                                            "photo": "${directBuy.photo}",
                                            "photo_uri": "${directBuy.photoUri ?: ""}",
                                            "created_at": "${directBuy.date}",
                                            "updated_at": "${directBuy.date}" 
                                        }
                                    }
                                """.trimIndent()

                                    append(feature)
                                    if (index < filteredItems.size - 1) append(",")
                                }
                                append("]}")
                            }
                        writer.write(geoJson)
                    }
                }
            }
            return true
        } catch (e: IOException) {
            Toast.makeText(context, R.string.error_export_msg, Toast.LENGTH_SHORT).show()
            return false
        }
    }

    fun createFileForSharing(): File? {
        // Get the current date and time
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val filename = if (exportFormat == "CSV") "direct_buy_items_$timestamp.csv" else "direct_buy_items_$timestamp.geojson"
        val mimeType = if (exportFormat == "CSV") "text/csv" else "application/geo+json"
        // Get the Downloads directory
        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, filename)

        try {
            file.bufferedWriter().use { writer ->
                if (exportFormat == "CSV") {
                    writer.write(
                        "remote_id,farmer_name,member_id,collection_site,agent_name,farm_village,farm_district,farm_size,latitude,longitude,polygon,created_at,updated_at\n",
                    )
                    filteredItems.forEach { directBuy ->
                        val line = "${directBuy.id},${directBuy.farmerName},${directBuy.farmerNumber},${directBuy.siteName},${directBuy.location},${directBuy.cherrySold},${directBuy.pricePerKg},${directBuy.paid},${directBuy.photo},${directBuy.photoUri},${directBuy.date},${directBuy.time}\n"
                        writer.write(line)
                    }
                } else {
                    val geoJson =
                        buildString {
                            append("{\"type\": \"FeatureCollection\", \"features\": [")
                            filteredItems.forEachIndexed { index, directBuy ->
                                val feature = """
                                    {
                                        "type": "Feature",
                                        "properties": {
                                            "id": "${directBuy.id}",
                                            "date": "${directBuy.date}",
                                            "time": "${directBuy.time}",
                                            "location": "${directBuy.location}",
                                            "site_name": "${directBuy.siteName}",
                                            "farmer_search": "${directBuy.farmerSearch}",
                                            "farmer_number": "${directBuy.farmerNumber}",
                                            "farmer_name": "${directBuy.farmerName}",
                                            "cherry_sold": ${directBuy.cherrySold},
                                            "price_per_kg": ${directBuy.pricePerKg},
                                            "paid": ${directBuy.paid},
                                            "photo": "${directBuy.photo}",
                                            "photo_uri": "${directBuy.photoUri ?: ""}",
                                            "created_at": "${directBuy.date}",
                                            "updated_at": "${directBuy.date}" 
                                        }
                                    }
                                """.trimIndent()

                                append(feature)
                                if (index < filteredItems.size - 1) append(",")
                            }
                            append("]}")
                        }
                    writer.write(geoJson)
                }
            }
            return file
        } catch (e: IOException) {
            Toast.makeText(context, R.string.error_export_msg, Toast.LENGTH_SHORT).show()
            return null
        }
    }

    val createDocumentLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    val context = activity?.applicationContext
                    if (context != null && createFile(context, uri)) {
                        Toast.makeText(context, R.string.success_export_msg, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

    fun initiateFileCreation(activity: Activity) {
        val mimeType = if (exportFormat == "CSV") "text/csv" else "application/geo+json"
        val intent =
            Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = mimeType
                val timestamp =
                    SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val filename =
                    if (exportFormat == "CSV") "direct_buy_items_$timestamp.csv" else "direct_buy_items_$timestamp.geojson"
                putExtra(Intent.EXTRA_TITLE, filename)
            }
        createDocumentLauncher.launch(intent)
    }

    // Function to share the file
    fun shareFile(file: File) {
        val fileURI: Uri =
            context.let {
                FileProvider.getUriForFile(
                    it,
                    context.applicationContext.packageName.toString() + ".provider",
                    file,
                )
            }

        val shareIntent =
            Intent(Intent.ACTION_SEND).apply {
                type = if (exportFormat == "CSV") "text/csv" else "application/geo+json"
                putExtra(Intent.EXTRA_SUBJECT, "Direct Buy Data")
                putExtra(Intent.EXTRA_TEXT, "Sharing the Direct Buy data file.")
                putExtra(Intent.EXTRA_STREAM, fileURI)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        val chooserIntent = Intent.createChooser(shareIntent, "Share file")
        activity.startActivity(chooserIntent)
    }

    fun exportFile(activity: Activity) {
        showConfirmationDialog = true
    }

    // Function to handle the share action
    fun shareFileAction() {
        showConfirmationDialog = true
    }


    if (showFormatDialog) {
        FormatSelectionDialog(
            onDismiss = { showFormatDialog = false },
            onFormatSelected = { format ->
                exportFormat = format
                showFormatDialog = false

                when (action) {
                    Action.Export -> {
                        // Export all
                        exportFile(activity)
                    }
                    Action.Share -> {
                        // Share all farms
                        shareFileAction()
                    }
                    else -> {}
                }
            }
        )
    }

    if (showConfirmationDialog) {
        ConfirmationDialogDirectBuy(
            filteredItems,
            action = action!!, // Ensure action is not null
            // selectedIds = selectedIds,
            onConfirm = {
                when (action) {
                    Action.Export -> initiateFileCreation(activity)
                    Action.Share -> {
                        val file = createFileForSharing()
                        if (file != null) {
                            shareFile(file)
                        }
                    }

                    else -> {}
                }
            },
            onDismiss = { showConfirmationDialog = false },
        )
    }

    // Simulate data loading delay
    LaunchedEffect(Unit) {
        delay(2000) // Simulate loading time
        isLoading = false
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = stringResource(id = R.string.confirm)) },
            text = { Text(text = stringResource(id = R.string.delete_this_item_cannot)) },
            confirmButton = {
                Button(
                    onClick = {
                        itemToDelete?.let { farmViewModel.deleteBoughtItemDirectBuy(it) }
                        showDialog = false
                        itemToDelete = null
                    }
                ) {
                    Text(text = stringResource(id = R.string.delete))
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog = false
                        itemToDelete = null
                    }
                ) {
                    Text(text = stringResource(id = R.string.cancel))
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
                    title = { Text(text = stringResource(id = R.string.bought_items)) },
                    navigationIcon = {
                        IconButton(onClick = { drawerVisible = !drawerVisible }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        if (filteredItems.isNotEmpty()) {
                            IconButton(onClick = {action = Action.Export;showFormatDialog = true}, modifier = Modifier.size(36.dp)) {
                                Icon(
                                    painter = painterResource(id = R.drawable.save),
                                    contentDescription = "Export",
                                    modifier = Modifier.size(24.dp),
                                )
                            }
                        }
                        if (filteredItems.isNotEmpty()) {
                            IconButton(onClick = {action = Action.Share;showFormatDialog = true}, modifier = Modifier.size(36.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
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
                        .padding(end = 0.dp, bottom = 72.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Direct Buy")
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                if (isSearchActive) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text(stringResource(id = R.string.search_placeholder)) },
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
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        isLoading -> {
                            // CircularProgressIndicator()
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                items(5) {
                                    SkeletonBoughtItemCardDirectBuy()
                                }
                            }
                        }

                        filteredItems.isEmpty() -> {
                            Text(stringResource(id = R.string.no_bought_items),
                            color = MaterialTheme.colorScheme.onSurface)
//                            Box(
//                                modifier = Modifier.fillMaxSize(),
//                                contentAlignment = Alignment.Center
//                            ) {
//                                Image(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .padding(16.dp, 8.dp),
//                                    painter = painterResource(id = R.drawable.no_data2),
//                                    contentDescription = null
//                                )
//                            }
                        }

                        else -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 56.dp)
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
//        if (drawerVisible) {
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(Color(0x99000000))
//                    .clickable { drawerVisible = false },
//                contentAlignment = Alignment.TopStart
//            ) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxHeight()
//                        .width(250.dp)
//                        .background(MaterialTheme.colorScheme.surface)
//                ) {
//                    Column(
//                        modifier = Modifier
//                            .fillMaxHeight()
//                            .weight(1f)
//                            .padding(16.dp)
//                    ) {
//                        // Header
//                        Text(
//                            text = stringResource(id = R.string.menu),
//                            style = MaterialTheme.typography.headlineSmall,
//                            modifier = Modifier.padding(bottom = 16.dp)
//                        )
//                        Divider()
//
//                        // Scrollable Content
//                        Box(modifier = Modifier.weight(1f)) {
//                            LazyColumn(
//                                verticalArrangement = Arrangement.spacedBy(16.dp),
//                                contentPadding = PaddingValues(bottom = 64.dp)
//                            ) {
//                                item {
//                                    DrawerItem(
//                                        text = stringResource(id = R.string.home),
//                                        painter = painterResource(R.drawable.home),
//                                        onClick = {
//                                            // navController.navigate("shopping")
//                                            //navController.previousBackStackEntry
//                                            drawerVisible = false
//                                        }
//                                    )
//                                }
//                                item {
//                                    DrawerItem(
//                                        text = stringResource(id = R.string.collection_site_registration),
//                                        painter = painterResource(R.drawable.add_collection_site),
//                                        onClick = {
//                                            navController.navigate("siteList")
//                                            drawerVisible = false
//                                        }
//                                    )
//                                }
//                                item {
//                                    DrawerItem(
//                                        text = stringResource(id = R.string.farmer_registration),
//                                        painter = painterResource(R.drawable.person_add),
//                                        onClick = {
//                                            navController.navigate("siteList")
//                                            drawerVisible = false
//                                        }
//                                    )
//                                }
//
//                                item {
//                                    DrawerItem(
//                                        text = stringResource(id = R.string.akrabi_registration),
//                                        painter = painterResource(R.drawable.person_add),
//                                        onClick = {
//                                            navController.navigate("akrabi_list_screen")
//                                            drawerVisible = false
//                                        }
//                                    )
//                                }
//
//                                item {
//                                    Divider()
//                                }
//                                item {
//                                    // Dark Mode Toggle
//                                    Row(
//                                        verticalAlignment = Alignment.CenterVertically,
//                                        modifier = Modifier.fillMaxWidth()
//                                    ) {
//                                        Text(
//                                            text = stringResource(id = R.string.light_dark_theme),
//                                            style = MaterialTheme.typography.titleMedium
//                                        )
//                                        Spacer(modifier = Modifier.weight(1f))
//                                        Switch(
//                                            checked = darkMode.value,
//                                            onCheckedChange = {
//                                                darkMode.value = it
//                                                sharedPreferences.edit().putBoolean("dark_mode", it).apply()
//                                            }
//                                        )
//                                    }
//                                }
//                                item {
//                                    Divider()
//                                }
//                                // using checkbox
//
//                                item {
//                                    Text(
//                                        text = stringResource(id = R.string.select_language),
//                                        style = MaterialTheme.typography.titleMedium,
//                                        color = MaterialTheme.colorScheme.onBackground
//                                    )
//                                    Box(
//                                        modifier = Modifier
//                                            .width(230.dp)
//                                            .padding(8.dp)
//                                    ) {
//                                        var expanded by remember { mutableStateOf(false) } // Ensure expanded is inside the Box
//                                        OutlinedButton(
//                                            onClick = { expanded = true },
//                                            modifier = Modifier.fillMaxWidth()
//                                        ) {
//                                            Text(text = currentLanguage.displayName, color = MaterialTheme.colorScheme.onBackground)
//
//                                            Icon(
//                                                imageVector = Icons.Default.ArrowDropDown,
//                                                contentDescription = null,
//                                                tint = MaterialTheme.colorScheme.onBackground
//                                            )
//                                        }
//                                        DropdownMenu(
//                                            expanded = expanded,
//                                            onDismissRequest = { expanded = false },
//                                            modifier = Modifier
//                                                .width(230.dp) // Set the width of the DropdownMenu to match the Box
//                                                .background(MaterialTheme.colorScheme.background) // Set the background color to white for visibility
//                                        ) {
//                                            languages.forEach { language ->
//                                                DropdownMenuItem(
//                                                    text = {
//                                                        Text(
//                                                            text = language.displayName,
//                                                            color = MaterialTheme.colorScheme.onBackground
//                                                        )
//                                                    },
//                                                    onClick = {
//                                                        languageViewModel.selectLanguage(language, context)
//                                                        expanded = false
//                                                    },
//                                                    modifier = Modifier
//                                                        .background(MaterialTheme.colorScheme.background) // Ensure each menu item has a white background
//                                                )
//                                            }
//                                        }
//                                    }
//                                }
//
//                                item {
//                                    // Logout Item
//                                    DrawerItem(
//                                        text = stringResource(id = R.string.logout),
//                                        painter = painterResource(R.drawable.logout),
//                                        onClick = {
//                                            // Call your logout function here
//                                            // navigate to login screen or refresh UI
//                                            navController.popBackStack()
//                                            drawerVisible = false
//                                        }
//                                    )
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
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

}


//@Composable
//fun LanguageCardSideBar(language: Language, isSelected: Boolean, onSelect: (String) -> Unit) {
//    val cardElevation = if (isSelected) 8.dp else 4.dp
//    val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
//
//    Card(
//        modifier = Modifier
//            .padding(4.dp) // Reduced padding
//            .clickable { onSelect(language.code) }
//            .border(
//                width = 1.dp,
//                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
//                shape = RoundedCornerShape(8.dp) // Rounded corners
//            ),
//        colors = CardDefaults.cardColors(
//            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
//        ),
//        elevation = CardDefaults.cardElevation(
//            defaultElevation = cardElevation
//        ),
//        shape = RoundedCornerShape(8.dp) // Rounded corners
//    ) {
//        Text(
//            text = language.displayName,
//            color = textColor,
//            fontSize = 10.sp, // Smaller font size
//            modifier = Modifier
//                .padding(8.dp) // Adjusted padding for smaller card
//                .fillMaxWidth()
//                .align(Alignment.CenterHorizontally)
//        )
//    }
//}


@Composable
fun DrawerItem(text: String, painter: Painter, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .padding(end = 16.dp)
                .size(24.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DirectBuyDetailScreen(directBuy: DirectBuy, onBack: () -> Unit, navController: NavHostController,
                          farmViewModel: FarmViewModel) {

    var showDialog by remember { mutableStateOf(false) } // State to show/hide delete confirmation dialog
    var itemToDelete by remember { mutableStateOf<DirectBuy?>(null) } // State to hold the item to delete

    fun deleteDirectBuy(directBuy: DirectBuy,farmViewModel: FarmViewModel) {
        farmViewModel.deleteBoughtItemDirectBuy(directBuy)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.item_details_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 64.dp),
        ) {

            // Display the image if available
            directBuy.photoUri?.let { imageUrl ->
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
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
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DetailText(label = stringResource(R.string.date), value = directBuy.date.toString())
                    DetailText(label = stringResource(R.string.time), value = directBuy.time)
                    DetailText(label = stringResource(R.string.location), value = directBuy.location)
                    DetailText(label = stringResource(R.string.farmer_name), value = directBuy.farmerName)
                    DetailText(label = stringResource(R.string.site_name), value = directBuy.siteName)
                    DetailText(label = stringResource(R.string.cherry_sold), value = "${directBuy.cherrySold}")
                    DetailText(label = stringResource(R.string.price_per_kg), value = "$${directBuy.pricePerKg}")
                    DetailText(label = stringResource(R.string.total_paid), value = "$${directBuy.paid}", bold = true)
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
                            navController.navigate("direct_buy/edit/${directBuy.id}")
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
                            itemToDelete = directBuy
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
                            itemToDelete?.let { deleteDirectBuy(it, farmViewModel ) } // Perform the delete action
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
                },
                containerColor = MaterialTheme.colorScheme.background, // Background that adapts to light/dark
                tonalElevation = 6.dp // Adds a subtle shadow for better UX
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BoughtItemDetailScreen(buyThroughAkrabi: BuyThroughAkrabi, onBack: () -> Unit, navController: NavHostController,
                           farmViewModel: FarmViewModel) {

    var showDialog by remember { mutableStateOf(false) } // State to show/hide delete confirmation dialog
    var itemToDelete by remember { mutableStateOf<BuyThroughAkrabi?>(null) } // State to hold the item to delete

    fun deleteBoughtItem(buyThroughAkrabi: BuyThroughAkrabi,farmViewModel: FarmViewModel) {
        farmViewModel.deleteBoughtItemBuyThroughAkrabi(buyThroughAkrabi)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.item_details_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 64.dp),
        ) {

            // Display the image if available
            buyThroughAkrabi.photoUri?.let { imageUrl ->
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription =  stringResource(R.string.bought_item_image_description),
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
                    DetailText(label = stringResource(R.string.date), value = buyThroughAkrabi.date.toString())
                    DetailText(label = stringResource(R.string.time), value = buyThroughAkrabi.time)
                    DetailText(label = stringResource(R.string.location), value = buyThroughAkrabi.location)
                    DetailText(label = stringResource(R.string.akrabi_name), value = buyThroughAkrabi.akrabiName)
                    DetailText(label = stringResource(R.string.site_name), value = buyThroughAkrabi.siteName)
                    DetailText(label = stringResource(R.string.cherry_sold), value = "${buyThroughAkrabi.cherrySold}")
                    DetailText(label = stringResource(R.string.price_per_kg), value = "$${buyThroughAkrabi.pricePerKg}")
                    DetailText(label = stringResource(R.string.total_paid), value = "$${buyThroughAkrabi.paid}", bold = true)
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
                            navController.navigate("buy_through_akrabi/edit/${buyThroughAkrabi.id}")
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
                            itemToDelete = buyThroughAkrabi
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
                            itemToDelete?.let { deleteBoughtItem(it, farmViewModel ) } // Perform the delete action
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
                },
                containerColor = MaterialTheme.colorScheme.background, // Background that adapts to light/dark
                tonalElevation = 6.dp // Adds a subtle shadow for better UX
            )
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
                    text = "${stringResource(id=R.string.site_name)}: ${item.siteName}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${stringResource(id=R.string.cherry_sold)}: ${item.cherrySold}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${stringResource(id=R.string.total_paid)}: ${item.paid}",
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
                    text = "${stringResource(id=R.string.site_name)}: ${item.siteName}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${stringResource(id=R.string.cherry_sold)}: ${item.cherrySold}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${stringResource(id=R.string.total_paid)}: ${item.paid}",
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

@Composable
fun SkeletonBoughtItemCardDirectBuy() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .shimmer(), // Apply shimmer effect
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(20.dp)
                        .background(Color.Gray, shape = RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(20.dp)
                        .background(Color.Gray, shape = RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(20.dp)
                        .background(Color.Gray, shape = RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(20.dp)
                        .background(Color.Gray, shape = RoundedCornerShape(4.dp))
                )
            }
            Row(
                modifier = Modifier.padding(end = 0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.Gray, shape = RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.Gray, shape = RoundedCornerShape(4.dp))
                )
            }
        }
    }
}


@Composable
fun SkeletonBoughtItemCardBuyThroughAkrabi() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .shimmer(), // Apply shimmer effect
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Skeleton for akrabiName
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(20.dp)
                        .background(Color.Gray, shape = RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Skeleton for siteName
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(20.dp)
                        .background(Color.Gray, shape = RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Skeleton for cherrySold
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(20.dp)
                        .background(Color.Gray, shape = RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Skeleton for totalPaid
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(20.dp)
                        .background(Color.Gray, shape = RoundedCornerShape(4.dp))
                )
            }

            Row(
                modifier = Modifier.padding(end = 0.dp)
            ) {
                // Skeleton for edit icon
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.Gray, shape = RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))

                // Skeleton for delete icon
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.Gray, shape = RoundedCornerShape(4.dp))
                )
            }
        }
    }
}


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
                title = { Text(text =stringResource(id = R.string.update_bought_item)) }
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
                label = { Text(text= stringResource(id=R.string.farmer_number)) },
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
                    Text(text= stringResource(id =R.string.cancel), color = Color.White)
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
                    Text(text= stringResource(id =R.string.save_changes))
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
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

    // Get the strings from resources within a @Composable context
    val locationError = stringResource(id = R.string.location)
    val siteError = stringResource(id = R.string.site_name)
    val akrabiError = stringResource(id = R.string.akrabi_name)
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
    val akrabiLabel = stringResource(id = R.string.akrabi_name)
    val akrabiNumberLabel = stringResource(id = R.string.akrabi_number)
    val SiteLabel = stringResource(id = R.string.site_name)
    val selectAkrabiLabel = stringResource(id = R.string.akrabi_name)
    val cherrySoldLabel = stringResource(id = R.string.cherry_sold)
    val pricePerKgLabel = stringResource(id = R.string.price_per_kg)
    val paidLabel = stringResource(id = R.string.paid)
    val submitLabel = stringResource(id = R.string.submit)

    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text(text = stringResource(id = R.string.confirm_update)) },
            text = { Text(text = stringResource(id =R.string.are_you_sure_save_the_changes)) },
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
                    Text(text = stringResource(id = R.string.confirm))
                }
            },
            dismissButton = {
                Button(
                    onClick = { showConfirmationDialog = false }
                ) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            },
            containerColor = MaterialTheme.colorScheme.background, // Background that adapts to light/dark
            tonalElevation = 6.dp // Adds a subtle shadow for better UX
        )
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text =stringResource(id = R.string.update_bought_item)) }
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

//        // Date and Time pickers
//        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//            TextField(
//                value = date.toString(),
//                onValueChange = { date = LocalDate.parse(it) },
//                label = { Text("Date") },
//                modifier = Modifier.weight(1f),
//                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
//                readOnly = true,
//                trailingIcon = {
//                    IconButton(onClick = { /* Show date picker */ }) {
//                        Icon(imageVector = Icons.Default.DateRange, contentDescription = "Pick Date")
//                    }
//                }
//            )
//            TextField(
//                value = time.toString(),
//                onValueChange = { time = LocalTime.parse(it) },
//                label = { Text("Time") },
//                modifier = Modifier.weight(1f),
//                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
//                readOnly = true,
//                trailingIcon = {
//                    IconButton(onClick = { /* Show time picker */ }) {
//                        Icon(imageVector = Icons.Default.DateRange, contentDescription = "Pick Time")
//                    }
//                }
//            )
//        }

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
                        onNextFocus(focusRequesterSite, focusRequesterAkrabi)
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

//        TextField(
//            value = akrabiSearch,
//            onValueChange = { akrabiSearch = it },
//            label = { Text("Akrabi Search") }
//        )

            OutlinedTextField(
                value = akrabiName,
                onValueChange = { akrabiName = it },
                label = { Text(text = stringResource(id = R.string.akrabi_name)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = {
                        onNextFocus(focusRequesterAkrabi, focusRequesterCherrySold)
                    }
                ),
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequesterAkrabi),
                isError = validationErrors.contains(akrabiLabel)
            )

            if (validationErrors.contains(akrabiLabel)) {
                Text(
                    text = stringResource(id = R.string.required_field),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }


            OutlinedTextField(
                value = akrabiNumber,
                onValueChange = { akrabiNumber = it },
                label = { Text(text = stringResource(id = R.string.akrabi_number)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Number
                )
            )

            if (validationErrors.contains(akrabiNumber)) {
                Text(
                    text = stringResource(id = R.string.required_field),
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
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(text = stringResource(id = R.string.cancel), color = Color.White)
                }

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
                            showConfirmationDialog = true
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(id = R.string.save_changes))
                }
            }
        }
    }
}
