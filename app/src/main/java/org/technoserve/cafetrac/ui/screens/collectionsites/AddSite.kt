
package com.example.cafetrac.ui.screens.collectionsites

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cafetrac.database.models.CollectionSite
import org.technoserve.cafetrac.viewmodels.FarmViewModel
import com.example.cafetrac.database.models.Language
import org.technoserve.cafetrac.viewmodels.LanguageViewModel
import org.joda.time.Instant
import org.technoserve.cafetrac.ui.components.SiteForm
import org.technoserve.cafetraorg.technoserve.cafetrac.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSite(navController: NavController, languageViewModel: LanguageViewModel,
            darkMode: MutableState<Boolean>,
            languages: List<Language>) {

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(id = R.string.add_site)) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back to Site List")
                        }
                    }
                )
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                ) {

                    Spacer(modifier = Modifier.height(16.dp))
                    SiteForm(navController)
                }
            }
        )

    }
}



fun addSite(
    farmViewModel: FarmViewModel,
    name: String,
    agentName: String,
    phoneNumber: String,
    email: String,
    village: String,
    district: String,
): CollectionSite {
    val site = CollectionSite(
        name,
        agentName,
        phoneNumber,
        email,
        village,
        district,
        createdAt = Instant.now().millis,
        updatedAt = Instant.now().millis
    )
    farmViewModel.addSite(site){isAdded->
        if (isAdded) {
            Log.d(TAG, " site added")
        }
    }
    return site
}