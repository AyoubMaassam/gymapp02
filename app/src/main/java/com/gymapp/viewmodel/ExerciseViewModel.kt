package com.gymapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymapp.data.db.entities.Exercise
import com.gymapp.data.repository.ExerciseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val repository: ExerciseRepository
) : ViewModel() {

    // تتبع التمارين التي يتم جلب صورها حالياً لمنع تكرار الطلبات
    private val fetchingExerciseIds = mutableSetOf<Int>()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("الكل")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val categories = listOf("الكل", "صدر", "ظهر", "كتف", "ذراع", "ساق", "بطن", "كارديو")

    @OptIn(ExperimentalCoroutinesApi::class)
    val exercises: StateFlow<List<Exercise>> =
        _searchQuery.flatMapLatest { query ->
            _selectedCategory.flatMapLatest { category ->
                repository.searchAndFilter(query, category)
            }
        }.onEach { list ->
            // تحفيز جلب الـ GIFs للتمارين التي لم يتم جلبها بعد
            list.filter {
                (it.gifUrl.isEmpty() || !it.gifUrl.contains("exercisedb")) && !fetchingExerciseIds.contains(it.id)
            }.forEach { exercise ->
                fetchingExerciseIds.add(exercise.id)
                viewModelScope.launch {
                    try {
                        repository.ensureExerciseGif(exercise)
                    } finally {
                        // ملاحظة: لا نزيل الـ ID من المجموعة فوراً لضمان عدم المحاولة مرة أخرى في نفس الجلسة إذا فشل
                    }
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
    }

    fun getExerciseById(id: Int): StateFlow<Exercise?> =
        repository.getExerciseById(id)
            .onEach { exercise ->
                exercise?.let {
                    if ((it.gifUrl.isEmpty() || !it.gifUrl.contains("exercisedb")) && !fetchingExerciseIds.contains(it.id)) {
                        fetchingExerciseIds.add(it.id)
                        viewModelScope.launch {
                            repository.ensureExerciseGif(it)
                        }
                    }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )
}