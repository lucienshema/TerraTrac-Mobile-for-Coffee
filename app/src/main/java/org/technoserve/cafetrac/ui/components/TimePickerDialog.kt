package org.technoserve.cafetrac.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import org.technoserve.cafetraorg.technoserve.cafetrac.R
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onTimeSelected: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    var showingPicker by remember { mutableStateOf(true) }
    val state = rememberTimePickerState()
    val formatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val configuration = LocalConfiguration.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (showingPicker) "Select Time" else "Enter Time") },
        text = {
            Column {
                if (showingPicker && configuration.screenHeightDp > 400) {
                    TimePicker(state = state)
                } else {
                    TimeInput(state = state)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val selectedTime = LocalTime.of(state.hour, state.minute)
                    onTimeSelected(selectedTime)
                    onDismiss()
                }
            ) {
                Text(text= stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text= stringResource(R.string.cancel))
            }
        },
        icon = {
            if (configuration.screenHeightDp > 400) {
                IconButton(onClick = { showingPicker = !showingPicker }) {
                    Icon(
                        imageVector = if (showingPicker) Icons.Default.Create else Icons.Default.DateRange,
                        contentDescription = if (showingPicker) "Switch to Text Input" else "Switch to Touch Input"
                    )
                }
            }
        }
    )
}