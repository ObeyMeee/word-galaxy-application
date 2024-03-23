package ua.com.andromeda.wordgalaxy.ui.screens.start.menu.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.utils.openLink
import ua.com.andromeda.wordgalaxy.utils.sendEmail


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AboutTopAppBar(navigateUp: () -> Unit, modifier: Modifier = Modifier) {
    TopAppBar(
        title = { Text(text = stringResource(R.string.about_us)) },
        navigationIcon = {
            IconButton(onClick = navigateUp) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                )
            }
        },
        modifier = modifier,
    )
}

@Composable
fun AboutBottomAppBar(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val telegramLink = stringResource(R.string.telegram_link)
    val instagramLink = stringResource(R.string.instagram_link)
    val buyMeACoffeeLink = stringResource(R.string.buy_me_a_coffee_link)
    val playstoreLink = stringResource(R.string.playstore_link)
    val supportEmail = stringResource(R.string.support_email)
    val scope = rememberCoroutineScope()
    val versionName = context.packageManager
        .getPackageInfo(
            context.packageName,
            0
        ).versionName
    BottomAppBar(modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            SocialItem(
                onClick = {
                    context.openLink(playstoreLink)
                },
                painter = painterResource(R.drawable.menu_playstore_icon),
                contentDescription = "Play store",
                tint = Color.Unspecified,
            )
            SocialItem(
                onClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar("Coming soon!")
                    }
//                    context.openLink("")
                },
                painter = painterResource(R.drawable.web_icon),
                contentDescription = "Web site",
            )
            SocialItem(
                onClick = { context.openLink(buyMeACoffeeLink) },
                painter = rememberVectorPainter(Icons.Outlined.Coffee),
                contentDescription = "Buy me a coffee",
            )
            SocialItem(
                onClick = { context.sendEmail(supportEmail, "Version $versionName") },
                painter = rememberVectorPainter(Icons.Outlined.Email),
                contentDescription = "Send email",
            )
            SocialItem(
                onClick = { context.openLink(instagramLink) },
                painter = painterResource(R.drawable.instagram_icon),
                contentDescription = "Instagram page",
            )
            SocialItem(
                onClick = { context.openLink(telegramLink) },
                painter = painterResource(R.drawable.telegram_icon),
                contentDescription = "Telegram channel",
            )
        }
    }
}

@Composable
private fun SocialItem(
    onClick: () -> Unit,
    painter: Painter,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    tint: Color = MaterialTheme.colorScheme.onPrimaryContainer,

    ) {
    IconButton(
        onClick = onClick,
        modifier = modifier.background(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = MaterialTheme.shapes.extraLarge,
        ),
    ) {
        Icon(
            painter = painter,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(dimensionResource(R.dimen.icon_size_default))
        )
    }
}
