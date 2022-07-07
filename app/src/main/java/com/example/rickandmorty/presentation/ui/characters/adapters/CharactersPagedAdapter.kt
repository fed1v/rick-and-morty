package com.example.rickandmorty.presentation.ui.characters.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rickandmorty.R
import com.example.rickandmorty.databinding.ItemCharacterBinding
import com.example.rickandmorty.presentation.models.CharacterPresentation
import com.example.rickandmorty.util.OnItemSelectedListener

class CharactersPagedAdapter(
    private val onItemSelectedListener: OnItemSelectedListener<CharacterPresentation>
) : PagingDataAdapter<CharacterPresentation, CharactersPagedAdapter.CharactersPagedViewHolder>(
        diffCallback = CharactersPagedDiffCallback()
    ) {


    override fun onBindViewHolder(holder: CharactersPagedViewHolder, position: Int) {
        val character = getItem(position)

        character?.let {
            holder.bind(it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharactersPagedViewHolder {
        return CharactersPagedViewHolder(
            itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_character, parent, false),
            listener = onItemSelectedListener
        )
    }


    class CharactersPagedViewHolder(
        itemView: View,
        private val listener: OnItemSelectedListener<CharacterPresentation>
    ) : RecyclerView.ViewHolder(itemView) {

        val binding = ItemCharacterBinding.bind(itemView)

        fun bind(character: CharacterPresentation) {
            Glide.with(binding.root.context)
                .load(character.image)
                .placeholder(R.drawable.ic_image)
                .error(R.drawable.ic_image)
                .into(binding.characterImage)

            binding.characterName.text = character.name
            binding.characterStatus.text = character.status
            binding.characterSpecies.text = character.species

            val genderDrawable = when (character.gender) {
                "Male" -> ContextCompat.getDrawable(binding.root.context, R.drawable.ic_male)
                "Female" -> ContextCompat.getDrawable(binding.root.context, R.drawable.ic_female)
                "Genderless" -> ContextCompat.getDrawable(binding.root.context, R.drawable.ic_genderless)
                else -> ContextCompat.getDrawable(binding.root.context, R.drawable.ic_question_mark)
            }

            binding.genderImage.setImageDrawable(genderDrawable)

            itemView.setOnClickListener { listener.onSelectItem(character) }
        }
    }


}

class CharactersPagedDiffCallback : DiffUtil.ItemCallback<CharacterPresentation>() {

    override fun areItemsTheSame(
        oldItem: CharacterPresentation,
        newItem: CharacterPresentation
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: CharacterPresentation,
        newItem: CharacterPresentation
    ): Boolean {
        return oldItem == newItem
    }
}