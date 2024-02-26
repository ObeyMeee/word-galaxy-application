package ua.com.andromeda.wordgalaxy.ui.common

import androidx.annotation.DimenRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp

@Composable
fun VerticalSpacer(@DimenRes height: Int) {
    Spacer(modifier = Modifier.height(dimensionResource(height)))
}

@Composable
fun HorizontalSpacer(@DimenRes width: Int) {
    Spacer(modifier = Modifier.width(dimensionResource(width)))
}

@Composable
fun Divider(modifier: Modifier = Modifier) {
    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(color = MaterialTheme.colorScheme.surface)
    )
}