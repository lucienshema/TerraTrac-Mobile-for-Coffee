package org.technoserve.cafetrac.repositories

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import org.technoserve.cafetrac.database.models.Farm
import com.example.cafetrac.database.dao.FarmDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.technoserve.cafetrac.database.models.BuyThroughAkrabi
import org.technoserve.cafetrac.database.models.CollectionSite
import org.technoserve.cafetrac.database.models.DirectBuy

class FarmRepository(private val farmDAO: FarmDAO) {

    val readAllSites: LiveData<List<CollectionSite>> = farmDAO.getSites()
    val readData: LiveData<List<Farm>> = farmDAO.getData()
    fun readAllFarms(siteId: Long): LiveData<List<Farm>> {
        return farmDAO.getAll(siteId)
    }

    fun getAllFarms(): List<Farm> {
        return farmDAO.getAllFarms()
    }

    fun getAllSites(): List<CollectionSite>{
        return farmDAO.getAllSites()
    }

    fun readAllFarmsSync(siteId: Long): List<Farm> {
        return farmDAO.getAllSync(siteId)
    }

    fun readFarm(farmId: Long): LiveData<List<Farm>> {
        return farmDAO.getFarmById(farmId)
    }

    suspend fun addFarm(farm: Farm) {
        val existingFarm = isFarmDuplicate(farm)
        if (existingFarm == null) {
            farmDAO.insert(farm)
        } else {
            // If the farm exists and needs an update, perform the update
            if (farmNeedsUpdate(existingFarm, farm)) {
                farmDAO.update(farm)
            }
        }
    }

    private suspend fun addFarms(farms: List<Farm>) {
        farmDAO.insertAllIfNotExists(farms)
    }

    suspend fun isSiteDuplicate(collectionSite: CollectionSite): CollectionSite? {
        return farmDAO.getSiteByDetails(
            collectionSite.siteId,
            collectionSite.district,
            collectionSite.name,
            collectionSite.village
        )
    }

    suspend fun addSite(site: CollectionSite) : Boolean {
        // Check if the site already exists
        val existingSite = isSiteDuplicate(site)

        if (existingSite == null) {
            Log.d(TAG, "Attempting to insert new site: $site")
            val insertResult = farmDAO.insertSite(site)
            Log.d(TAG, "Insert operation result: $insertResult")
            if (insertResult != -1L) {
                Log.d(TAG, "New site inserted: $site")
                return true
            } else {
                Log.d(TAG, "Insertion was ignored (likely due to conflict strategy)")
                return false
            }
        } else {
            Log.d(TAG, "Site already exists: $existingSite")
            return false
        }
    }

    fun getLastFarm(): LiveData<List<Farm>> {
        return farmDAO.getLastFarm()
    }

    suspend fun getFarmBySiteId(siteId: Long): Farm? {
        return farmDAO.getFarmBySiteId(siteId)
    }


    suspend fun updateFarm(farm: Farm) {
        farmDAO.update(farm)
    }

    private suspend fun updateFarms(farms: List<Farm>) {
        farms.forEach { updateFarm(it) }
    }

    suspend fun updateSite(site: CollectionSite) {
        farmDAO.updateSite(site)
    }


    suspend fun deleteFarm(farm: Farm) {
        farmDAO.delete(farm)
    }

    suspend fun deleteFarmById(farm: Farm) {
        farmDAO.deleteFarmByRemoteId(farm.remoteId)
    }
    suspend fun deleteById(farmId: Long) {
        farmDAO.deleteFarmById(farmId)
    }


    suspend fun deleteAllFarms() {
        farmDAO.deleteAll()
    }

    suspend fun updateSyncStatus(id: Long) {
        farmDAO.updateSyncStatus(id)
    }

    suspend fun updateSyncListStatus(ids: List<Long>) {
        farmDAO.updateSyncListStatus(ids)
    }

    suspend fun deleteList(ids: List<Long>) {
        farmDAO.deleteList(ids)
    }

    suspend fun deleteListSite(ids: List<Long>) {
        farmDAO.deleteListSite(ids)
    }

    // Function to get the total number of farms for a site
    fun getTotalFarmsForSite(siteId: Long): LiveData<Int> {
        return farmDAO.getTotalFarmsForSite(siteId)
    }

