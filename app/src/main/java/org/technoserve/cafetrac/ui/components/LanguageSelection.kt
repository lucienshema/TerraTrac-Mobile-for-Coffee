package org.technoserve.cafetrac.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

import com.example.cafetrac.database.models.Language
import org.technoserve.cafetraorg.technoserve.cafetrac.R

// Language Selection Composable
@Composable
fun LanguageSelection(
    currentLanguage: Language,
    languages: List<Language>,
    onLanguageSelected: (Language) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }


    Text(
        text = stringResource(id = R.string.select_language),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground
    )

    Box(
        modifier = Modifier
            .width(230.dp)
            .padding(8.dp)
    ) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = currentLanguage.displayName,color = MaterialTheme.colorScheme.onBackground)
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(230.dp).background(MaterialTheme.colorScheme.background)
        ) {
            languages.forEach { language ->
                DropdownMenuItem(
                    text = { Text(language.displayName,color = MaterialTheme.colorScheme.onBackground) },
                    onClick = {
                        onLanguageSelected(language)
                        expanded = false
                    },
                    modifier = Modifier.background(MaterialTheme.colorScheme.background)
                )
            }
        }
    }
}