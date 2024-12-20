package org.technoserve.cafetrac.ui.components

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.sp
import org.technoserve.cafetraorg.technoserve.cafetrac.R


/**
 *  This function is used to display the header for the farm list with search, export, share, and import buttons
 *  @param title: The title of the header
 *  @param onBackClicked: A function to be called when the back button is clicked
 *  @param onExportClicked: A function to be called when the export button is clicked
 *  @param onShareClicked: A function to be called when the share button is clicked
 *  @param onImportClicked: A function to be called when the import button is clicked
 * @param onSearchQueryChanged : A function to be called when the search query is changed
 * @param showExport: A function to be called when the export button is clicked and the export button is clicked
 * @param showShar: A function to be called when the export button is clicked
 * @param showSearch: A function to be called when the export button is clicked
 * @param onRestoreClicked: A function to be called when the restore button is clicked and the restore button is clicked
 */


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmListHeaderPlots(
    title: String,
    onAddFarmClicked: () -> Unit,
    onBackClicked: () -> Unit,
    onBackSearchClicked: () -> Unit,
    onExportClicked: () -> Unit,
    onShareClicked: () -> Unit,
    onImportClicked: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    searchQuery: String, // Pass the search query as a parameter
    isSearchVisible: Boolean, // Control search visibility from outside
    onSearchVisibilityChanged: (Boolean) -> Unit,// Add this
    onBuyThroughAkrabiClicked: () -> Unit, // Added this
    showAdd: Boolean,
    showExport: Boolean,
    showShare: Boolean,
    showSearch: Boolean,
    showBuyThroughAkrabi: Boolean // Added this
) {
    val context = LocalContext.current as Activity

//    // State for holding the search query
//    var searchQuery by remember { mutableStateOf("") }
//    var isSearchVisible by remember { mutableStateOf(false) }

    // State for tracking if import has been completed
    var isImportDisabled by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text(text = title, fontSize = 18.sp) },
        navigationIcon = {
            IconButton(onClick = onBackClicked) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            if (showExport) {
                IconButton(onClick = onExportClicked, modifier = Modifier.size(24.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.save),
                        contentDescription = "Export",
                        modifier = Modifier.size(24.dp),
                    )
                }
                Spacer(modifier = Modifier.width(2.dp))
            }
            if (showShare) {
                IconButton(onClick = onShareClicked, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(2.dp))
            }
            IconButton(
                onClick = {
                    if (!isImportDisabled) {
                        onImportClicked()
                        isImportDisabled = true // Disable the import icon after importing
                    }
                },
                modifier = Modifier.size(24.dp),
                enabled = !isImportDisabled // Disable the button if import is completed
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icons8_import_file_48),
                    contentDescription = "Import",
                    modifier = Modifier.size(24.dp),
                )
            }
//            Spacer(modifier = Modifier.width(2.dp))
//            // New button for buying through Akrabi
//            if (showBuyThroughAkrabi) {
//                IconButton(onClick = onBuyThroughAkrabiClicked, modifier = Modifier.size(24.dp)) {
//                    Icon(Icons.Default.ShoppingCart, contentDescription = "Buy Through Akrabi", modifier = Modifier.size(24.dp))
//                }
//                Spacer(modifier = Modifier.width(2.dp))
//            }

            Spacer(modifier = Modifier.width(2.dp))

            if (showAdd) {
                IconButton(onClick = {
                    // Remove plot_size from shared preferences
                    val sharedPref =
                        context.getSharedPreferences("FarmCollector", Context.MODE_PRIVATE)
                    if (sharedPref.contains("plot_size")) {
                        sharedPref.edit().remove("plot_size").apply()
                    }
                    if (sharedPref.contains("selectedUnit")) {
                        sharedPref.edit().remove("selectedUnit").apply()
                    }
                    // Call the onAddFarmClicked lambda
                    onAddFarmClicked()
                }, modifier = Modifier.size(24.dp)) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add",
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(2.dp))
            }
            if (showSearch) {
                IconButton(onClick = {
//                    isSearchVisible = !isSearchVisible
                    onSearchVisibilityChanged(!isSearchVisible)
                }, modifier = Modifier.size(24.dp)) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
    )

//    // Conditional rendering of the search field
//    if (isSearchVisible && showSearch) {
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            modifier =
//            Modifier
//                .padding(horizontal = 16.dp)
//                .fillMaxWidth(),
//        ) {
//            OutlinedTextField(
//                value = searchQuery,
//                onValueChange = {
//                    searchQuery = it
//                    onSearchQueryChanged(it)
//                },
//                modifier =
//                Modifier
//                    .padding(start = 8.dp)
//                    .weight(1f),
//                label = { Text(stringResource(R.string.search)) },
//                leadingIcon = {
//                    IconButton(onClick = {
//                        // onBackSearchClicked()
//                        searchQuery = ""
//                        onSearchQueryChanged("")
//                        isSearchVisible = !isSearchVisible
//                    }) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                    }
//                },
//                singleLine = true,
//                colors =
//                TextFieldDefaults.outlinedTextFieldColors(
//                    cursorColor = MaterialTheme.colorScheme.onSurface,
//                ),
//            )
//        }
//    }

    // Conditional rendering of the search field
    if (isSearchVisible && showSearch) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f),
                label = { Text(stringResource(R.string.search)) },
                leadingIcon = {
                    IconButton(onClick = {
                        onBackSearchClicked()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                singleLine = true,
            )
        }
    }
}