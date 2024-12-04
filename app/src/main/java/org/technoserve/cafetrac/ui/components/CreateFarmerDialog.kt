package org.technoserve.cafetrac.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

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
        containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = 6.dp,
        title = { Text("Create New Farmer") },
    )
}
