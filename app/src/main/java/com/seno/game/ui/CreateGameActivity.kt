package com.seno.game.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration
import com.seno.game.R
import com.seno.game.base.BaseActivity
import com.seno.game.databinding.ActivityCreateGameBinding
import com.seno.game.di.network.DiffDocRef
import com.seno.game.extensions.getTodayDate
import com.seno.game.extensions.startActivity
import com.seno.game.extensions.toast
import com.seno.game.manager.AccountManager
import com.seno.game.model.Player
import com.seno.game.ui.game.diffgame.DiffPictureGameActivity
import com.seno.game.ui.main.home.HomeViewModel
import com.seno.game.util.QRCodeUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@AndroidEntryPoint
class CreateGameActivity : BaseActivity<ActivityCreateGameBinding>(
    layoutResId = R.layout.activity_create_game
), OnCreateGameEventListener {
    private val homeViewModel by viewModels<HomeViewModel>()
    private var snapshotListener: ListenerRegistration? = null

    private lateinit var todayDate: String
    private lateinit var roomUid: String
    private lateinit var uid: String
    private var isChief: Boolean = false

    @DiffDocRef
    @Inject
    lateinit var diffGameDocRef: DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()
        if (isChief) {
            reqCreateQRCode()
        } else {
            setQRCode()
        }
        setRecyclerView()
        observeFlowData()
        observePlayerJoin(
            date = todayDate,
            roomUid = roomUid
        )
    }

    private fun init() {
        binding.eventListener = this@CreateGameActivity

        isChief = intent.getBooleanExtra("isChief", false)
        todayDate = intent.getStringExtra("date")!!
        uid = intent.getStringExtra("uid")!!
        roomUid = intent.getStringExtra("roomUid")!!

        if (isChief) {
            binding.tvReady.text = "시작"
        } else {
            binding.tvReady.text = "준비"
        }
    }

    private fun setRecyclerView() {
        binding.rvPlayer.adapter = ConcatAdapter(WaitingRoomAdapter())

        val itemAnimator = binding.rvPlayer.itemAnimator
        if (itemAnimator is SimpleItemAnimator) {
            itemAnimator.supportsChangeAnimations = false
        }
    }

    private fun reqCreateQRCode() {
        // QR 코드 생성
        AccountManager.firebaseUid?.let { uid ->
            homeViewModel.reqCreateRoom(
                date = todayDate,
                uid = uid,
                roomUid = roomUid,
                nickName = "nick"
            )
        }
    }

    private fun setQRCode() {
        binding.ivQRCode.setImageBitmap(QRCodeUtil.createQRCode(uid = roomUid))
    }

    private fun observeFlowData() {
        lifecycleScope.launch {
            launch {
                homeViewModel.createRoomFlow.collect { it ->
                    it?.roomUid?.let { roomUid ->
                        binding.ivQRCode.setImageBitmap(QRCodeUtil.createQRCode(uid = roomUid))
                        val numberedPlayerList = it.playerList.mapIndexed { index, player ->
                            player.apply { id = index }
                        }
                        ((binding.rvPlayer.adapter as ConcatAdapter).adapters[0] as WaitingRoomAdapter).submitList(numberedPlayerList)
                    }
                }
            }

            launch {
                homeViewModel.loadingFlow.collect {
                    binding.pbLoading.visibility = if (it) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                }
            }

            launch {
                homeViewModel.message.collect { toast(message = it) }
            }

            launch {
                homeViewModel.exitRoomFlow.collect {
                    Timber.e("kkh exitRoomFlow : $it")
                }
            }
        }
    }

    private fun observePlayerJoin(date: String, roomUid: String) {
        snapshotListener = diffGameDocRef.collection(date).document(roomUid)
            .addSnapshotListener { snapShot, error ->
                if (error != null) {
                    toast(message = getString(R.string.network_error))
                }

                if (snapShot != null && snapShot.exists()) {
                    val numberedPlayerList = (snapShot.get("playerList") as ArrayList<HashMap<String, Any>>).mapIndexed { index, hashMap ->
                        AccountManager.firebaseUid?.let { uid ->
                            // 방장이 변경되었음
                            if (hashMap["uid"] as String == uid && index == 0) {
                                isChief = true
                                binding.tvReady.text = "시작"
                            }
                        }

                        Player(
                            id = index,
                            uid = (hashMap["uid"] as String),
                            nickName = (hashMap["nickName"] as String),
                            isReady = if (index == 0) {
                                true
                            } else {
                                (hashMap["ready"] as Boolean)
                            }
                        )
                    }
                    ((binding.rvPlayer.adapter as ConcatAdapter).adapters[0] as WaitingRoomAdapter).submitList(numberedPlayerList)
                }
            }
    }

    override fun onDestroy() {
        snapshotListener?.remove()
        homeViewModel.reqExitRoom(
            date = todayDate,
            uid = uid,
            roomUid = roomUid
        )
        super.onDestroy()
    }

    override fun onClickReady() {
        if (isChief) {
            val currentPlayerList = ((binding.rvPlayer.adapter as ConcatAdapter).adapters[0] as WaitingRoomAdapter).currentList
            if (currentPlayerList.size < 2) {
                return
            }

            for (player in currentPlayerList) {
                if (!player.isReady) {
                    return
                }
            }

            binding.tvReady.isEnabled = false
            startActivity(DiffPictureGameActivity::class.java)
        } else {
            homeViewModel.reqGameReady(
                date = todayDate,
                uid = uid,
                roomUid = roomUid
            )
        }
    }
}

interface OnCreateGameEventListener {
    fun onClickReady()
}