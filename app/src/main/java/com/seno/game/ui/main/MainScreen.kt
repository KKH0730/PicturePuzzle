package com.seno.game.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.seno.game.R
import com.seno.game.extensions.checkNetworkConnectivityForComposable
import com.seno.game.extensions.restartApp
import com.seno.game.navigation.NavigationGraph
import com.seno.game.ui.common.RestartDialog
import com.seno.game.util.SoundUtil

@Composable
fun MainScreen() {
    MainUI()
}

@Composable
fun MainUI() {
    val context = LocalContext.current
    if (context.checkNetworkConnectivityForComposable()) {
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
    } else {
        RestartDialog(
            title = context.getString(R.string.network_error_title),
            content = context.getString(R.string.network_error),
            confirmText = context.getString(R.string.alert_dialog_restart),
            onClickConfirm = { (context as MainActivity).restartApp() }
        )
    }
}