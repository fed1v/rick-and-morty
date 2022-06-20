package com.example.rickandmorty.presentation.ui.locations

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rickandmorty.databinding.ItemLocationBinding
import com.example.rickandmorty.presentation.ui.models.LocationPresentation

class LocationsAdapter(
    private val onLocationClicked: (LocationPresentation) -> Unit
) : RecyclerView.Adapter<LocationsAdapter.LocationsViewHolder>() {

    var locationsList: List<LocationPresentation> = listOf()
        set(value) {
            field = value
            //    notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationsViewHolder {
        return LocationsViewHolder(ItemLocationBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: LocationsViewHolder, position: Int) {
        val currentEpisode = locationsList[position]
        holder.binding.root.setOnClickListener {
            onLocationClicked(locationsList[position])
        }
        holder.bind(currentEpisode)
    }

    override fun getItemCount(): Int = locationsList.size

    class LocationsViewHolder(
        val binding: ItemLocationBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(location: LocationPresentation) {
            binding.locationDimension.text = location.dimension
            binding.locationName.text = location.name
            binding.locationType.text = location.type
        }
    }
}