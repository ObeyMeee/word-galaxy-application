package ua.com.andromeda.wordgalaxy.core.presentation.components

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
import ua.com.andromeda.wordgalaxy.R

@Composable
fun VerticalSpacer(@DimenRes height: Int) {
    Spacer(modifier = Modifier.height(dimensionResource(height)))
}

@Composable
fun VerticalSpacer2() = VerticalSpacer(R.dimen.padding_smallest)

@Composable
fun VerticalSpacer4() = VerticalSpacer(R.dimen.padding_smaller)

@Composable
fun VerticalSpacer8() = VerticalSpacer(R.dimen.padding_small)

@Composable
fun VerticalSpacer12() = VerticalSpacer(R.dimen.padding_mediumish)

@Composable
fun VerticalSpacer16() = VerticalSpacer(R.dimen.padding_medium)

@Composable
fun VerticalSpacer24() = VerticalSpacer(R.dimen.padding_large)

@Composable
fun VerticalSpacer32() = VerticalSpacer(R.dimen.padding_larger)

@Composable
fun VerticalSpacer40() = VerticalSpacer(R.dimen.padding_largest)

@Composable
fun VerticalSpacer64() = VerticalSpacer(R.dimen.padding_huge)


@Composable
fun HorizontalSpacer(@DimenRes width: Int) {
    Spacer(modifier = Modifier.width(dimensionResource(width)))
}


@Composable
fun HorizontalSpacer2() = HorizontalSpacer(R.dimen.padding_smallest)

@Composable
fun HorizontalSpacer4() = HorizontalSpacer(R.dimen.padding_smaller)

@Composable
fun HorizontalSpacer8() = HorizontalSpacer(R.dimen.padding_small)

@Composable
fun HorizontalSpacer12() = HorizontalSpacer(R.dimen.padding_mediumish)

@Composable
fun HorizontalSpacer16() = HorizontalSpacer(R.dimen.padding_medium)

@Composable
fun HorizontalSpacer24() = HorizontalSpacer(R.dimen.padding_large)

@Composable
fun HorizontalSpacer32() = HorizontalSpacer(R.dimen.padding_larger)

@Composable
fun HorizontalSpacer40() = HorizontalSpacer(R.dimen.padding_largest)

@Composable
fun HorizontalSpacer64() = HorizontalSpacer(R.dimen.padding_huge)

@Composable
fun Divider(modifier: Modifier = Modifier) {
    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(color = MaterialTheme.colorScheme.surface)
    )
}