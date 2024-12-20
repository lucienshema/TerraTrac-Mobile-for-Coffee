package org.technoserve.cafetrac.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import org.technoserve.cafetrac.database.AppDatabase
import org.technoserve.cafetrac.database.models.Farm
import com.example.cafetrac.database.helpers.MyPagingSource
import com.example.cafetrac.database.helpers.RefreshableLiveData
import com.example.cafetrac.database.sync.remote.ApiService
import org.technoserve.cafetrac.repositories.FarmRepository
import org.technoserve.cafetrac.ui.screens.farms.truncateToDecimalPlaces
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.Instant
import org.json.JSONObject
import org.technoserve.cafetrac.database.models.BuyThroughAkrabi
import org.technoserve.cafetrac.database.models.CollectionSite
import org.technoserve.cafetrac.database.models.DirectBuy
import org.technoserve.cafetrac.database.models.FarmRequest
import org.technoserve.cafetraorg.technoserve.cafetrac.BuildConfig
import org.technoserve.cafetraorg.technoserve.cafetrac.R

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import java.util.regex.Pattern

data class ImportResult(
    val success: Boolean,
    val message: String,
    val importedFarms: List<Farm>,
    val duplicateFarms: List<String> = emptyList(),
    val farmsNeedingUpdate: List<Farm> = emptyList(),
    val invalidFarms: List<String> = emptyList()
)

data class FarmAddResult(
    val success: Boolean,
    val message: String,
    val farm: Farm,
)

data class ParsedFarms(
    val validFarms: List<Farm>,
    val invalidFarms: List<String>
)

// Define a sealed class for restore status
sealed class RestoreStatus {
    object InProgress : RestoreStatus()
    data class Success(val addedCount: Int, val message: String, val sitesCreated: Int = 0) : RestoreStatus()
    data class Error(val message: String) : RestoreStatus()
}


data class CollectionSiteRestore(
    val id: Long,
    val local_cs_id:Long,
    val name: String,
    val device_id: String,
    val agent_name: String,
    val email: String,
    val phone_number: String,
    val village: String,
    val district: String,
    val created_at: String,
    val updated_at: String
)

data class FarmRestore(
    val id: Long,
    val remote_id: String,
    val farmer_name: String,
    val member_id: String?,
    val size: Double,
    val agent_name: String?,
    val village: String,
    val district: String,
    val latitude: Double,
    val longitude: Double,
    val coordinates: List<List<Double>>,
    val accuracyArray: List<Float?>?,
    val created_at: String,
    val updated_at: String,
    val site_id: Long
)

data class ServerFarmResponse(
    val device_id: String,
    val collection_site: CollectionSiteRestore,
    val farms: List<FarmRestore>
)




class FarmViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val repository: FarmRepository
    val readAllSites: RefreshableLiveData<List<CollectionSite>>
    val readData: RefreshableLiveData<List<Farm>>


    private val _farms = MutableLiveData<List<Farm>>()
    val farms: LiveData<List<Farm>> get() = _farms

    private val _restoreStatus = MutableLiveData<RestoreStatus>()
    val restoreStatus: LiveData<RestoreStatus> get() = _restoreStatus

    private val _filteredFarms = MutableLiveData<List<Farm>>()
    val filteredFarms: LiveData<List<Farm>> get() = _filteredFarms

    private val _boughtItems = MutableStateFlow<List<BuyThroughAkrabi>>(emptyList())
    val boughtItems: StateFlow<List<BuyThroughAkrabi>> get() = _boughtItems

    private val _boughtItemsDirectBuy = MutableStateFlow<List<DirectBuy>>(emptyList())
    val boughtItemsDirectBuy: StateFlow<List<DirectBuy>> = _boughtItemsDirectBuy

    private val _originalBoughtItems = MutableStateFlow<List<BuyThroughAkrabi>>(emptyList())
    private val _originalBoughtDirectItems = MutableStateFlow<List<DirectBuy>>(emptyList())

    private val _selectedSiteIds = MutableLiveData<List<Long>>()
    val selectedSiteIds: LiveData<List<Long>> get() = _selectedSiteIds

    private val apiService: ApiService


    init {
        val farmDAO = AppDatabase.getInstance(application).farmsDAO()
        repository = FarmRepository(farmDAO)
        readAllSites = RefreshableLiveData { repository.readAllSites }
        readData = RefreshableLiveData { repository.readData }

        // Load initial data
        loadBoughtItemsDirectBuy()

        // Filter farms based on selected site IDs
        filterFarmsBySiteIds()

        // Observe data changes and filter farms
        readData.observeForever { farms ->
            _farms.value = farms
            filterFarmsBySiteIds() // Ensure farms are filtered based on selectedSiteIds when data is loaded
        }

        _selectedSiteIds.observeForever { siteIds ->
            filterFarmsBySiteIds() // Filter farms whenever selectedSiteIds change
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

    }

    private fun loadBoughtItemsDirectBuy() {
        // Load data from data source (e.g., database)
        // Update the StateFlow with the loaded data
        viewModelScope.launch {
            _boughtItems.value = repository.getBoughtItemsByDateRange("2024-01-01", "2024-12-31")
            _boughtItemsDirectBuy.value = repository.getDirectBuysByDateRange("2024-01-01", "2024-12-31")
            _originalBoughtItems.value = repository.getBoughtItemsByDateRange("2024-01-01", "2024-12-31")
            _originalBoughtDirectItems.value = repository.getDirectBuysByDateRange("2024-01-01", "2024-12-31")
        }
    }


    val readAllBoughtItems: Flow<List<BuyThroughAkrabi>> = repository.getAllBoughtItems()

    fun insertBoughtItem(buyThroughAkrabi: BuyThroughAkrabi) = viewModelScope.launch {
        repository.insert(buyThroughAkrabi)
        // Refresh the list
        loadBoughtItemsDirectBuy()
    }

    fun getBoughtItemById(id: Long): Flow<BuyThroughAkrabi?> = repository.getBoughtItemById(id)

    fun getBoughtItemDirectBuyById(id: Long): Flow<DirectBuy?> = repository.getDirectBuyById(id)

    fun getBoughtItemDirectById(itemId: Long?): DirectBuy? {
        return boughtItemsDirectBuy.value.find { it.id == itemId }
    }

    fun getBoughtItemThroughAkrabiById(itemId: Long?): BuyThroughAkrabi? {
        return boughtItems.value.find { it.id == itemId }
    }


    fun insertBoughtItemDirect(directBuy: DirectBuy) = viewModelScope.launch {
        repository.insertDirectBuy(directBuy)
        // Refresh the list
        loadBoughtItemsDirectBuy()
    }
    fun updateDirectBuy(directBuy: DirectBuy) = viewModelScope.launch {
        repository.updateDirectBuy(directBuy)
        // Refresh the list
        loadBoughtItemsDirectBuy()
    }

    fun deleteBoughtItemDirectBuy(directBuy: DirectBuy) = viewModelScope.launch {
        repository.deleteDirectBuy(directBuy)
        // Refresh the list
        loadBoughtItemsDirectBuy()
    }

    fun updateBuyThroughAkrabi(akrabi: BuyThroughAkrabi) = viewModelScope.launch {
        repository.updateBuyThroughAkrabi(akrabi)
        // Refresh the list
        loadBoughtItemsDirectBuy()
    }

    fun deleteBoughtItemBuyThroughAkrabi(buyThroughAkrabi: BuyThroughAkrabi) = viewModelScope.launch {
        repository.deleteBuyThroughAkrabi(buyThroughAkrabi)
        // Refresh the list
        loadBoughtItemsDirectBuy()
    }

    fun normalizeDate(dateString: String): String {
        return dateString.split("-").map { it.padStart(2, '0') }.joinToString("-")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun filterBoughtItems(startDate: String, endDate: String) {
        viewModelScope.launch {
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

            // Normalize date strings
            val normalizedStartDate = normalizeDate(startDate)
            val normalizedEndDate = normalizeDate(endDate)

            val start = LocalDate.parse(normalizedStartDate, dateFormatter)
            val end = LocalDate.parse(normalizedEndDate, dateFormatter)

            // Access the original list of bought items
            val originalItems = _boughtItems.value


            // Filter the items based on the date range
            val filteredItems = originalItems.filter { item ->
                val itemDate = LocalDate.parse(normalizeDate(item.date.toString()), dateFormatter)
                itemDate.isAfter(start.minusDays(1)) && itemDate.isBefore(end.plusDays(1))
            }

            // Update the _boughtItems with the filtered list
            _boughtItems.value = filteredItems
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun filterBoughtItemsDirectBuy(startDate: String, endDate: String) {
        viewModelScope.launch {
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

            // Normalize date strings
            val normalizedStartDate = normalizeDate(startDate)
            val normalizedEndDate = normalizeDate(endDate)

            val start = LocalDate.parse(normalizedStartDate, dateFormatter)
            val end = LocalDate.parse(normalizedEndDate, dateFormatter)

            // Access the original list of bought items
            val originalItems = _boughtItemsDirectBuy.value

            // Filter the items based on the date range
            val filteredItems = originalItems.filter { item ->
                val itemDate = LocalDate.parse(normalizeDate(item.date.toString()), dateFormatter)
                itemDate.isAfter(start.minusDays(1)) && itemDate.isBefore(end.plusDays(1))
            }

            // Update the _boughtItems with the filtered list
            _boughtItemsDirectBuy.value = filteredItems
        }
    }

    fun clearFilter() {
        viewModelScope.launch {
            // Restore the original list
            _boughtItems.value=_originalBoughtItems.value
        }
    }

    fun clearFilterDirectBuy() {
        viewModelScope.launch {
            // Restore the original list
            _boughtItemsDirectBuy.value = _originalBoughtDirectItems.value
        }
    }

    fun getLastSiteId(): Long? {
        // Assuming `readAllSites` is a list or LiveData holding the collection sites
        return readAllSites.value?.lastOrNull()?.siteId
    }


    fun readAllData(siteId: Long): LiveData<List<Farm>> = repository.readAllFarms(siteId)

    fun getSingleFarm(farmId: Long): LiveData<List<Farm>> = repository.readFarm(farmId)

    fun addFarm(
        farm: Farm,
        siteId: Long,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (!repository.isFarmDuplicateBoolean(farm)) {
                repository.addFarm(farm)
                FarmAddResult(success = true, message = "Farm added successfully", farm)
                // Update the LiveData list
                _farms.postValue(repository.readAllFarms(siteId).value ?: emptyList())
            } else {
                FarmAddResult(
                    success = false,
                    message = "Duplicate farm: ${farm.farmerName}, Site ID: ${farm.siteId}. Needs update.",
                    farm,
                )
            }
        }
    }

    fun addSite(site: CollectionSite, onSuccess: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.addSite(site)
            withContext(Dispatchers.Main) {
                onSuccess(result)  // Return the result to the main thread
            }
        }
    }

    fun getLastFarm(): LiveData<List<Farm>> = repository.getLastFarm()

    // Updates an existing farm in the repository and updates the LiveData list
    fun updateFarm(farm: Farm) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateFarm(farm)

            // Fetch the updated list and post the value to LiveData
            val updatedFarms = repository.readAllFarms(farm.siteId).value ?: emptyList()
            _farms.postValue(updatedFarms)
        }
    }

    fun updateSite(site: CollectionSite) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateSite(site)
        }
    }

    fun deleteFarm(farm: Farm) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFarm(farm)
        }
    }

    fun deleteFarmById(farm: Farm) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFarmById(farm)
        }
    }

    fun deleteAllFarms() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllFarms()
        }
    }

    fun updateSyncStatus(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateSyncStatus(id)
        }
    }

    fun updateSyncListStatus(ids: List<Long>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateSyncListStatus(ids)
        }
    }

    fun deleteList(ids: List<Long>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteList(ids)
        }
    }

    fun deleteListSite(ids: List<Long>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteListSite(ids)
        }
    }
    fun deleteById(farmId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteById(farmId)
        }
    }


    // Function to get the total number of farms for a site
    fun getTotalFarms(siteId: Long): LiveData<Int> {
        return repository.getTotalFarmsForSite(siteId)
    }

    // Function to get the number of farms with incomplete data for a site
    fun getFarmsWithIncompleteData(siteId: Long): LiveData<Int> {
        return repository.getFarmsWithIncompleteDataForSite(siteId)
    }


    private fun filterFarmsBySiteIds() {
        val siteIds = _selectedSiteIds.value ?: emptyList()
        _farms.value?.let { farms ->
            _farms.value = farms.filter { farm ->
                siteIds.contains(farm.siteId)
            }
        }
    }

    fun updateSelectedSiteIds(newSiteIds: List<Long>) {
        _selectedSiteIds.value = newSiteIds
    }

    // Function to filter farms based on selected IDs
    fun filterFarmsBySelectedIds(selectedIds: List<Long>) {
        viewModelScope.launch {
            _filteredFarms.value = repository.getFarmsBySelectedIds(selectedIds).value
        }
    }

    fun getFilteredFarms(selectedIds: List<Long>): LiveData<List<Farm>> {
        return repository.getFarmsBySelectedIds(selectedIds)
    }

    fun refreshData(siteId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            // Logic to refresh data, typically re-fetching from the database or repository
            repository.readAllFarms(siteId)
        }
    }

    val pager = Pager(PagingConfig(pageSize = 3)) {
        MyPagingSource(this)
    }.flow.cachedIn(viewModelScope)


    suspend fun getCollectionSites(page: Int, pageSize: Int): List<CollectionSite> {
        return repository.getCollectionSites(page, pageSize)
    }



    private fun parseDateStringToTimestamp(dateString: String): Long {
        val dateFormatter =
            java.text.SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", java.util.Locale.US)
        return dateFormatter.parse(dateString).time
    }

    private suspend fun parseGeoJson(
        geoJsonString: String,
        siteId: Long,
    ): ParsedFarms {
        val validFarms = mutableListOf<Farm>()
        val invalidFarms = mutableListOf<String>()

        try {
            val geoJson = JSONObject(geoJsonString)
            val features = geoJson.getJSONArray("features")

            for (i in 0 until features.length()) {
                val feature = features.getJSONObject(i)
                val properties = feature.getJSONObject("properties")
                val geometry = feature.getJSONObject("geometry")

                // Handling the remoteId
                val remoteId =
                    try {
                        val idString = properties.optString("remote_id")
                        if (idString.isNotEmpty()) UUID.fromString(idString) else UUID.randomUUID()
                    } catch (e: IllegalArgumentException) {
                        UUID.randomUUID()
                    }

                val farmerName = properties.optString("farmer_name", "Unknown")
                val memberId = properties.optString("member_id", "Unknown")
                val village = properties.optString("farm_village", "Unknown")
                val district = properties.optString("farm_district", "Unknown")
                val size = properties.optDouble("farm_size", Double.NaN).toFloat()
                val latitude =
                    properties.optDouble("latitude", Double.NaN).takeIf { !it.isNaN() }?.toString()
                val longitude =
                    properties.optDouble("longitude", Double.NaN).takeIf { !it.isNaN() }?.toString()

                val age = properties.optString("age", "0").toIntOrNull() ?: 0
                val gender = properties.optString("gender", "")
                val govtIdNumber = properties.optString("govtIdNumber", "")
                val numberOfTrees = properties.optString("numberOfTrees", "0").toIntOrNull() ?: 0
                val phone = properties.optString("phone", "")
                val photo = properties.optString("photo", "")

                val currentTime = System.currentTimeMillis()

                val createdAt =
                    try {
                        properties.optString("created_at").toLongOrNull() ?: currentTime
                    } catch (e: Exception) {
                        currentTime
                    }

                val updatedAt =
                    try {
                        properties.optString("updated_at").toLongOrNull() ?: currentTime
                    } catch (e: Exception) {
                        currentTime
                    }

                var coordinates: List<Pair<Double, Double>>? = null
                val geoType = geometry.getString("type")
                if (geoType == "Point") {
                    // Handle Point geometry
                    val coordArray = geometry.getJSONArray("coordinates")
                    val lon = coordArray.getDouble(1)
                    val lat = coordArray.getDouble(0)
                    coordinates = listOf(Pair(lon, lat))
                } else if (geoType == "Polygon") {
                    val coordArray = geometry.getJSONArray("coordinates").getJSONArray(0)
                    val coordList = mutableListOf<Pair<Double, Double>>()
                    for (j in 0 until coordArray.length()) {
                        val coord = coordArray.getJSONArray(j)
                        coordList.add(Pair(coord.getDouble(0), coord.getDouble(1)))
                    }
                    coordinates = coordList
                }

                var accuracyArray: List<Float?>? = null
                val accuracyArrayString = properties.optString("accuracyArray")

                println("Accuracy Array $accuracyArrayString")

                // Check if accuracyArrayString is not empty or null
                if (!accuracyArrayString.isNullOrEmpty()) {
                    // Remove square brackets and split the string by commas
                    val cleanAccuracyArrayString = accuracyArrayString.replace("[", "").replace("]", "")
                    val accuracyValues = cleanAccuracyArrayString.split(",").map { it.trim() }

                    // Convert to a list of floats
                    accuracyArray = accuracyValues.map { it.toFloatOrNull() }
                }

                println("Parsed Accuracy Array $accuracyArray")

                val newFarm = coordinates?.let {
                    Farm(
                        siteId = siteId,
                        remoteId = remoteId,
                        farmerPhoto = "farmer-photo",
                        farmerName = farmerName,
                        memberId = memberId,
                        village = village,
                        district = district,
                        purchases = 2.30f,
                        size = size.takeIf { !it.isNaN() } ?: 0f,
                        latitude = latitude ?: "0.0",
                        longitude = longitude ?: "0.0",
                        coordinates = it,
                        accuracyArray = accuracyArray ,
                        age = age,
                        gender = gender,
                        govtIdNumber = govtIdNumber,
                        numberOfTrees = numberOfTrees,
                        phone = phone,
                        photo = photo,
                        synced = false,
                        scheduledForSync = false,
                        createdAt = createdAt,
                        updatedAt = updatedAt,
                    )
                }

                if (newFarm != null) {
                    validFarms.add(newFarm)
                }
                else {
                    invalidFarms.add(feature.toString())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // return farms
        return ParsedFarms(validFarms, invalidFarms)
    }

    fun parseCoordinates(coordinatesString: String): List<Pair<Double, Double>> {
        val result = mutableListOf<Pair<Double, Double>>()
        val cleanedString = coordinatesString.trim().removeSurrounding("\"", "").replace(" ", "")

        if (cleanedString.isNotEmpty()) {
            // Check if the coordinates are in polygon or point format
            val isPolygon = cleanedString.startsWith("[[") && cleanedString.endsWith("]]")
            val isPoint = cleanedString.startsWith("[") && cleanedString.endsWith("]") && !isPolygon

            if (isPolygon) {
                // Handle Polygon Format
                val pairs =
                    cleanedString
                        .removePrefix("[[")
                        .removeSuffix("]]")
                        .split("],[")
                        .map { it.split(",") }
                for (pair in pairs) {
                    if (pair.size == 2) {
                        try {
                            val lat = pair[1].toDouble()
                            val lon = pair[0].toDouble()
                            result.add(Pair(lat, lon))
                        } catch (e: NumberFormatException) {
                            println("Error parsing polygon coordinate pair: ${pair.joinToString(",")}")
                        }
                    }
                }
            } else if (isPoint) {
                // Handle Point Format
                val coords = cleanedString.removePrefix("[").removeSuffix("]").split(", ")
                if (coords.size == 2) {
                    try {
                        val lat = coords[1].toDouble()
                        val lon = coords[0].toDouble()
                        result.add(Pair(lat, lon))
                    } catch (e: NumberFormatException) {
                        println("Error parsing point coordinate pair: ${coords.joinToString(",")}")
                    }
                }
            } else {
                println("Unrecognized coordinates format: $coordinatesString")
            }
        }
        return result
    }

    fun showCustomToast(context: Context, message: String, duration: Long) {
        val toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
        toast.show()

        // Dismiss the toast after the desired duration
        Handler(Looper.getMainLooper()).postDelayed({
            toast.cancel()
        }, duration)
    }


    @RequiresApi(Build.VERSION_CODES.N)
    suspend fun importFile(
        context: Context,
        uri: Uri,
        siteId: Long,
    ): ImportResult =
        withContext(Dispatchers.IO) {
            var message = ""
            var success = false
            val importedFarms = mutableListOf<Farm>()
            val duplicateFarms = mutableListOf<String>()
            val farmsNeedingUpdate = mutableListOf<Farm>()
            val invalidFarms = mutableListOf<String>()
            try {
                // Check file extension before proceeding
                val fileName =
                    uri.lastPathSegment ?: throw IllegalArgumentException("Invalid file URI")
                if (!fileName.endsWith(".csv", true) && !fileName.endsWith(".geojson", true)) {
                    message = context.getString(R.string.unsupported_file_format)
                    return@withContext ImportResult(success, message, importedFarms)
                }

                val inputStream =
                    context.contentResolver.openInputStream(uri)
                        ?: throw IllegalArgumentException("Cannot open file input stream")
                val reader = BufferedReader(InputStreamReader(inputStream))
                val firstLine = reader.readLine()
                val farms = mutableListOf<Farm>()

                println("First line: $firstLine")

                if (firstLine.trim().startsWith("{")) {
                    // It's a GeoJSON file
                    val content = StringBuilder()
                    content.append(firstLine)
                    reader.lines().forEach { content.append(it) }
                    reader.close()
                    // val newFarms = parseGeoJson(content.toString(), siteId)
                    val parsedFarms = parseGeoJson(content.toString(), siteId)
                    val newFarms = parsedFarms.validFarms
                    val GeojsoninvalidFarms = parsedFarms.invalidFarms.toString()
                    invalidFarms.add(GeojsoninvalidFarms)
                    println("Parsed farms from GeoJSON: $newFarms")
                    for (newFarm in newFarms) {
                        if (!repository.isFarmDuplicateBoolean(newFarm)) {
                            println("Adding farm: ${newFarm.farmerName}, Site ID: ${newFarm.siteId}")
                            addFarm(newFarm, newFarm.siteId)
                            importedFarms.add(newFarm)
                        }

                        val existingFarm = newFarm.remoteId?.let {
                            repository.getFarmByDetails(newFarm)
                        }


                        if (repository.farmNeedsUpdateImport(newFarm)) {
                            // Farm needs an update
                            println("Farm needs update: ${newFarm.farmerName}, Site ID: ${newFarm.siteId}")
                            farmsNeedingUpdate.add(newFarm)
                        } else {
                            if (existingFarm?.let { repository.isDuplicateFarm(it, newFarm) } == true) {
                                // Farm is a duplicate but does not need an update
                                val duplicateMessage =
                                    "Duplicate farm: ${newFarm.farmerName}, Site ID: ${newFarm.siteId}"
                                println(duplicateMessage)
                                duplicateFarms.add(duplicateMessage)
                            }
                        }
                    }
                    message = context.getString(R.string.geojson_import_successful)
                    success = true
                } else if (firstLine.contains(",")) {
                    // It's a CSV file
                    var line: String? = firstLine
                    line = reader.readLine() // Read first data line
                    while (line != null) {
                        val values =
                            line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)".toRegex()) // Split CSV line, ignoring commas within quotes

                        if (values.size >= 13) {
                            val remoteId =
                                try {
                                    if (values[0].isNotEmpty()) UUID.fromString(values[0]) else UUID.randomUUID()
                                } catch (e: IllegalArgumentException) {
                                    UUID.randomUUID()
                                }

                            val farmerName = values.getOrNull(1) ?: ""
                            val memberId = values.getOrNull(2) ?: ""
                            val siteName = values.getOrNull(3) ?: ""
                            val agentName = values.getOrNull(4) ?: ""
                            val village = values.getOrNull(5) ?: ""
                            val district = values.getOrNull(6) ?: ""
                            val size = values.getOrNull(7)?.toFloatOrNull()
                            val latitude = values.getOrNull(8)
                            val longitude = values.getOrNull(9)

                            val age = values.getOrNull(5)
                            val gender = values.getOrNull(5) ?: ""
                            val govtIdNumber = values.getOrNull(5) ?: ""
                            val numberOfTrees = values.getOrNull(5)
                            val phone = values.getOrNull(5) ?: ""
                            val photo = values.getOrNull(5) ?: ""

                            // Extract and parse coordinates
                            val coordinatesString =
                                values.getOrNull(10)?.removeSurrounding("\"", "\"") ?: ""
                            val coordinates = parseCoordinates(coordinatesString)
                            println("Coordinates $coordinates")

                            val currentTime = System.currentTimeMillis()
                            val createdAt =
                                try {
                                    if (values
                                            .getOrNull(11)
                                            ?.isNotEmpty() == true
                                    ) {
                                        parseDateStringToTimestamp(values[11])
                                    } else {
                                        currentTime
                                    }
                                } catch (e: Exception) {
                                    currentTime
                                }

                            val updatedAt =
                                try {
                                    if (values
                                            .getOrNull(12)
                                            ?.isNotEmpty() == true
                                    ) {
                                        parseDateStringToTimestamp(values[12])
                                    } else {
                                        currentTime
                                    }
                                } catch (e: Exception) {
                                    currentTime
                                }

                            var accuracyArray : List<Float?>? = null

                            val accuraciesString = values.getOrNull(11)?.removeSurrounding("\"", "\"") ?: ""
                            accuracyArray = if (accuraciesString.isNotBlank()) {
                                accuraciesString.split(",").map { it.toFloatOrNull() }
                            } else {
                                listOf(null)
                            }

                            // Process each record here
                            println("Processing record for remote ID: $remoteId")

                            val newFarm =
                                Farm(
                                    siteId = siteId,
                                    remoteId = remoteId,
                                    farmerPhoto = "farmer-photo",
                                    farmerName = farmerName,
                                    memberId = memberId,
                                    village = village,
                                    district = district,
                                    purchases = 2.30f,
                                    size =size ?: 0f,
                                    latitude = latitude ?: "0.0",
                                    longitude = longitude ?: "0.0",
                                    coordinates = coordinates ?: emptyList(),
                                    accuracyArray = accuracyArray,
                                    age = age?.toInt(),  // New field, default value if null
                                    gender = gender ?: "",  // New field, default value if null
                                    govtIdNumber = govtIdNumber ?: "",  // New field, default value if null
                                    numberOfTrees = numberOfTrees?.toInt(),  // New field, default value if null
                                    phone = phone ?: "",  // New field, default value if null
                                    photo = photo ?: "",  // New field, default value if null
                                    synced = false,
                                    scheduledForSync = false,
                                    createdAt = createdAt,
                                    updatedAt = updatedAt,
                                )

                            if (!repository.isFarmDuplicateBoolean(newFarm)) {
                                println("Adding farm: ${newFarm.farmerName}, Site ID: ${newFarm.siteId}")
                                addFarm(newFarm, newFarm.siteId)
                                importedFarms.add(newFarm)
                            }

                            val existingFarm = newFarm.remoteId?.let {
                                repository.getFarmByDetails(newFarm)
                            }

                            if (repository.farmNeedsUpdateImport(newFarm)) {
                                // Farm needs an update
                                println("Farm needs update: ${newFarm.farmerName}, Site ID: ${newFarm.siteId}")
                                farmsNeedingUpdate.add(newFarm)
                            } else {
                                if (existingFarm?.let { repository.isDuplicateFarm(it, newFarm) } == true) {
                                    // Farm is a duplicate but does not need an update
                                    val duplicateMessage =
                                        "Duplicate farm: ${newFarm.farmerName}, Site ID: ${newFarm.siteId}"
                                    println(duplicateMessage)
                                    duplicateFarms.add(duplicateMessage)
                                }
                            }
                        } else {
                            println("Line does not contain enough data: $line")
                            val invalidFarm =
                                "Record of ${values.getOrNull(1)} is not inserted"
                            invalidFarms.add(invalidFarm)
                        }
                        line = reader.readLine()
                    }
                    reader.close()
                    println("Parsed farms from CSV: $farms")
                    // repository.importFarms(farms)
                    // importFarms(siteId,farms)

                    message = context.getString(R.string.csv_import_successful)
                    success = true
                } else {
                    message = context.getString(R.string.unsupported_file_format)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                message = context.getString(R.string.import_failed_with_error, e.message)
            }

            // Show a toast message for duplicate farms
            withContext(Dispatchers.Main) {
                if (duplicateFarms.isNotEmpty()) {
                    val duplicateFarmsMessage = context.getString(R.string.duplicate_farms, duplicateFarms.size)
                    //Toast.makeText(context, "${duplicateFarms.size} Duplicate farms exist", Toast.LENGTH_LONG).show()
                    Toast.makeText(context, duplicateFarmsMessage, Toast.LENGTH_LONG).show()
                }
            }

            // Show a toast message for invalid farms
            withContext(Dispatchers.Main) {
                if (invalidFarms.isNotEmpty()) {
                    // Extract farmer names from invalid farm messages
                    val farmerNames = invalidFarms.joinToString(separator = ", ") {
                        // Extract the farmer's name from the string format "Record of [farmer_name] is not inserted"
                        it.substringAfter("Record of ").substringBefore(" is not inserted")
                    }

                    // Construct the toast message
                    val invalidFarmsMessage = context.getString(
                        R.string.invalid_farms_with_names,
                        invalidFarms.size,
                        farmerNames
                    )

                    // Show a custom duration toast
                    showCustomToast(context, invalidFarmsMessage, 5000)
                }
            }
            // Show a toast message for farms that needs updates
            withContext(Dispatchers.Main) {
                if (farmsNeedingUpdate.isNotEmpty()) {
                    val farmsNeedingUpdateMessage = context.getString(R.string.farms_needing_update, farmsNeedingUpdate.size)
                    Toast.makeText(context, farmsNeedingUpdateMessage, Toast.LENGTH_LONG).show()
                }
            }
            // Flag farmers with new plot info
            flagFarmersWithNewPlotInfo(siteId, farmsNeedingUpdate, this@FarmViewModel)

            return@withContext ImportResult(
                success,
                message,
                importedFarms,
                duplicateFarms,
                farmsNeedingUpdate,
                invalidFarms
            )
        }

    private suspend fun flagFarmersWithNewPlotInfo(
        siteId: Long,
        farmsNeedingUpdate: List<Farm>,
        farmViewModel: FarmViewModel,
    ) = withContext(Dispatchers.IO) {
        val existingFarms = farmViewModel.getExistingFarms(siteId)
        val existingFarmMap = existingFarms.associateBy { it.remoteId }

        for (farmNeedingUpdate in farmsNeedingUpdate) {
            val existingFarm = existingFarmMap[farmNeedingUpdate.remoteId]

            if (existingFarm != null && existingFarm != farmNeedingUpdate) {
                existingFarm.needsUpdate = true
                farmViewModel.updateFarm(existingFarm)
                println(
                    "Flagging farm for update: ${existingFarm.farmerName}, Site ID: ${existingFarm.siteId}, NeedsUpdate:${existingFarm.needsUpdate}",
                )
                repository.updateFarm(existingFarm)
                println("Farm updated successfully")
            } else if (existingFarm == null) {
                farmNeedingUpdate.needsUpdate = false
                println("New farm detected, flagging for update: ${farmNeedingUpdate.farmerName}, Site ID: ${farmNeedingUpdate.siteId}")
            }
        }
        farmsNeedingUpdate.forEach { farm ->
            farmViewModel.updateFarm(farm)
            println("Updating farm: ${farm.farmerName}, Site ID: ${farm.siteId}, needsUpdate: ${farm.needsUpdate}")
        }
    }

    fun getTemplateContent(fileType: String): String =
        when (fileType) {
            "csv" ->
                """remote_id,          // Optional: can be null if not provided
           farmer_name,        // can not be null 
           member_id,          // Optional: can be null if not provided
           collection_site,   // Optional: can be null if not provided
           agent_name,        // Optional: can be null if not provided
           farm_village,      // can not be null 
           farm_district,     // can not be null 
           farm_size,         // Optional: can be null if not provided; default value could be 0.0
           latitude,          // Optional: can be null if not provided
           longitude,         // Optional: can be null if not provided
           polygon,           // Optional: can be null if not provided; used for Polygon type
           created_at,        // Optional: can be null if not provided; default to current time if not available
           updated_at         // Optional: can be null if not provided; default to current time if not available
"""

            "geojson" ->
                """{
    "type": "FeatureCollection",
    "features": [
        {
            "type": "Feature",
            "properties": {
                "remote_id": "",                    // Optional: can be null if not provided
                "farmer_name": "",                  // can not be null 
                "member_id": "",                   // Optional: can be null if not provided
                "collection_site": "",              // Optional: can be null if not provided
                "agent_name": "",                   // Optional: can be null if not provided
                "farm_village": "",                 // can not be null 
                "farm_district": "",                // can not be null 
                "farm_size": 0.0,                   // Optional: use a default value of 0.0 if not provided
                "latitude": "",                     // Optional: can be null if not provided
                "longitude": "",                    // Optional: can be null if not provided
                "created_at": "",                   // Optional: can be null if not provided
                "updated_at": ""                    // Optional: can be null if not provided
            },
            "geometry": {
                "type": "Point",                    // Use "Polygon" if coordinates are for a polygon
                "coordinates": ["longitude", "latitude"]  // Replace with actual coordinates; can be null if not provided
            }
        },
        
        {
            "type": "Feature",
            "properties": {
                "remote_id": "",                    // Optional: can be null if not provided
                "farmer_name": "",                  // Optional: can be null if not provided
                "member_id": "",                    // Optional: can be null if not provided
                "collection_site": "",              // Optional: can be null if not provided
                "agent_name": "",                   // Optional: can be null if not provided
                "farm_village": "",                 // Optional: can be null if not provided
                "farm_district": "",                // Optional: can be null if not provided
                "farm_size": "farm size is double", // Replace with actual value; can be null if not provided
                "latitude": "latitude value in double", // Replace with actual value; can be null if not provided
                "longitude": "longitude value in double", // Replace with actual value; can be null if not provided
                "created_at": "",                   // Optional: can be null if not provided
                "updated_at": ""                    // Optional: can be null if not provided
            },
            "geometry": {
                "type": "Polygon",                  // Use "Point" if coordinates are for a point
                "coordinates": [[["longitude","latitude"], ["longitude","latitude"],["longitude", "latitude"], ["longitude", "latitude"], ["longitude", "latitude"], ["longitude", "latitude"]]]
                // Replace with actual coordinates; can be null if not provided
            }
        }
    ]
}"""

            else -> throw IllegalArgumentException("Unsupported file type: $fileType")
        }

    // Define the method for saving the file to the URI
    suspend fun saveFileToUri(
        context: Context,
        uri: Uri,
        templateContent: String,
    ) {
        withContext(Dispatchers.IO) {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(templateContent.toByteArray())
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, R.string.template_downloaded, Toast.LENGTH_SHORT).show()
                }
            } ?: withContext(Dispatchers.Main) {
                Toast
                    .makeText(context, R.string.template_download_failed, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    suspend fun getExistingFarms(siteId: Long): List<Farm> =
        withContext(Dispatchers.IO) {
            repository.readAllFarmsSync(siteId)
        }


    suspend fun getAllExistingFarms(): List<Farm> =
        withContext(Dispatchers.IO) {
            repository.getAllFarms()
        }

    suspend fun getAllSites(): List<CollectionSite> =
        withContext(Dispatchers.IO) {
            repository.getAllSites()
        }

    // restore data from the server


    val TAG = "FarmConversion"

    // Regular expression pattern to detect scientific notation
    val scientificNotationPattern = Pattern.compile("^[+-]?\\d*\\.?\\d+([eE][+-]?\\d+)?$")

    // Function to format the float value to avoid scientific notation
    fun formatFloatValue(value: Float, decimalPlaces: Int): Float {
        // Format the float to a string with the specified number of decimal places
        val formattedValue = String.format("%.${decimalPlaces}f", value)

        // Check if the formatted value is in scientific notation and truncate if needed
        val finalValue = if (scientificNotationPattern.matcher(formattedValue).matches()) {
            truncateToDecimalPlaces(formattedValue, decimalPlaces)
        } else {
            formattedValue
        }

        // Convert the final formatted string back to a float and return it
        return finalValue.toFloat()
    }

    // Extension function to convert CollectionSiteRestore to CollectionSite
    fun CollectionSiteRestore.toCollectionSite(): CollectionSite {
        return CollectionSite(
            name = this.name,
            agentName = this.agent_name,
            phoneNumber = this.phone_number,
            email = this.email,
            village = this.village,
            district = this.district,
            createdAt =  Instant.now().millis,
            updatedAt =  Instant.now().millis
        ).apply {
            siteId = this@toCollectionSite.local_cs_id
        }
    }

    @SuppressLint("DefaultLocale")
    fun FarmRestore.toFarm(local_cs_id:Long): Farm {
        android.util.Log.d(TAG, "Starting conversion for FarmRestore id: $id")

        // Convert remote_id to UUID
        val remoteId: UUID = try {
            java.util.UUID.fromString(this.remote_id)
        } catch (e: IllegalArgumentException) {
            android.util.Log.e(TAG, "Invalid UUID format for remote_id: ${this.remote_id} in FarmRestore id: $id", e)
            java.util.UUID.randomUUID() // Fallback or handle appropriately
        }
        android.util.Log.d(TAG, "Converted remoteId: $remoteId")

        // Parse created_at
        val createdAt: Long = try {
            org.joda.time.Instant.now().millis
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error parsing created_at: ${this.created_at} in FarmRestore id: $id", e)
            java.lang.System.currentTimeMillis() // Fallback or handle appropriately
        }
        android.util.Log.d(TAG, "Converted createdAt: $createdAt")

        // Parse updated_at
        val updatedAt: Long = try {
            org.joda.time.Instant.now().millis
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error parsing updated_at: ${this.updated_at} in FarmRestore id: $id", e)
            java.lang.System.currentTimeMillis() // Fallback or handle appropriately
        }
        android.util.Log.d(TAG, "Converted updatedAt: $updatedAt")

        // Convert coordinates
        val coordinatesMapped: List<Pair<Double, Double>>? = try {
            // Assuming this.coordinates is of type List<List<Double>>?
            this.coordinates?.map { coordList ->
                if (coordList.size >= 2) {
                    val first = coordList[0]
                    val second = coordList[1]
                    Pair(first, second)
                } else {
                    android.util.Log.w(TAG, "Invalid coordinate list size: ${coordList.size} for FarmRestore id: $id")
                    Pair(0.0, 0.0) // Adjust default value as needed
                }
            }
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error mapping coordinates for FarmRestore id: $id", e)
            null
        }

        // Format the coordinates into the desired string format
        val formattedCoordinates = coordinatesMapped?.joinToString(prefix = "[", postfix = "]") { "[%.6f, %.6f]".format(it.first, it.second) }

        android.util.Log.d(TAG, "Converted coordinates: $formattedCoordinates")


        val accuracyArray = this.accuracyArray
        android.util.Log.d(TAG, "Converted accuracy Array: $accuracyArray")


        // Convert size from Double to Float
        val sizeFloat: Float = this.size.toFloat()
        val formattedSize = formatFloatValue(sizeFloat, 9)
        android.util.Log.d(TAG, "Converted size: ${formattedSize}")


        // Convert latitude and longitude from Double to String
        val latitudeStr: String = this.latitude.toString()
        val longitudeStr: String = this.longitude.toString()
        android.util.Log.d(TAG, "Converted latitude: $latitudeStr, longitude: $longitudeStr")

        // Handle member_id
        val memberId: String = this.member_id ?: "100"
        android.util.Log.d(TAG, "Handled memberId: '$memberId'")



        // Create Farm object
        val farm = Farm(
            siteId = local_cs_id,
            remoteId = remoteId,
            farmerPhoto = "farmer_photo",
            farmerName = this.farmer_name,
            memberId = memberId,
            village = this.village,
            district = this.district,
            purchases =0.toFloat(),
            size = formattedSize,
            latitude = latitudeStr,
            longitude = longitudeStr,
            coordinates = coordinatesMapped,
            accuracyArray = accuracyArray,
            age = 0,  // New field, default value if null
            gender = "Male",  // New field, default value if null
            govtIdNumber = "0",  // New field, default value if null
            numberOfTrees = 0,  // New field, default value if null
            phone = "" ,  // New field, default value if null
            photo = "",  // New field, default value if null
            synced = false,
            scheduledForSync = false,
            createdAt = createdAt,
            updatedAt = updatedAt,
            needsUpdate = false
        )
        android.util.Log.d(TAG, "Created Farm object: $farm")

        return farm
    }



    // Restore data from the server
    fun restoreData(
        deviceId: String?,
        phoneNumber: String?,
        email: String?,
        farmViewModel: FarmViewModel,
        onCompletion: (Boolean) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _restoreStatus.postValue(RestoreStatus.InProgress)

                // Get local farms and sites from the repository
                val localFarms = farmViewModel.getAllExistingFarms()
                val localSites = farmViewModel.getAllSites()
                val siteIds = localSites.map { site -> site.siteId } // Extract all site IDs


                Log.d(TAG, "Local Farms: $localFarms")
                Log.d(TAG, "Local Sites: $localSites")
                Log.d(TAG, "Site IDs: $siteIds")

                // Prepare the request body
                val farmRequest = FarmRequest(
                    device_id = deviceId.orEmpty(),
                    email = email.orEmpty(),
                    phone_number = phoneNumber.orEmpty()
                )

                // Fetch farms from the server
                val serverFarms = apiService.getFarmsByDeviceId(farmRequest)
                // Initialize Gson
                val gson = Gson()

                // Convert the JSON to the ServerFarmResponse object
                val serverFarmResponseList: List<ServerFarmResponse> = gson.fromJson(
                    gson.toJson(serverFarms),
                    Array<ServerFarmResponse>::class.java
                ).toList()

                // Extract and flatten the list of farms
                val collectionSites: Map<Long, CollectionSiteRestore> =
                    serverFarmResponseList.associateBy(
                        { it.collection_site.local_cs_id },
                        { it.collection_site }
                    )

                // Group farms by their associated collection site (using site_id in FarmRestore to map to CollectionSiteRestore)
                val farmEntities: List<Farm> = serverFarmResponseList.flatMap { serverFarmResponse ->
                    // Extract the local_cs_id from the collection_site
                    val collectionSiteLocalId = serverFarmResponse.collection_site.local_cs_id

                    // Convert each FarmRestore to Farm with the correct collectionSiteLocalId
                    serverFarmResponse.farms.map { farmRestore ->
                        Log.d(TAG, "Converting FarmRestore: $farmRestore for Collection Site Local ID: $collectionSiteLocalId")

                        // Convert FarmRestore to Farm, associating it with the correct local_cs_id
                        val farm = farmRestore.toFarm(collectionSiteLocalId)

                        Log.d(TAG, "Converted Farm: $farm")

                        farm // Return the converted farm
                    }
                }

                Log.d(TAG, "All Converted Farms: $farmEntities")



                // Initialize counters for added and updated farms
                var addedCount = 0
                var createdSiteCount = 0

                // Create missing sites and add/update farms
                farmEntities.forEach { serverFarm ->
                    val siteId = serverFarm.siteId
                    val localSite = localSites.find { it.siteId == siteId }
                    val localFarm = localFarms.find { it.remoteId == serverFarm.remoteId }

                    if (localSite == null) {
                        // Site doesn't exist locally, create it
                        val siteToCreate = collectionSites[siteId]
                        if (siteToCreate != null) {
                            val collectionSite = siteToCreate.toCollectionSite()
                            Log.d(TAG, "Creating new site: $collectionSite")
                            addSite(collectionSite){isAdded ->
                                if (isAdded) {
                                    createdSiteCount++
                                }
                            }
                        }
                    }

                    // Add or update the farm
                    if (localFarm == null) {
                        // Farm doesn't exist locally, add it
                        if (serverFarm.size == 0f || serverFarm.latitude == "0.0" || serverFarm.longitude == "0.0") {
                            serverFarm.needsUpdate = true
                            addFarm(serverFarm, siteId)
                            Log.d(
                                TAG,
                                "Farm needs update due to missing size or coordinates: $serverFarm"
                            )
                            addedCount++
                        } else {
                            // Add the farm as it is complete
                            Log.d(TAG, "Adding new farm: $serverFarm")
                            addFarm(serverFarm, siteId)
                            Log.d(TAG, "New farm added successfully")
                            addedCount++
                        }
                    } else {
                        // Log the farm that already exists
                        Log.d(TAG, "Farm already exists locally: $localFarm")

                        // Optionally update the local farm if the serverFarm has better information
                        if (serverFarm.size != 0f && serverFarm.latitude != "0.0" && serverFarm.longitude != "0.0") {
                            // Check if localFarm needs an update
                            if (localFarm.size == 0f || localFarm.latitude == "0.0" || localFarm.longitude == "0.0") {
                                localFarm.needsUpdate = true
                                farmViewModel.updateFarm(localFarm)
                                Log.d(
                                    TAG,
                                    "Updated local farm due to missing size or coordinates: $localFarm"
                                )
                            }
                        }
                    }
                }

                Log.d(TAG, "Total farms added: $addedCount")
                Log.d(TAG, "Total sites created: $createdSiteCount")

                // Prepare a success message
                val message = "Restoration completed: $addedCount farms added, $createdSiteCount sites created."

                // Refresh the farms LiveData
                _farms.postValue(repository.getAllFarms())

                // Post the completed status with the message
                _restoreStatus.postValue(
                    RestoreStatus.Success(
                        addedCount = addedCount,
                        sitesCreated = createdSiteCount,
                        message = message
                    )
                )

                // Notify completion with the success status
                onCompletion(addedCount > 0 || createdSiteCount > 0)
            } catch (e: Exception) {
                // Post the error status with the exception message
                _restoreStatus.postValue(RestoreStatus.Error("Failed to restore data: ${e.message}"))

                // Notify completion with failure status
                onCompletion(false)
            }
        }
    }


}

class FarmViewModelFactory(
    private val application: Application,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(FarmViewModel::class.java)) {
            return FarmViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
