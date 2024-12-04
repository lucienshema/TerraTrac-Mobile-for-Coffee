package org.technoserve.cafetrac.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.cafetrac.database.models.DirectBuy
import org.technoserve.cafetrac.ui.screens.farms.Action
import org.technoserve.cafetraorg.technoserve.cafetrac.R


@Composable
fun ConfirmationDialogDirectBuy(
    listItems: List<DirectBuy>,
    action: Action,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    fun validateDirectBuy(directBuyItems: List<DirectBuy>): Pair<Int, List<DirectBuy>> {
        val incompletedirectBuyItems =
            directBuyItems.filter { directBuy ->
                directBuy.farmerName.isEmpty() ||
                        directBuy.siteName.isEmpty() ||
                        directBuy.location.isEmpty()
            }
        return Pair(directBuyItems.size, incompletedirectBuyItems)
    }
    val (totaldirectBuyItems, incompletedirectBuyItems) = validateDirectBuy(listItems)
    val message =
        when (action) {
            Action.Export -> stringResource(
                R.string.confirm_export_items,
                totaldirectBuyItems,
                incompletedirectBuyItems.size
            )

            Action.Share -> stringResource(
                R.string.confirm_share_items,
                totaldirectBuyItems,
                incompletedirectBuyItems.size
            )
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
    )
}