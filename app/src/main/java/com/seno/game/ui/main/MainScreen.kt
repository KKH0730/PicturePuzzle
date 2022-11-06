package com.seno.game.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.seno.game.navigation.NavigationGraph
import com.seno.game.ui.main.component.BottomNavigationBar

@Composable
fun MainScreen() {
    val navController = rememberNavController()

//    Scaffold(
//        bottomBar = { BottomNavigationBar(navController = navController) },
//    ) { innerPadding ->
//        Box(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
//            NavigationGraph(navController = navController)
//        }
//    }


    Box {
        NavigationGraph(navController = navController)
    }
}