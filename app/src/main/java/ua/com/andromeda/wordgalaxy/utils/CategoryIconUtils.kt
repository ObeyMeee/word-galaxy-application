package ua.com.andromeda.wordgalaxy.utils

import android.content.Context
import ua.com.andromeda.wordgalaxy.data.model.Category

typealias CategoryName = String

const val RESOURCE_NOT_FOUND = 0

fun CategoryName.toIconNameRes() =
    "${this.filter { it != '-' }.lowercase().replace(' ', '_')}_category_icon"

fun Context.getCategoryIconIdentifier(
    category: Category,
    defType: String = "drawable",
    defPackage: String = packageName
) = getCategoryIconIdentifier(category.name, defType, defPackage)


fun Context.getCategoryIconIdentifier(
    categoryName: CategoryName,
    defType: String = "drawable",
    defPackage: String = packageName
) = resources.getIdentifier(categoryName.toIconNameRes(), defType, defPackage)
