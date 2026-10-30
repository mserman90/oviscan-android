package com.oviscan.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.oviscan.app.OviScanApplication
import com.oviscan.app.data.local.entity.MeasurementEntity
import com.oviscan.app.data.local.entity.SheepEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

class HerdViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as OviScanApplication).repository

    val allSheep: StateFlow<List<SheepEntity>> = repository.allSheep
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMeasurements: StateFlow<List<MeasurementEntity>> = repository.allMeasurements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val highCount: StateFlow<Int> = repository.highRiskCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val inconclusiveCount: StateFlow<Int> = repository.inconclusiveCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val lowCount: StateFlow<Int> = repository.lowRiskCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredSheep: StateFlow<List<SheepEntity>> = combine(allSheep, _searchQuery) { list, query ->
        if (query.isBlank()) list
        else list.filter { it.earTag.contains(query, ignoreCase = true) || it.breed.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addSheep(earTag: String, breed: String, ageYears: Int, daysAgoInseminated: Int, notes: String) {
        viewModelScope.launch {
            val inseminationMillis = System.currentTimeMillis() - (daysAgoInseminated * 24L * 60L * 60L * 1000L)
            val sheep = SheepEntity(
                earTag = earTag.trim().uppercase(),
                breed = breed.trim(),
                ageYears = ageYears,
                inseminationDateMillis = inseminationMillis,
                notes = notes
            )
            repository.insertSheep(sheep)
        }
    }

    fun exportCsv(onExported: (File) -> Unit) {
        viewModelScope.launch {
            val measurements = allMeasurements.value
            val file = repository.exportHerdReportToCsv(measurements)
            onExported(file)
        }
    }
}
