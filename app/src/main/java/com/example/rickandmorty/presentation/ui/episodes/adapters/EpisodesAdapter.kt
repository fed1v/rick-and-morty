package com.example.rickandmorty.presentation.ui.episodes.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rickandmorty.databinding.ItemEpisodeInCharacterDetailsBinding
import com.example.rickandmorty.presentation.models.EpisodePresentation
import com.example.rickandmorty.util.OnItemSelectedListener

class EpisodesAdapter(
    private val onEpisodeClicked: OnItemSelectedListener<EpisodePresentation>
) : RecyclerView.Adapter<EpisodesAdapter.EpisodesViewHolder>() {

    var episodesList: List<EpisodePresentation> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodesViewHolder {
        return EpisodesViewHolder(
            binding = ItemEpisodeInCharacterDetailsBinding.inflate(
                LayoutInflater.from(parent.context)
            ),
            listener = onEpisodeClicked
        )
    }

    override fun onBindViewHolder(holder: EpisodesViewHolder, position: Int) {
        val currentEpisode = episodesList[position]
        holder.bind(currentEpisode)
    }

    override fun getItemCount(): Int = episodesList.size

    class EpisodesViewHolder(
        private val binding: ItemEpisodeInCharacterDetailsBinding,
        private val listener: OnItemSelectedListener<EpisodePresentation>
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(episode: EpisodePresentation) {
            binding.episodeAirDate.text = episode.airDate
            binding.episodeEpisode.text = episode.episode
            binding.episodeName.text = episode.name

            itemView.setOnClickListener { listener.onSelectItem(episode) }
        }
    }
}