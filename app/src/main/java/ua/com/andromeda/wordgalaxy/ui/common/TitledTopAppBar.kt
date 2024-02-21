package ua.com.andromeda.wordgalaxy.ui.common

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ua.com.andromeda.wordgalaxy.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitledTopAppBar(
    @StringRes titleRes: Int,
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit = {},
) {
    TopAppBar(
        title = {
            Text(text = stringResource(titleRes))
        },
        navigationIcon = {
            IconButton(onClick = navigateUp) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        },
        modifier = modifier
    )
}