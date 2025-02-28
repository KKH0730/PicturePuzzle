package com.seno.game.ui.main.home.game.diff_picture.create_game.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.seno.game.data.model.Player
import com.seno.game.ui.main.home.game.diff_picture.create_game.viewholder.DiffPictureWaitingViewHolder

class WaitingRoomAdapter: ListAdapter<Player, RecyclerView.ViewHolder>(
    object: DiffUtil.ItemCallback<Player>() {
        override fun areItemsTheSame(oldItem: Player, newItem: Player): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: Player, newItem: Player): Boolean {
            return oldItem == newItem
        }
    }
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DiffPictureWaitingViewHolder.create(parent = parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as DiffPictureWaitingViewHolder).bind(data = getItem(position), position = position)
    }
}