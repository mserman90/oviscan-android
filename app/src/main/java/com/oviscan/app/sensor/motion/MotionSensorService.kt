package com.oviscan.app.sensor.motion

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MotionSensorService(private val context: Context) : SensorEventListener {

    private val tag = "MotionSensorService"
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gyroscope: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    private val samplesList = ArrayList<MotionFeatureExtractor.AccelSample>()
    private var isTracking = false

    // Live dynamic acceleration for UI feedback (0.0 to 1.0)
    private val _liveMotionLevel = MutableStateFlow(0f)
    val liveMotionLevel: StateFlow<Float> = _liveMotionLevel

    fun startTracking() {
        if (isTracking) return
        synchronized(samplesList) {
            samplesList.clear()
        }

        // 50 Hz = 20,000 microseconds delay
        val samplingPeriodUs = 20_000

        accelerometer?.let {
            sensorManager.registerListener(this, it, samplingPeriodUs)
        }
        gyroscope?.let {
            sensorManager.registerListener(this, it, samplingPeriodUs)
        }

        isTracking = true
        Log.d(tag, "Motion tracking registered at 50 Hz (delay: ${samplingPeriodUs}us)")
    }

    fun stopTracking(): List<MotionFeatureExtractor.AccelSample> {
        if (!isTracking) return emptyList()
        isTracking = false
        sensorManager.unregisterListener(this)

        return synchronized(samplesList) {
            ArrayList(samplesList)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null || !isTracking) return

        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val sample = MotionFeatureExtractor.AccelSample(
                timestampNanos = event.timestamp,
                x = event.values[0],
                y = event.values[1],
                z = event.values[2]
            )

            synchronized(samplesList) {
                samplesList.add(sample)
            }

            // Update live UI gauge (0.0 to 1.0)
            val dynamic = sample.dynamicMagnitude
            _liveMotionLevel.value = (dynamic / 5.0f).coerceIn(0f, 1f)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No-op for inertial sensors
    }

    fun isTracking(): Boolean = isTracking
}
