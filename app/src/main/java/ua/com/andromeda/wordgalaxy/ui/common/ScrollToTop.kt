package ua.com.andromeda.wordgalaxy.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ua.com.andromeda.wordgalaxy.ui.SCROLL_TO_THE_TOP_ANIMATION_DURATION_MILLIS

@Composable
fun ScrollToTop(
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    action: () -> Unit = {}
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = slideInVertically(tween(SCROLL_TO_THE_TOP_ANIMATION_DURATION_MILLIS)) { it }
                + fadeIn(tween(SCROLL_TO_THE_TOP_ANIMATION_DURATION_MILLIS), .4f),
        exit = slideOutVertically(tween(SCROLL_TO_THE_TOP_ANIMATION_DURATION_MILLIS)) { it }
                + fadeOut(tween(SCROLL_TO_THE_TOP_ANIMATION_DURATION_MILLIS), .4f)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            FloatingActionButton(
                modifier = Modifier
                    .padding(16.dp)
                    .size(50.dp)
                    .align(Alignment.BottomEnd),
                onClick = action
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowUpward, contentDescription = "scroll to top"
                )
            }
        }
    }
}

@Composable
fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember {
        mutableIntStateOf(firstVisibleItemIndex)
    }
    var previousScrollOffset by remember {
        mutableIntStateOf(firstVisibleItemScrollOffset)
    }

    return remember {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}