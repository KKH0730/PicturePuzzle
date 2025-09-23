package com.seno.game.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.seno.game.R
import com.seno.game.ui.main.home.game.diff_picture.multi.entry.multi.screen.DPMultiPlayScreen
import com.seno.game.ui.main.home.game.diff_picture.multi.entry.multi.screen.LobbyRoomScreen
import com.seno.game.ui.main.home.screen.HomeScreen
import com.seno.game.ui.main.screen.MainScreen

enum class NavigationRoute(val routeName: String, val icon: Int?) {
    MAIN("MAIN", null),
    LOGIN("LOGIN", null),
    HOME("HOME", icon = R.drawable.ic_launcher_foreground),
    HOME2("HOME2", R.drawable.ic_launcher_foreground),
    HOME3("HOME3", R.drawable.ic_launcher_foreground),
    PROFILE("PROFILE", R.drawable.ic_launcher_foreground),
    SPLASH("SPLASH", null),
    LOBBY_SCREEN("LobbyScreen", null),
    DP_MULTIPLAY_SCREEN("DPMultiPlayScreen", null),
}

@Composable
fun NavigationGraph(startRoute: NavigationRoute = NavigationRoute.HOME, navController: NavHostController, modifier: Modifier = Modifier) {
    val routeAction = remember(navController) { RouteAction(navController) }

    NavHost(navController = navController, startDestination = startRoute.routeName, modifier = modifier) {
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

        composable(route = NavigationRoute.LOBBY_SCREEN.routeName) { backStackEntry ->
            LobbyRoomScreen(navController = navController)
        }

        composable(NavigationRoute.DP_MULTIPLAY_SCREEN.routeName) {
            DPMultiPlayScreen(navController = navController)
        }
    }
}