package com.seno.game.navigation

import androidx.navigation.NavHostController

class RouteAction(private val navHostController: NavHostController) {

    // 화면 이동
    val navTo: (NavigationRoute) -> Unit = { route -> navHostController.navigate(route.routeName) }

    // 뒤로가기
    val navBack: () -> Unit = { navHostController.navigateUp() }
}