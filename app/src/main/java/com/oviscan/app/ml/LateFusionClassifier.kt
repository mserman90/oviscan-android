package com.oviscan.app.ml

import com.oviscan.app.domain.model.MeasurementMode
import com.oviscan.app.domain.model.PregnancyRiskLevel
import com.oviscan.app.domain.model.ScreeningResult
import com.oviscan.app.domain.model.SensorDataBatch

object LateFusionClassifier {

    /**
     * Executes late fusion using ONLY standard built-in Android smartphone sensors:
     * - Built-in Microphone (AudioRecord 44.1 kHz)
     * - Built-in Accelerometer (SensorManager 50 Hz)
     * - Built-in Standard Camera (CameraX Optical Flank Motion)
     * - Insemination Calendar Prior (17-day estrus non-return logic)
     */
    fun classify(batch: SensorDataBatch): ScreeningResult {
        val xaiList = mutableListOf<String>()

        // 1. Built-in Acoustic Feature Analysis (AudioRecord 44.1 kHz)
        // Checks rumination rhythms, normal breathing acoustics vs agitated estrus bleating.
        val audioPregnancyScore: Float
        if (batch.acousticEventCount > 6 && batch.estimatedPitchHz > 400f) {
            audioPregnancyScore = 0.22f
            xaiList.add("Dahili Mikrofon: Sık ve tiz meleme tespit edildi (östrus/kızgınlık huzursuzluğu şüphesi, gebe olma olasılığını düşürüyor).")
        } else if (batch.acousticEventCount in 1..4 && batch.audioSnrDb > 10f) {
            audioPregnancyScore = 0.80f
            xaiList.add("Dahili Mikrofon: Düzenli geviş getirme/solunum örüntüsü tespit edildi (gebelik ile uyumlu sakin metabolik akustik).")
        } else {
            audioPregnancyScore = 0.50f
            xaiList.add("Dahili Mikrofon: Akustik profil nötr/ara seviyede.")
        }

        // 2. Built-in Accelerometer Feature Analysis (SensorManager 50 Hz)
        // Evaluates 3-axis dynamic magnitude and resting/lying posture ratio.
        val motionPregnancyScore: Float
        if (batch.mode == MeasurementMode.WEARABLE_COLLAR) {
            if (batch.motionActivityIndex < 0.35f && batch.lyingRatio > 0.40f) {
                motionPregnancyScore = 0.84f
                xaiList.add("Dahili İvmeölçer (Tasma): Yüksek dinlenme ve yatma oranı (%${(batch.lyingRatio * 100).toInt()}), gebelik ile uyumlu.")
            } else if (batch.motionActivityIndex > 0.65f) {
                motionPregnancyScore = 0.18f
                xaiList.add("Dahili İvmeölçer (Tasma): Yüksek aktivite ve ajitasyon (östrus hareketliliği olabilir).")
            } else {
                motionPregnancyScore = 0.50f
                xaiList.add("Dahili İvmeölçer (Tasma): Aktivite düzeyi standart seviyede.")
            }
        } else {
            // Spot check mode (held in user's hand or chest touch)
            if (batch.motionAverageMagnitude < 0.8f) {
                motionPregnancyScore = 0.72f
                xaiList.add("Dahili İvmeölçer (Elde): Hayvan ölçüm penceresinde sakin durdu.")
            } else {
                motionPregnancyScore = 0.44f
                xaiList.add("Dahili İvmeölçer (Elde): Ölçüm sırasında hayvan hareketliydi.")
            }
        }

        // 3. Biological & Temporal Insemination Prior (Standard Software Calendar)
        // Ewe estrus cycle is ~16-17 days. Absence of heat at day 25-45 indicates progestational retention.
        var temporalBias = 0.0f
        when {
            batch.daysSinceInsemination in 25..35 -> {
                temporalBias = 0.09f // First complete missed estrus cycle
                xaiList.add("Tohumlama Takvimi: ${batch.daysSinceInsemination}. gün (1. kızgınlık döngüsü aşılmış, non-return pozitif).")
            }
            batch.daysSinceInsemination in 36..55 -> {
                temporalBias = 0.14f // Second missed estrus cycle
                xaiList.add("Tohumlama Takvimi: ${batch.daysSinceInsemination}. gün (2. kızgınlık döngüsü de aşılmış).")
            }
            batch.daysSinceInsemination < 25 -> {
                temporalBias = -0.12f
                xaiList.add("Tohumlama Takvimi: Erken dönem (<25. gün), duyarlılık düşüktür.")
            }
        }

        // 4. Built-in Camera Visual Analysis (CameraX - Optical Respiration & Flank Motion)
        var cameraScore: Float? = null
        if (batch.cameraRespirationBpm != null) {
            val bpm = batch.cameraRespirationBpm
            if (bpm in 26.0f..38.0f) {
                cameraScore = 0.80f
                xaiList.add("Dahili Kamera (Optik Solunum): Karın solunum frekansı dakikada ${bpm.toInt()} nefes (gebelik metabolik hız artışı ile uyumlu).")
            } else if (bpm < 24.0f) {
                cameraScore = 0.48f
                xaiList.add("Dahili Kamera (Optik Solunum): Standart dinlenme solunumu (${bpm.toInt()} BPM).")
            } else {
                cameraScore = 0.35f
                xaiList.add("Dahili Kamera (Optik Solunum): Hızlı/düzensiz solunum (${bpm.toInt()} BPM).")
            }
        }

        // 5. Multimodal Late Fusion Calculation
        val audioWeight = if (cameraScore != null) 0.45f else 0.50f
        val motionWeight = if (cameraScore != null) 0.40f else 0.50f
        val cameraWeight = if (cameraScore != null) 0.15f else 0.0f

        var fusedProb = (audioPregnancyScore * audioWeight) +
                (motionPregnancyScore * motionWeight) +
                ((cameraScore ?: 0f) * cameraWeight) +
                temporalBias

        fusedProb = fusedProb.coerceIn(0.05f, 0.95f)

        // Confidence calculation based on microphone SNR and sampling completeness
        val durationConfidence = (batch.audioDurationSeconds / 30f).coerceIn(0.5f, 1.0f)
        val snrConfidence = (batch.audioSnrDb / 30f).coerceIn(0.6f, 1.0f)
        val confidence = (0.7f * durationConfidence + 0.3f * snrConfidence).coerceIn(0.50f, 0.95f)

        val riskLevel = PregnancyRiskLevel.fromProbability(fusedProb)

        return ScreeningResult(
            probability = fusedProb,
            riskLevel = riskLevel,
            confidence = confidence,
            audioScore = audioPregnancyScore,
            motionScore = motionPregnancyScore,
            cameraScore = cameraScore,
            morphologyScore = null,
            xaiReasons = xaiList,
            measurementDurationSec = batch.audioDurationSeconds.toInt(),
            mode = batch.mode
        )
    }
}
