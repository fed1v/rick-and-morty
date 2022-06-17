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
import com.example.rickandmorty.presentation.ui.hostActivity


class CharactersListFragment : Fragment() {

    private lateinit var binding: FragmentCharactersListBinding
    private lateinit var charactersAdapter: CharactersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharactersListBinding.inflate(inflater, container, false)
        setBottomNavigationCheckedItem()

        initRecyclerView()

        showCharacters(CharactersProvider.charactersList)

        return binding.root
    }

    private fun initRecyclerView(){
        charactersAdapter = CharactersAdapter { onCharacterClicked(it) }
        binding.rvCharacters.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvCharacters.adapter = charactersAdapter
    }

    private fun showCharacters(characters: List<Character>) {
        charactersAdapter.charactersList = characters
    }

    private fun setBottomNavigationCheckedItem() {
        hostActivity().setBottomNavItemChecked(MENU_ITEM_NUMBER)
    }

    private fun onCharacterClicked(character: Character) {
        println("Character: ${character.name}")
        hostActivity().openFragment(
            fragment = CharacterDetailsFragment.newInstance(character),
            tag = "CharacterDetailsFragment"
        )
    }

    companion object {

        private const val MENU_ITEM_NUMBER: Int = 0

        fun newInstance(param1: String, param2: String) =
            CharactersListFragment().apply {
                arguments = Bundle().apply {
                    //        putString(ARG_PARAM1, param1)
                    //        putString(ARG_PARAM2, param2)
                }
            }
    }


}