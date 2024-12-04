package org.technoserve.cafetrac.database.models


data class DeviceFarmDto(
    val device_id: String,
    val collection_site: CollectionSiteDto,
    val farms: List<FarmDetailDto>
)

