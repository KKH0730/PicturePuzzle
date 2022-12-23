package com.seno.game.ui.game.diff_picture.single.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.seno.game.databinding.ViewholderAnswerMarkItemBinding
import com.seno.game.ui.game.diff_picture.single.model.AnswerMark

class AnswerMarkAdapter : ListAdapter<AnswerMark, AnswerMarkAdapter.AnswerMarkViewHolder>(
    object: DiffUtil.ItemCallback<AnswerMark>() {
        override fun areItemsTheSame(oldItem: AnswerMark, newItem: AnswerMark): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: AnswerMark, newItem: AnswerMark): Boolean  = oldItem == newItem
    }
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerMarkViewHolder {
        return AnswerMarkViewHolder.create(parent = parent)
    }

    override fun onBindViewHolder(holder: AnswerMarkViewHolder, position: Int) {
        holder.bind(data = getItem(position))
    }

    class AnswerMarkViewHolder(
        private val binding: ViewholderAnswerMarkItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: AnswerMark) {
            binding.apply {
                viewholder = this@AnswerMarkViewHolder
                answerMark = data
            }
        }

        companion object {
            fun create(parent: ViewGroup): AnswerMarkViewHolder {
                return AnswerMarkViewHolder(
                    binding = ViewholderAnswerMarkItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}