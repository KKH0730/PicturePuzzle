package com.seno.game

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import com.google.firebase.auth.FirebaseAuth
import com.seno.game.extensions.screenHeight
import com.seno.game.extensions.screenWidth
import com.seno.game.extensions.startActivity
import com.seno.game.theme.AppTheme
import com.seno.game.ui.game.areagame.AreaGameActivity
import com.seno.game.ui.game.diffgame.DiffPictureGameActivity
import com.seno.game.ui.game.humminjeongeumgame.HunMinJeongEumActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel>()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setAuthentication() {
            if (it) {
                setContent {
                    AppTheme {
                        Surface(Modifier.fillMaxSize()) {
                            Column(Modifier.fillMaxSize()) {
                                Button(
                                    onClick = {
                                        startActivity(HunMinJeongEumActivity::class.java)
                                        overridePendingTransition(
                                            R.anim.slide_right_enter,
                                            R.anim.slide_right_exit
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(text = "훈민정음")
                                }

                                Button(
                                    onClick = {
                                        startActivity(AreaGameActivity::class.java)
                                        overridePendingTransition(R.anim.slide_right_enter,
                                            R.anim.slide_right_exit)
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(text = "땅따먹기")
                                }

                                Button(
                                    onClick = {
                                        startActivity(DiffPictureGameActivity::class.java)
                                        overridePendingTransition(R.anim.slide_right_enter,
                                            R.anim.slide_right_exit)
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(text = "틀린그림찾기")
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    private fun setAuthentication(callback: (Boolean) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            callback(true)
        } else {
            Timber.e("kkh not user ")
            auth.signInAnonymously()
                .addOnSuccessListener { callback(true) }
                .addOnFailureListener { callback(false) }
        }
    }
}


