package com.oviscan.app.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.oviscan.app.data.local.entity.MeasurementEntity
import com.oviscan.app.domain.model.PregnancyRiskLevel
import com.oviscan.app.ui.theme.*
import com.oviscan.app.ui.viewmodel.HerdViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HerdViewModel,
    onNavigateBack: () -> Unit
) {
    val measurements by viewModel.allMeasurements.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ölçüm ve Rapor Geçmişi", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri", tint = TextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.exportCsv { file ->
                            try {
                                val uri = FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.fileprovider",
                                    file
                                )
                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/csv"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Sürü Raporunu Paylaş"))
                            } catch (e: Exception) {
                                // Fallback
                            }
                        }
                    }) {
                        Icon(Icons.Default.FileDownload, contentDescription = "CSV Dışa Aktar", tint = GreenLight)
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
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Toplam ${measurements.size} Tarama Kaydı",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
                TextButton(onClick = {
                    viewModel.exportCsv { /* File export handled */ }
                }) {
                    Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("CSV Dışa Aktar", color = GreenLight)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (measurements.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Henüz kayıtlı bir ölçüm bulunmuyor.",
                        color = TextSecondary,
                        fontSize = 15.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(measurements, key = { it.id }) { item ->
                        MeasurementHistoryCard(item)
                    }
                }
            }
        }
    }
}

@Composable
fun MeasurementHistoryCard(item: MeasurementEntity) {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    val dateStr = dateFormat.format(Date(item.timestampMillis))

    val riskLevel = try {
        PregnancyRiskLevel.valueOf(item.riskLevel)
    } catch (e: Exception) {
        PregnancyRiskLevel.INCONCLUSIVE
    }
    val levelColor = Color(riskLevel.hexColor)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.sheepEarTag,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextPrimary
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(levelColor.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "%${(item.probability * 100).toInt()} ${riskLevel.titleTr}",
                        color = levelColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$dateStr • Tohumlama ${item.daysSinceInsemination}. Gün",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Text(
                    text = "Güven: %${(item.confidence * 100).toInt()}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            // Veterinary Validation Status Badge
            if (item.isVetVerified != null) {
                Spacer(modifier = Modifier.height(6.dp))
                val (vetText, vetBadgeColor) = if (item.isVetVerified == true) {
                    "Ultrason Teyidi: Gebe Doğrulandı" to GreenLight
                } else {
                    "Ultrason Teyidi: Boş Doğrulandı" to RedLight
                }
                Text(
                    text = "✓ $vetText",
                    fontSize = 11.sp,
                    color = vetBadgeColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
