package com.oviscan.app.domain.model

data class SensorDataBatch(
    val earTag: String,
    val daysSinceInsemination: Int,
    val breed: String,
    val ageYears: Int,
    val mode: MeasurementMode,
    
    // 1. Built-in Microphone Features (AudioRecord 44.1 kHz Mono)
    val audioDurationSeconds: Float,
    val audioAverageRms: Float,
    val audioPeakRms: Float,
    val audioSnrDb: Float,
    val acousticEventCount: Int,
    val mfccCoefficients: FloatArray,
    val estimatedPitchHz: Float,
    val harmonicToNoiseRatio: Float,

    // 2. Built-in Accelerometer Features (SensorManager 50 Hz)
    val motionDurationSeconds: Float,
    val motionAverageMagnitude: Float,
    val motionStdDev: Float,
    val motionActivityIndex: Float,
    val motionEntropy: Float,
    val lyingRatio: Float,

    // 3. Built-in Camera Features (Standard RGB CameraX - Optical Flank Motion & Morphometrics)
    val cameraRespirationBpm: Float? = null,
    val cameraFlankExpansionRatio: Float? = null
)
