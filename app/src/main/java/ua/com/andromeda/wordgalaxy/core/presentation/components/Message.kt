package ua.com.andromeda.wordgalaxy.core.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import ua.com.andromeda.wordgalaxy.R

@Composable
fun Message(
    message: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.errorContainer,
    icon: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error
        )
    }
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val shape = ShapeDefaults.Large
        Row(
            modifier = Modifier
                .border(
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.primary
                    ),
                    shape = shape
                )
                .clip(shape)
                .background(backgroundColor)
                .padding(dimensionResource(R.dimen.padding_medium)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
            HorizontalSpacer(R.dimen.padding_small)
            Text(text = message)
        }
    }
}
