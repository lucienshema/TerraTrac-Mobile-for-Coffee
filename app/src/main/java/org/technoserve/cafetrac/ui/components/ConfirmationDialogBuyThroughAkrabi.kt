package org.technoserve.cafetrac.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.cafetrac.database.models.BuyThroughAkrabi
import org.technoserve.cafetrac.ui.screens.farms.Action
import org.technoserve.cafetraorg.technoserve.cafetrac.R


@Composable
fun ConfirmationDialogBuyThroughAkrabi(
    listItems: List<BuyThroughAkrabi>,
    action: Action,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    fun validateBuyThroughAkrabi(buyThroughAkrabiItems: List<BuyThroughAkrabi>): Pair<Int, List<BuyThroughAkrabi>> {
        val incompletebuyThroughAkrabiItems =
            buyThroughAkrabiItems.filter { buyThroughAkrabiItem ->
                buyThroughAkrabiItem.akrabiName.isEmpty() ||
                        buyThroughAkrabiItem.siteName.isEmpty() ||
                        buyThroughAkrabiItem.location.isEmpty()
            }
        return Pair(buyThroughAkrabiItems.size, incompletebuyThroughAkrabiItems)
    }
    val (totalbuyThroughAkrabiItems, incompletebuyThroughAkrabiItems) = validateBuyThroughAkrabi(
        listItems
    )
    val message =
        when (action) {
            Action.Export -> stringResource(
                R.string.confirm_export_items,
                totalbuyThroughAkrabiItems,
                incompletebuyThroughAkrabiItems.size
            )

            Action.Share -> stringResource(
                R.string.confirm_share_items,
                totalbuyThroughAkrabiItems,
                incompletebuyThroughAkrabiItems.size
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
