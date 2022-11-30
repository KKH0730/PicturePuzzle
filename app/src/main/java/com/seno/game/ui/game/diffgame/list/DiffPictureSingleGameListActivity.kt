package com.seno.game.ui.game.diffgame.list

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.seno.game.R
import com.seno.game.R.*
import com.seno.game.theme.AppTheme
import com.seno.game.ui.game.diffgame.list.component.GridGameLevelList

class DiffPictureSingleGameListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                Surface(Modifier.fillMaxSize()) {
                    DiffPictureSingleGameListScreen()
                }
            }
        }
    }
}

@Composable
fun DiffPictureSingleGameListScreen() {
    GridGameLevelList()
}