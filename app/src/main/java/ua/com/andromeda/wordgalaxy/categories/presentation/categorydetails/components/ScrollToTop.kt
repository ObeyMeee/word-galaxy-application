package ua.com.andromeda.wordgalaxy.categories.presentation.categorydetails.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.launch
import ua.com.andromeda.wordgalaxy.R

private const val ANIMATION_DURATION_MILLIS = 400

@Composable
fun ScrollToTop(
    listState: LazyListState,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val slideAnimationSpec = tween<IntOffset>(ANIMATION_DURATION_MILLIS)
    val buttonHeight = dimensionResource(R.dimen.scroll_to_top_button_height)

    AnimatedVisibility(
        visible = listState.isScrollingUp() && listState.isScrollInProgress,
        modifier = modifier,
        enter = slideInVertically(slideAnimationSpec) { -it },
        exit = slideOutVertically(slideAnimationSpec) { -it },
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        listState.animateScrollToItem(0)
                    }
                },
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_medium))
                    .size(buttonHeight)
                    .absoluteOffset(y = buttonHeight * 1.5f)
                    .align(Alignment.TopCenter),
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = stringResource(R.string.scroll_to_the_top)
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