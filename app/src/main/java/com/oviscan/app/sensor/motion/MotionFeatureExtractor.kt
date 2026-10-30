package com.oviscan.app.sensor.motion

import kotlin.math.*

object MotionFeatureExtractor {

    data class MotionFeatures(
        val durationSeconds: Float,
        val sampleCount: Int,
        val averageDynamicMagnitude: Float,
        val stdDevMagnitude: Float,
        val activityIndex: Float,
        val entropy: Float,
        val lyingRatio: Float,
        val window5sMagnitudes: FloatArray
    )

    data class AccelSample(
        val timestampNanos: Long,
        val x: Float,
        val y: Float,
        val z: Float
    ) {
        val totalMagnitude: Float
            get() = sqrt(x * x + y * y + z * z)

        val dynamicMagnitude: Float
            get() = abs(totalMagnitude - 9.81f)

        // Estimated pitch angle relative to gravity vector
        val inclinationAngleDeg: Float
            get() {
                val mag = totalMagnitude
                return if (mag > 0.1f) {
                    (acos((y / mag).coerceIn(-1f, 1f)) * (180f / Math.PI.toFloat()))
                } else 0f
            }
    }

    /**
     * Extracts statistical and spectral gait/activity features from 50 Hz motion data.
     */
    fun extractFeatures(samples: List<AccelSample>, samplingRateHz: Float = 50f): MotionFeatures {
        if (samples.isEmpty()) {
            return MotionFeatures(
                durationSeconds = 0f,
                sampleCount = 0,
                averageDynamicMagnitude = 0f,
                stdDevMagnitude = 0f,
                activityIndex = 0f,
                entropy = 0f,
                lyingRatio = 0f,
                window5sMagnitudes = FloatArray(0)
            )
        }

        val count = samples.size
        val durationSec = count / samplingRateHz

        var sumDynamic = 0.0
        var sumSquares = 0.0
        var lyingCount = 0

        for (s in samples) {
            val d = s.dynamicMagnitude
            sumDynamic += d
            sumSquares += d * d

            // Sheep posture: When lying down, collar pitch tilts significantly (>65 deg from vertical)
            if (s.inclinationAngleDeg > 65f) {
                lyingCount++
            }
        }

        val avgDynamic = (sumDynamic / count).toFloat()
        val variance = ((sumSquares / count) - (avgDynamic * avgDynamic)).coerceAtLeast(0.0).toFloat()
        val stdDev = sqrt(variance)

        // Activity Index: Normalized metric where 0.0 = calm/resting/lying, 1.0 = high agitation/mounting/estrus
        val activityIndex = (avgDynamic * 0.4f + stdDev * 0.6f).coerceIn(0f, 10f) / 10f

        // Shannon Entropy of activity distribution (quantized into 10 bins)
        val entropy = calculateEntropy(samples.map { it.dynamicMagnitude }, numBins = 10)

        val lyingRatio = lyingCount.toFloat() / count

        // 5-second window aggregated dynamic intensities
        val windowSamples = (samplingRateHz * 5).toInt().coerceAtLeast(1)
        val numWindows = (count / windowSamples).coerceAtLeast(1)
        val windowMagnitudes = FloatArray(numWindows)

        for (w in 0 until numWindows) {
            val start = w * windowSamples
            val end = (start + windowSamples).coerceAtMost(count)
            var wSum = 0f
            for (i in start until end) {
                wSum += samples[i].dynamicMagnitude
            }
            windowMagnitudes[w] = wSum / (end - start)
        }

        return MotionFeatures(
            durationSeconds = durationSec,
            sampleCount = count,
            averageDynamicMagnitude = avgDynamic,
            stdDevMagnitude = stdDev,
            activityIndex = activityIndex,
            entropy = entropy,
            lyingRatio = lyingRatio,
            window5sMagnitudes = windowMagnitudes
        )
    }

    private fun calculateEntropy(values: List<Float>, numBins: Int = 10): Float {
        if (values.isEmpty()) return 0f
        val maxVal = (values.maxOrNull() ?: 1f).coerceAtLeast(0.1f)
        val bins = IntArray(numBins)

        for (v in values) {
            val binIdx = ((v / maxVal) * (numBins - 1)).toInt().coerceIn(0, numBins - 1)
            bins[binIdx]++
        }

        var ent = 0.0
        val total = values.size.toDouble()
        for (b in bins) {
            if (b > 0) {
                val p = b / total
                ent -= p * log2(p)
            }
        }
        return (ent / log2(numBins.toDouble())).toFloat().coerceIn(0f, 1f)
    }
}
