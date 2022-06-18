package com.example.rickandmorty.presentation.ui.locations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rickandmorty.data.Character
import com.example.rickandmorty.data.CharactersProvider
import com.example.rickandmorty.data.Location
import com.example.rickandmorty.data.LocationsProvider
import com.example.rickandmorty.databinding.FragmentLocationDetailsBinding
import com.example.rickandmorty.presentation.ui.characters.CharacterDetailsFragment
import com.example.rickandmorty.presentation.ui.characters.CharactersAdapter
import com.example.rickandmorty.presentation.ui.hostActivity


class LocationDetailsFragment : Fragment() {

    private lateinit var binding: FragmentLocationDetailsBinding
    private lateinit var location: Location
    private lateinit var toolbar: Toolbar
    private lateinit var charactersAdapter: CharactersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        location = arguments?.getParcelable("Location") ?: Location(-1, "", "", "")
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLocationDetailsBinding.inflate(LayoutInflater.from(requireContext()))
        setBottomNavigationCheckedItem()

        initToolbar()
        showLocation()
        initRecyclerView()
        showLocationResidents(CharactersProvider.charactersList)

        return binding.root
    }

    private fun showLocationResidents(charactersList: List<Character>) {
        charactersAdapter.charactersList = charactersList
    }

    private fun initRecyclerView() {
        charactersAdapter = CharactersAdapter { onCharacterClicked(it) }
        binding.rvLocationResidents.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvLocationResidents.adapter = charactersAdapter
    }

    private fun onCharacterClicked(character: Character) {
        hostActivity().openFragment(
            CharacterDetailsFragment.newInstance(character),
            "CharacterDetailsFragment"
        )
    }

    private fun initToolbar() {
        toolbar = binding.locationToolbar
        hostActivity().setSupportActionBar(toolbar)
        hostActivity().supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                println("OnBackPressed")
                hostActivity().onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setBottomNavigationCheckedItem() {
        hostActivity().setBottomNavItemChecked(MENU_ITEM_NUMBER)
    }

    private fun showLocation() {
        binding.locationDimension.text = location.dimension
        binding.locationName.text = location.name
        binding.locationType.text = location.type
    }

    companion object {

        private const val MENU_ITEM_NUMBER: Int = 1

        fun newInstance(location: Location) =
            LocationDetailsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("Location", location)
                }
            }
    }
}