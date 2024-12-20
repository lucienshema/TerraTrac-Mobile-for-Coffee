package org.technoserve.cafetrac.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.technoserve.cafetraorg.technoserve.cafetrac.R


/**
 * This function is used to display a dialog for selecting the file format
 *
 * @param onDismiss: A function to be called when the dialog is dismissed
 * @param onFormatSelected: A function to be called when the format is selected
 *
 * Note: This dialog should be used within a Composable function that is called when the user selects the "Share" or "Export" option in the menu.
 */

@Composable
fun FormatSelectionDialog(
    onDismiss: () -> Unit,
    onFormatSelected: (String) -> Unit,
) {
    var selectedFormat by remember { mutableStateOf("CSV") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = stringResource(R.string.select_file_format)) },
        text = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedFormat == "CSV",
                        onClick = { selectedFormat = "CSV" },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.surface, // Adapts to light/dark mode
                            unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) // Slightly transparent for unselected
                        )
                    )
                    Text(
                        text = stringResource(R.string.csv),
                        color = MaterialTheme.colorScheme.onSurface // Adapts to light/dark mode
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedFormat == "GeoJSON",
                        onClick = { selectedFormat = "GeoJSON" },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.surface, // Adapts to light/dark mode
                            unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) // Slightly transparent for unselected
                        )
                    )
                    Text(
                        text = stringResource(R.string.geojson),
                        color = MaterialTheme.colorScheme.onSurface // Adapts to light/dark mode
                    )
                }
            }

        },
        confirmButton = {
            Button(
                onClick = {
                    onFormatSelected(selectedFormat)
                    onDismiss()
                },
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text(stringResource(R.string.cancel))
            }
        },
        containerColor = MaterialTheme.colorScheme.background, // Background that adapts to light/dark
        tonalElevation = 6.dp // Adds a subtle shadow for better UX
    )
}