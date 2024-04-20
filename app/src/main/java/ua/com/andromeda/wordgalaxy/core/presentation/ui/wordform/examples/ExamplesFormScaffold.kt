package ua.com.andromeda.wordgalaxy.core.presentation.ui.wordform.examples

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import ua.com.andromeda.wordgalaxy.core.presentation.components.TitledTopAppBar

@Composable
fun ExampleListFormScaffold(
    @StringRes topAppBarTextRes: Int,
    @StringRes floatingActionButtonTextRes: Int,
    navigateUp: () -> Unit,
    onFloatingActionButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    floatingActionButtonIcon: ImageVector? = null,
    floatingActionButtonPosition: FabPosition = FabPosition.End,
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
            ExtendedFloatingActionButton(
                text = {
                    Text(text = stringResource(floatingActionButtonTextRes))
                },
                icon = {
                    floatingActionButtonIcon?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = null
                        )
                    }
                },
                onClick = onFloatingActionButtonClick
            )
        },
        floatingActionButtonPosition = floatingActionButtonPosition,
        modifier = modifier,
        content = content,
    )
}