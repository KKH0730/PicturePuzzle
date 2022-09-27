package com.seno.game

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.seno.game.theme.AppTheme
import com.seno.game.ui.game.GameViewModel
import com.seno.game.ui.game.humminjeongeum.HunMinJeongEumActivity
import com.seno.game.ui.game.humminjeongeum.HunMinJeongEumScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContent {
            AppTheme {
                Surface(Modifier.fillMaxSize()) {

                    Box(Modifier.fillMaxSize()) {
                        Button(onClick = {
                            startActivity(Intent(this@MainActivity, HunMinJeongEumActivity::class.java))
                        }) {
                            Text(text = "버튼")
                        }
                    }
                }
            }
        }
    }
}