package com.seno.game.ui.game.humminjeongeumgame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.seno.game.theme.AppTheme
import com.seno.game.ui.game.GameViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HunMinJeongEumActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            AppTheme {
                Surface(Modifier.fillMaxSize()) {

                    val viewModel = hiltViewModel<GameViewModel>()
                    HunMinJeongEumScreen(viewModel)
                }
            }
        }
    }
}