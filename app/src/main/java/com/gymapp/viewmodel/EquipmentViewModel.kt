package com.gymapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymapp.data.db.entities.Equipment
import com.gymapp.data.db.entities.Exercise
import com.gymapp.data.repository.EquipmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class EquipmentViewModel @Inject constructor(
    private val repository: EquipmentRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedType = MutableStateFlow("الكل")
    val selectedType: StateFlow<String> = _selectedType.asStateFlow()

    val types = listOf("الكل", "حر", "آلة", "كابل", "هوائي")

    @OptIn(ExperimentalCoroutinesApi::class)
    val equipment: StateFlow<List<Equipment>> =
        _searchQuery.flatMapLatest { query ->
            _selectedType.flatMapLatest { type ->
                repository.searchAndFilter(query, type)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onTypeSelected(type: String) {
        _selectedType.value = type
    }

    fun getEquipmentById(id: Int): StateFlow<Equipment?> =
        repository.getEquipmentById(id)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )

    fun getExercisesByEquipmentId(equipmentId: Int): StateFlow<List<Exercise>> =
        repository.getExercisesByEquipmentId(equipmentId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
}