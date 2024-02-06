package ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.newcategory

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.ui.screens.common.CenteredLoadingSpinner
import ua.com.andromeda.wordgalaxy.ui.screens.common.Message
import ua.com.andromeda.wordgalaxy.utils.RESOURCE_NOT_FOUND
import ua.com.andromeda.wordgalaxy.utils.getCategoryIconIdentifier


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewCategoryScreen(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.new_category))
                },
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = {
                    Text(text = stringResource(R.string.add))
                },
                icon = {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                },
                onClick = {
                    /*TODO*/
                }
            )
        }
    ) { innerPadding ->
        NewCategoryMain(modifier = Modifier.padding(innerPadding))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewCategoryMain(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel: NewCategoryViewModel = viewModel(factory = NewCategoryViewModel.factory)
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is NewCategoryUiState.Default -> {
            CenteredLoadingSpinner()
        }

        is NewCategoryUiState.Error -> {
            Message(
                message = state.message,
                backgroundColor = MaterialTheme.colorScheme.errorContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }

        is NewCategoryUiState.Success -> {
            Column(modifier) {
                OutlinedTextField(
                    value = state.title,
                    onValueChange = viewModel::updateCategoryTitle,
                    label = { Text("Title") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            // Handle Next button click, if needed
                        }
                    )
                )
                ExposedDropdownMenuBox(
                    expanded = state.parentCategoriesExpanded,
                    onExpandedChange = viewModel::expandParentCategories
                ) {
                    val selectedCategory = state.selectedCategory
                    OutlinedTextField(
                        value = selectedCategory?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = {
                            Text(text = "Parent Category")
                        },
                        leadingIcon = {
                            if (selectedCategory != null) {
                                val iconRes = context.getCategoryIconIdentifier(selectedCategory)
                                if (iconRes != RESOURCE_NOT_FOUND) {
                                    Icon(
                                        painter = painterResource(iconRes),
                                        contentDescription = null,
                                        tint = Color.Unspecified,
                                        modifier = Modifier
                                            .size(dimensionResource(R.dimen.icon_size_largest))
                                            .padding(dimensionResource(R.dimen.padding_small))
                                    )
                                }
                            }
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(state.parentCategoriesExpanded)
                        },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = state.parentCategoriesExpanded,
                        onDismissRequest = viewModel::expandParentCategories
                    ) {
                        state.parentCategories.forEach { category ->
                            DropdownMenuItem(
                                text = {
                                    Text(text = category.name)
                                },
                                onClick = {
                                    viewModel.updateParentCategory(category)
                                },
                                leadingIcon = {
                                    val iconRes = context.getCategoryIconIdentifier(category)
                                    if (iconRes != RESOURCE_NOT_FOUND) {
                                        Icon(
                                            painter = painterResource(iconRes),
                                            contentDescription = null,
                                            tint = Color.Unspecified,
                                            modifier = Modifier
                                                .size(dimensionResource(R.dimen.icon_size_largest))
                                                .padding(dimensionResource(R.dimen.padding_small))
                                        )
                                    }
                                }
                            )
                        }
                    }
                }

            }
        }
    }
}