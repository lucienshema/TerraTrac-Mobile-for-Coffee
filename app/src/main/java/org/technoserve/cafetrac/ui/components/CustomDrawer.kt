package org.technoserve.cafetrac.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import com.example.cafetrac.database.models.Language
import org.technoserve.cafetraorg.technoserve.cafetrac.R


@Composable
fun CustomDrawer(
    drawerVisible: Boolean,
    onClose: () -> Unit,
    navController: NavController,
    darkMode: MutableState<Boolean>,
    currentLanguage: Language,
    languages: List<Language>,
    onLanguageSelected: (Language) -> Unit,
    onLogout: () -> Unit
) {
    if (drawerVisible) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .clickable { onClose() },
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
                            // Add your drawer items here
                            item {
                                DrawerItem(
                                    text = stringResource(id = R.string.home),
                                    painter = painterResource(R.drawable.home),
                                    onClick = {
                                        if (currentRoute == "shopping") {
                                            onClose()
                                        }
                                        else {
                                            navController.navigate("shopping")
                                            onClose()
                                        }

                                    }
                                )
                            }
                            item {
                                DrawerItem(
                                    text = stringResource(id = R.string.collection_site_registration),
                                    painter = painterResource(R.drawable.add_collection_site),
                                    onClick = {
                                        navController.navigate("siteList")
                                        onClose()
                                    }
                                )
                            }
                            item {
                                DrawerItem(
                                    text = stringResource(id = R.string.farmer_registration),
                                    painter = painterResource(R.drawable.person_add),
                                    onClick = {
                                        navController.navigate("siteList")
                                        onClose()
                                    }
                                )
                            }
                            item {
                                DrawerItem(
                                    text = stringResource(id = R.string.akrabi_registration),
                                    painter = painterResource(R.drawable.person_add),
                                    onClick = {
                                        navController.navigate("akrabi_list_screen")
                                        onClose()
                                    }
                                )
                            }

                            item {
                                Divider()
                            }

                            // Dark Mode Toggle
                            item {
                                DarkModeToggle(darkMode)
                            }

                            item {
                                Divider()
                            }

                            // Language Dropdown
                            item {
                                LanguageSelection(
                                    currentLanguage = currentLanguage,
                                    languages = languages,
                                    onLanguageSelected = { onLanguageSelected(it) }
                                )
                            }

                            // Logout Item
                            item {
                                DrawerItem(
                                    text = stringResource(id = R.string.logout),
                                    painter = painterResource(R.drawable.logout),
                                    onClick = {
                                        onLogout()
                                        onClose()
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