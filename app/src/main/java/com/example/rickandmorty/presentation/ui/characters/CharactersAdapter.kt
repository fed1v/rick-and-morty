package com.example.rickandmorty.presentation.ui.characters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rickandmorty.data.Character
import com.example.rickandmorty.databinding.ItemCharacterBinding

class CharactersAdapter(
    private val onCharacterClicked: (Character) -> Unit
) : RecyclerView.Adapter<CharactersAdapter.CharacterViewHolder>() {

    var charactersList: List<Character> = listOf()
        set(value) {
            field = value
        //    notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        return CharacterViewHolder(ItemCharacterBinding.inflate(LayoutInflater.from(parent.context)))
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
        val binding: ItemCharacterBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(character: Character) {
            binding.characterGender.text = character.gender
            binding.characterName.text = character.name
            binding.characterStatus.text = character.status
            binding.characterSpecies.text = character.species
        }
    }
}