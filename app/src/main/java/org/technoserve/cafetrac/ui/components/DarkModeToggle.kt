package org.technoserve.cafetrac.ui.components

import android.content.Context
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import org.technoserve.cafetraorg.technoserve.cafetrac.R


// Dark Mode Toggle Composable
@Composable
fun DarkModeToggle(darkMode: MutableState<Boolean>) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("theme_mode", Context.MODE_PRIVATE)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()) {
        Text(text = stringResource(id = R.string.light_dark_theme), style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = darkMode.value,
            onCheckedChange = {
                darkMode.value = it
                sharedPreferences.edit().putBoolean("dark_mode", it).apply()
            }
        )
    }

}
