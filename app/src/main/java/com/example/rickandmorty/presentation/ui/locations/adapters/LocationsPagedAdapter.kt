package com.example.rickandmorty.presentation.ui.locations.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.rickandmorty.R
import com.example.rickandmorty.databinding.ItemLocationBinding
import com.example.rickandmorty.presentation.models.LocationPresentation
import com.example.rickandmorty.util.OnItemSelectedListener

class LocationsPagedAdapter(
    private val onItemSelectedListener: OnItemSelectedListener<LocationPresentation>
) : PagingDataAdapter<LocationPresentation, LocationsPagedAdapter.LocationsPagedViewHolder>(
    diffCallback = LocationsPagedDiffCallback()
) {

    override fun onBindViewHolder(holder: LocationsPagedViewHolder, position: Int) {
        val location = getItem(position)

        location?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationsPagedViewHolder {
        return LocationsPagedViewHolder(
            itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_location, parent, false),
            listener = onItemSelectedListener
        )
    }

    class LocationsPagedViewHolder(
        itemView: View,
        private val listener: OnItemSelectedListener<LocationPresentation>
    ) : RecyclerView.ViewHolder(itemView) {

        private val binding = ItemLocationBinding.bind(itemView)

        fun bind(location: LocationPresentation) {
            binding.locationDimension.text = location.dimension
            binding.locationName.text = location.name
            binding.locationType.text = location.type

            itemView.setOnClickListener { listener.onSelectItem(location) }
        }
    }
}

class LocationsPagedDiffCallback : DiffUtil.ItemCallback<LocationPresentation>() {

    override fun areItemsTheSame(
        oldItem: LocationPresentation,
        newItem: LocationPresentation
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: LocationPresentation,
        newItem: LocationPresentation
    ): Boolean {
        return oldItem == newItem
    }
}