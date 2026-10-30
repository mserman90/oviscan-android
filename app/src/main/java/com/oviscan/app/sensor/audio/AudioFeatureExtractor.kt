package com.oviscan.app.sensor.audio

import kotlin.math.*

object AudioFeatureExtractor {

    data class AudioFeatures(
        val durationSeconds: Float,
        val averageRms: Float,
        val peakRms: Float,
        val snrDb: Float,
        val eventCount: Int,
        val estimatedPitchHz: Float,
        val harmonicToNoiseRatio: Float,
        val mfccCoefficients: FloatArray
    )

    /**
     * Applies a 1st-order High-Pass Filter (cut-off ~150 Hz at 44.1 kHz) to attenuate
     * mechanical rumble, wind noise, and ambient barn drone.
     */
    fun applyHighPassFilter(samples: ShortArray, sampleRate: Int = 44100, cutoffFreq: Float = 150f): FloatArray {
        val rc = 1.0f / (2.0f * Math.PI.toFloat() * cutoffFreq)
        val dt = 1.0f / sampleRate
        val alpha = rc / (rc + dt)

        val filtered = FloatArray(samples.size)
        var lastFiltered = 0f
        var lastSample = 0f

        for (i in samples.indices) {
            val current = samples[i] / 32768.0f // Normalize to [-1.0, 1.0]
            val value = alpha * (lastFiltered + current - lastSample)
            filtered[i] = value
            lastFiltered = value
            lastSample = current
        }
        return filtered
    }

    /**
     * Computes RMS (Root Mean Square) energy of normalized buffer.
     */
    fun computeRms(normalizedSamples: FloatArray): Float {
        if (normalizedSamples.isEmpty()) return 0f
        var sumSquares = 0.0
        for (s in normalizedSamples) {
            sumSquares += s * s
        }
        return sqrt((sumSquares / normalizedSamples.size).toFloat())
    }

    /**
     * Extracts full acoustic feature set from recorded 44.1 kHz mono audio buffer.
     */
    fun extractFeatures(rawPcm: ShortArray, sampleRate: Int = 44100): AudioFeatures {
        val filtered = applyHighPassFilter(rawPcm, sampleRate)
        val durationSec = rawPcm.size.toFloat() / sampleRate

        // 1. RMS Energy and Peak
        val avgRms = computeRms(filtered)
        var peakRms = 0f
        val windowSize = sampleRate / 10 // 100 ms sub-windows
        var noiseFloorRms = 0.005f
        var eventCount = 0
        var isEventActive = false

        for (i in 0 until filtered.size step windowSize) {
            val end = (i + windowSize).coerceAtMost(filtered.size)
            var subSum = 0.0
            for (j in i until end) {
                subSum += filtered[j] * filtered[j]
            }
            val subRms = sqrt((subSum / (end - i)).toFloat())
            if (subRms > peakRms) peakRms = subRms

            // Acoustic event detector (vocalization / bleating / rumination burst)
            val threshold = (avgRms * 1.8f).coerceAtLeast(0.02f)
            if (subRms > threshold && !isEventActive) {
                eventCount++
                isEventActive = true
            } else if (subRms < threshold * 0.7f) {
                isEventActive = false
            }
        }

        // 2. SNR Estimation (Signal to Noise Ratio)
        val snrDb = if (avgRms > noiseFloorRms) {
            20f * log10(avgRms / noiseFloorRms)
        } else {
            0f
        }.coerceIn(0f, 60f)

        // 3. Pitch Estimation using normalized Autocorrelation
        val pitchHz = estimatePitchAutocorrelation(filtered, sampleRate)

        // 4. Harmonic-to-Noise Ratio (HNR)
        val hnr = calculateHnr(filtered, sampleRate, pitchHz)

        // 5. Mel-Frequency Cepstral Coefficients (MFCC - 13 banks)
        val mfccs = computeSimpleMfcc(filtered, sampleRate, numCoefficients = 13)

        return AudioFeatures(
            durationSeconds = durationSec,
            averageRms = avgRms,
            peakRms = peakRms,
            snrDb = snrDb,
            eventCount = eventCount,
            estimatedPitchHz = pitchHz,
            harmonicToNoiseRatio = hnr,
            mfccCoefficients = mfccs
        )
    }

