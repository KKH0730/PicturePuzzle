package com.seno.game.ui.main.home.game.diff_picture.find_game

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.zxing.integration.android.IntentIntegrator
import com.seno.game.R
import com.seno.game.base.BaseActivity
import com.seno.game.databinding.ActivityFindGameBinding
import com.seno.game.extensions.getTodayDate
import com.seno.game.extensions.startActivity
import com.seno.game.extensions.toast
import com.seno.game.manager.AccountManager
import com.seno.game.ui.main.home.game.diff_picture.create_game.CreateGameActivity
import com.seno.game.ui.main.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class FindGameActivity : BaseActivity<ActivityFindGameBinding>(
    layoutResId = R.layout.activity_find_game
) {
    private val homeViewModel by viewModels<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        statQRCodeScan()
        observeFlowData()
    }

    private fun statQRCodeScan() {
        val intentIntegrator = IntentIntegrator(this@FindGameActivity).setOrientationLocked(true)
        intentIntegrator.initiateScan()
    }

    private fun observeFlowData() {
        lifecycleScope.launch {
            launch {
                homeViewModel.enterRoomFlow.collect {
                    it?.roomUid?.let { roomUid ->
                        it.date?.let { date ->
                            AccountManager.firebaseUid?.let { uid ->
                                startActivity(CreateGameActivity::class.java) {
                                    putExtra("date", date)
                                    putExtra("uid", uid)
                                    putExtra("roomUid", roomUid)
                                    putExtra("isChief", false)
                                }

                                finish()
                            }
                        }
                    }
                }
            }
            launch {
                homeViewModel.message.collect { toast(message = it) }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                finish()
            } else {
                AccountManager.firebaseUid?.let { uid ->
                    result.contents.let { roomUid ->
                        val date = Date(Calendar.getInstance().timeInMillis)
                        homeViewModel.reqEnterRoom(
                            date = date.getTodayDate(),
                            uid = uid,
                            roomUid = roomUid,
                            nickName = "Find${Calendar.getInstance().timeInMillis}"
                        )
                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}