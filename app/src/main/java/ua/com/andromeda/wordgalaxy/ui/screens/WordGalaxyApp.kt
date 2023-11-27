package ua.com.andromeda.wordgalaxy.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.ui.navigation.WordGalaxyNavHost


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordGalaxyApp() {
    Scaffold(topBar = { WordGalaxyTopAppBar() }) { innerPadding ->
        WordGalaxyNavHost(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordGalaxyTopAppBar() {
    CenterAlignedTopAppBar(title = {
        Image(
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = null
        )
    })
}