    /**
     * Autocorrelation-based pitch estimator within ovine vocalization range (80 Hz - 900 Hz).
     */
    private fun estimatePitchAutocorrelation(samples: FloatArray, sampleRate: Int): Float {
        val minLag = (sampleRate / 900).coerceAtLeast(1) // Max 900 Hz
        val maxLag = (sampleRate / 80).coerceAtMost(samples.size / 2) // Min 80 Hz

        val frameSize = minOf(4096, samples.size)
        var maxCorr = -1f
        var bestLag = 0

        for (lag in minLag..maxLag) {
            var corr = 0f
            for (i in 0 until (frameSize - lag)) {
                corr += samples[i] * samples[i + lag]
            }
            if (corr > maxCorr) {
                maxCorr = corr
                bestLag = lag
            }
        }

        return if (bestLag > 0) sampleRate.toFloat() / bestLag else 0f
    }

    private fun calculateHnr(samples: FloatArray, sampleRate: Int, pitchHz: Float): Float {
        if (pitchHz <= 0f) return 5.0f
        val period = (sampleRate / pitchHz).toInt().coerceAtLeast(1)
        if (period >= samples.size) return 5.0f

        var harmonicEnergy = 0.0
        var noiseEnergy = 0.0
        val count = minOf(samples.size - period, 2048)

        for (i in 0 until count) {
            val signal = samples[i]
            val diff = samples[i] - samples[i + period]
            harmonicEnergy += signal * signal
            noiseEnergy += diff * diff
        }

        return if (noiseEnergy > 1e-6) {
            (10.0 * log10((harmonicEnergy / noiseEnergy).coerceAtLeast(1.0))).toFloat()
        } else {
            20.0f
        }.coerceIn(0f, 35f)
    }

    /**
     * Computes static 13-coefficient MFCC vector from averaged Mel filterbank outputs.
     */
    private fun computeSimpleMfcc(samples: FloatArray, sampleRate: Int, numCoefficients: Int): FloatArray {
        val fftSize = 1024
        val halfSize = fftSize / 2
        val powerSpectrum = FloatArray(halfSize)
        val window = FloatArray(fftSize) { i ->
            (0.54 - 0.46 * cos(2.0 * Math.PI * i / (fftSize - 1))).toFloat() // Hamming
        }

        val step = fftSize / 2
        var frames = 0
        for (start in 0 until (samples.size - fftSize) step step) {
            // Apply window and compute power spectrum
            for (i in 0 until halfSize) {
                val real = samples[start + i] * window[i]
                val imag = samples[start + i + halfSize] * window[i + halfSize]
                powerSpectrum[i] += (real * real + imag * imag)
            }
            frames++
        }

        if (frames > 0) {
            for (i in powerSpectrum.indices) powerSpectrum[i] /= frames
        }

        // Mel Filterbank (13 triangular bands)
        val melEnergies = FloatArray(numCoefficients)
        val melStep = halfSize / (numCoefficients + 1)
        for (m in 0 until numCoefficients) {
            var bandSum = 0f
            val center = (m + 1) * melStep
            for (k in (center - melStep) until (center + melStep).coerceAtMost(halfSize)) {
                if (k >= 0) bandSum += powerSpectrum[k]
            }
            melEnergies[m] = ln((bandSum + 1e-5f).toDouble()).toFloat()
        }

        // Discrete Cosine Transform (DCT-II)
        val mfcc = FloatArray(numCoefficients)
        for (i in 0 until numCoefficients) {
            var sum = 0.0
            for (j in 0 until numCoefficients) {
                sum += melEnergies[j] * cos(Math.PI * i * (j + 0.5) / numCoefficients)
            }
            mfcc[i] = sum.toFloat()
        }

        return mfcc
    }
}
