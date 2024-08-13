package com.example.egnss4coffeev2.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update



@Dao
interface AkrabiDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAkrabi(akrabi: Akrabi)

    @Update
    suspend fun updateAkrabi(akrabi: Akrabi)

    @Delete
    suspend fun deleteAkrabi(akrabi: Akrabi)

    @Query("SELECT * FROM Akrabis")
    fun getAllAkrabis(): LiveData<List<Akrabi>>

    @Query("SELECT * FROM Akrabis WHERE id = :id")
    fun getAkrabiById(id: Long): LiveData<Akrabi?>
}

