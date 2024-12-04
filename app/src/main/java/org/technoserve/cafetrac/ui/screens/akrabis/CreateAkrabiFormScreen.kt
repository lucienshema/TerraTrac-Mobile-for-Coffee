package com.example.cafetrac.ui.screens.akrabis

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController

import com.example.cafetrac.database.models.CollectionSite
import org.technoserve.cafetrac.viewmodels.AkrabiViewModel
import org.technoserve.cafetraorg.technoserve.cafetrac.R

@Composable
fun CreateAkrabiFormScreen(navController: NavController, akrabiViewModel: AkrabiViewModel, collectionSites: List<CollectionSite>) {
    CreateAkrabiForm(
        navController = navController,
        title= stringResource(id= R.string.create_akrabi_form),
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