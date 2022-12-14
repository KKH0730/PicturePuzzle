package com.seno.game.ui.game.diffgame.list

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.seno.game.extensions.startActivity
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

    companion object {
        fun start(context: Context) {
            context.startActivity(DiffPictureSingleGameListActivity::class.java)
        }
    }
}

@Composable
fun DiffPictureSingleGameListScreen() {
    GridGameLevelList()
}