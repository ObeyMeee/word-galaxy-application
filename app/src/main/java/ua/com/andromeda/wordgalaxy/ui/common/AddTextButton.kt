package ua.com.andromeda.wordgalaxy.ui.common

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import ua.com.andromeda.wordgalaxy.R

@Composable
fun AddTextButton(
    onClick: () -> Unit,
    @StringRes labelRes: Int,
    modifier: Modifier = Modifier,
) {
    val text = stringResource(labelRes)

    TextButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = text
            )
            HorizontalSpacer(R.dimen.padding_small)
            Text(text = text)
        }
    }
}