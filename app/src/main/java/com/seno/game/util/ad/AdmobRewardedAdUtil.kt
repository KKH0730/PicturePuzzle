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

class AdmobRewardedAdUtil(private val activity: Activity) {
    private var rewardedAd: RewardedAd? = null

    fun init() {
        MobileAds.initialize(activity)
    }

    @SuppressLint("VisibleForTests")
    fun loadRewardedAd(
        onAdFailedToLoad: () -> Unit,
        onAdLoaded: () -> Unit,
    ) {
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

        rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                rewardedAd = null
            }

            override fun onAdDismissedFullScreenContent() {
                rewardedAd = null
            }
        }

        rewardedAd?.show(
            activity
        ) { _ ->
            onRewarded.invoke()
        }
    }
}