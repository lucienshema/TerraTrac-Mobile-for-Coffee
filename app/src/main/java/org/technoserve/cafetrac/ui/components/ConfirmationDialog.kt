package org.technoserve.cafetrac.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.technoserve.cafetrac.database.models.Farm
import org.technoserve.cafetrac.ui.screens.farms.Action
import org.technoserve.cafetraorg.technoserve.cafetrac.R


@Composable
fun ConfirmationDialog(
    listItems: List<Farm>,
    action: Action,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    fun validateFarms(farms: List<Farm>): Pair<Int, List<Farm>> {
        val incompleteFarms =
            farms.filter { farm ->
                farm.farmerName.isEmpty() ||
                        farm.district.isEmpty() ||
                        farm.village.isEmpty() ||
                        farm.latitude == "0.0" ||
                        farm.longitude == "0.0" ||
                        farm.size == 0.0f ||
                        farm.remoteId.toString().isEmpty()
            }
        return Pair(farms.size, incompleteFarms)
    }
    val (totalFarms, incompleteFarms) = validateFarms(listItems)
    val message =
        when (action) {
            Action.Export -> stringResource(
                R.string.confirm_export,
                totalFarms,
                incompleteFarms.size
            )

            Action.Share -> stringResource(R.string.confirm_share, totalFarms, incompleteFarms.size)
        }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.confirm)) },
        text = { Text(text = message) },
        confirmButton = {
            Button(onClick = {
                onConfirm()
                onDismiss()
            }) {
                Text(text = stringResource(R.string.yes))
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text(text = stringResource(R.string.no))
            }
        },
        containerColor = MaterialTheme.colorScheme.background, // Background that adapts to light/dark
        tonalElevation = 6.dp // Adds a subtle shadow for better UX
    )
}