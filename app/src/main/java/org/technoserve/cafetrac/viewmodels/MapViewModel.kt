
package org.technoserve.cafetrac.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cafetrac.database.models.MapState
import com.example.cafetrac.database.models.ZoneClusterItem
import com.example.cafetrac.database.helpers.map.ZoneClusterManager
import org.technoserve.cafetrac.utils.map.calculateCameraViewPoints
import org.technoserve.cafetrac.utils.map.getCenterOfPolygon
import com.example.cafetrac.utils.GeoCalculator
import org.technoserve.cafetrac.utils.convertSize
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.MapType
import com.google.maps.android.ktx.model.polygonOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor() : ViewModel() {
    val state: MutableState<MapState> = mutableStateOf(
        MapState(
            lastKnownLocation = null,
            markers = mutableListOf(),
            clusterItems = emptyList(),
            clearMap = false,
            mapType = MapType.NORMAL,
            onMapTypeChange = {}
        )
    )

    // Properties for coordinates and areas
    private val _coordinates = MutableLiveData<List<Pair<Double, Double>>>()
    val coordinates: LiveData<List<Pair<Double, Double>>>
    get() = _coordinates

    private val _calculatedArea = MutableLiveData<Double>()
    val calculatedArea: LiveData<Double>
    get() = _calculatedArea

    private val _size = MutableStateFlow("")
    val size: StateFlow<String> = _size.asStateFlow()

    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()

    // Property to store user's choice (0 for calculated, 1 for entered)
    private val _userChoice = MutableLiveData<Int>()
    val userChoice: LiveData<Int>
    get() = _userChoice

    // Method to set coordinates and calculated area
    fun calculateArea(coordinates: List<Pair<Double, Double>>?) : Double {
        _coordinates.value = coordinates
        val area = GeoCalculator.calculateArea(coordinates)
        _calculatedArea.value = area
        return area
    }

    // Method to set entered area
    fun setSize(size: String) {
        _size.value = size
    }

    // Method to update the size
    fun updateSize(newSize: String) {
        _size.value = newSize
    }

    // Method to handle user's choice
    fun setUserChoice(choice: Int) {
        _userChoice.value = choice
    }

    // Method to show dialog for choosing area
    fun showAreaDialog(calculatedArea: String, enteredArea: String) {
        val areaNum = enteredArea.toDoubleOrNull()
        if (areaNum != null) {
            _calculatedArea.value = calculatedArea.toDoubleOrNull()
            _size.value = enteredArea
            _showDialog.value = true
        } else {
            _size.value = "Invalid input."
        }
    }

    fun dismissDialog() {
        _showDialog.value = false
    }

    fun updateSizeWithChoice(choice: String) {
        _size.value = choice
        _showDialog.value = false
    }
    // Method to retrieve the size input
    fun getSizeInput(): Double? {
        return _size.value.toDoubleOrNull()
    }

    // Save the Calculate Area if the entered Size is greater than 4 otherwise keep the entered size Value
    fun saveSize(selectedUnit: String, coordinatesData: List<Pair<Double, Double>>?): Number {
        val currentSize = size.value.toFloatOrNull() ?: 0.0f
        val finalSize = if (currentSize < 4f) {
            convertSize(currentSize.toDouble(), selectedUnit)
        } else {
            calculateArea(coordinatesData)
        }
        updateSize(finalSize.toString())
        return finalSize
    }



    @SuppressLint("MissingPermission")
    fun getDeviceLocation(
        fusedLocationProviderClient: FusedLocationProviderClient
    ) {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    state.value = state.value.copy(
                        lastKnownLocation = task.result,
                    )
                }
            }
        } catch (e: SecurityException) {
            // Show error or something
        }
    }

    fun setupClusterManager(
        context: Context,
        map: GoogleMap,
    ): ZoneClusterManager {
        val clusterManager = ZoneClusterManager(context, map)
        clusterManager.addItems(state.value.clusterItems)
        return clusterManager
    }

    fun calculateZoneLatLngBounds(): LatLngBounds {
        // Get all the points from all the polygons and calculate the camera view that will show them all.
        try {
            val latLongs = state.value.clusterItems.map { it.polygonOptions }
                .map { it.points.map { LatLng(it.latitude, it.longitude) } }.flatten()
            return latLongs.calculateCameraViewPoints().getCenterOfPolygon()
        } catch (e: IllegalStateException) {
            println("Cannot calculate the view coordinates of nothing. : ${e.message}")
        }
        return LatLngBounds(LatLng(0.0, 0.0), LatLng(0.0, 0.0))
    }

    companion object {
        private val POLYGON_FILL_COLOR = Color.parseColor("#ABF44336")
    }

    fun addCoordinate(latitude: Double, longitude: Double) {
        val currentClusterItems = state.value.clusterItems.toMutableList()
        val lastItemId =
            currentClusterItems.lastOrNull()?.id?.substringAfter("-")?.toIntOrNull() ?: 0
        val newClusterItem = ZoneClusterItem(
            id = "zone-$lastItemId",
            title = "Coords:",
            snippet = "($latitude, $longitude)",
            polygonOptions = polygonOptions { add(LatLng(latitude, longitude)) }
        )
        currentClusterItems.add(newClusterItem)
        state.value = state.value.copy(clusterItems = currentClusterItems)
    }

    fun addCoordinates(coordinates: List<Pair<Double?, Double?>>) {
        // Add coordinates on the map, this list of of LatLong form a polygons
        if (coordinates.isEmpty()) {
            return  // Return early without performing any further actions
        }
        val currentClusterItems = state.value.clusterItems.toMutableList()
        val lastItemId =
            currentClusterItems.lastOrNull()?.id?.substringAfter("-")?.toIntOrNull() ?: 0

        val polygonOptions = polygonOptions {
            coordinates.forEach { (latitude, longitude) ->
                if (latitude!= null && longitude!= null)
                    add(LatLng(latitude, longitude))
            }
            fillColor(POLYGON_FILL_COLOR)
        }
        // This detail displays when user hit on the polygon
        val newClusterItem = ZoneClusterItem(
            id = "zone-${lastItemId + 1}",
            title = "Central Point",
            snippet = "(Lat: ${coordinates[0].first}, Long: ${coordinates[0].second})",
            polygonOptions = polygonOptions
        )

        currentClusterItems.add(newClusterItem)
        state.value = state.value.copy(clusterItems = currentClusterItems)
    }

    fun addMarker(coordinate: Pair<Double, Double>) {
        // Add marker or point on google map
        val currentMarkers = state.value.markers?.toMutableList()
            ?: mutableListOf() // If markers list is null, initialize it with a new mutable list
        currentMarkers.let {
            it.add(coordinate)
            state.value = state.value.copy(markers = it)
        }

        addCoordinate(coordinate.first, coordinate.second)
    }

    fun clearCoordinates() {
        // Clear everything on google map (poly-lines, polygons, markers,etc)
        try {
            state.value =
                state.value.copy(clusterItems = emptyList(), markers = emptyList(), clearMap = true)
        } catch (e: IllegalStateException) {
            println("Can't : ${e.message}")
        }
    }

    fun removeLastCoordinate() {
        // Remove the last added marker on the map
        if (state.value.markers?.isNotEmpty() == true) {
            val currentMarker = state.value.markers?.dropLast(1)
            state.value = state.value.copy(markers = currentMarker)
        }
    }

    fun onMapTypeChange(mapType: MapType) {
        state.value = state.value.copy(mapType = mapType)
    }
}