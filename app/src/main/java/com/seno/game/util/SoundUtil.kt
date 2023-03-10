package com.seno.game.util

import android.content.Context
import android.media.MediaPlayer
import com.seno.game.R
import com.seno.game.prefs.PrefsManager

object SoundUtil {
    private var backgroundMediaPlayer: MediaPlayer? = null
    private var rightAnswerSoundMediaPlayer: MediaPlayer? = null
    private var wrongAnswerSoundMediaPlayer: MediaPlayer? = null

    val isBGMPlaying: Boolean?
        get() = backgroundMediaPlayer?.isPlaying

    fun startBackgroundSound(context: Context) {
        if (backgroundMediaPlayer != null) {
            release(isBackgroundSound = true)
        }

        backgroundMediaPlayer = MediaPlayer.create(context, R.raw.bgm_picnic).apply {
            setVol(leftVol = PrefsManager.backgroundVolume, rightVol = PrefsManager.backgroundVolume, isBackgroundSound = true)
            setOnCompletionListener {
                startBackgroundSound(context = context)
            }
        }
        playBackgroundBGM()
    }

    fun startRightAnswerSoundMediaPlayer(context: Context) {
        if (rightAnswerSoundMediaPlayer != null) {
            release(isBackgroundSound = false)
        }

        rightAnswerSoundMediaPlayer = MediaPlayer.create(context, R.raw.effect_right_answer).apply {
            setVol(leftVol = PrefsManager.effectVolume, rightVol = PrefsManager.effectVolume, isBackgroundSound = false)
            setOnCompletionListener {}
        }
        playEffectSound(isRightAnswer = true)
    }

    fun startWrongAnswerSoundMediaPlayer(context: Context) {
        if (wrongAnswerSoundMediaPlayer != null) {
            release(isBackgroundSound = false)
        }

        wrongAnswerSoundMediaPlayer = MediaPlayer.create(context, R.raw.effect_wrong_answer).apply {
            setVol(leftVol = PrefsManager.effectVolume, rightVol = PrefsManager.effectVolume, isBackgroundSound = false)
            setOnCompletionListener {}
        }
        playEffectSound(isRightAnswer = false)
    }

    private fun playBackgroundBGM() {
        backgroundMediaPlayer?.let { mediaPlayer ->
            if (!mediaPlayer.isPlaying) {
                mediaPlayer.start()
            }
        }
    }

    private fun playEffectSound(isRightAnswer: Boolean) {
        if (isRightAnswer){
            rightAnswerSoundMediaPlayer
        } else {
            wrongAnswerSoundMediaPlayer
        }?.let { mediaPlayer ->
            if (!mediaPlayer.isPlaying) {
                mediaPlayer.start()
            }
        }
    }

    fun restartBackgroundBGM() {
        if (isBGMPlaying == true) {
            return
        }
        backgroundMediaPlayer?.start()
    }

    fun pause(isBackgroundSound: Boolean) {
        if (isBackgroundSound) {
            backgroundMediaPlayer?.let { mediaPlayer ->
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.pause()
                }
            }
        } else {
            rightAnswerSoundMediaPlayer?.let { mediaPlayer ->
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.pause()
                }
            }
            wrongAnswerSoundMediaPlayer?.let { mediaPlayer ->
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
            rightAnswerSoundMediaPlayer?.let { mediaPlayer ->
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                    mediaPlayer.reset()
                    mediaPlayer.release()
                    this.rightAnswerSoundMediaPlayer = null
                }
            }

            wrongAnswerSoundMediaPlayer?.let { mediaPlayer ->
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                    mediaPlayer.reset()
                    mediaPlayer.release()
                    this.wrongAnswerSoundMediaPlayer = null
                }
            }
        }
    }

    fun release() {
        backgroundMediaPlayer?.let { mediaPlayer ->
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
                mediaPlayer.reset()
                mediaPlayer.release()
                this.backgroundMediaPlayer = null
            }
        }
        rightAnswerSoundMediaPlayer?.let { mediaPlayer ->
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
                mediaPlayer.reset()
                mediaPlayer.release()
                this.rightAnswerSoundMediaPlayer = null
            }
        }

        wrongAnswerSoundMediaPlayer?.let { mediaPlayer ->
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
                mediaPlayer.reset()
                mediaPlayer.release()
                this.wrongAnswerSoundMediaPlayer = null
            }
        }
    }

    fun setVol(leftVol: Float, rightVol: Float, isBackgroundSound: Boolean) {
        if (isBackgroundSound) {
            backgroundMediaPlayer?.setVolume(leftVol, rightVol)
        } else {
            rightAnswerSoundMediaPlayer?.setVolume(leftVol, rightVol)
            wrongAnswerSoundMediaPlayer?.setVolume(leftVol, rightVol)
        }
    }
}