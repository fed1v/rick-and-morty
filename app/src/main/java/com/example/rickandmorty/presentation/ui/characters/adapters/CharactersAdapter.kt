package com.example.rickandmorty.presentation.ui.characters.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rickandmorty.R
import com.example.rickandmorty.databinding.ItemCharacterInDetailsFragmentsBinding
import com.example.rickandmorty.presentation.models.CharacterPresentation

class CharactersAdapter(
    private val onCharacterClicked: (CharacterPresentation) -> Unit
) : RecyclerView.Adapter<CharactersAdapter.CharacterViewHolder>() {

    var charactersList: List<CharacterPresentation> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        return CharacterViewHolder(ItemCharacterInDetailsFragmentsBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val currentCharacter = charactersList[position]
        holder.binding.root.setOnClickListener {
            onCharacterClicked(charactersList[position])
        }
        holder.bind(currentCharacter)
    }

    override fun getItemCount(): Int = charactersList.size

    class CharacterViewHolder(
        val binding: ItemCharacterInDetailsFragmentsBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(character: CharacterPresentation) {
            Glide.with(binding.root.context)
                .load(character.image)
                .placeholder(R.drawable.ic_image)
                .error(R.drawable.ic_image)
                .into(binding.characterImage)


            binding.characterGender.text = character.gender
            binding.characterName.text = character.name
            binding.characterStatus.text = character.status
            binding.characterSpecies.text = character.species
        }
    }
}