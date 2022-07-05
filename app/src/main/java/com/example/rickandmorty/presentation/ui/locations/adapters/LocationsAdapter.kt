package com.example.rickandmorty.presentation.ui.locations.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rickandmorty.databinding.ItemLocationBinding
import com.example.rickandmorty.presentation.models.LocationPresentation
import com.example.rickandmorty.util.OnItemSelectedListener

class LocationsAdapter(
    private val onLocationClicked: OnItemSelectedListener<LocationPresentation>
) : RecyclerView.Adapter<LocationsAdapter.LocationsViewHolder>() {

    var locationsList: List<LocationPresentation> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationsViewHolder {
        return LocationsViewHolder(
            binding = ItemLocationBinding.inflate(LayoutInflater.from(parent.context)),
            listener = onLocationClicked
        )
    }

    override fun onBindViewHolder(holder: LocationsViewHolder, position: Int) {
        val currentEpisode = locationsList[position]
        holder.bind(currentEpisode)
    }

    override fun getItemCount(): Int = locationsList.size

    class LocationsViewHolder(
        private val binding: ItemLocationBinding,
        private val listener: OnItemSelectedListener<LocationPresentation>
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(location: LocationPresentation) {
            binding.locationDimension.text = location.dimension
            binding.locationName.text = location.name
            binding.locationType.text = location.type

            itemView.setOnClickListener { listener.onSelectItem(location) }
        }
    }
}