package com.oviscan.app.data.local.dao

import androidx.room.*
import com.oviscan.app.data.local.entity.SheepEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SheepDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSheep(sheep: SheepEntity)

    @Update
    suspend fun updateSheep(sheep: SheepEntity)

    @Delete
    suspend fun deleteSheep(sheep: SheepEntity)

    @Query("SELECT * FROM sheep ORDER BY inseminationDateMillis DESC")
    fun getAllSheep(): Flow<List<SheepEntity>>

    @Query("SELECT * FROM sheep WHERE earTag = :earTag LIMIT 1")
    suspend fun getSheepByEarTag(earTag: String): SheepEntity?

    @Query("SELECT COUNT(*) FROM sheep")
    fun getSheepCount(): Flow<Int>
}
