package ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ua.com.andromeda.wordgalaxy.R

@Composable
fun VocabularyScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController()
) {
    val categories = getCategories()
    VocabularyCategoryList(categories, modifier = modifier)
}

@Composable
private fun getCategories() = listOf(
    VocabularyCategory(1, rememberVectorPainter(Icons.Default.NoteAdd), "My words", 23, 100),
    VocabularyCategory(2, painterResource(R.drawable.money_category_icon), "Money", 123, 23),
    VocabularyCategory(3, painterResource(R.drawable.colors_category_icon), "Colours", 55, 11),
    VocabularyCategory(
        4,
        painterResource(R.drawable.furniture_category_icon),
        "Furniture",
        111,
        44
    ),
    VocabularyCategory(5, painterResource(R.drawable.city_category_icon), "City", 220, 66),
    VocabularyCategory(
        6,
        painterResource(R.drawable.travels_category_icon),
        "Travels",
        122,
        22
    ),
    VocabularyCategory(
        7,
        painterResource(R.drawable.family_category_icon),
        "Family",
        32,
        88
    ),
    VocabularyCategory(8, painterResource(R.drawable.animals_category_icon), "Animals", 32, 88),
    VocabularyCategory(9, painterResource(R.drawable.photo_category_icon), "Photo", 34, 0),
    VocabularyCategory(
        10,
        painterResource(R.drawable.date_and_time_category_icon),
        "Date and time",
        66,
        4
    ),
    VocabularyCategory(11, painterResource(R.drawable.food_category_icon), "Food", 388, 6),
    VocabularyCategory(
        12,
        painterResource(R.drawable.phrasal_verbs_category_icon),
        "Phrasal verbs",
        111,
        43
    ),
    VocabularyCategory(13, painterResource(R.drawable.numbers_category_icon), "Numbers", 111, 43),
    VocabularyCategory(15, painterResource(R.drawable.numbers_category_icon), "Numbers", 111, 43),
    VocabularyCategory(16, painterResource(R.drawable.numbers_category_icon), "Numbers", 111, 43),
    VocabularyCategory(17, painterResource(R.drawable.numbers_category_icon), "Numbers", 111, 43),
    VocabularyCategory(18, painterResource(R.drawable.numbers_category_icon), "Numbers", 111, 43),
)

@Composable
private fun VocabularyCategoryList(
    categories: List<VocabularyCategory>,
    modifier: Modifier = Modifier,
) {
    val state = rememberLazyListState()
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
        state = state,
        modifier = modifier
    ) {
        item {
            Row(
                modifier = Modifier.clickable { },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_category),
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = MaterialTheme.shapes.extraLarge
                        )
                        .size(dimensionResource(R.dimen.icon_size_largest))
                )
                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_medium)))
                Text(text = stringResource(R.string.add_category))
            }
        }
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
        modifier = modifier
            .clickable { },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = category.icon,
            contentDescription = category.title,
            tint = Color.Unspecified,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.extraLarge
                )
                .size(dimensionResource(R.dimen.icon_size_largest))
                .padding(dimensionResource(R.dimen.padding_small))
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
