package ua.com.andromeda.wordgalaxy.core.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ua.com.andromeda.wordgalaxy.R

@Composable
fun StartBottomAppBar(
    currentRoute: String?,
    navigateTo: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    BottomAppBar(modifier) {
        NavigationBar {
            BottomNavigationBarItem(
                destination = Destination.Start.HomeScreen(),
                currentRoute = currentRoute,
                icon = painterResource(R.drawable.flash_cards_icon),
                label = R.string.learn,
                navigateTo = navigateTo,
            )
            BottomNavigationBarItem(
                destination = Destination.Start.VocabularyScreen.CategoriesScreen(),
                currentRoute = currentRoute,
                icon = rememberVectorPainter(Icons.Default.Book),
                label = R.string.vocabulary,
                navigateTo = navigateTo,
            )
            BottomNavigationBarItem(
                destination = Destination.Start.MenuScreen(),
                currentRoute = currentRoute,
                icon = rememberVectorPainter(Icons.Default.Menu),
                label = R.string.menu,
                navigateTo = navigateTo,
            )
        }
    }
}

@Composable
fun RowScope.BottomNavigationBarItem(
    destination: String,
    currentRoute: String?,
    icon: Painter,
    @StringRes label: Int,
    modifier: Modifier = Modifier,
    navigateTo: (String) -> Unit,
) {
    NavigationBarItem(
        selected = destination == currentRoute,
        onClick = { navigateTo(destination) },
        icon = {
            Icon(
                painter = icon,
                contentDescription = stringResource(label),
                modifier = Modifier.size(dimensionResource(R.dimen.icon_size_default))
            )
        },
        modifier = modifier,
        label = {
            Text(text = stringResource(label))
        },
        alwaysShowLabel = true
    )
}