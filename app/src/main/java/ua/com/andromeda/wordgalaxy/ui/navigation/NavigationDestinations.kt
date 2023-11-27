package ua.com.andromeda.wordgalaxy.ui.navigation

sealed class Destination(protected val route: String, vararg params: String) {
    val fullRoute: String = if (params.isEmpty()) route else {
        // TODO: refactor
        val builder = StringBuilder(route)
        params.forEach { builder.append("/{$it}") }
        builder.toString()
    }

    sealed class NoArgumentsDestination(route: String): Destination(route) {
        operator fun invoke() = route
    }

    object HomeScreen: NoArgumentsDestination("home")
}