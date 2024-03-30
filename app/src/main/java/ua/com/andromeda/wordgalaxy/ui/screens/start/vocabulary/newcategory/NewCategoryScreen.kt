package ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.newcategory

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.hilt.navigation.compose.hiltViewModel
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.ui.common.Message
import ua.com.andromeda.wordgalaxy.ui.common.TitledTopAppBar
import ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.newcategory.iconpicker.ExtendedIconsPicker


@Composable
fun NewCategoryScreen(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: NewCategoryViewModel = hiltViewModel()
    val fabEnabled by viewModel.fabEnabled.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TitledTopAppBar(titleRes = R.string.new_category, navigateUp = navigateUp)
        },
        floatingActionButton = {
            val containerColor =
                if (fabEnabled) FloatingActionButtonDefaults.containerColor
                else MaterialTheme.colorScheme.surfaceVariant

            ExtendedFloatingActionButton(
                text = {
                    Text(text = stringResource(R.string.add))
                },
                icon = {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                },
                onClick = {
                    viewModel.createCategory()
                    navigateUp()
                },
                containerColor = containerColor,
                contentColor = if (fabEnabled) contentColorFor(containerColor) else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    ) { innerPadding ->
        NewCategoryMain(
            modifier = Modifier.padding(innerPadding),
            viewModel = viewModel,
        )
    }
}

@Composable
fun NewCategoryMain(
    modifier: Modifier = Modifier,
    viewModel: NewCategoryViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
    ) {
        OutlinedTextField(
            value = state.title,
            onValueChange = viewModel::updateCategoryTitle,
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            )
        )
        AnimatedVisibility(visible = state.selectedIcon == null) {
            Message(
                message = stringResource(R.string.no_icons_selected),
                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                icon = {
                    Icon(imageVector = Icons.Default.Info, contentDescription = null)
                }
            )
        }
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))) {
                Text(
                    text = stringResource(R.string.category_icon),
                    style = MaterialTheme.typography.titleMedium
                )
                ExtendedIconsPicker(
                    onSelected = viewModel::updateSelectedIcon,
                )
            }
        }
    }
}