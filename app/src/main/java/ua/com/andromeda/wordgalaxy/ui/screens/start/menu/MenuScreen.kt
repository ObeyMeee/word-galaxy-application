package ua.com.andromeda.wordgalaxy.ui.screens.start.menu

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.jamal.composeprefs3.ui.PrefsScope
import com.jamal.composeprefs3.ui.PrefsScreen
import com.jamal.composeprefs3.ui.prefs.TextPref
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.data.local.dataStore
import ua.com.andromeda.wordgalaxy.ui.navigation.Destination
import ua.com.andromeda.wordgalaxy.utils.openLink
import ua.com.andromeda.wordgalaxy.utils.shareLink

@Composable
fun MenuScreen(
    navigateTo: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val appUri = stringResource(R.string.playstore_link)
    val iconModifier = Modifier.size(dimensionResource(R.dimen.icon_size_default))

    PrefsScreen(dataStore = context.dataStore, modifier = modifier) {
        settingsItem(
            onClick = { navigateTo(Destination.Start.MenuScreen.SettingsScreen()) },
            iconModifier = iconModifier,
        )
        shareItem(
            onClick = { context.shareLink(appUri) },
            iconModifier = iconModifier,
        )
        rateItem(
            onClick = { context.openLink(appUri) },
            iconModifier = iconModifier,
        )
        aboutItem(
            onClick = { navigateTo(Destination.Start.MenuScreen.AboutScreen()) },
            iconModifier = iconModifier,
        )
        supportItem(
            onClick = {
                context.openLink("https://github.com/ObeyMeee/word-galaxy-application")
            },
            iconModifier = iconModifier,
        )
    }
}


private fun PrefsScope.settingsItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
) {
    prefsItem {
        TextPref(
            title = stringResource(R.string.settings),
            onClick = onClick,
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.menu_settings_icon),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = iconModifier,
                )
            },
            modifier = modifier,
            enabled = true,
        )
    }
}

private fun PrefsScope.shareItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
) {
    prefsItem {
        TextPref(
            title = stringResource(R.string.share),
            summary = stringResource(R.string.tap_to_share_the_app),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    modifier = iconModifier,
                )
            },
            modifier = modifier,
            enabled = true,
            onClick = onClick,
        )
    }
}

private fun PrefsScope.rateItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
) {
    prefsItem {
        TextPref(
            title = stringResource(R.string.rate_us),
            summary = stringResource(R.string.tap_to_show_our_playstore_page),
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.menu_playstore_icon),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = iconModifier,
                )
            },
            modifier = modifier,
            enabled = true,
            onClick = onClick,
        )
    }
}

private fun PrefsScope.aboutItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
) {
    prefsItem {
        TextPref(
            title = stringResource(R.string.about_us),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = iconModifier,
                )
            },
            enabled = true,
            modifier = modifier,
            onClick = onClick,
        )
    }
}

private fun PrefsScope.supportItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier
) {
    prefsItem {
        TextPref(
            title = stringResource(R.string.support),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.HelpCenter,
                    contentDescription = null,
                    modifier = iconModifier,
                )
            },
            modifier = modifier,
            summary = stringResource(R.string.tap_to_go_to_our_support_page),
            enabled = true,
            onClick = onClick,
        )
    }
}