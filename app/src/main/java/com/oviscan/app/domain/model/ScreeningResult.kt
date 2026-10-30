package com.oviscan.app.domain.model

enum class MeasurementMode(val labelTr: String) {
    SPOT_CHECK("Elde Hızlı Tarama (30-60 sn)"),
    WEARABLE_COLLAR("Tasma / Koşum Kipi (15-30 dk)")
}

data class ScreeningResult(
    val probability: Float,
    val riskLevel: PregnancyRiskLevel,
    val confidence: Float,
    val audioScore: Float,       // From built-in microphone
    val motionScore: Float,      // From built-in accelerometer
    val cameraScore: Float?,     // From built-in standard camera
    val morphologyScore: Float?,
    val xaiReasons: List<String>,
    val measurementDurationSec: Int,
    val mode: MeasurementMode,
    val timestamp: Long = System.currentTimeMillis()
)
