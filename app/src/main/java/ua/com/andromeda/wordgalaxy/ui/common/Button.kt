package ua.com.andromeda.wordgalaxy.ui.common

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ua.com.andromeda.wordgalaxy.R

@Composable
fun EnabledFloatingActionButton(
    @StringRes textRes: Int,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minWidth = 56.dp, minHeight = 56.dp),
        enabled = enabled,
    ) {
        Text(text = stringResource(textRes))
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = null,
            modifier = Modifier.padding(
                start = dimensionResource(R.dimen.padding_medium)
            )
        )
    }
}

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