package ua.com.andromeda.wordgalaxy.core.presentation.ui.wordform

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.core.presentation.components.EnabledFloatingActionButton
import ua.com.andromeda.wordgalaxy.core.presentation.components.TitledTopAppBar

@Composable
fun WordFormScaffold(
    @StringRes topAppBarTextRes: Int,
    navigateUp: () -> Unit,
    onFabClick: () -> Unit,
    fabEnabled: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        topBar = {
            TitledTopAppBar(
                titleRes = topAppBarTextRes,
                navigateUp = navigateUp,
            )
        },
        floatingActionButton = {
            EnabledFloatingActionButton(
                textRes = R.string.next,
                enabled = fabEnabled,
                onClick = onFabClick
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        modifier = modifier,
        content = content,
    )
}