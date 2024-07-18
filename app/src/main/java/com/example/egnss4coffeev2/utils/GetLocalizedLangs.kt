package com.example.egnss4coffeev2.utils

import android.content.Context
import com.example.egnss4coffeev2.R


fun getLocalizedLanguages(context: Context): List<Language> {
    val languages = listOf(
        Language("en", context.getString(R.string.english)),
        Language("fr", context.getString(R.string.french)),
        Language("es", context.getString(R.string.spanish)),
        Language("am", context.getString(R.string.amharic)),
        Language("om", context.getString(R.string.oromo))
    )

    return languages.map { language ->
        Language(language.code, language.displayName)
    }
}
