package ua.com.andromeda.wordgalaxy.ui.navigation

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.ui.theme.WordGalaxyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartTopAppBar(modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(
        title = {
            Image(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier.height(110.dp)
            )
        },
        modifier = modifier
    )
}


@Preview
@Composable
fun StartTopAppBarPreview() {
    WordGalaxyTheme {
        Surface {
            StartTopAppBar(
                modifier = Modifier.padding(
                    dimensionResource(R.dimen.padding_small)
                )
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun StartTopAppBarDarkPreview() {
    WordGalaxyTheme {
        Surface {
            StartTopAppBar()
        }
    }
}