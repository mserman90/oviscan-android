package com.oviscan.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oviscan.app.domain.model.MeasurementMode
import com.oviscan.app.ui.components.DisclaimerBanner
import com.oviscan.app.ui.components.LiveAudioWaveform
import com.oviscan.app.ui.components.SensorStatusBadge
import com.oviscan.app.ui.theme.*
import com.oviscan.app.ui.viewmodel.ScreeningViewModel
import com.oviscan.app.ui.viewmodel.SessionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeasurementScreen(
    viewModel: ScreeningViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToResult: () -> Unit
) {
    val sheep by viewModel.currentSheep.collectAsState()
    val sessionState by viewModel.sessionState.collectAsState()
    val selectedMode by viewModel.selectedMode.collectAsState()
    val elapsedSeconds by viewModel.elapsedSeconds.collectAsState()
    val liveRms by viewModel.liveRms.collectAsState()
    val waveformPoints by viewModel.waveformPoints.collectAsState()
    val liveMotion by viewModel.liveMotion.collectAsState()

    val totalSeconds = viewModel.totalDurationSeconds
    val progress = (elapsedSeconds.toFloat() / totalSeconds).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "progress")

    LaunchedEffect(sessionState) {
        if (sessionState == SessionState.COMPLETED) {
            onNavigateToResult()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sensör Verisi Toplama", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.resetSession()
                        onNavigateBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface,
                    titleContentColor = TextPrimary
                )
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Target Animal Info Card
            sheep?.let { s ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = s.earTag,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "${s.breed} • ${s.ageYears} Yaş",
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1E3A1E))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${s.getDaysSinceInsemination()}. Gün",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = GreenLight
                            )
                        }
                    }
                }
            }

            // Mode Selector Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedMode == MeasurementMode.SPOT_CHECK,
                    onClick = { viewModel.setMeasurementMode(MeasurementMode.SPOT_CHECK) },
                    label = { Text("Elde Hızlı Tarama (30 sn)") },
                    enabled = sessionState == SessionState.IDLE,
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = selectedMode == MeasurementMode.WEARABLE_COLLAR,
                    onClick = { viewModel.setMeasurementMode(MeasurementMode.WEARABLE_COLLAR) },
                    label = { Text("Tasma / Koşum (60 sn)") },
                    enabled = sessionState == SessionState.IDLE,
                    modifier = Modifier.weight(1f)
                )
            }

            // Real-time Built-in Phone Sensor Status Row (Zero External Hardware)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SensorStatusBadge(
                    label = "DAHİLİ MİKROFON",
                    statusText = if (sessionState == SessionState.RECORDING) "44.1 kHz Aktif" else "Hazır",
                    isActive = sessionState == SessionState.RECORDING,
                    modifier = Modifier.weight(1f)
                )
                SensorStatusBadge(
                    label = "DAHİLİ İVMEÖLÇER",
                    statusText = if (sessionState == SessionState.RECORDING) "50 Hz Örnekleme" else "Hazır",
                    isActive = sessionState == SessionState.RECORDING,
                    modifier = Modifier.weight(1f)
                )
                SensorStatusBadge(
                    label = "DAHİLİ KAMERA",
                    statusText = if (sessionState == SessionState.RECORDING) "Optik Solunum" else "Hazır",
                    isActive = sessionState == SessionState.RECORDING,
                    modifier = Modifier.weight(1f)
                )
            }

            // Live Audio Waveform Visualizer
            LiveAudioWaveform(
                waveformPoints = waveformPoints,
                currentRms = liveRms
            )

            // Dynamic Motion Level Indicator
            Column(
                modifier = Modifier
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
                        text = "HAREKET / AKTİVİTE ŞİDDETİ (IMU)",
                        color = Color(0xFFB0BEC5),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "İndeks: %.2f".format(liveMotion),
                        color = AccentGold,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { liveMotion },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = AccentGold,
                    trackColor = Color(0xFF333333)
                )
            }

            // Measurement Progress Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = when (sessionState) {
                            SessionState.IDLE -> "Ölçüme Hazır"
                            SessionState.RECORDING -> "Sinyal Toplanıyor: $elapsedSeconds / $totalSeconds sn"
                            SessionState.PROCESSING -> "On-Device ML & Late Fusion Analiz Ediliyor..."
                            SessionState.COMPLETED -> "Tamamlandı!"
                            SessionState.ERROR -> "Hata Oluştu"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = GreenLight,
                        trackColor = Color(0xFF333333)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Action Buttons
            when (sessionState) {
                SessionState.IDLE -> {
                    Button(
                        onClick = { viewModel.startScreening() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("30 Saniyelik Ölçümü Başlat", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
                SessionState.RECORDING -> {
                    Button(
                        onClick = { viewModel.stopAndAnalyze() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RedAlert),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Stop, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Durdur ve Erken Analiz Et", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
                SessionState.PROCESSING -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = GreenLight)
                    }
                }
                else -> {}
            }

            // Legal banner
            DisclaimerBanner()
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
