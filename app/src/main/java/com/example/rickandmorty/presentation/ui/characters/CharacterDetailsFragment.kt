package com.example.rickandmorty.presentation.ui.characters

import android.os.Bundle
import android.view.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.rickandmorty.R
import com.example.rickandmorty.data.Character
import com.example.rickandmorty.databinding.FragmentCharacterDetailsBinding
import com.example.rickandmorty.presentation.ui.hostActivity


class CharacterDetailsFragment : Fragment() {

    private lateinit var binding: FragmentCharacterDetailsBinding
    private lateinit var character: Character
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        character = arguments?.getParcelable("Character") ?: Character(-1, "", "", "", "")
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharacterDetailsBinding.inflate(inflater)
        setBottomNavigationCheckedItem()

        initToolbar()
        showCharacter()

        hostActivity().setSupportActionBar(binding.characterToolbar)

        return binding.root
    }

    private fun initToolbar() {
        toolbar = binding.characterToolbar
        hostActivity().setSupportActionBar(toolbar)
        hostActivity().supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                hostActivity().onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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