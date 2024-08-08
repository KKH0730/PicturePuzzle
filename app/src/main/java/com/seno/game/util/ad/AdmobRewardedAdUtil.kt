package com.seno.game.util.ad

import android.annotation.SuppressLint
import android.app.Activity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.seno.game.R
import timber.log.Timber

class AdmobRewardedAdUtil(private val activity: Activity) {
    private var rewardedAd: RewardedAd? = null

    fun init() {
        MobileAds.initialize(activity)
    }

    @SuppressLint("VisibleForTests")
    fun loadRewardedAd(
        onCalledBeforeLoadRewardedAd: (() -> Unit)? = null,
        onAdFailedToLoad: () -> Unit,
        onAdLoaded: () -> Unit,
        onAdDismissedFullScreenContent: (() -> Unit)? = null,
        onAdFailedToShowFullScreenContent: (() -> Unit)? = null
    ) {
        onCalledBeforeLoadRewardedAd?.invoke()

        if (rewardedAd == null) {
            val adRequest = AdRequest.Builder().build()
            RewardedAd.load(activity, activity.getString(R.string.reward_ad_unit_id_for_test), adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        this@AdmobRewardedAdUtil.rewardedAd = null
                        onAdFailedToLoad.invoke()
                    }

                    override fun onAdLoaded(rewardedAd: RewardedAd) {
                        this@AdmobRewardedAdUtil.rewardedAd = rewardedAd
                        this@AdmobRewardedAdUtil.rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {

                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                this@AdmobRewardedAdUtil.rewardedAd = null
                                onAdFailedToShowFullScreenContent?.invoke()
                            }

                            override fun onAdDismissedFullScreenContent() {
                                this@AdmobRewardedAdUtil.rewardedAd = null
                                onAdDismissedFullScreenContent?.invoke()
                            }
                        }
                        onAdLoaded.invoke()
                    }
                }
            )
        } else {
            onAdLoaded.invoke()
        }
    }

    fun showRewardedAd(
        onRewarded: () -> Unit
    ) {
        if (rewardedAd == null) {
            return
        }

        rewardedAd?.show(
            activity
        ) { _ ->
            onRewarded.invoke()
        }
    }

    fun release() {
        rewardedAd?.fullScreenContentCallback = null
        rewardedAd = null

    }
}