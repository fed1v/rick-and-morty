package com.example.rickandmorty.presentation.ui.characters.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rickandmorty.R
import com.example.rickandmorty.databinding.ItemCharacterInDetailsFragmentsBinding
import com.example.rickandmorty.presentation.models.CharacterPresentation
import com.example.rickandmorty.util.OnItemSelectedListener

class CharactersAdapter(
    private val onCharacterClicked: OnItemSelectedListener<CharacterPresentation>
) : RecyclerView.Adapter<CharactersAdapter.CharacterViewHolder>() {

    var charactersList: List<CharacterPresentation> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        return CharacterViewHolder(
            binding = ItemCharacterInDetailsFragmentsBinding.inflate(
                LayoutInflater.from(parent.context)
            ),
            listener = onCharacterClicked
        )
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val currentCharacter = charactersList[position]
        holder.bind(currentCharacter)
    }

    override fun getItemCount(): Int = charactersList.size

    class CharacterViewHolder(
        private val binding: ItemCharacterInDetailsFragmentsBinding,
        private val listener: OnItemSelectedListener<CharacterPresentation>
    ) : RecyclerView.ViewHolder(binding.root) {

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
                "Genderless" -> ContextCompat.getDrawable(
                    binding.root.context,
                    R.drawable.ic_genderless
                )
                else -> ContextCompat.getDrawable(binding.root.context, R.drawable.ic_question_mark)
            }
            binding.genderImage.setImageDrawable(genderDrawable)

            itemView.setOnClickListener { listener.onSelectItem(character) }
        }
    }
}