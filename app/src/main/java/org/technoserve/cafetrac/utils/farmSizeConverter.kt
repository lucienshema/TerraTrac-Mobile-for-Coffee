package org.technoserve.cafetrac.utils

fun convertSize(size: Double, selectedUnit: String): Double {
    return when (selectedUnit) {
        "Ha" -> size // If already in hectares, return as is
        "Acres" -> size * 0.404686 // Convert Acres to hectares
        "Sqm" -> size * 0.0001 // Convert square meters to hectares
        "Timad" -> size * 0.24
        "Fichesa" -> size * 0.25
        "Manzana" -> (size * 0.0001) * 7000
        "Tarea" -> (size * 0.0001) * 432
        else -> throw IllegalArgumentException("Unsupported unit: $selectedUnit")
    }
}
