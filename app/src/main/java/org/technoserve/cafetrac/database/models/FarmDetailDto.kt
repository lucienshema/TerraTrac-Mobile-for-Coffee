package org.technoserve.cafetrac.database.models

data class FarmDetailDto(
    val remote_id: String,
    val farmer_name: String,
    val member_id: String,
    val village: String,
    val district: String,
    val size: Float,
    val latitude: Double,
    val longitude: Double,
    val coordinates: List<List<Double?>>?,
    val accuracies: List<Float?>?,
)

