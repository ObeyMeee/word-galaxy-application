package ua.com.andromeda.wordgalaxy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.chillibits.simplesettings.tool.getPrefObserver
import dagger.hilt.android.AndroidEntryPoint
import ua.com.andromeda.wordgalaxy.ui.screens.WordGalaxyApp
import ua.com.andromeda.wordgalaxy.ui.theme.WordGalaxyTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isDarkTheme by remember {
                mutableStateOf(false)
            }
            getPrefObserver(
                context = applicationContext,
                name = "dark_theme",
                observer = { isDarkTheme = it },
                default = isSystemInDarkTheme()
            )
            WordGalaxyTheme(darkTheme = isDarkTheme) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WordGalaxyApp()
                }
            }
        }
    }
}