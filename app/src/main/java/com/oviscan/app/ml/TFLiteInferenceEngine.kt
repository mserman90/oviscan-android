package com.oviscan.app.ml

import android.content.Context
import android.util.Log
import com.oviscan.app.domain.model.ScreeningResult
import com.oviscan.app.domain.model.SensorDataBatch
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class TFLiteInferenceEngine(private val context: Context) {

    private val tag = "TFLiteInferenceEngine"
    private val modelFileName = "oviscan_fusion_model.tflite"
    private var interpreter: Interpreter? = null
    private var isModelLoaded = false

    init {
        loadModel()
    }

    private fun loadModel() {
        try {
            val assetFileDescriptor = context.assets.openFd(modelFileName)
            val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
            val fileChannel = fileInputStream.channel
            val startOffset = assetFileDescriptor.startOffset
            val declaredLength = assetFileDescriptor.declaredLength
            val modelBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)

            val options = Interpreter.Options().apply {
                setNumThreads(4)
                setUseNNAPI(false)
            }
            interpreter = Interpreter(modelBuffer, options)
            isModelLoaded = true
            Log.d(tag, "TensorFlow Lite model loaded successfully from assets.")
        } catch (e: Exception) {
            Log.w(tag, "TFLite model ($modelFileName) using rule-calibrated on-device classifier: ${e.message}")
            isModelLoaded = false
        }
    }

    /**
     * Executes multimodal inference on features collected solely from standard built-in Android sensors.
     */
    fun runInference(batch: SensorDataBatch): ScreeningResult {
        if (!isModelLoaded || interpreter == null) {
            return LateFusionClassifier.classify(batch)
        }

        try {
            // Input feature vector shape: [1, 24] - All standard phone sensors
            val inputBuffer = ByteBuffer.allocateDirect(1 * 24 * 4).apply {
                order(ByteOrder.nativeOrder())
                rewind()
            }

            // Audio (Mic) features
            for (i in 0 until 13) {
                inputBuffer.putFloat(batch.mfccCoefficients.getOrElse(i) { 0f })
            }
            inputBuffer.putFloat(batch.audioAverageRms)
            inputBuffer.putFloat(batch.acousticEventCount.toFloat())
            inputBuffer.putFloat(batch.estimatedPitchHz)

            // Motion (Accelerometer) features
            inputBuffer.putFloat(batch.motionAverageMagnitude)
            inputBuffer.putFloat(batch.motionStdDev)
            inputBuffer.putFloat(batch.motionActivityIndex)
            inputBuffer.putFloat(batch.lyingRatio)

            // Metadata / Calendar features
            inputBuffer.putFloat(batch.daysSinceInsemination.toFloat())
            inputBuffer.putFloat(batch.ageYears.toFloat())

            // Built-in Camera features
            inputBuffer.putFloat(batch.cameraRespirationBpm ?: 28.0f)
            inputBuffer.putFloat(batch.cameraFlankExpansionRatio ?: 1.0f)

            // Output buffer shape: [1, 3] -> Softmax: [Low, Inconclusive, High]
            val outputBuffer = ByteBuffer.allocateDirect(1 * 3 * 4).apply {
                order(ByteOrder.nativeOrder())
                rewind()
            }

            interpreter?.run(inputBuffer, outputBuffer)
            outputBuffer.rewind()

            val pLow = outputBuffer.float
            val pInconclusive = outputBuffer.float
            val pHigh = outputBuffer.float

            val baseResult = LateFusionClassifier.classify(batch)
            val fusedProb = pHigh.coerceIn(0.01f, 0.99f)

            return baseResult.copy(
                probability = fusedProb,
                confidence = maxOf(pLow, pInconclusive, pHigh)
            )
        } catch (e: Exception) {
            Log.e(tag, "Inference error in TFLite: ${e.message}", e)
            return LateFusionClassifier.classify(batch)
        }
    }

    fun close() {
        interpreter?.close()
        interpreter = null
    }
}
