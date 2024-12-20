package com.example.cafetrac.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import org.technoserve.cafetrac.database.models.Farm
import kotlinx.coroutines.flow.Flow
import org.technoserve.cafetrac.database.models.BuyThroughAkrabi
import org.technoserve.cafetrac.database.models.CollectionSite
import org.technoserve.cafetrac.database.models.DirectBuy
import java.util.UUID

@Dao
interface FarmDAO {

    @Transaction
    @Query("SELECT * FROM Farms WHERE siteId = :siteId ORDER BY createdAt DESC")
    fun getAll(siteId: Long): LiveData<List<Farm>>

    @Transaction
    @Query("SELECT * FROM Farms WHERE siteId = :siteId ORDER BY createdAt DESC")
    fun getAllSync(siteId: Long): List<Farm>

    @Transaction
    @Query("SELECT * FROM Farms ORDER BY createdAt DESC")
    fun getAllFarms(): List<Farm>

    @Transaction
    @Query("SELECT * FROM CollectionSites ORDER BY createdAt DESC")
    fun getAllSites(): List<CollectionSite>

    @Transaction
    @Query("SELECT * FROM CollectionSites ORDER BY createdAt DESC")
    fun getSites(): LiveData<List<CollectionSite>>

    @Transaction
    @Query("SELECT * FROM Farms ORDER BY createdAt DESC")
    fun getData(): LiveData<List<Farm>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(farm: Farm)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(farms: List<Farm>)


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSite(site: CollectionSite) : Long

    @Update
    fun updateSite(site: CollectionSite)

    @Transaction
    @Query("SELECT * FROM Farms WHERE id = :id ORDER BY id DESC")
    fun getFarmById(id: Long): LiveData<List<Farm>>

    @Transaction
    @Query("SELECT * FROM Farms WHERE id = :id ORDER BY id DESC")
    fun getRawFarmById(id: Long): List<Farm>

    @Transaction
    @Query("SELECT * FROM Farms ORDER BY id DESC LIMIT 1")
    fun getLastFarm(): LiveData<List<Farm>>

    @Update
    fun update(farm: Farm)

    @Delete
    suspend fun delete(farm: Farm)

    @Query("DELETE FROM Farms")
    fun deleteAll()

    @Query("UPDATE Farms SET synced=1 WHERE id = :id")
    fun updateSyncStatus(id: Long)

    @Query("SELECT * FROM CollectionSites WHERE siteId = :siteId LIMIT 1")
    fun getCollectionSiteById(siteId: Long): CollectionSite?


    @Query("UPDATE farms SET synced = :synced WHERE remote_id = :remoteId")
    suspend fun updateFarmSyncStatus(remoteId: UUID, synced: Boolean)

    @Query("UPDATE Farms SET scheduledForSync=1 WHERE id IN (:ids)")
    fun updateSyncListStatus(ids: List<Long>)

    @Query("DELETE FROM Farms WHERE id IN (:ids)")
    fun deleteList(ids: List<Long>)

    @Query("DELETE FROM CollectionSites WHERE siteId IN (:ids)")
    fun deleteListSite(ids: List<Long>)

    @Query("SELECT * FROM Farms WHERE synced = 0")
    suspend fun getUnsyncedFarms(): List<Farm>

    @Query("SELECT * FROM Farms WHERE remote_id=:remoteId LIMIT 1")
    suspend fun getFarmByRemoteId(remoteId: UUID): Farm?

    @Query("SELECT * FROM Farms WHERE  siteId = :siteId LIMIT 1")
    suspend fun getFarmBySiteId(siteId: Long): Farm?

    @Query("DELETE FROM Farms WHERE id = :id")
    suspend fun deleteFarmById(id: Long)

    @Query("DELETE FROM Farms WHERE remote_id = :remoteId")
    suspend fun deleteFarmByRemoteId(remoteId: UUID)

    @Query("SELECT * FROM farms WHERE remote_id = :remoteId OR (farmerName = :farmerName AND village = :village AND district = :district) LIMIT 1")
    suspend fun getFarmByDetails(remoteId: UUID, farmerName: String, village: String, district: String): Farm?

    @Transaction
    suspend fun insertAllIfNotExists(farms: List<Farm>) {
        farms.forEach { farm ->
            if (farm.remoteId?.let { getFarmByRemoteId(it) } == null) {
                insertAll(listOf(farm))
            }
        }
    }
    @Query("SELECT * FROM BuyThroughAkrabi ORDER BY date DESC, time DESC")
    fun getAllBoughtItems(): Flow<List<BuyThroughAkrabi>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(buyThroughAkrabi: BuyThroughAkrabi)

    @Query("SELECT * FROM BuyThroughAkrabi WHERE id = :id")
    fun getBoughtItemById(id: Long): Flow<BuyThroughAkrabi>

    @Query("SELECT * FROM BuyThroughAkrabi WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getBoughtItemsByDateRange(startDate: String, endDate: String): List<BuyThroughAkrabi>

    @Query("SELECT * FROM DirectBuy ORDER BY date DESC, time DESC")
    fun getAllBoughtItemsDirectBuy(): Flow<List<DirectBuy>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDirectBuy(directBuy: DirectBuy)

    @Update
    suspend fun updateDirectBuy(directBuy: DirectBuy)

    @Delete
    suspend fun deleteDirectBuy(directBuy: DirectBuy)

    @Update
    suspend fun updateBuyThroughAkrabi(akrabi: BuyThroughAkrabi)

    @Delete
    suspend fun deleteBuyThroughAkrabi(akrabi: BuyThroughAkrabi)


    @Query("SELECT * FROM DirectBuy WHERE id = :id")
    fun getDirectBuyById(id: Long): Flow<DirectBuy>

    @Query("SELECT * FROM DirectBuy WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getDirectBuysByDateRange(startDate: String, endDate: String): List<DirectBuy>

    // Query to get the total number of farms for a site
    @Query("SELECT COUNT(*) FROM Farms WHERE siteId = :siteId")
    fun getTotalFarmsForSite(siteId: Long): LiveData<Int>

    // Query to get the number of farms with incomplete data for a site
    @Query("SELECT COUNT(*) FROM Farms WHERE siteId = :siteId AND needsUpdate = 1")
    fun getFarmsWithIncompleteDataForSite(siteId: Long): LiveData<Int>

    @Query("SELECT * FROM Farms WHERE siteId IN (:selectedIds)")
    fun getFarmsBySelectedIds(selectedIds: List<Long>): LiveData<List<Farm>>

    @Query("SELECT * FROM CollectionSites LIMIT :limit OFFSET :offset")
    fun getCollectionSites(offset: Int, limit: Int): List<CollectionSite>

    @Query("SELECT * FROM CollectionSites WHERE siteId = :localCsId OR (name = :siteName AND village = :village AND district = :district) LIMIT 1")
    suspend fun getSiteByDetails(localCsId: Long, siteName: String, village: String, district: String): CollectionSite?


}