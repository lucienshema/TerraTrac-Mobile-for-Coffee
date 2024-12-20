package org.technoserve.cafetrac.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.technoserve.cafetraorg.technoserve.cafetrac.R

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            // Blocks Sunday and Saturday from being selected.
            @RequiresApi(Build.VERSION_CODES.O)
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val dayOfWeek = Instant.ofEpochMilli(utcTimeMillis)
                    .atZone(ZoneId.of("UTC"))
                    .toLocalDate()
                    .dayOfWeek
                return dayOfWeek != DayOfWeek.SUNDAY && dayOfWeek != DayOfWeek.SATURDAY
            }
            // Allow selecting dates from the current year forward.
            @RequiresApi(Build.VERSION_CODES.O)
            override fun isSelectableYear(year: Int): Boolean {
                return year >= LocalDate.now().year
            }
        }
    )

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Select Date") },
        text = {
            Column {
                DatePicker(state = datePickerState)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Selected date: ${datePickerState.selectedDateMillis?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    } ?: "No selection"}",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val selectedDate = Instant.ofEpochMilli(it)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        onDateSelected(selectedDate)
                    }
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
        containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = 6.dp
    )
}