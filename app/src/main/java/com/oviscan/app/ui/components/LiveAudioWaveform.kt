package com.oviscan.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oviscan.app.ui.theme.DarkCard
import com.oviscan.app.ui.theme.GreenLight

@Composable
fun LiveAudioWaveform(
    waveformPoints: FloatArray,
    currentRms: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkCard)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "AKUSTİK SPEKTRUM (44.1 kHz PCM)",
                color = Color(0xFFB0BEC5),
                fontSize = 12.sp
            )
            Text(
                text = "RMS: %.2f".format(currentRms),
                color = GreenLight,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Dynamic Waveform Canvas
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(Color(0xFF141414))
                .clip(RoundedCornerShape(8.dp))
        ) {
            val width = size.width
            val height = size.height
            val centerY = height / 2f

            // Baseline center grid
            drawLine(
                color = Color(0xFF333333),
                start = Offset(0f, centerY),
                end = Offset(width, centerY),
                strokeWidth = 1f
            )

            if (waveformPoints.isNotEmpty()) {
                val path = Path()
                val stepX = width / (waveformPoints.size - 1).coerceAtLeast(1)

                for (i in waveformPoints.indices) {
                    val x = i * stepX
                    val sample = waveformPoints[i].coerceIn(-1f, 1f)
                    val y = centerY - (sample * (centerY * 0.9f))

                    if (i == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }
                }

                drawPath(
                    path = path,
                    color = GreenLight,
                    style = Stroke(width = 2.5f)
                )
            }
        }
    }
}
