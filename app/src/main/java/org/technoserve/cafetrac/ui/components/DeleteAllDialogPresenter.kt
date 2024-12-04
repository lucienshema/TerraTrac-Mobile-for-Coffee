package org.technoserve.cafetrac.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.technoserve.cafetraorg.technoserve.cafetrac.R


/**
 *  This function is used to display a warning dialog when the user tries to delete a farm
 *  It shows a warning icon, a message, and two buttons: "Yes" and "No"
 *  When the user clicks "Yes", the onProceedFn function is called to perform the deletion action
 *  When the user clicks "No", the delete dialog is dismissed by setting showDeleteDialog to false
 *  The containerColor and textColors are set to match the Material3 theme's background and error colors respectively
 *  The modifier is set to adjust the padding and size of the dialog to better fit the layout and adhere to the design guidelines of the Material3 component library
 *  The text and button texts are localized using the stringResource function to support different languages
 *  Note: This is a simplified version of the DeleteAllDialogPresenter function. In a real-world application, you may want to add additional logic and features to handle
 * deleting all resources associated with this dialog
 */


@Composable
fun DeleteAllDialogPresenter(
    showDeleteDialog: MutableState<Boolean>,
    onProceedFn: (deleteAll: Boolean) -> Unit,
) {
    val (selectedOption, setSelectedOption) = remember { mutableStateOf(true) } // true for deleting all data, false for deleting selected data

    if (showDeleteDialog.value) {
        AlertDialog(
            modifier = Modifier.padding(horizontal = 32.dp),
            onDismissRequest = { showDeleteDialog.value = false },
            title = { Text(text = stringResource(id = R.string.delete_this_item)) },
            text = {
                Column {
                    Text(stringResource(id = R.string.are_you_sure))
                    Text(stringResource(id = R.string.item_will_be_deleted))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Options for deleting all data or only selected data
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedOption,
                            onClick = { setSelectedOption(true) }
                        )
                        Text(
                            text = stringResource(id = R.string.delete_all),
                            modifier = Modifier.clickable { setSelectedOption(true) }
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = !selectedOption,
                            onClick = { setSelectedOption(false) }
                        )
                        Text(
                            text = stringResource(id = R.string.delete_selected),
                            modifier = Modifier.clickable { setSelectedOption(false) }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onProceedFn(selectedOption)
                    showDeleteDialog.value = false
                }) {
                    Text(text = stringResource(id = R.string.yes))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog.value = false }) {
                    Text(text = stringResource(id = R.string.no))
                }
            },
        )
    }
}