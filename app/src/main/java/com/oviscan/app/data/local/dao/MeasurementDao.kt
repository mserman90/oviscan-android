package com.oviscan.app.data.local.dao

import androidx.room.*
import com.oviscan.app.data.local.entity.MeasurementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MeasurementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeasurement(measurement: MeasurementEntity): Long

    @Query("SELECT * FROM measurements ORDER BY timestampMillis DESC")
    fun getAllMeasurements(): Flow<List<MeasurementEntity>>

    @Query("SELECT * FROM measurements WHERE sheepEarTag = :earTag ORDER BY timestampMillis DESC")
    fun getMeasurementsForSheep(earTag: String): Flow<List<MeasurementEntity>>

    @Query("SELECT * FROM measurements ORDER BY timestampMillis DESC LIMIT 1")
    fun getLatestMeasurement(): Flow<MeasurementEntity?>

    @Query("SELECT * FROM measurements WHERE id = :id LIMIT 1")
    suspend fun getMeasurementById(id: Long): MeasurementEntity?

    @Query("UPDATE measurements SET isVetVerified = :verified, vetDiagnosisDateMillis = :dateMillis, vetNotes = :notes WHERE id = :measurementId")
    suspend fun updateVetVerification(measurementId: Long, verified: Boolean, dateMillis: Long, notes: String)

    @Query("SELECT COUNT(*) FROM measurements WHERE riskLevel = 'HIGH'")
    fun getHighRiskCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM measurements WHERE riskLevel = 'INCONCLUSIVE'")
    fun getInconclusiveCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM measurements WHERE riskLevel = 'LOW'")
    fun getLowRiskCount(): Flow<Int>
}
