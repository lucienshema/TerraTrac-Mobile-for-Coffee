package org.technoserve.cafetrac.ui.components

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

import com.example.cafetrac.database.models.Language
import org.technoserve.cafetrac.ui.components.CustomDrawer
import org.technoserve.cafetrac.viewmodels.LanguageViewModel
import org.technoserve.cafetraorg.technoserve.cafetrac.R


/**
 * The FarmListHeader component displays the top app bar with a title, search field, and navigation icons.
 *
 * @param title The title to be displayed in the top app bar.
 * @param onSearchQueryChanged A callback function to handle changes in the search query.
 * @param onBackClicked A callback function to handle the back button click event.
 * @param showSearch A boolean flag indicating whether to show the search field.
 * @param showRestore A boolean flag indicating whether to show the restore button.
 *  @param onRestoreClicked A callback function to handle the restore button click event.
 */


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FarmListHeader(
    title: String,
    onSearchQueryChanged: (String) -> Unit,
    onAddFarmClicked: () -> Unit,
    onBackClicked: () -> Unit,
    onBackSearchClicked: () -> Unit,
    showAdd: Boolean,
    showSearch: Boolean,
    selectedItemsCount: Int,
    selectAllEnabled: Boolean,
    isAllSelected: Boolean,
    onSelectAllChanged: (Boolean) -> Unit,
    navController: NavController,
    darkMode: MutableState<Boolean>,
    languageViewModel: LanguageViewModel,
    languages: List<Language>
) {
    // State for holding the search query
    var searchQuery by remember { mutableStateOf("") }

    var isSearchVisible by remember { mutableStateOf(false) }

    val currentLanguage by languageViewModel.currentLanguage.collectAsState()
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("theme_mode", Context.MODE_PRIVATE)
    var drawerVisible by remember { mutableStateOf(false) }

    TopAppBar(
        modifier =
        Modifier
            .background(MaterialTheme.colorScheme.primary)
            .fillMaxWidth(),
        navigationIcon = {
            IconButton(onClick = { drawerVisible = !drawerVisible }) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
        },
        title = {
            Text(
                text = title,
                style =
                MaterialTheme.typography.bodySmall.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                ),
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                textAlign = TextAlign.Center,
            )
        },
        actions = {
            if (showAdd) {
                IconButton(onClick = onAddFarmClicked) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                }
            }
            if (showSearch) {
                IconButton(onClick = {
                    isSearchVisible = !isSearchVisible
                }) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            }

            if (selectedItemsCount > 0 && selectAllEnabled) {
                Checkbox(
                    checked = isAllSelected,
                    onCheckedChange = { onSelectAllChanged(it) }
                )
            }

        },
    )
    // Conditional rendering of the search field
    if (isSearchVisible && showSearch) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
            Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    onSearchQueryChanged(it)
                },
                modifier =
                Modifier
                    .padding(start = 8.dp)
                    .weight(1f),
                label = { Text(stringResource(R.string.search)) },
                leadingIcon = {
                    IconButton(onClick = {
                        // onBackSearchClicked()
                        searchQuery = ""
                        onSearchQueryChanged("")
                        isSearchVisible = !isSearchVisible
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                singleLine = true,
                colors =
                TextFieldDefaults.outlinedTextFieldColors(
                    cursorColor = MaterialTheme.colorScheme.onSurface,
                ),
            )
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
