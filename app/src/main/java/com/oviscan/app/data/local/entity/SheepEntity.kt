package com.oviscan.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sheep")
data class SheepEntity(
    @PrimaryKey
    val earTag: String, // RFID or official ear tag (e.g. TR06-123456)
    val breed: String,  // e.g. "Akkaraman", "Morkaraman", "Merinos", "İvesi"
    val ageYears: Int = 2,
    val inseminationDateMillis: Long, // Tohumlama tarihi
    val notes: String = "",
    val createdAtMillis: Long = System.currentTimeMillis()
) {
    fun getDaysSinceInsemination(currentTimeMillis: Long = System.currentTimeMillis()): Int {
        val diffMillis = currentTimeMillis - inseminationDateMillis
        return (diffMillis / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0)
    }
}
