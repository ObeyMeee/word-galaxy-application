package ua.com.andromeda.wordgalaxy.menu.presentation.about.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.core.presentation.components.DefaultClickableText
import ua.com.andromeda.wordgalaxy.core.presentation.components.HorizontalSpacer
import ua.com.andromeda.wordgalaxy.core.presentation.components.appendDefaultText
import ua.com.andromeda.wordgalaxy.core.presentation.components.pushUrlAnnotation

@Composable
fun AboutAppAlertDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            OutlinedButton(onClick = onDismissRequest) {
                Text(text = stringResource(R.string.cancel))
            }
        },
        modifier = modifier,
        icon = {
            Icon(
                imageVector = Icons.Outlined.Favorite,
                contentDescription = null,
            )
        },
        title = { Text(text = stringResource(R.string.about_this_app)) },
        text = {
            Column(modifier = Modifier.verticalScroll(scrollState)) {
                val textModifier = Modifier.padding(
                    bottom = dimensionResource(R.dimen.padding_mediumish)
                )
                Text(
                    text = stringResource(R.string.about_this_app_description),
                    modifier = textModifier,
                )
                val specialThanksColor = MaterialTheme.colorScheme.secondary
                BasicText(
                    text = buildSpecialThanks(),
                    modifier = textModifier,
                    color = { specialThanksColor },
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = stringResource(R.string.rate_this_app),
                    modifier = textModifier,
                )
                DefaultClickableText(
                    text = buildThanksToOpenResources(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    )
}

private fun buildSpecialThanks() = buildAnnotatedString {
    append("✨Окрема подяка ")
    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
        append("Пані Лізі")
    }
    append(", яка ")
    withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
        append("\"просто обирала кольори\"")
    }
    append(". Без тебе застосунок не був би такий гарний✌\uFE0F")
}

@Composable
fun buildThanksToOpenResources() = buildAnnotatedString {
    appendDefaultText("🙏All icons being used in app I came from material design and ")
    pushUrlAnnotation(stringResource(R.string.flaticon_link), "Flaticon")
    appendDefaultText(", specifically created by ")
    pushUrlAnnotation(stringResource(R.string.freepik_link), "Freepik")
    appendDefaultText(".\n\n🫂Thanks to ")
    pushUrlAnnotation(stringResource(R.string.dictionaryapi_link), "Dictionary API")
    appendDefaultText(" and ")
    pushUrlAnnotation(stringResource(R.string.detect_language_link), "Detect language API")
    appendDefaultText(" for your free api. I was trying not to spam too many requests with respect\uD83D\uDE00\n\n📕Translations are come from ")
    pushUrlAnnotation(stringResource(R.string.dict_link))
    appendDefaultText(" and ")
    pushUrlAnnotation(stringResource(R.string.cambridge_dictionary_link))
}

@Composable
fun SpacedRepetitionAlertDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    val benefits = listOf(
        stringResource(R.string.spaced_repetition_benefit1),
        stringResource(R.string.spaced_repetition_benefit2),
        stringResource(R.string.spaced_repetition_benefit3),
    )

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            OutlinedButton(onClick = onDismissRequest) {
                Text(text = stringResource(R.string.cancel))
            }
        },
        modifier = modifier,
        icon = {
            Icon(
                imageVector = Icons.Outlined.Lightbulb,
                contentDescription = null,
            )
        },
        title = {
            Text(text = stringResource(R.string.how_is_spaced_repetition_effective))
        },
        text = {
            Column(modifier = Modifier.verticalScroll(scrollState)) {
                Text(text = stringResource(R.string.spaced_repetition_description))
                benefits.forEach {
                    BenefitItem(text = it)
                }
            }
        },
    )
}

@Composable
fun BenefitItem(
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.padding(vertical = dimensionResource(R.dimen.padding_small))) {
        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.Green)
        HorizontalSpacer(R.dimen.padding_small)
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
