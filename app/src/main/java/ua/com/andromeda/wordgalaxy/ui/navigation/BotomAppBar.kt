package ua.com.andromeda.wordgalaxy.ui.navigation

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ua.com.andromeda.wordgalaxy.R

@Composable
fun StartBottomAppBar(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    BottomAppBar(modifier) {
        NavigationBar {
            BottomNavigationBarItem(
                destination = Destination.Start.HomeScreen(),
                currentRoute = currentRoute,
                icon = painterResource(R.drawable.flash_cards_icon),
                label = R.string.learn,
                navController = navController
            )
            BottomNavigationBarItem(
                destination = Destination.Start.VocabularyScreen.CategoriesScreen(),
                currentRoute = currentRoute,
                icon = rememberVectorPainter(Icons.Default.Book),
                label = R.string.vocabulary,
                navController = navController
            )
            BottomNavigationBarItem(
                destination = Destination.Start.MenuScreen(),
                currentRoute = currentRoute,
                icon = rememberVectorPainter(Icons.Default.Menu),
                label = R.string.menu,
                navController = navController
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
    navController: NavController = rememberNavController()
) {
    NavigationBarItem(
        selected = destination == currentRoute,
        onClick = { navController.navigate(destination) },
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