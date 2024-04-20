package ua.com.andromeda.wordgalaxy.menu.presentation.about

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.core.presentation.components.DefaultClickableText
import ua.com.andromeda.wordgalaxy.core.presentation.components.VerticalSpacer
import ua.com.andromeda.wordgalaxy.core.presentation.components.appendDefaultText
import ua.com.andromeda.wordgalaxy.core.presentation.components.pushUrlAnnotation
import ua.com.andromeda.wordgalaxy.menu.presentation.about.components.AboutAppAlertDialog
import ua.com.andromeda.wordgalaxy.menu.presentation.about.components.AboutBottomAppBar
import ua.com.andromeda.wordgalaxy.menu.presentation.about.components.AboutTopAppBar
import ua.com.andromeda.wordgalaxy.menu.presentation.about.components.SpacedRepetitionAlertDialog

@Composable
fun AboutScreen(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        modifier = modifier,
        topBar = {
            AboutTopAppBar(navigateUp)
        },
        bottomBar = {
            AboutBottomAppBar(snackbarHostState)
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { innerPadding ->
        AboutMain(modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun AboutMain(modifier: Modifier = Modifier) {
    val viewModel: AboutViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val versionName = context.packageManager
        .getPackageInfo(
            context.packageName,
            0
        )
        .versionName
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(R.drawable.app_logo),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.clip(MaterialTheme.shapes.medium)
        )
        Text(
            text = stringResource(R.string.version, versionName),
            modifier = Modifier.padding(vertical = dimensionResource(R.dimen.padding_mediumish)),
        )
        DescriptionRow(
            expandAboutApp = viewModel::expandAboutApp,
            expandAboutSpacedRepetition = viewModel::expandAboutSpacedRepetition,
            modifier = Modifier.fillMaxWidth(),
        )
        AboutCard(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_mediumish))
        )
    }
    Dialogs(state, viewModel)
}

@Composable
private fun AboutCard(modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(dimensionResource(R.dimen.padding_mediumish))) {
            Text(text = stringResource(R.string.issues_with_app))
            VerticalSpacer(R.dimen.padding_mediumish)
            DefaultClickableText(
                text = buildSupportText(),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun buildSupportText() = buildAnnotatedString {
    appendDefaultText("\uD83D\uDCB8If you find this app useful you can ")
    pushUrlAnnotation(
        url = stringResource(R.string.buy_me_a_coffee_link),
        text = "Buy me a coffee."
    )
    appendDefaultText(" This definitely helps me work on this project harder\uD83D\uDE09")
}

@Composable
private fun Dialogs(
    state: AboutUiState,
    viewModel: AboutViewModel,
) {
    val configuration = LocalConfiguration.current
    val dialogHeight = configuration.screenHeightDp.dp / 1.25f

    AnimatedVisibility(visible = state.aboutAppExpanded) {
        AboutAppAlertDialog(
            onDismissRequest = viewModel::expandAboutApp,
            modifier = Modifier.heightIn(min = 0.dp, max = dialogHeight),
        )
    }
    AnimatedVisibility(visible = state.aboutSpacedRepetitionExpanded) {
        SpacedRepetitionAlertDialog(
            onDismissRequest = viewModel::expandAboutSpacedRepetition,
            modifier = Modifier.heightIn(min = 0.dp, max = dialogHeight),
        )
    }
}

@Composable
private fun DescriptionRow(
    expandAboutApp: (Boolean) -> Unit,
    expandAboutSpacedRepetition: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Absolute.SpaceEvenly
    ) {
        Button(onClick = { expandAboutApp(true) }) {
            Text(text = stringResource(R.string.about_this_app))
        }
        Button(onClick = { expandAboutSpacedRepetition(true) }) {
            Text(text = "Spaced repetition")
        }
    }
}

