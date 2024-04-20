package ua.com.andromeda.wordgalaxy.core.presentation.ui.reportmistake

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.core.presentation.components.CenteredLoadingSpinner
import ua.com.andromeda.wordgalaxy.core.presentation.components.Message
import ua.com.andromeda.wordgalaxy.core.presentation.components.TitledTopAppBar
import ua.com.andromeda.wordgalaxy.core.presentation.components.VerticalSpacer

@Composable
fun ReportMistakeScreen(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: ReportMistakeViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TitledTopAppBar(
                titleRes = R.string.report_a_mistake,
                navigateUp = navigateUp
            )
        },
        floatingActionButton = {
            if (uiState is ReportMistakeUiState.Success) {
                val successState = uiState as ReportMistakeUiState.Success
                val successfulMessage = stringResource(
                    R.string.report_on_word_has_been_sent_successfully,
                    successState.report.word
                )

                ReportMistakeSendButton(
                    enabled = successState.isFormValid && !successState.isReportSending,
                    modifier = Modifier.defaultMinSize(minWidth = 56.dp, minHeight = 56.dp)
                ) {
                    viewModel.send()
                    navigateUp()
                    Toast.makeText(context, successfulMessage, Toast.LENGTH_LONG).show()
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        modifier = modifier
    ) { innerPadding ->
        ReportMistakeMain(modifier = Modifier.padding(innerPadding))
    }
}

@Composable
private fun ReportMistakeSendButton(
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
    ) {
        Icon(imageVector = Icons.Default.Send, contentDescription = null)
        Text(
            text = stringResource(R.string.send),
            modifier = Modifier.padding(
                start = dimensionResource(R.dimen.padding_medium)
            )
        )
    }
}

@Composable
private fun ReportMistakeMain(
    modifier: Modifier = Modifier,
    viewModel: ReportMistakeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is ReportMistakeUiState.Default -> {
            CenteredLoadingSpinner()
        }

        is ReportMistakeUiState.Error -> {
            ReportMistakeErrorMessage(state.message)
        }

        is ReportMistakeUiState.Success -> {
            ReportMistakeForm(report = state.report, modifier = modifier)
        }
    }
}

@Composable
private fun ReportMistakeForm(
    report: Report,
    modifier: Modifier = Modifier,
    viewModel: ReportMistakeViewModel = hiltViewModel()
) {
    val transparent = Color.Transparent
    val fillMaxWidth = Modifier.fillMaxWidth()

    Column(modifier) {
        Text(text = stringResource(R.string.suggest_a_correction))
        VerticalSpacer(R.dimen.padding_medium)

        ReportMistakeTextField(
            value = report.word,
            onValueChange = { viewModel.updateWordInput(it) },
            labelRes = R.string.word,
            indicatorColor = transparent,
            modifier = fillMaxWidth
        )
        VerticalSpacer(R.dimen.padding_small)

        ReportMistakeTextField(
            value = report.translation,
            onValueChange = { viewModel.updateTranslationInput(it) },
            labelRes = R.string.translation,
            indicatorColor = transparent,
            modifier = fillMaxWidth
        )
        VerticalSpacer(R.dimen.padding_larger)

        CommentTextField(
            value = report.comment ?: "",
            onValueChange = viewModel::updateComment,
            indicatorColor = transparent,
            modifier = fillMaxWidth
        )
    }
}

@Composable
private fun ReportMistakeErrorMessage(message: String) {
    Message(
        message = message,
        backgroundColor = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.fillMaxSize()
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun ReportMistakeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    @StringRes labelRes: Int,
    indicatorColor: Color,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(text = "${stringResource(labelRes)}*")
        },
        trailingIcon = {
            IconButton(onClick = { onValueChange("") }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.clear_input)
                )
            }
        },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = indicatorColor,
            unfocusedIndicatorColor = indicatorColor,
            disabledIndicatorColor = indicatorColor
        ),
        shape = MaterialTheme.shapes.medium,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        modifier = modifier
    )
}

@Composable
private fun CommentTextField(
    value: String,
    onValueChange: (String) -> Unit,
    indicatorColor: Color,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(text = stringResource(R.string.comment_optional))
        },
        minLines = 10,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = indicatorColor,
            unfocusedIndicatorColor = indicatorColor,
            disabledIndicatorColor = indicatorColor
        ),
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
    )
}