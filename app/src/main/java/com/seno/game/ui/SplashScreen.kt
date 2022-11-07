package com.seno.game.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.seno.game.extensions.noRippleClickable
import com.seno.game.navigation.RouteAction

@Composable
fun SplashScreen(navController: NavHostController) {
    val routeAction = remember(navController) { RouteAction(navController) }


    Box(modifier = Modifier.fillMaxSize()) {
        Text(text = "splash", modifier = Modifier.align(Alignment.Center).noRippleClickable {})
    }
}