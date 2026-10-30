package com.oviscan.app.data.local.repository

import android.content.Context
import com.oviscan.app.data.local.dao.MeasurementDao
import com.oviscan.app.data.local.dao.SheepDao
import com.oviscan.app.data.local.entity.MeasurementEntity
import com.oviscan.app.data.local.entity.SheepEntity
import com.oviscan.app.domain.model.ScreeningResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class ScreeningRepository(
    private val sheepDao: SheepDao,
    private val measurementDao: MeasurementDao,
    private val context: Context
) {

    val allSheep: Flow<List<SheepEntity>> = sheepDao.getAllSheep()
    val allMeasurements: Flow<List<MeasurementEntity>> = measurementDao.getAllMeasurements()
    val highRiskCount: Flow<Int> = measurementDao.getHighRiskCount()
    val inconclusiveCount: Flow<Int> = measurementDao.getInconclusiveCount()
    val lowRiskCount: Flow<Int> = measurementDao.getLowRiskCount()

    suspend fun getSheepByEarTag(earTag: String): SheepEntity? = withContext(Dispatchers.IO) {
        sheepDao.getSheepByEarTag(earTag)
    }

    suspend fun insertSheep(sheep: SheepEntity) = withContext(Dispatchers.IO) {
        sheepDao.insertSheep(sheep)
    }

    suspend fun saveScreeningResult(
        earTag: String,
        result: ScreeningResult,
        daysSinceInsemination: Int
    ): Long = withContext(Dispatchers.IO) {
        val entity = MeasurementEntity(
            sheepEarTag = earTag,
            timestampMillis = result.timestamp,
            daysSinceInsemination = daysSinceInsemination,
            measurementMode = result.mode.name,
            probability = result.probability,
            riskLevel = result.riskLevel.name,
            confidence = result.confidence,
            audioScore = result.audioScore,
            motionScore = result.motionScore,
            cameraScore = result.cameraScore,
            xaiExplanations = result.xaiReasons.joinToString("\n"),
            recommendedAction = result.riskLevel.recommendedActionTr
        )
        measurementDao.insertMeasurement(entity)
    }

    suspend fun updateVetVerification(
        measurementId: Long,
        isPregnant: Boolean,
        notes: String
    ) = withContext(Dispatchers.IO) {
        measurementDao.updateVetVerification(
            measurementId = measurementId,
            verified = isPregnant,
            dateMillis = System.currentTimeMillis(),
            notes = notes
        )
    }

    suspend fun exportHerdReportToCsv(measurements: List<MeasurementEntity>): File = withContext(Dispatchers.IO) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.getDefault())
        val fileName = "OviScan_Suru_Raporu_${dateFormat.format(Date())}.csv"
        val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val file = File(exportDir, fileName)

        FileWriter(file).use { writer ->
            writer.append("Ölçüm ID;Küpe No;Tarih;Tohumlama Sonrası Gün;Ölçüm Kipi;Gebe Olasılığı (%);Risk Seviyesi;Güven Skoru (%);Dahili Ses Skoru;Dahili Hareket Skoru;Dahili Kamera Skoru;Veteriner Doğrulaması;Veteriner Notu\n")
            
            val rowDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            for (m in measurements) {
                val vetStatus = when (m.isVetVerified) {
                    true -> "Doğrulandı: Gebe"
                    false -> "Doğrulandı: Boş"
                    null -> "Beklemede / Doğrulanmadı"
                }
                writer.append("${m.id};")
                writer.append("${m.sheepEarTag};")
                writer.append("${rowDateFormat.format(Date(m.timestampMillis))};")
                writer.append("${m.daysSinceInsemination};")
                writer.append("${m.measurementMode};")
                writer.append("%.1f;".format(Locale.US, m.probability * 100))
                writer.append("${m.riskLevel};")
                writer.append("%.1f;".format(Locale.US, m.confidence * 100))
                writer.append("%.2f;".format(Locale.US, m.audioScore))
                writer.append("%.2f;".format(Locale.US, m.motionScore))
                writer.append("${m.cameraScore?.let { "%.2f".format(Locale.US, it) } ?: "N/A"};")
                writer.append("$vetStatus;")
                writer.append("\"${m.vetNotes.replace("\"", "\"\"")}\"\n")
            }
        }
        file
    }
}
