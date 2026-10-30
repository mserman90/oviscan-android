package com.oviscan.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.oviscan.app.OviScanApplication
import com.oviscan.app.data.local.entity.SheepEntity
import com.oviscan.app.domain.model.MeasurementMode
import com.oviscan.app.domain.model.ScreeningResult
import com.oviscan.app.domain.model.SensorDataBatch
import com.oviscan.app.ml.TFLiteInferenceEngine
import com.oviscan.app.sensor.audio.AudioCaptureService
import com.oviscan.app.sensor.audio.AudioFeatureExtractor
import com.oviscan.app.sensor.camera.VisualFlankAnalyzer
import com.oviscan.app.sensor.motion.MotionFeatureExtractor
import com.oviscan.app.sensor.motion.MotionSensorService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class SessionState {
    IDLE,
    RECORDING,
    PROCESSING,
    COMPLETED,
    ERROR
}

class ScreeningViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as OviScanApplication).repository
    
    // 100% Standard Built-in Phone Sensor Services (No dongles, No external hardware)
    private val audioService = AudioCaptureService()
    private val motionService = MotionSensorService(application)
    private val visualFlankAnalyzer = VisualFlankAnalyzer()
    private val inferenceEngine = TFLiteInferenceEngine(application)

    private val _sessionState = MutableStateFlow(SessionState.IDLE)
    val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    private val _currentSheep = MutableStateFlow<SheepEntity?>(null)
    val currentSheep: StateFlow<SheepEntity?> = _currentSheep.asStateFlow()

    private val _selectedMode = MutableStateFlow(MeasurementMode.SPOT_CHECK)
    val selectedMode: StateFlow<MeasurementMode> = _selectedMode.asStateFlow()

    private val _elapsedSeconds = MutableStateFlow(0)
    val elapsedSeconds: StateFlow<Int> = _elapsedSeconds.asStateFlow()

    val totalDurationSeconds: Int
        get() = if (_selectedMode.value == MeasurementMode.SPOT_CHECK) 30 else 60

    val liveRms: StateFlow<Float> = audioService.liveRmsFlow
    val waveformPoints: StateFlow<FloatArray> = audioService.waveformPointsFlow
    val liveMotion: StateFlow<Float> = motionService.liveMotionLevel

    private val _lastResult = MutableStateFlow<ScreeningResult?>(null)
    val lastResult: StateFlow<ScreeningResult?> = _lastResult.asStateFlow()

    private var countdownJob: Job? = null

    fun selectSheep(sheep: SheepEntity) {
        _currentSheep.value = sheep
        _sessionState.value = SessionState.IDLE
        _lastResult.value = null
    }

    fun setMeasurementMode(mode: MeasurementMode) {
        if (_sessionState.value == SessionState.IDLE) {
            _selectedMode.value = mode
        }
    }

    fun startScreening() {
        val sheep = _currentSheep.value ?: return
        if (_sessionState.value == SessionState.RECORDING) return

        _sessionState.value = SessionState.RECORDING
        _elapsedSeconds.value = 0

        // Start standard Android microphone and accelerometer
        audioService.startCapture()
        motionService.startTracking()
        visualFlankAnalyzer.startAnalysis()

        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            val targetSeconds = totalDurationSeconds
            for (sec in 1..targetSeconds) {
                delay(1000)
                _elapsedSeconds.value = sec
            }
            stopAndAnalyze()
        }
    }

    fun stopAndAnalyze() {
        countdownJob?.cancel()
        countdownJob = null
        if (_sessionState.value != SessionState.RECORDING) return

        _sessionState.value = SessionState.PROCESSING

        viewModelScope.launch(Dispatchers.Default) {
            try {
                // Collect data exclusively from built-in phone hardware
                val rawAudio = audioService.stopCapture()
                val rawMotion = motionService.stopTracking()
                val cameraReading = visualFlankAnalyzer.stopAndGetReading()
                val sheep = _currentSheep.value ?: return@launch

                // 1. Feature extraction in background thread
                val audioFeats = AudioFeatureExtractor.extractFeatures(rawAudio)
                val motionFeats = MotionFeatureExtractor.extractFeatures(rawMotion)
                val daysSinceInsem = sheep.getDaysSinceInsemination()

                val batch = SensorDataBatch(
                    earTag = sheep.earTag,
                    daysSinceInsemination = daysSinceInsem,
                    breed = sheep.breed,
                    ageYears = sheep.ageYears,
                    mode = _selectedMode.value,
                    audioDurationSeconds = audioFeats.durationSeconds,
                    audioAverageRms = audioFeats.averageRms,
                    audioPeakRms = audioFeats.peakRms,
                    audioSnrDb = audioFeats.snrDb,
                    acousticEventCount = audioFeats.eventCount,
                    mfccCoefficients = audioFeats.mfccCoefficients,
                    estimatedPitchHz = audioFeats.estimatedPitchHz,
                    harmonicToNoiseRatio = audioFeats.harmonicToNoiseRatio,
                    motionDurationSeconds = motionFeats.durationSeconds,
                    motionAverageMagnitude = motionFeats.averageDynamicMagnitude,
                    motionStdDev = motionFeats.stdDevMagnitude,
                    motionActivityIndex = motionFeats.activityIndex,
                    motionEntropy = motionFeats.entropy,
                    lyingRatio = motionFeats.lyingRatio,
                    cameraRespirationBpm = cameraReading.respirationBpm,
                    cameraFlankExpansionRatio = cameraReading.flankExpansionRatio
                )

                // 2. Multimodal On-Device Inference
                val result = inferenceEngine.runInference(batch)

                // 3. Save to Room database
                repository.saveScreeningResult(
                    earTag = sheep.earTag,
                    result = result,
                    daysSinceInsemination = daysSinceInsem
                )

                withContext(Dispatchers.Main) {
                    _lastResult.value = result
                    _sessionState.value = SessionState.COMPLETED
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _sessionState.value = SessionState.ERROR
                }
            }
        }
    }

    fun recordVetVerification(measurementId: Long, isPregnant: Boolean, notes: String) {
        viewModelScope.launch {
            repository.updateVetVerification(measurementId, isPregnant, notes)
        }
    }

    fun resetSession() {
        countdownJob?.cancel()
        audioService.stopCapture()
        motionService.stopTracking()
        _sessionState.value = SessionState.IDLE
        _elapsedSeconds.value = 0
    }

    override fun onCleared() {
        super.onCleared()
        audioService.stopCapture()
        motionService.stopTracking()
        inferenceEngine.close()
    }
}
