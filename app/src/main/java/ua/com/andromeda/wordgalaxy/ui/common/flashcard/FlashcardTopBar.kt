package ua.com.andromeda.wordgalaxy.ui.common.flashcard

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.ui.navigation.Destination


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardTopBar(
    amountWordsToReview: Int,
    currentRoute: String,
    navigateUp: () -> Unit,
    navigateTo: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {
            TopNavigationBar(
                currentRoute = currentRoute,
                amountWordsToReview = amountWordsToReview,
                navigateTo = navigateTo,
            )
        },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = navigateUp) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopNavigationBar(
    amountWordsToReview: Int,
    currentRoute: String,
    navigateTo: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val learnWordsScreen = Destination.Study.LearnWordsScreen()
    val reviewWordsScreen = Destination.Study.ReviewWordsScreen()

    NavigationBar(modifier = modifier) {
        NavigationBarItem(
            selected = currentRoute == learnWordsScreen,
            onClick = {
                navigateTo(learnWordsScreen)
            },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.bulb_icon),
                    contentDescription = null,
                    tint = Color.Yellow,
                    modifier = Modifier.size(dimensionResource(R.dimen.icon_size_default))
                )
            },
            label = {
                Text(text = stringResource(R.string.learn_new_words))
            }
        )
        NavigationBarItem(
            selected = currentRoute == reviewWordsScreen,
            onClick = { navigateTo(reviewWordsScreen) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = Color.Green
                )
            },
            label = {
                BadgedBox(
                    badge = {
                        Badge { Text(text = "$amountWordsToReview") }
                    }
                ) {
                    Text(text = stringResource(R.string.review_words))
                }
            }
        )
    }
}
