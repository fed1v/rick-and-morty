package com.example.rickandmorty.presentation.ui.characters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rickandmorty.data.Character
import com.example.rickandmorty.data.CharactersProvider
import com.example.rickandmorty.databinding.FragmentCharactersListBinding


class CharactersListFragment : Fragment() {

    private lateinit var binding: FragmentCharactersListBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharactersListBinding.inflate(inflater, container, false)

        binding.rvCharacters.layoutManager = GridLayoutManager(requireContext(), 2)
        val charactersAdapter = CharactersAdapter { onCharacterClicked(it) }
        charactersAdapter.charactersList = CharactersProvider.charactersList
        binding.rvCharacters.adapter = charactersAdapter

        return binding.root
    }

    private fun onCharacterClicked(character: Character) {
        println("Character: ${character.name}")
    }

    companion object {
        fun newInstance(param1: String, param2: String) =
            CharactersListFragment().apply {
                arguments = Bundle().apply {
                    //        putString(ARG_PARAM1, param1)
                    //        putString(ARG_PARAM2, param2)
                }
            }
    }


}