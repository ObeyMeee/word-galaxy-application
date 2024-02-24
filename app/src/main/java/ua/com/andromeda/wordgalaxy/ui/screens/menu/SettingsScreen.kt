package ua.com.andromeda.wordgalaxy.ui.screens.menu

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.chillibits.simplesettings.clicklistener.DialogClickListener
import com.chillibits.simplesettings.core.SimpleSettings
import com.chillibits.simplesettings.core.SimpleSettingsConfig
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.ui.DEFAULT_AMOUNT_WORDS_TO_LEARN_PER_DAY
import ua.com.andromeda.wordgalaxy.ui.KEY_AMOUNT_WORDS_TO_LEARN_PER_DAY

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()
    val configuration = SimpleSettingsConfig
        .Builder()
        .showResetOption(true)
        .displayHomeAsUpEnabled(true)
        .build()
    SimpleSettings(context, configuration).show {
        Section {
            title = "Appearance"

            SwitchPref {
                title = "Dark theme"
                summary = "Tap to change theme"
                key = "dark_theme"
                defaultValue = isDarkTheme
                icon = R.drawable.night_mode_icon
            }
            SwitchPref {
                title = "Turn on animations"
                summary = "Tap to turn on/off animations"
                key = "animation_enabled"
                defaultValue = true
            }
        }
        Section {
            title = "General"
            SwitchPref {
                title = "Show transcriptions"
                key = "transcriptions_enabled"
                defaultValue = true
            }
            SeekBarPref {
                title = "How many words per day you want to learn"
                key = KEY_AMOUNT_WORDS_TO_LEARN_PER_DAY
                summary = "Summary"
                min = 1
                max = 50
                defaultValue = DEFAULT_AMOUNT_WORDS_TO_LEARN_PER_DAY
                showValue = true
                icon = R.drawable.bulb_icon
                iconSpaceReserved = true
            }
            TextPref {
                title = "Dialog"
                summary = "Tap to show alert dialog"
                onClick = DialogClickListener("Test", "This is a test", DialogClickListener.Type.OK)
            }
        }
    }
}