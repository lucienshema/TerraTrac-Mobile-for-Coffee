package org.technoserve.cafetrac.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.cafetrac.database.models.Language
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.technoserve.cafetraorg.technoserve.cafetrac.R
import java.util.Locale

class LanguageViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPreferences =
        application.getSharedPreferences("settings", Context.MODE_PRIVATE)

    // Dynamic list of localized languages
    val languages: List<Language> = getLocalizedLanguages(application)

    private val _currentLanguage = MutableStateFlow(getDefaultLanguage())
    val currentLanguage: StateFlow<Language> = _currentLanguage

    fun selectLanguage(language: Language, context: Context) {
        _currentLanguage.value = language
        savePreferredLanguage(language)
        updateLocale(context, Locale(language.code))
    }

    private fun getDefaultLanguage(): Language {
        val savedLanguageCode =
            sharedPreferences.getString("preferred_language", Locale.getDefault().language)
        return languages.find { it.code == savedLanguageCode } ?: languages.first()
    }

    private fun savePreferredLanguage(language: Language) {
        sharedPreferences.edit().putString("preferred_language", language.code).apply()
    }

    /**
     * This function is used to update the locale with the selected language
     */

    fun updateLocale(context: Context, locale: Locale) {
        Locale.setDefault(locale)

        val config = context.resources.configuration

        config.setLocale(locale)
        config.setLayoutDirection(locale)

        context.resources.updateConfiguration(config, context.resources.displayMetrics)

        val resources = context.resources
        val dm = resources.displayMetrics
        val conf = resources.configuration
        conf.setLocale(locale)
        resources.updateConfiguration(conf, dm)
    }



    /**
     * This function is used to retrieve the languages that are available in the app
     */

    fun getLocalizedLanguages(context: Context): List<Language> {
        val languages = listOf(
            Language("en", context.getString(R.string.english)),
            Language("fr", context.getString(R.string.french)),
            Language("es", context.getString(R.string.spanish)),
            Language("am", context.getString(R.string.amharic)),
            Language("om", context.getString(R.string.oromo)),
            Language("sw", context.getString(R.string.swahili))
        )

        return languages.map { language ->
            Language(language.code, language.displayName)
        }
    }
}
