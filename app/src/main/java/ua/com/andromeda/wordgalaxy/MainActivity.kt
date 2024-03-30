package ua.com.andromeda.wordgalaxy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import dagger.hilt.android.AndroidEntryPoint
import ua.com.andromeda.wordgalaxy.data.local.PreferenceDataStoreConstants.KEY_DARK_THEME
import ua.com.andromeda.wordgalaxy.data.local.PreferenceDataStoreHelper
import ua.com.andromeda.wordgalaxy.ui.navigation.WordGalaxyNavHost
import ua.com.andromeda.wordgalaxy.ui.theme.WordGalaxyTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var dataStoreHelper: PreferenceDataStoreHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isSystemInDarkTheme = isSystemInDarkTheme()
            var isDarkTheme by remember {
                mutableStateOf(isSystemInDarkTheme)
            }
            LaunchedEffect(Unit) {
                dataStoreHelper
                    .get(KEY_DARK_THEME, isSystemInDarkTheme)
                    .collect { isDarkTheme = it }
            }
            WordGalaxyTheme(darkTheme = isDarkTheme) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WordGalaxyNavHost(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dimensionResource(R.dimen.padding_small))
                    )
                }
            }
        }
    }
}