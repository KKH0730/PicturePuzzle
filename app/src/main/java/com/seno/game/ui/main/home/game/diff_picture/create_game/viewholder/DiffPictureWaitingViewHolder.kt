package com.seno.game.ui.main.home.game.diff_picture.create_game.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seno.game.databinding.ViewholderDiffPictureWatingBinding
import com.seno.game.data.model.Player

class DiffPictureWaitingViewHolder(
    private val binding: ViewholderDiffPictureWatingBinding,
): RecyclerView.ViewHolder(binding.root) {

    fun bind(data: Player, position: Int) {
        binding.data = data
        binding.position = position

        binding.executePendingBindings()
    }

    companion object {
        fun create(parent: ViewGroup): DiffPictureWaitingViewHolder {
            return DiffPictureWaitingViewHolder(
                binding = ViewholderDiffPictureWatingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }
}