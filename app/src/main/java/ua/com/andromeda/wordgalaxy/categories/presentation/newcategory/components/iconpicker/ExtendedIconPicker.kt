package ua.com.andromeda.wordgalaxy.categories.presentation.newcategory.components.iconpicker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.core.presentation.components.CenteredLoadingSpinner

@Composable
fun ExtendedIconsPicker(
    onSelected: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: IconsViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
    ) {
        if (state.loading) {
            CenteredLoadingSpinner()
            return@Column
        }

        SearchTextField {
            viewModel.updateSearch(it)
        }
        IconsList(
            icons = state.icons,
            onIconClick = {
                viewModel.onClickIcon(it)
                val id = it.id
                val selected = if (state.selectedIcon == id) null else id
                onSelected(selected)
            }
        )
    }
}

@Composable
private fun SearchTextField(
    modifier: Modifier = Modifier,
    onSearchChanged: (String) -> Unit,
) {
    var search by remember { mutableStateOf("") }
    OutlinedTextField(
        label = {
            Text(text = stringResource(R.string.search))
        },
        value = search,
        onValueChange = {
            search = it
            onSearchChanged(it)
        },
        trailingIcon = {
            Icon(imageVector = Icons.Filled.Image, null)
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Search
        ),
        modifier = modifier,
    )
}

@Composable
private fun IconsList(
    icons: List<List<IconItem>>,
    onIconClick: (IconItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn {
            items(items = icons) {
                IconListRow(
                    icons = it,
                    onClick = onIconClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun IconListRow(
    icons: List<IconItem>,
    modifier: Modifier = Modifier,
    onClick: (IconItem) -> Unit = {}
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        icons.forEach {
            IconItem(
                icon = it,
                selected = it.selected,
                modifier = Modifier
                    .width(dimensionResource(R.dimen.icon_item_width))
                    .padding(dimensionResource(R.dimen.padding_smaller))
                    .clickable { onClick(it) }
            )
        }
    }
}

@Composable
private fun IconItem(
    icon: IconItem,
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (selected)
        MaterialTheme.colorScheme.primaryContainer
    else
        MaterialTheme.colorScheme.surface

    Column(
        modifier = modifier.background(backgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        var text = "Not found"
        var vector = Icons.Filled.Image
        var color = MaterialTheme.colorScheme.error

        val image = icon.image
        if (image != null) {
            text = icon.name
            color = MaterialTheme.colorScheme.primary
            vector = image
        }

        Icon(
            imageVector = vector,
            contentDescription = icon.name,
            tint = color,
            modifier = Modifier.size(dimensionResource(R.dimen.icon_size_larger))
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )

        AnimatedVisibility(visible = selected) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(dimensionResource(R.dimen.icon_size_small))
                )
            }
        }
    }
}
