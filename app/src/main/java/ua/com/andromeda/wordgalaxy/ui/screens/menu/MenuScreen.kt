package ua.com.andromeda.wordgalaxy.ui.screens.menu

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContactSupport
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShortText
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.ui.common.HorizontalSpacer
import ua.com.andromeda.wordgalaxy.ui.navigation.Destination

data class MenuItemState(
    val icon: ImageVector,
    @StringRes val labelRes: Int,
    val onClick: () -> Unit
)

@Composable
fun MenuScreen(
    navigateTo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val menuItems = listOf(
        MenuItemState(
            icon = Icons.Filled.Settings,
            labelRes = R.string.settings,
            onClick = {
                navigateTo(Destination.Start.Menu.SettingsScreen())
            },
        ),
        MenuItemState(
            icon = Icons.Filled.Share,
            labelRes = R.string.share,
            onClick = {},
        ),

        MenuItemState(
            icon = Icons.Outlined.Star,
            labelRes = R.string.rate_us,
            onClick = {},
        ),
        MenuItemState(
            icon = Icons.Filled.ContactSupport,
            labelRes = R.string.support,
            onClick = {},
        ),
        MenuItemState(
            icon = Icons.Default.ShortText,
            labelRes = R.string.about_us,
            onClick = {},
        ),

        )

    Card(modifier) {
        Column(
            modifier = Modifier.padding(
                dimensionResource(R.dimen.padding_medium)
            ),
        ) {
            menuItems.forEach {
                MenuItem(
                    icon = it.icon,
                    labelRes = it.labelRes,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clickable(onClick = it.onClick)
                )
            }
        }
    }
}

@Composable
private fun MenuItem(
    icon: ImageVector,
    @StringRes labelRes: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(imageVector = icon, contentDescription = null)
        HorizontalSpacer(R.dimen.padding_mediumish)
        Text(text = stringResource(labelRes))
    }
}
