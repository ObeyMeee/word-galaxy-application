package ua.com.andromeda.wordgalaxy.categories.presentation.newcategory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ua.com.andromeda.wordgalaxy.core.data.repository.category.CategoryRepository
import ua.com.andromeda.wordgalaxy.core.domain.model.Category
import javax.inject.Inject

@HiltViewModel
class NewCategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
) : ViewModel() {
    private var _uiState = MutableStateFlow(NewCategoryUiState())
    val uiState = _uiState.asStateFlow()
    val fabEnabled = uiState.map { it.title.isNotBlank() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )

    private val coroutineDispatcher = Dispatchers.IO

    fun updateCategoryTitle(value: String) {
        _uiState.update { state ->
            state.copy(title = value)
        }
    }

    fun updateSelectedIcon(icon: String?) {
        _uiState.update { state ->
            state.copy(selectedIcon = icon)
        }
    }

    fun createCategory() = viewModelScope.launch(coroutineDispatcher) {
        if (!fabEnabled.value) return@launch

        val currentState = uiState.value
        val category = Category(
            name = currentState.title,
            materialIconId = currentState.selectedIcon
        )
        categoryRepository.insert(category)
    }
}