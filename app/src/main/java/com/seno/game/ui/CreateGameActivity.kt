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
import com.seno.game.extensions.toast
import com.seno.game.manager.AccountManager
import com.seno.game.model.Player
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
) {
    private val homeViewModel by viewModels<HomeViewModel>()
    private var snapshotListener: ListenerRegistration? = null

    private lateinit var todayDate: String
    private lateinit var roomUid: String

    @DiffDocRef
    @Inject
    lateinit var diffGameDocRef: DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()
        if (intent.getBooleanExtra("isChief", false)) {
            createQRCode()
        }
        setRecyclerView()
        observeFlowData()
        observePlayerJoin(
            date = todayDate,
            roomUid = roomUid
        )
    }

    private fun init() {
        todayDate = intent.getStringExtra("date")!!
        roomUid = intent.getStringExtra("roomUid")!!
    }

    private fun setRecyclerView() {
        binding.rvPlayer.adapter = ConcatAdapter(WaitingRoomAdapter())

        val itemAnimator = binding.rvPlayer.itemAnimator
        if (itemAnimator is SimpleItemAnimator) {
            itemAnimator.supportsChangeAnimations = false
        }
    }

    private fun createQRCode() {
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

    private fun observeFlowData() {
        lifecycleScope.launch {
            launch {
                homeViewModel.createRoomFlow.collect {
                    it?.roomUid?.let { uid ->
                        binding.ivQRCode.setImageBitmap(QRCodeUtil.createQRCode(uid = uid))
                        ((binding.rvPlayer.adapter as ConcatAdapter).adapters[0] as WaitingRoomAdapter).submitList(it.playerList)
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
        }
    }

    private fun observePlayerJoin(date: String, roomUid: String) {
        snapshotListener = diffGameDocRef.collection(date).document(roomUid)
            .addSnapshotListener { snapShot, error ->
                if (error != null) {
                    toast(message = getString(R.string.network_error))
                }

                if (snapShot != null && snapShot.exists()) {
                    val list = (snapShot.get("playerList") as ArrayList<HashMap<String, Any>>).map {
                        Player(
                            uid = (it["uid"] as String),
                            nickName = (it["nickName"] as String),
                            isReady = (it["ready"] as Boolean)
                        )
                    }
                    ((binding.rvPlayer.adapter as ConcatAdapter).adapters[0] as WaitingRoomAdapter).submitList(list)
                }
            }
    }

    override fun onDestroy() {
        snapshotListener?.remove()
        super.onDestroy()
    }
}