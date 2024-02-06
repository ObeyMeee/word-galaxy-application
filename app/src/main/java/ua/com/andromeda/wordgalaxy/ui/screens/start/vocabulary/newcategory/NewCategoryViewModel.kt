package ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.newcategory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ua.com.andromeda.wordgalaxy.WordGalaxyApplication
import ua.com.andromeda.wordgalaxy.data.model.Category
import ua.com.andromeda.wordgalaxy.data.repository.category.CategoryRepository
import ua.com.andromeda.wordgalaxy.data.repository.category.CategoryRepositoryImpl

class NewCategoryViewModel(
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    private var _uiState: MutableStateFlow<NewCategoryUiState> =
        MutableStateFlow(NewCategoryUiState.Default)
    val uiState: StateFlow<NewCategoryUiState> = _uiState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            categoryRepository.findAllByParentCategoryId()
                .map {
                    NewCategoryUiState.Success(
                        parentCategories = it
                    )
                }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(SUBSCRIBE_TIMEOUT_MILLIS),
                    initialValue = NewCategoryUiState.Default
                )
                .collect {
                    _uiState.value = it
                }
        }
    }

    private fun updateState(action: (NewCategoryUiState.Success) -> NewCategoryUiState.Success) {
        _uiState.update {
            if (it is NewCategoryUiState.Success) {
                action(it)
            } else {
                NewCategoryUiState.Error()
            }
        }
    }

    fun updateCategoryTitle(value: String) {
        updateState {
            it.copy(title = value)
        }
    }

    fun updateParentCategory(value: Category?) {
        updateState {
            it.copy(
                selectedCategory = value,
                parentCategoriesExpanded = false
            )
        }
    }

    fun expandParentCategories(expanded: Boolean = false) {
        updateState {
            it.copy(parentCategoriesExpanded = expanded)
        }
    }

    companion object {
        private const val SUBSCRIBE_TIMEOUT_MILLIS = 5_000L
        val factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as WordGalaxyApplication
                val categoryDao = application.appDatabase.categoryDao()
                val categoryRepository = CategoryRepositoryImpl(categoryDao)
                NewCategoryViewModel(categoryRepository)
            }
        }
    }
}