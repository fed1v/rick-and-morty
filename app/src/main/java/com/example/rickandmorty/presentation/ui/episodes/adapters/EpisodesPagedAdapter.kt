package com.example.rickandmorty.presentation.ui.episodes.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.rickandmorty.R
import com.example.rickandmorty.databinding.ItemEpisodeBinding
import com.example.rickandmorty.presentation.models.EpisodePresentation
import com.example.rickandmorty.util.OnItemSelectedListener

class EpisodesPagedAdapter(
    private val onItemSelectedListener: OnItemSelectedListener<EpisodePresentation>
) : PagingDataAdapter<EpisodePresentation, EpisodesPagedAdapter.EpisodesPagedViewHolder>(
    diffCallback = EpisodesPagedDiffCallback()
) {

    override fun onBindViewHolder(holder: EpisodesPagedViewHolder, position: Int) {
        val episode = getItem(position)

        episode?.let {
            holder.bind(it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodesPagedViewHolder {
        return EpisodesPagedViewHolder(
            itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_episode, parent, false),
            listener = onItemSelectedListener
        )
    }

    class EpisodesPagedViewHolder(
        itemView: View,
        private val listener: OnItemSelectedListener<EpisodePresentation>
    ) : RecyclerView.ViewHolder(itemView) {

        private val binding = ItemEpisodeBinding.bind(itemView)

        fun bind(episode: EpisodePresentation) {
            binding.episodeAirDate.text = episode.airDate
            binding.episodeEpisode.text = episode.episode
            binding.episodeName.text = episode.name

            itemView.setOnClickListener { listener.onSelectItem(episode) }
        }
    }
}

class EpisodesPagedDiffCallback : DiffUtil.ItemCallback<EpisodePresentation>() {

    override fun areItemsTheSame(
        oldItem: EpisodePresentation,
        newItem: EpisodePresentation
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: EpisodePresentation,
        newItem: EpisodePresentation
    ): Boolean {
        return oldItem == newItem
    }
}