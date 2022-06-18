package com.example.rickandmorty.presentation.ui.characters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import com.example.rickandmorty.R
import com.example.rickandmorty.data.Character
import com.example.rickandmorty.databinding.FragmentCharacterDetailsBinding
import com.example.rickandmorty.presentation.ui.hostActivity


class CharacterDetailsFragment : Fragment() {

    private lateinit var binding: FragmentCharacterDetailsBinding
    private lateinit var character: Character

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        character = arguments?.getParcelable("Character") ?: Character(-1, "", "", "", "")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharacterDetailsBinding.inflate(inflater)
        setBottomNavigationCheckedItem()

        showCharacter()

        hostActivity().setSupportActionBar(binding.characterToolbar)

        return binding.root
    }

    private fun setBottomNavigationCheckedItem() {
        hostActivity().setBottomNavItemChecked(MENU_ITEM_NUMBER)
    }

    private fun showCharacter() {
        binding.characterImage.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.rick_image))
        binding.characterName.text = character.name
        binding.characterSpecies.text = character.species
        binding.characterStatus.text = character.status
        binding.characterGender.text = character.gender
    }


    companion object {

        private const val MENU_ITEM_NUMBER: Int = 0

        fun newInstance(character: Character) =
            CharacterDetailsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("Character", character)
                }
            }
    }
}