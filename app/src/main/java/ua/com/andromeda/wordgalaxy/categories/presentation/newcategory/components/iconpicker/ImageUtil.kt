package ua.com.andromeda.wordgalaxy.categories.presentation.newcategory.components.iconpicker

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.ui.graphics.vector.ImageVector

object ImageUtil {
    fun createImageVector(name: String): ImageVector {
        return try {
            val className = "androidx.compose.material.icons.filled.${name}Kt"
            val cl = Class.forName(className)
            val method = cl.declaredMethods.first()
            method.invoke(null, Icons.Filled) as ImageVector
        } catch (ex: Exception) {
            Log.e("ImageNotFound", name)
            Icons.Default.ImageNotSupported
        }
    }

}