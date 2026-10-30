package com.oviscan.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "measurements",
    foreignKeys = [
        ForeignKey(
            entity = SheepEntity::class,
            parentColumns = ["earTag"],
            childColumns = ["sheepEarTag"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["sheepEarTag"]),
        Index(value = ["timestampMillis"])
    ]
)
data class MeasurementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sheepEarTag: String,
    val timestampMillis: Long = System.currentTimeMillis(),
    val daysSinceInsemination: Int,
    val measurementMode: String,
    val probability: Float,
    val riskLevel: String, // HIGH, INCONCLUSIVE, LOW
    val confidence: Float,
    val audioScore: Float,       // Dahili mikrofon skoru
    val motionScore: Float,      // Dahili ivmeölçer skoru
    val cameraScore: Float? = null, // Dahili kamera optik solunum/kontur skoru
    val xaiExplanations: String, // Newline separated bullet points
    val recommendedAction: String,
    
    // Veterinary Ultrasound / PAG Reference Validation
    val isVetVerified: Boolean? = null, // null=pending, true=confirmed pregnant, false=confirmed open/empty
    val vetDiagnosisDateMillis: Long? = null,
    val vetNotes: String = ""
)
