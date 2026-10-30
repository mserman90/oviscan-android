package com.oviscan.app.sensor.audio

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.ByteArrayOutputStream
import kotlin.math.sqrt

class AudioCaptureService {

    private val tag = "AudioCaptureService"
    private val sampleRate = 44100
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT

    private var audioRecord: AudioRecord? = null
    private var recordingJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Live RMS for UI feedback (0.0 to 1.0)
    private val _liveRmsFlow = MutableStateFlow(0f)
    val liveRmsFlow: StateFlow<Float> = _liveRmsFlow

    // Live wave snippet (last 64 points) for UI waveform drawing
    private val _waveformPointsFlow = MutableStateFlow(FloatArray(64))
    val waveformPointsFlow: StateFlow<FloatArray> = _waveformPointsFlow

    private val pcmBufferList = ArrayList<Short>()
    private var isRecording = false

    @SuppressLint("MissingPermission")
    fun startCapture(onBufferTick: ((rms: Float) -> Unit)? = null) {
        if (isRecording) return

        val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
        val bufferSize = maxOf(minBufferSize, 4096)

        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                bufferSize
            )

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                Log.e(tag, "AudioRecord could not be initialized")
                return
            }

            audioRecord?.startRecording()
            isRecording = true
            pcmBufferList.clear()

            recordingJob = scope.launch {
                val readBuffer = ShortArray(1024)
                val waveSnippet = FloatArray(64)

                while (isActive && isRecording) {
                    val readCount = audioRecord?.read(readBuffer, 0, readBuffer.size) ?: -1
                    if (readCount > 0) {
                        synchronized(pcmBufferList) {
                            for (i in 0 until readCount) {
                                pcmBufferList.add(readBuffer[i])
                            }
                        }

                        // Compute instant RMS for UI waveform
                        var sum = 0.0
                        for (i in 0 until readCount) {
                            val norm = readBuffer[i] / 32768f
                            sum += norm * norm
                        }
                        val rms = sqrt(sum / readCount).toFloat()
                        _liveRmsFlow.value = (rms * 4.0f).coerceIn(0f, 1f)

                        // Sample snippet for visualizer
                        val step = (readCount / 64).coerceAtLeast(1)
                        for (i in 0 until 64) {
                            val idx = (i * step).coerceAtMost(readCount - 1)
                            waveSnippet[i] = readBuffer[idx] / 32768f
                        }
                        _waveformPointsFlow.value = waveSnippet.clone()

                        onBufferTick?.invoke(rms)
                    }
                }
            }
            Log.d(tag, "Audio capture started at 44.1 kHz mono")
        } catch (e: Exception) {
            Log.e(tag, "Error starting AudioRecord: ${e.message}", e)
            stopCapture()
        }
    }

    fun stopCapture(): ShortArray {
        isRecording = false
        recordingJob?.cancel()
        recordingJob = null

        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (e: Exception) {
            Log.e(tag, "Error releasing AudioRecord", e)
        } finally {
            audioRecord = null
        }

        return synchronized(pcmBufferList) {
            val result = ShortArray(pcmBufferList.size)
            for (i in pcmBufferList.indices) {
                result[i] = pcmBufferList[i]
            }
            result
        }
    }

    fun isCapturing(): Boolean = isRecording
}
