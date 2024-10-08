package com.example.egnss4coffeev2.database

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class FarmRepository(private val farmDAO: FarmDAO) {

    val readAllSites: LiveData<List<CollectionSite>> = farmDAO.getSites()
    val readData: LiveData<List<Farm>> = farmDAO.getData()
    fun readAllFarms(siteId: Long): LiveData<List<Farm>> {
        return farmDAO.getAll(siteId)
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

    suspend fun addSite(site: CollectionSite) {
        farmDAO.insertSite(site)
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

//    suspend fun isFarmDuplicateBoolean(farm: Farm): Boolean {
//        return farm.remoteId?.let { farmDAO.getFarmByRemoteId(it) } != null
//    }
//
//    suspend fun isFarmDuplicate(farm: Farm): Farm? {
//        return farm.remoteId?.let { farmDAO.getFarmByRemoteId(it) }
//    }
//
//    // Function to fetch a farm by remote ID
//    suspend fun getFarmByRemoteId(remoteId: UUID): Farm? {
//        return farmDAO.getFarmByRemoteId(remoteId)
//    }

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


//    suspend fun deleteFarmByRemoteId(remoteId: UUID) {
//        farmDAO.deleteFarmByRemoteId(remoteId)
//    }
//


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



