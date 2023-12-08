package ua.com.andromeda.wordgalaxy.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.ui.navigation.WordGalaxyNavHost


@Composable
fun WordGalaxyApp() {
    WordGalaxyNavHost(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_small))
    )
}