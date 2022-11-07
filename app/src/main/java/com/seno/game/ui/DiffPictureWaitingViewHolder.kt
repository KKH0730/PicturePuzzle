package com.seno.game.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seno.game.databinding.ViewholderDiffPictureWatingBinding
import com.seno.game.model.Player
import timber.log.Timber

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