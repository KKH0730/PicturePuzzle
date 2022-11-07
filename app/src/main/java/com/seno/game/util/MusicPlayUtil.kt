package com.seno.game.util

import android.content.Context
import android.media.MediaPlayer
import com.seno.game.R

object MusicPlayUtil {
    private var mediaPlayer: MediaPlayer? = null

    val isPlaying: Boolean?
        get() = mediaPlayer?.isPlaying

    fun startBackgroundBGM(context: Context) {
        if (mediaPlayer != null) {
            release()
        }

        mediaPlayer = MediaPlayer.create(context, R.raw.bgm_picnic).apply {
            setVol(0.5f, 0.5f)
            setOnCompletionListener {
                startBackgroundBGM(context = context)
            }
        }
        play()
    }

    // 플레이 체크
    private fun play() {
        mediaPlayer?.let { mediaPlayer ->
            if (!mediaPlayer.isPlaying) {
                mediaPlayer.start()
            }
        }
    }

    fun restart() {
        mediaPlayer?.start()
    }

    fun pause() {
        mediaPlayer?.let { mediaPlayer ->
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            }
        }
    }

    fun release() {
        mediaPlayer?.let { mediaPlayer ->
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
                mediaPlayer.reset()
                mediaPlayer.release()
                this.mediaPlayer = null
            }
        }
    }

    fun setVol(leftVol: Float, rightVol: Float) {
        mediaPlayer?.setVolume(leftVol, rightVol)
    }
}