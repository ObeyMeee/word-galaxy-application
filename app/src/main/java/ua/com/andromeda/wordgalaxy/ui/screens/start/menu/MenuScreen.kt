package ua.com.andromeda.wordgalaxy.ui.screens.start.menu

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.preference.Preference
import com.chillibits.simplesettings.core.SimpleSettings
import com.chillibits.simplesettings.core.SimpleSettingsConfig
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.ui.DEFAULT_AMOUNT_WORDS_TO_LEARN_PER_DAY
import ua.com.andromeda.wordgalaxy.ui.KEY_AMOUNT_WORDS_TO_LEARN_PER_DAY
import ua.com.andromeda.wordgalaxy.ui.MAX_AMOUNT_WORDS_TO_LEARN_PER_DAY
import ua.com.andromeda.wordgalaxy.ui.MIN_AMOUNT_WORDS_TO_LEARN_PER_DAY

@Composable
fun MenuScreen(
    navigateTo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()
    val configuration = SimpleSettingsConfig
        .Builder()
        .showResetOption(true)
        .setActivityTitle("Menu")
        .displayHomeAsUpEnabled(true)
        .build()
    // TODO: add real url
    val appUri = "https://play.google.com/store/apps/details?id=ua.com.andromeda.wordgalaxy"

    SimpleSettings(context, configuration).show {
        Section {
            Page {
                title = "Settings"
                icon = R.drawable.menu_settings_icon
                Section {
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
                            icon = R.drawable.menu_animation_icon
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
                            icon = R.drawable.menu_transcription_icon
                            defaultValue = true
                        }
                        SwitchPref {
                            title = "Automatically pronounce English words"
                            key = "automatically_pronounce_english_words"
                            icon = R.drawable.menu_pronounce_english_words_icon
                            defaultValue = true
                        }
                        SeekBarPref {
                            title = "How many words per day you want to learn?"
                            key = KEY_AMOUNT_WORDS_TO_LEARN_PER_DAY
                            min = MIN_AMOUNT_WORDS_TO_LEARN_PER_DAY
                            max = MAX_AMOUNT_WORDS_TO_LEARN_PER_DAY
                            defaultValue = DEFAULT_AMOUNT_WORDS_TO_LEARN_PER_DAY
                            showValue = true
                            icon = R.drawable.bulb_icon
                            iconSpaceReserved = true
                        }
                    }
                }
            }
            TextPref {
                title = "Share"
                summary = "Tap to share the app"
                icon = R.drawable.menu_share_icon
                onClick = Preference.OnPreferenceClickListener {
                    context.shareLink(appUri)
                    true
                }
            }
            TextPref {
                title = "Rate"
                summary = "Tap to show our PlayStore page"
                icon = R.drawable.menu_playstore_icon
                onClick = GoToWebsiteClickListener(context, appUri)
            }
            LibsPref {
                title = "libs"
                edgeToEdge = true
                aboutAppNameRes = R.string.app_name
                aboutAppSpecial1 = "About app special 1"
                aboutAppSpecial2 = "About app special 2"
                aboutAppSpecial3 = "About app special 3"
                aboutAppSpecial1Description = "About app special 1 description"
                aboutAppSpecial2Description = "About app special 2 description"
                aboutAppSpecial3Description = "About app special 3 description"
                aboutShowIcon = true
                aboutShowVersion = true
                aboutShowVersionCode = true
                aboutShowVersionName = true
                aboutVersionString = "about version string"
                showLicense = true
                showLicenseDialog = true
                showLoadingProgress = true
                showVersion = true
                sort = true
            }
            TextPref {
                title = "Support"
                summary = "Tap to go to the our support page"
                icon = R.drawable.menu_support_icon
                onClick = GoToWebsiteClickListener(context, appUri)
            }
        }
    }
}

fun Context.shareLink(uriString: String) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_TEXT, uriString)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    startActivity(shareIntent)
}

class GoToWebsiteClickListener(
    private val context: Context,
    private val uriString: String,
) : Preference.OnPreferenceClickListener {
    override fun onPreferenceClick(preference: Preference): Boolean {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(uriString)
        )
        context.startActivity(intent)
        return true
    }
}