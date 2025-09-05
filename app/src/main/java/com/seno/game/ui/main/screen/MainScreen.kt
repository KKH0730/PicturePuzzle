package com.seno.game.ui.main.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.seno.game.R
import com.seno.game.extensions.checkNetworkConnectivityForComposable
import com.seno.game.extensions.restartApp
import com.seno.game.navigation.NavigationGraph
import com.seno.game.ui.component.CommonAlertDialog
import com.seno.game.ui.main.MainActivity
import com.seno.game.util.MusicPlayUtil

@Composable
fun MainScreen() {
    MainUI()
}

@Composable
fun MainUI() {
    val context = LocalContext.current
    if (context.checkNetworkConnectivityForComposable()) {
        MusicPlayUtil.startBackgroundSound(context = context as MainActivity)
        val navController = rememberNavController()

        Box {
            NavigationGraph(navController = navController)
        }
    } else {
        CommonAlertDialog(
            title = context.getString(R.string.network_error_title),
            content = context.getString(R.string.network_error),
            confirmText = context.getString(R.string.alert_dialog_restart),
            onClickConfirm = { (context as MainActivity).restartApp() }
        )
    }
}