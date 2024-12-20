
package com.example.cafetrac.database.helpers.map

import android.content.Context
import com.example.cafetrac.database.models.ZoneClusterItem
import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.collections.MarkerManager


class ZoneClusterManager(
    context: Context,
    googleMap: GoogleMap,
): ClusterManager<ZoneClusterItem>(context, googleMap, MarkerManager(googleMap))