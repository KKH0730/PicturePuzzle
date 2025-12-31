package com.seno.game.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.seno.game.ui.main.home.game.diff_picture.multi.entry.multi.screen.DPMultiPlayScreen
import com.seno.game.ui.main.home.game.diff_picture.multi.entry.multi.screen.LobbyRoomScreen
import com.seno.game.ui.main.home.screen.HomeScreen
import com.seno.game.ui.main.screen.MainScreen

enum class NavigationRoute(val routeName: String) {
    MAIN("MAIN"),
    LOGIN("LOGIN"),
    HOME("HOME"),
    HOME2("HOME2"),
    HOME3("HOME3"),
    PROFILE("PROFILE"),
    SPLASH("SPLASH"),
    LOBBY_SCREEN("LobbyScreen"),
    DP_MULTIPLAY_SCREEN("DPMultiPlayScreen"),
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