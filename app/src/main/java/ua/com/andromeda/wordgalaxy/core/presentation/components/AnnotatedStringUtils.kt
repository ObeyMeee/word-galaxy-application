package ua.com.andromeda.wordgalaxy.core.presentation.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.UrlAnnotation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import ua.com.andromeda.wordgalaxy.utils.openLink

@OptIn(ExperimentalTextApi::class)
@Composable
fun DefaultClickableText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    softWrap: Boolean = true,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
) {
    val context = LocalContext.current
    androidx.compose.foundation.text.ClickableText(
        text = text,
        modifier = modifier,
        style = style,
        softWrap = softWrap,
        overflow = overflow,
        maxLines = maxLines,
        onTextLayout = onTextLayout,
    ) { offset ->
        text.getUrlAnnotations(
            start = offset,
            end = offset
        ).firstOrNull()?.let {
            context.openLink(it.item.url)
        }
    }
}

@Composable
@OptIn(ExperimentalTextApi::class)
fun AnnotatedString.Builder.pushUrlAnnotation(url: String, text: String = url) {
    pushUrlAnnotation(UrlAnnotation(url))
    appendLink(text)
    pop()
}

@Composable
private fun AnnotatedString.Builder.appendLink(
    text: String,
    style: SpanStyle = SpanStyle(
        color = MaterialTheme.colorScheme.primary,
        textDecoration = TextDecoration.Underline,
        fontWeight = FontWeight.Bold,
    )
) = withStyle(style) {
    append(text)
}

@Composable
fun AnnotatedString.Builder.appendDefaultText(
    text: String,
    style: SpanStyle = SpanStyle(color = MaterialTheme.colorScheme.secondary)
): Unit = withStyle(style) {
    append(text)
}
