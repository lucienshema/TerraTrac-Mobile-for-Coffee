package org.technoserve.cafetrac.database.models

data class FarmRequest(
    val device_id: String,
    val email: String = "",
    val phone_number: String = ""
)
