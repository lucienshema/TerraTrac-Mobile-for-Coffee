package org.technoserve.cafetrac.database.models


data class CollectionSiteDto(
    val local_cs_id: Long,
    val name: String,
    val agent_name: String,
    val phone_number: String?,
    val email: String?,
    val village: String?,
    val district: String?
)
