package com.oviscan.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.oviscan.app.domain.model.PregnancyRiskLevel
import com.oviscan.app.ui.components.DisclaimerBanner
import com.oviscan.app.ui.theme.*
import com.oviscan.app.ui.viewmodel.ScreeningViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    viewModel: ScreeningViewModel,
    onNavigateHome: () -> Unit,
    onStartNewScreening: () -> Unit
) {
    val result by viewModel.lastResult.collectAsState()
    val sheep by viewModel.currentSheep.collectAsState()

    var showVetDialog by remember { mutableStateOf(false) }
    var vetConfirmedStatus by remember { mutableStateOf<Boolean?>(null) }
    var vetNotesInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tarama ve Karar Destek Sonucu", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateHome) {
                        Icon(Icons.Default.Close, contentDescription = "Kapat", tint = TextPrimary)
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
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(modifier = Modifier.height(6.dp))

            // Mandatory Persistent Disclaimer
            DisclaimerBanner()

            result?.let { res ->
                val levelColor = Color(res.riskLevel.hexColor)
                val probPercent = (res.probability * 100).toInt()
                val confPercent = (res.confidence * 100).toInt()

                // High-visibility Result Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Header chip
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(levelColor.copy(alpha = 0.2f))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = res.riskLevel.titleTr,
                                color = levelColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Large Percentage Display
                        Text(
                            text = "%$probPercent",
                            fontSize = 54.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = levelColor
                        )

                        Text(
                            text = "Gebe Olma Tahmin Olasılığı (Güven: %$confPercent)",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        Divider(color = Color(0xFF333333))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Multi-modal Signal Contributions (100% Built-in Phone Sensors)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            SignalContributionColumn(
                                label = "Dahili Ses",
                                score = res.audioScore,
                                color = GreenLight
                            )
                            SignalContributionColumn(
                                label = "Dahili İvme",
                                score = res.motionScore,
                                color = AccentGold
                            )
                            res.cameraScore?.let { cam ->
                                SignalContributionColumn(
                                    label = "Dahili Kamera",
                                    score = cam,
                                    color = Color(0xFF64B5F6)
                                )
                            }
                        }
                    }
                }

                // Explainable AI (XAI) Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Psychology,
                                contentDescription = null,
                                tint = AccentGold,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Açıklanabilirlik (Karara Etki Eden Sinyaller)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        for (reason in res.xaiReasons) {
                            Row(
                                modifier = Modifier.padding(vertical = 3.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text("• ", color = AccentGold, fontWeight = FontWeight.Bold)
                                Text(
                                    text = reason,
                                    style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary)
                                )
                            }
                        }
                    }
                }

                // Recommended Action Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2820)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "ÖNERİLEN OPERASYONEL ADIM",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenLight
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = res.riskLevel.recommendedActionTr,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                        )
                    }
                }

                // Veterinary Ultrasound Ground Truth Verification Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Veteriner Referans Doğrulaması",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = if (vetConfirmedStatus == null) "Ultrason teyidi henüz girilmedi" else "Sonuç kaydedildi",
                                    fontSize = 12.sp,
                                    color = if (vetConfirmedStatus == null) TextSecondary else GreenLight
                                )
                            }
                            Button(
                                onClick = { showVetDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37474F)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Doğrula", fontSize = 12.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onNavigateHome,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Sürü Listesi", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onStartNewScreening,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Yeni Tarama", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Veterinary Ultrasound Dialog
    if (showVetDialog) {
        AlertDialog(
            onDismissRequest = { showVetDialog = false },
            title = { Text("Veteriner Ultrason / PAG Doğrulaması", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Ultrason veya laboratuvar testi sonucuna göre koyunun kesin durumunu giriniz:")

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                vetConfirmedStatus = true
                                showVetDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Gebe Doğrulandı")
                        }
                        Button(
                            onClick = {
                                vetConfirmedStatus = false
                                showVetDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = RedAlert),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Boş Doğrulandı")
                        }
                    }

                    OutlinedTextField(
                        value = vetNotesInput,
                        onValueChange = { vetNotesInput = it },
                        label = { Text("Klinik / Ultrason Notu (Fetus çapı, gün vb.)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showVetDialog = false }) {
                    Text("Kapat")
                }
            }
        )
    }
}

@Composable
fun SignalContributionColumn(
    label: String,
    score: Float,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 11.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "%${(score * 100).toInt()}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
