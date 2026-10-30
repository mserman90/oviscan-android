package com.oviscan.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.oviscan.app.data.local.entity.SheepEntity
import com.oviscan.app.ui.components.DisclaimerBanner
import com.oviscan.app.ui.theme.*
import com.oviscan.app.ui.viewmodel.HerdViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HerdViewModel,
    onStartScreeningForSheep: (SheepEntity) -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val sheepList by viewModel.filteredSheep.collectAsState()
    val highCount by viewModel.highCount.collectAsState()
    val inconclusiveCount by viewModel.inconclusiveCount.collectAsState()
    val lowCount by viewModel.lowCount.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "OviScan",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Koyun Gebelik Ön Tarama ve Karar Destek",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(Icons.Default.History, contentDescription = "Geçmiş Ölçümler", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface,
                    titleContentColor = TextPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = GreenLight,
                contentColor = Color.Black
            ) {
                Icon(Icons.Default.Add, contentDescription = "Yeni Hayvan Ekle")
            }
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

            // Permanent Legal Alert
            DisclaimerBanner()

            Spacer(modifier = Modifier.height(16.dp))

            // Flock Statistics KPI Summary Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                KpiCard(
                    title = "Gebe Olası",
                    count = highCount,
                    accentColor = GreenPrimary,
                    modifier = Modifier.weight(1f)
                )
                KpiCard(
                    title = "Belirsiz",
                    count = inconclusiveCount,
                    accentColor = AmberWarning,
                    modifier = Modifier.weight(1f)
                )
                KpiCard(
                    title = "Boş / Düşük",
                    count = lowCount,
                    accentColor = RedAlert,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar for RFID / Ear Tag
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                label = { Text("Küpe No / RFID veya Irk Ara") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenLight,
                    unfocusedBorderColor = Color(0xFF424242),
                    focusedLabelColor = GreenLight,
                    cursorColor = GreenLight
                ),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Herd List Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sürü Listesi (${sheepList.size} Hayvan)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sheep List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(sheepList, key = { it.earTag }) { sheep ->
                    SheepCard(
                        sheep = sheep,
                        onScanClick = { onStartScreeningForSheep(sheep) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddSheepDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { tag, breed, age, days, notes ->
                viewModel.addSheep(tag, breed, age, days, notes)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun KpiCard(
    title: String,
    count: Int,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = count.toString(),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
        }
    }
}

@Composable
fun SheepCard(
    sheep: SheepEntity,
    onScanClick: () -> Unit
) {
    val days = sheep.getDaysSinceInsemination()
    val isCriticalPeriod = days in 25..45

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
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = sheep.earTag,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${sheep.breed} • ${sheep.ageYears} Yaş",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Tohumlama Sonrası: $days. Gün",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isCriticalPeriod) GreenLight else TextSecondary
                    )
                    if (isCriticalPeriod) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(GreenDark)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Tarama Dönemi (25-45g)", fontSize = 10.sp, color = GreenLight)
                        }
                    }
                }
            }

            // Big field-friendly scan button
            Button(
                onClick = onScanClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenPrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Default.Sensors, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Tara", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AddSheepDialog(
    onDismiss: () -> Unit,
    onAdd: (earTag: String, breed: String, age: Int, daysAgo: Int, notes: String) -> Unit
) {
    var tag by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("Akkaraman") }
    var ageStr by remember { mutableStateOf("2") }
    var daysStr by remember { mutableStateOf("28") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yeni Koyun Kaydı", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = tag,
                    onValueChange = { tag = it },
                    label = { Text("Küpe No / RFID (ör: TR06-1234)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = breed,
                    onValueChange = { breed = it },
                    label = { Text("Irk") },
                    singleLine = true
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = ageStr,
                        onValueChange = { ageStr = it },
                        label = { Text("Yaş") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = daysStr,
                        onValueChange = { daysStr = it },
                        label = { Text("Tohumlama (Gün)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notlar (Suni tohumlama, koç vb.)") },
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (tag.isNotBlank()) {
                        val age = ageStr.toIntOrNull() ?: 2
                        val days = daysStr.toIntOrNull() ?: 28
                        onAdd(tag, breed, age, days, notes)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GreenLight)
            ) {
                Text("Kaydet", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}
