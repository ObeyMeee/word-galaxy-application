package ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.categorydetails

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.utils.Direction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailsTopAppBar(
    title: String,
    sortOrder: WordSortOrder,
    direction: Direction,
    modifier: Modifier = Modifier,
    menuExpanded: Boolean = false,
    updateSortDirection: () -> Unit = {},
    openConfirmResetProgressDialog: (Boolean) -> Unit = {},
    navigateUp: () -> Unit = {},
    expandMenu: (Boolean) -> Unit = {},
    openOrderDialog: (Boolean) -> Unit = {},
) {
    TopAppBar(
        title = { Text(text = title) },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = navigateUp) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        },
        actions = {
            SortIconButton(direction, onClick = updateSortDirection)
            TopAppBarMenu(
                onExpand = expandMenu,
                expanded = menuExpanded,
                sortOrder = sortOrder,
                openConfirmResetProgressDialog = openConfirmResetProgressDialog,
                openOrderDialog = openOrderDialog,
                modifier = Modifier.wrapContentSize(Alignment.TopStart)
            )
        }
    )
}

@Composable
private fun TopAppBarMenu(
    expanded: Boolean,
    onExpand: (Boolean) -> Unit,
    sortOrder: WordSortOrder,
    openConfirmResetProgressDialog: (Boolean) -> Unit,
    openOrderDialog: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        IconButton(onClick = { onExpand(true) }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.show_more)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpand(false) },
        ) {
            DropdownMenuItem(
                text = {
                    Text(stringResource(R.string.reset_progress))
                },
                onClick = {
                    openConfirmResetProgressDialog(true)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.HourglassEmpty,
                        contentDescription = null
                    )
                }
            )
            DropdownMenuItem(
                text = {
                    Text(stringResource(R.string.word_order, sortOrder.label))
                },
                onClick = { openOrderDialog(true) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Sort,
                        contentDescription = null
                    )
                }
            )
        }
    }
}

@Composable
private fun SortIconButton(
    direction: Direction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Crossfade(
        targetState = direction,
        modifier = modifier,
        label = "OrderIconAnimation"
    ) {
        val sortIcon = if (it == Direction.ASC)
            R.drawable.sort_ascending_icon
        else
            R.drawable.sort_descending_icon
        IconButton(onClick = onClick) {
            Icon(
                painter = painterResource(sortIcon),
                contentDescription = null,
                modifier = Modifier.size(dimensionResource(R.dimen.icon_size_default))
            )
        }
    }
}
