package com.seno.game.util

import android.content.Context
import android.media.MediaPlayer
import com.seno.game.R

object MusicPlayUtil {
    private var backgroundMediaPlayer: MediaPlayer? = null
    private var effectMediaPlayer: MediaPlayer? = null

    val isPlaying: Boolean?
        get() = backgroundMediaPlayer?.isPlaying

    fun startBackgroundSound(context: Context) {
        if (backgroundMediaPlayer != null) {
            release(isBackgroundSound = true)
        }

        backgroundMediaPlayer = MediaPlayer.create(context, R.raw.bgm_picnic).apply {
            setVol(leftVol = 0.5f, rightVol = 0.5f, isBackgroundSound = true)
            setOnCompletionListener {
                startBackgroundSound(context = context)
            }
        }
        play(isBackgroundSound = true)
    }

    fun startEffectSound(context: Context) {
        if (effectMediaPlayer != null) {
            release(isBackgroundSound = false)
        }

        effectMediaPlayer = MediaPlayer.create(context, R.raw.bgm_picnic).apply {
            setVol(leftVol = 0.5f, rightVol = 0.5f, isBackgroundSound = false)
            setOnCompletionListener {
                startEffectSound(context = context)
            }
        }
        play(isBackgroundSound = false)
    }

    // 플레이 체크
    private fun play(isBackgroundSound: Boolean) {
        if (isBackgroundSound) {
            backgroundMediaPlayer?.let { mediaPlayer ->
                if (!mediaPlayer.isPlaying) {
                    mediaPlayer.start()
                }
            }
        } else {
            effectMediaPlayer?.let { mediaPlayer ->
                if (!mediaPlayer.isPlaying) {
                    mediaPlayer.start()
                }
            }
        }

    }

    fun restart(isBackgroundSound: Boolean) {
        if (isBackgroundSound) {
            backgroundMediaPlayer?.start()
        } else {
            effectMediaPlayer?.start()
        }
    }

    fun pause(isBackgroundSound: Boolean) {
        if (isBackgroundSound) {
            backgroundMediaPlayer?.let { mediaPlayer ->
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.pause()
                }
            }
        } else {
            effectMediaPlayer?.let { mediaPlayer ->
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.pause()
                }
            }
        }
    }

    fun release(isBackgroundSound: Boolean) {
        if (isBackgroundSound) {
            backgroundMediaPlayer?.let { mediaPlayer ->
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                    mediaPlayer.reset()
                    mediaPlayer.release()
                    this.backgroundMediaPlayer = null
                }
            }
        } else {
            effectMediaPlayer?.let { mediaPlayer ->
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                    mediaPlayer.reset()
                    mediaPlayer.release()
                    this.effectMediaPlayer = null
                }
            }
        }
    }

    fun setVol(leftVol: Float, rightVol: Float, isBackgroundSound: Boolean) {
        if (isBackgroundSound) {
            backgroundMediaPlayer?.setVolume(leftVol, rightVol)
        } else {
            effectMediaPlayer?.setVolume(leftVol, rightVol)
        }
    }
}