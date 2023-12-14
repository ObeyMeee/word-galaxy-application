package ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Desk
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.NoMeals
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.dimensionResource
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ua.com.andromeda.wordgalaxy.R

@Composable
fun VocabularyScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController()
) {
    val categories = listOf(
        VocabularyCategory(1, rememberVectorPainter(Icons.Default.NoteAdd), "My words", 23, 100),
        VocabularyCategory(2, rememberVectorPainter(Icons.Default.AttachMoney), "Money", 123, 23),
        VocabularyCategory(3, rememberVectorPainter(Icons.Default.ColorLens), "Colours", 55, 11),
        VocabularyCategory(4, rememberVectorPainter(Icons.Default.Desk), "Furniture", 111, 44),
        VocabularyCategory(5, rememberVectorPainter(Icons.Default.LocationCity), "City", 220, 66),
        VocabularyCategory(
            6,
            rememberVectorPainter(Icons.Default.AirplanemodeActive),
            "Travels",
            122,
            22
        ),
        VocabularyCategory(
            7,
            rememberVectorPainter(Icons.Default.FamilyRestroom),
            "Family",
            32,
            88
        ),
        VocabularyCategory(8, rememberVectorPainter(Icons.Default.NoMeals), "Animals", 32, 88),
        VocabularyCategory(9, rememberVectorPainter(Icons.Default.PhotoCamera), "Photo", 34, 0),
        VocabularyCategory(
            10,
            rememberVectorPainter(Icons.Default.CalendarMonth),
            "Date and time",
            66,
            4
        ),
        VocabularyCategory(11, rememberVectorPainter(Icons.Default.Fastfood), "Food", 388, 6),
    )
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
    ) {
        items(categories, key = { it.id }) {
            VocabularyCategoryItem(
                category = it,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun VocabularyCategoryItem(category: VocabularyCategory, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.clickable { },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = category.icon,
            contentDescription = category.title,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.extraLarge
                )
        )
        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_medium)))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = category.title)
            Text(
                text = "${category.amountWords} words",
                style = MaterialTheme.typography.labelMedium
            )
        }
        Text(
            text = "${category.wordsCompletedPercentage}%",
            style = MaterialTheme.typography.bodySmall
        )
    }

}

data class VocabularyCategory(
    val id: Int,
    val icon: Painter,
    val title: String,
    val amountWords: Int,
    val wordsCompletedPercentage: Int
)
