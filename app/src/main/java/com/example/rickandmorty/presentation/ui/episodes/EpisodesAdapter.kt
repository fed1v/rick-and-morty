package com.example.rickandmorty.presentation.ui.episodes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rickandmorty.data.Episode
import com.example.rickandmorty.databinding.ItemEpisodeBinding

class EpisodesAdapter(
    private val onEpisodeClicked: (Episode) -> Unit
) : RecyclerView.Adapter<EpisodesAdapter.EpisodesViewHolder>() {

    var episodesList: List<Episode> = listOf()
        set(value) {
            field = value
            //    notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodesViewHolder {
        return EpisodesViewHolder(ItemEpisodeBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: EpisodesViewHolder, position: Int) {
        val currentEpisode = episodesList[position]
        holder.binding.root.setOnClickListener {
            onEpisodeClicked(episodesList[position])
        }
        holder.bind(currentEpisode)
    }

    override fun getItemCount(): Int = episodesList.size

    class EpisodesViewHolder(
        val binding: ItemEpisodeBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(episode: Episode) {
            binding.episodeAirDate.text = episode.airDate
            binding.episodeEpisode.text = episode.episode
            binding.episodeName.text = episode.name
        }
    }
}