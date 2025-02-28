package com.seno.game.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.seno.game.R
import com.seno.game.extensions.checkNetworkConnectivityForComposable
import com.seno.game.extensions.restartApp
import com.seno.game.navigation.NavigationGraph
import com.seno.game.ui.common.RestartDialog
import com.seno.game.ui.main.home.HomeLoadingScreen
import com.seno.game.util.SoundUtil

@Composable
fun MainScreen() {
    val mainViewModel = hiltViewModel<MainViewModel>()
    val isShowNetworkErrorEvent = mainViewModel.showNetworkErrorEvent.collectAsStateWithLifecycle().value
    val savedGameInfo = mainViewModel.savedGameInfoToLocalDB.collectAsStateWithLifecycle().value
    savedGameInfo?.savedGameInfoToLocalDB()

    val context = LocalContext.current
    val navController = rememberNavController()

    Box {
        if (isShowNetworkErrorEvent) {
            RestartDialog(
                title = context.getString(R.string.network_error_title),
                content = context.getString(R.string.network_error),
                confirmText = context.getString(R.string.alert_dialog_restart),
                onClickConfirm = { (context as MainActivity).restartApp() }
            )
        } else {
            NavigationGraph(navController = navController)
        }
    }
}