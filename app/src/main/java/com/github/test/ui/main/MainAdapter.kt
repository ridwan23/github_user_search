package com.github.test.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.test.data.model.GitUser
import com.github.test.databinding.ItemUserBinding

class MainAdapter :
    PagingDataAdapter<GitUser, MainAdapter.PlayersViewHolder>(PlayersDiffCallback()) {


    override fun onBindViewHolder(holder: PlayersViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayersViewHolder {
        return PlayersViewHolder(
            ItemUserBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    inner class PlayersViewHolder(
        private val binding: ItemUserBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: GitUser?) {
            binding.tvLoginId.text = data?.login
            Glide.with(itemView.context)
                .load(data?.avatarUrl)
                .circleCrop()
                .into(binding.civUserAvatar)
        }
    }

    private class PlayersDiffCallback : DiffUtil.ItemCallback<GitUser>() {
        override fun areItemsTheSame(oldItem: GitUser, newItem: GitUser): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GitUser, newItem: GitUser): Boolean {
            return oldItem == newItem
        }
    }

}