package org.technoserve.cafetrac.ui.screens.directbuy

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.cafetrac.database.models.Language
import org.technoserve.cafetrac.ui.components.BoughtItemCardDirectBuy
import org.technoserve.cafetrac.ui.components.ConfirmationDialogDirectBuy
import org.technoserve.cafetrac.ui.components.CustomDrawer
import org.technoserve.cafetrac.ui.components.DateRangePicker
import org.technoserve.cafetrac.ui.components.FormatSelectionDialog
import org.technoserve.cafetrac.ui.components.SkeletonBoughtItemCardDirectBuy
import org.technoserve.cafetrac.ui.screens.farms.Action
import org.technoserve.cafetrac.viewmodels.FarmViewModel
import org.technoserve.cafetrac.viewmodels.LanguageViewModel
import kotlinx.coroutines.delay
import org.technoserve.cafetrac.database.models.DirectBuy
import org.technoserve.cafetraorg.technoserve.cafetrac.R
import java.io.BufferedWriter
import java.io.File
import java.io.IOException
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
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
                            Text(
                                stringResource(id = R.string.no_bought_items),
                                color = MaterialTheme.colorScheme.onSurface)
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