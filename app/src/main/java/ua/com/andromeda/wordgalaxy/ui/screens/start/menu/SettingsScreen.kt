package ua.com.andromeda.wordgalaxy.ui.screens.start.menu

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.jamal.composeprefs3.ui.GroupHeader
import com.jamal.composeprefs3.ui.PrefsScope
import com.jamal.composeprefs3.ui.PrefsScreen
import com.jamal.composeprefs3.ui.prefs.ListPref
import com.jamal.composeprefs3.ui.prefs.SwitchPref
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.data.local.PreferenceDataStoreConstants.AMOUNT_WORDS_TO_LEARN_PER_DAY
import ua.com.andromeda.wordgalaxy.data.local.PreferenceDataStoreConstants.ANIMATION_ENABLED
import ua.com.andromeda.wordgalaxy.data.local.PreferenceDataStoreConstants.DARK_THEME
import ua.com.andromeda.wordgalaxy.data.local.PreferenceDataStoreConstants.DEFAULT_AMOUNT_WORDS_TO_LEARN_PER_DAY
import ua.com.andromeda.wordgalaxy.data.local.PreferenceDataStoreConstants.DEFAULT_NOTIFICATIONS_FREQUENCY
import ua.com.andromeda.wordgalaxy.data.local.PreferenceDataStoreConstants.MAX_AMOUNT_WORDS_TO_LEARN_PER_DAY
import ua.com.andromeda.wordgalaxy.data.local.PreferenceDataStoreConstants.MAX_DEFAULT_NOTIFICATIONS_FREQUENCY
import ua.com.andromeda.wordgalaxy.data.local.PreferenceDataStoreConstants.MIN_AMOUNT_WORDS_TO_LEARN_PER_DAY
import ua.com.andromeda.wordgalaxy.data.local.PreferenceDataStoreConstants.MIN_DEFAULT_NOTIFICATIONS_FREQUENCY
import ua.com.andromeda.wordgalaxy.data.local.PreferenceDataStoreConstants.NOTIFICATIONS_FREQUENCY
import ua.com.andromeda.wordgalaxy.data.local.PreferenceDataStoreConstants.PRONOUNCE_ENGLISH_WORDS
import ua.com.andromeda.wordgalaxy.data.local.PreferenceDataStoreConstants.TRANSCRIPTIONS_ENABLED
import ua.com.andromeda.wordgalaxy.data.local.dataStore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.settings))
                },
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                })
        },
        modifier = modifier,
    ) { innerPadding ->
        SettingsMain(modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun SettingsMain(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    PrefsScreen(
        dataStore = context.dataStore,
        modifier = modifier
    ) {
        appearanceGroup()
        generalGroup()
        notificationsGroup()
    }
}

private fun PrefsScope.appearanceGroup() {
    prefsGroup({
        GroupHeader(
            title = stringResource(R.string.appearance),
        )
    }) {
        prefsItem {
            SwitchPref(
                title = "Dark theme",
                summary = "Tap to change theme",
                key = DARK_THEME,
                defaultChecked = isSystemInDarkTheme(),
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.night_mode_icon),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(dimensionResource(R.dimen.icon_size_default)),
                    )
                },
            )
        }
        prefsItem {
            SwitchPref(
                title = "Turn on animations",
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.menu_animation_icon),
                        contentDescription = null,
                        modifier = Modifier.size(dimensionResource(R.dimen.icon_size_default)),
                    )
                },
                summary = "Tap to turn on/off animations",
                key = ANIMATION_ENABLED,
                defaultChecked = true,
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
private fun PrefsScope.generalGroup() {
    prefsGroup({
        GroupHeader(
            title = stringResource(R.string.general),
        )
    }) {
        prefsItem {
            SwitchPref(
                title = "Show transcriptions",
                key = TRANSCRIPTIONS_ENABLED,
                defaultChecked = true,
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.menu_transcription_icon),
                        contentDescription = null,
                        modifier = Modifier.size(dimensionResource(R.dimen.icon_size_default)),
                    )
                },
            )
        }
        prefsItem {
            SwitchPref(
                title = "Automatically pronounce English words",
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.menu_pronounce_english_words_icon),
                        contentDescription = null,
                        modifier = Modifier.size(dimensionResource(R.dimen.icon_size_default)),
                    )
                },
                key = PRONOUNCE_ENGLISH_WORDS,
                defaultChecked = true,
            )
        }
        prefsItem {
            ListPref(
                key = AMOUNT_WORDS_TO_LEARN_PER_DAY,
                title = "How many words per day you want to learn?",
                useSelectedAsSummary = true,
                defaultValue = DEFAULT_AMOUNT_WORDS_TO_LEARN_PER_DAY.toString(),
                entries = (MIN_AMOUNT_WORDS_TO_LEARN_PER_DAY..MAX_AMOUNT_WORDS_TO_LEARN_PER_DAY).associate { value ->
                    val valueStr = value.toString()
                    valueStr to valueStr
                }
            )
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
private fun PrefsScope.notificationsGroup() {
    prefsGroup({
        GroupHeader(
            title = stringResource(R.string.notifications),
        )
    }) {
        prefsItem {
            ListPref(
                key = NOTIFICATIONS_FREQUENCY,
                title = "How often you want to receive notifications?",
                useSelectedAsSummary = true,
                defaultValue = DEFAULT_NOTIFICATIONS_FREQUENCY.toString(),
                entries = (MIN_DEFAULT_NOTIFICATIONS_FREQUENCY..MAX_DEFAULT_NOTIFICATIONS_FREQUENCY)
                    .associate {
                        it.toString() to "Once in $it hours"
                    }
            )
        }
    }
}