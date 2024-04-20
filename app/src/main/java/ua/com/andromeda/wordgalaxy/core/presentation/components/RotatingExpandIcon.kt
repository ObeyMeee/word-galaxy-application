package ua.com.andromeda.wordgalaxy.core.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import ua.com.andromeda.wordgalaxy.R

@Composable
fun RotatingExpandIcon(expanded: Boolean, modifier: Modifier = Modifier) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = spring(),
        label = "ExpandExampleAnimation"
    )

    Icon(
        imageVector = Icons.Default.KeyboardArrowDown,
        contentDescription = stringResource(R.string.expand),
        modifier = modifier.graphicsLayer {
            rotationZ = rotationAngle
        }
    )
}