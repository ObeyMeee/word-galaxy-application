package ua.com.andromeda.wordgalaxy.ui.screens.study.reportmistake

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
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
import ua.com.andromeda.wordgalaxy.ui.common.CenteredLoadingSpinner
import ua.com.andromeda.wordgalaxy.ui.common.Message

@Composable
fun ReportMistakeScreen(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: ReportMistakeViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val success = uiState as? ReportMistakeUiState.Success
    val successfulMessage = stringResource(
        R.string.report_on_word_has_been_sent_successfully,
        success?.report?.word ?: ""
    )

    Scaffold(
        topBar = {
            ReportMistakeTopAppBar(navigateUp = navigateUp)
        },
        floatingActionButton = {
            Button(
                onClick = {
                    viewModel.send()
                    navigateUp()
                    Toast.makeText(context, successfulMessage, Toast.LENGTH_LONG).show()
                },
                modifier = Modifier.defaultMinSize(minWidth = 56.dp, minHeight = 56.dp),
                enabled = success?.isFormValid ?: false && success?.isReportSending?.not() ?: false,
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = null)
                Text(
                    text = stringResource(R.string.send),
                    modifier = Modifier.padding(
                        start = dimensionResource(R.dimen.padding_medium)
                    )
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
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
    val viewModel: ReportMistakeViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is ReportMistakeUiState.Default -> {
            CenteredLoadingSpinner()
        }

        is ReportMistakeUiState.Error -> {
            Message(
                message = state.message,
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

        is ReportMistakeUiState.Success -> {
            val transparent = Color.Transparent
            val (word, translation, comment) = state.report

            Column(modifier) {
                Text(text = stringResource(R.string.suggest_a_correction))
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))

                val fillMaxWidth = Modifier.fillMaxWidth()
                ReportMistakeTextField(
                    value = word,
                    onValueChange = viewModel::updateWordInput,
                    labelRes = R.string.word,
                    modifier = fillMaxWidth
                )
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))
                ReportMistakeTextField(
                    value = translation,
                    onValueChange = viewModel::updateTranslationInput,
                    labelRes = R.string.translation,
                    modifier = fillMaxWidth
                )
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_large)))
                TextField(
                    value = comment ?: "",
                    onValueChange = viewModel::updateComment,
                    label = {
                        Text(text = stringResource(R.string.comment_optional))
                    },
                    minLines = 10,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = transparent,
                        unfocusedIndicatorColor = transparent,
                        disabledIndicatorColor = transparent
                    ),
                    shape = MaterialTheme.shapes.medium,
                    modifier = fillMaxWidth
                )
            }
        }
    }
}

@Composable
private fun ReportMistakeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    @StringRes labelRes: Int,
    modifier: Modifier = Modifier
) {
    val transparent = Color.Transparent
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
            focusedIndicatorColor = transparent,
            unfocusedIndicatorColor = transparent,
            disabledIndicatorColor = transparent
        ),
        shape = MaterialTheme.shapes.medium,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        modifier = modifier
    )
}