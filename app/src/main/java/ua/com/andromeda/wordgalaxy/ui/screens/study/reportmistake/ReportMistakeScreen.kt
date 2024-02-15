package ua.com.andromeda.wordgalaxy.ui.screens.study.reportmistake

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ua.com.andromeda.wordgalaxy.R

@Composable
fun ReportMistakeScreen(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            ReportMistakeTopAppBar(navigateUp = navigateUp)
        },
        modifier = modifier
    ) { innerPadding ->
        ReportMistakeMain(modifier = Modifier.padding(innerPadding))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReportMistakeTopAppBar(
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(text = stringResource(R.string.report_a_mistake))
        },
        navigationIcon = {
            IconButton(onClick = navigateUp) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        },
        modifier = modifier
    )
}

@Composable
private fun ReportMistakeMain(
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text(text = stringResource(R.string.suggest_a_correction))

    }
}