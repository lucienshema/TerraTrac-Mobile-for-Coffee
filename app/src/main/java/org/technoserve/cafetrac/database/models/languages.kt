package com.example.cafetrac.database.models

data class Language(val code: String, val displayName: String)

val languages = listOf(
    Language("en", "English"),
    Language("fr", "French"),
    Language("es", "Spanish"),
    Language("am", "Amharic"),
    Language("om", "Oromo"),
    Language("sw", "Swahili"),
)

