package com.seno.game.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.seno.game.R
import com.seno.game.ui.account.SignGateScreen
import com.seno.game.ui.main.MainScreen
import com.seno.game.ui.main.home.HomeScreen

enum class NavigationRoute(val routeName: String, val icon: Int?) {
    MAIN("MAIN", null),
    LOGIN("LOGIN", null),
    HOME("HOME", icon = R.drawable.ic_launcher_foreground),
    HOME2("HOME2", R.drawable.ic_launcher_foreground),
    HOME3("HOME3", R.drawable.ic_launcher_foreground),
    PROFILE("PROFILE", R.drawable.ic_launcher_foreground),
    SPLASH("SPLASH", null),
}

@Composable
fun NavigationGraph(startRoute: NavigationRoute = NavigationRoute.HOME, navController: NavHostController) {
    val routeAction = remember(navController) { RouteAction(navController) }

    NavHost(navController = navController, startDestination = startRoute.routeName) {
        composable(NavigationRoute.MAIN.routeName) {
            MainScreen()
        }
        composable(NavigationRoute.HOME.routeName) {
            HomeScreen()
        }

        composable(NavigationRoute.HOME2.routeName) {

        }

        composable(NavigationRoute.HOME3.routeName) {

        }

        composable(NavigationRoute.PROFILE.routeName) {

        }
    }
}