    // Function to get the number of farms with incomplete data for a site
    fun getFarmsWithIncompleteDataForSite(siteId: Long): LiveData<Int> {
        return farmDAO.getFarmsWithIncompleteDataForSite(siteId)
    }

    fun getFarmsBySelectedIds(selectedIds: List<Long>): LiveData<List<Farm>> {
        return farmDAO.getFarmsBySelectedIds(selectedIds)
    }


    suspend fun getCollectionSites(page: Int, pageSize: Int): List<CollectionSite> {
        val offset = (page - 1) * pageSize
        return withContext(Dispatchers.IO) {
            farmDAO.getCollectionSites(offset, pageSize)
        }
    }

    suspend fun isFarmDuplicateBoolean(farm: Farm): Boolean {
        return farmDAO.getFarmByDetails(
            farm.remoteId,
            farm.farmerName,
            farm.village,
            farm.district
        ) != null
    }

    suspend fun isFarmDuplicate(farm: Farm): Farm? {
        return farmDAO.getFarmByDetails(
            farm.remoteId,
            farm.farmerName,
            farm.village,
            farm.district
        )
    }

    // Function to fetch a farm by remote ID, farmer name, and address
    suspend fun getFarmByDetails(farm: Farm): Farm? {
        return farmDAO.getFarmByDetails(
            farm.remoteId,
            farm.farmerName,
            farm.village,
            farm.district
        )
    }


    fun farmNeedsUpdate(existingFarm: Farm, newFarm: Farm): Boolean {
        return existingFarm.farmerName != newFarm.farmerName ||
                existingFarm.size != newFarm.size ||
                existingFarm.village != newFarm.village ||
                existingFarm.district != newFarm.district
    }

    fun isDuplicateFarm(existingFarm: Farm, newFarm: Farm): Boolean {
        return existingFarm.farmerName == newFarm.farmerName &&
                existingFarm.size == newFarm.size &&
                existingFarm.village == newFarm.village &&
                existingFarm.district == newFarm.district
    }


    fun farmNeedsUpdateImport(newFarm: Farm): Boolean {
        return newFarm.farmerName.isEmpty() ||
                newFarm.district.isEmpty() ||
                newFarm.village.isEmpty() ||
                newFarm.latitude == "0.0" ||
                newFarm.longitude == "0.0" ||
                newFarm.size == 0.0f ||
                newFarm.remoteId.toString().isEmpty() ||
                newFarm.coordinates.isNullOrEmpty()
    }


    fun getAllBoughtItems(): Flow<List<BuyThroughAkrabi>> = farmDAO.getAllBoughtItems()

    fun getAllBoughtItemsDirectBuy(): Flow<List<DirectBuy>> = farmDAO.getAllBoughtItemsDirectBuy()

    suspend fun insert(buyThroughAkrabi: BuyThroughAkrabi) {
        farmDAO.insert(buyThroughAkrabi)
    }

    fun getBoughtItemById(id: Long): Flow<BuyThroughAkrabi?> = farmDAO.getBoughtItemById(id)

    // Function to get bought items by date range
    suspend fun getBoughtItemsByDateRange(startDate: String, endDate: String): List<BuyThroughAkrabi> {
        return farmDAO.getBoughtItemsByDateRange(startDate, endDate)
    }

    suspend fun insertDirectBuy(directBuy: DirectBuy) {
        farmDAO.insertDirectBuy(directBuy)
    }

    suspend fun updateDirectBuy(directBuy: DirectBuy) {
       farmDAO.updateDirectBuy(directBuy)
    }

    suspend fun deleteDirectBuy(directBuy: DirectBuy) {
        farmDAO.deleteDirectBuy(directBuy)
    }

    suspend fun updateBuyThroughAkrabi(akrabi: BuyThroughAkrabi) {
        farmDAO.updateBuyThroughAkrabi(akrabi)
    }

    suspend fun deleteBuyThroughAkrabi(akrabi: BuyThroughAkrabi) {
        farmDAO.deleteBuyThroughAkrabi(akrabi)
    }

    fun getDirectBuyById(id: Long): Flow<DirectBuy?> = farmDAO.getDirectBuyById(id)

    suspend fun getDirectBuysByDateRange(startDate: String, endDate: String): List<DirectBuy> {
        return farmDAO.getDirectBuysByDateRange(startDate, endDate)
    }
}



