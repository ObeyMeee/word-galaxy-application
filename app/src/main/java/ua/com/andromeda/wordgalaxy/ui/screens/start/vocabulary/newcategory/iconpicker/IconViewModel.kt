package ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.newcategory.iconpicker

import android.content.Context
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

data class IconItem(
    var id: String = "",
    var name: String = "",
    var image: ImageVector? = null,
    var selected: Boolean = false
)

data class IconsState(
    val icons: List<List<IconItem>> = emptyList(),
    var selectedIcon: String? = null,
    val loading: Boolean = false
)

private const val AMOUNT_ICONS_PER_ROW = 4
private const val AMOUNT_VISIBLE_ICONS = 52

@HiltViewModel
class IconsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private var _state = MutableStateFlow(IconsState())
    val state = _state.asStateFlow()

    private var searchJob: Job? = null

    init {
        updateSearch("")
    }

    fun updateSearch(search: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)

            _state.update { it.copy(loading = true) }

            val icons = getNamesIcons()
                .filter { it.contains(search, ignoreCase = true) }
                .take(AMOUNT_VISIBLE_ICONS)
                .map { parseIconItem(it) }

            val chunks = icons.chunked(AMOUNT_ICONS_PER_ROW)

            updateSelection(chunks)
        }
    }

    private fun updateSelection(icons: List<List<IconItem>>) {
        val selected = state.value.selectedIcon
        val newIcons = icons.map { row ->
            row.map { icon ->
                icon.copy(selected = selected == icon.id)
            }
        }
        _state.update { it.copy(icons = newIcons, loading = false, selectedIcon = selected) }
    }

    fun onClickIcon(icon: IconItem) {
        _state.update {
            val id = icon.id
            val selectedIcon = if (it.selectedIcon == id) null else id
            it.copy(selectedIcon = selectedIcon)
        }
        updateSelection(state.value.icons)
    }


    private fun parseIconItem(line: String): IconItem {
        val splitted = line.split(",")
        val id = splitted[0]
        val name = splitted[1]
        val image = ImageUtil.createImageVector(id)

        return IconItem(id, name, image)
    }

    private fun getNamesIcons(): List<String> {
        val inputStream = context.assets.open("icons-names.txt")
        val reader = BufferedReader(InputStreamReader(inputStream))
        val lines = reader.readLines()
        reader.close()
        return lines
    }
}